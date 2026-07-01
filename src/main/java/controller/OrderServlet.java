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

        // 1. Kiểm tra đăng nhập bằng ContextPath để tránh lỗi đường dẫn 404
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=loginForm");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        request.setAttribute("orderList", orderDAO.getOrdersByUserId(user.getId()));
        request.getRequestDispatcher("/WEB-INF/views/client/order/orderHistory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        // Lấy Guest Token từ Cookie (Cho chức năng giỏ hàng vãng lai)
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

        // ===============================================
        // LUỒNG 1: HIỂN THỊ TRANG XÁC NHẬN THANH TOÁN
        // ===============================================
        if (path.equals("/checkout")) {
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth?action=loginForm");
                return;
            }

            List<CartItem> cartItems = cartDAO.getCartItems(user.getId(), guestToken);
            if (cartItems == null || cartItems.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            double totalPrice = cartItems.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("totalPrice", totalPrice);
            request.getRequestDispatcher("/WEB-INF/views/client/order/checkout.jsp").forward(request, response);
        } // ===============================================
        // LUỒNG 2: XỬ LÝ LƯU ĐƠN HÀNG XUỐNG DATABASE
        // ===============================================
        else if (path.equals("/order")) {
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth?action=loginForm");
                return;
            }

            int userId = user.getId();
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");

            try {
                List<CartItem> cartItems = cartDAO.getCartItems(userId, guestToken);

                if (cartItems == null || cartItems.isEmpty()) {
                    request.setAttribute("errorMsg", "Giỏ hàng rỗng, không thể tiến hành thanh toán!");
                    request.getRequestDispatcher("/WEB-INF/views/client/order/checkout.jsp").forward(request, response);
                    return;
                }

                // Thực thi Transaction (Trừ kho, Thêm Bill, Xóa Giỏ)
                boolean success = orderDAO.placeOrder(userId, address, phone, cartItems);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/order");
                }
            } catch (Exception e) {
                // Bắt lỗi Transaction (Hết hàng tồn kho) và ném về lại trang checkout
                request.setAttribute("errorMsg", e.getMessage());

                List<CartItem> cartItems = cartDAO.getCartItems(userId, guestToken);
                double totalPrice = cartItems.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
                request.setAttribute("cartItems", cartItems);
                request.setAttribute("totalPrice", totalPrice);

                request.getRequestDispatcher("/WEB-INF/views/client/order/checkout.jsp").forward(request, response);
            }
        }
    }
}
