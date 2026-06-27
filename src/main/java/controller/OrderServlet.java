package controller;

import dao.CartDAO;
import dao.OrderDAO;
import model.*;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "OrderServlet", urlPatterns = {"/order", "/checkout"})
public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        // Chỉ cho phép xem đơn hàng khi đã đăng nhập
        if (user == null) {
            response.sendRedirect("auth");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        request.setAttribute("orderList", orderDAO.getOrdersByUserId(user.getId()));
        request.getRequestDispatcher("/WEB-INF/orderHistory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        // Lấy Guest Token từ Cookie
        String guestToken = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("guest_token")) {
                    guestToken = c.getValue();
                }
            }
        }

        CartDAO cartDAO = new CartDAO();
        OrderDAO orderDAO = new OrderDAO();
        String path = request.getServletPath();

        if (path.equals("/checkout")) {
            // Nếu chưa đăng nhập, chuyển về trang auth
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth?action=loginForm");
                return; // Dừng lại không cho vào trang checkout
            }

            // Nếu đã đăng nhập thì mới cho thanh toán
            int userId = user.getId();

            // Lấy giỏ hàng theo User hoặc Guest
            List<CartItem> cartItems = cartDAO.getCartItems(user != null ? user.getId() : null, guestToken);
            if (cartItems == null || cartItems.isEmpty()) {
                response.sendRedirect("cart");
                return;
            }
            double totalPrice = cartItems.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("totalPrice", totalPrice);
            request.getRequestDispatcher("/WEB-INF/checkout.jsp").forward(request, response);
        } else if (path.equals("/order")) {
            // Xử lý đặt hàng tương tự, nhớ truyền user.getId() hoặc null
            // ... (Code đặt hàng của Phúc)
        }
    }
}
