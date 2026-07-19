package controller;

import dao.CartDAO;
import model.CartItem;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private String getGuestToken(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("guest_token")) {
                    return c.getValue();
                }
            }
        }
        String token = java.util.UUID.randomUUID().toString();
        Cookie cookie = new Cookie("guest_token", token);
        cookie.setMaxAge(60 * 60 * 24 * 30);
        cookie.setPath("/");
        response.addCookie(cookie);
        return token;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");
        Integer userId = (user != null) ? user.getId() : null;
        String guestToken = (user == null) ? getGuestToken(request, response) : null;

        CartDAO cartDAO = new CartDAO();
        String action = request.getParameter("action") == null ? "view" : request.getParameter("action");

        if (action.equals("view")) {
            List<CartItem> cartItems = cartDAO.getCartItems(userId, guestToken);

            // 💡 TỰ ĐỘNG TÍNH TỔNG TIỀN TẠM TÍNH CỦA GIỎ HÀNG
            double subTotal = 0;
            if (cartItems != null) {
                for (CartItem item : cartItems) {
                    // Giả sử Model CartItem của bạn có item.getProduct().getPrice() và item.getQuantity()
                    // Thay thế bằng hàm lấy giá chính xác của bạn nếu cần (ví dụ: item.getPrice())
                    subTotal += item.getProduct().getPrice() * item.getQuantity();
                }
            }

            request.setAttribute("cartItems", cartItems);
            request.setAttribute("subTotal", subTotal); // Đẩy tổng tiền gốc xuống giao diện

            request.getRequestDispatcher("/WEB-INF/views/client/cart/cart.jsp").forward(request, response);

        } else if (action.equals("remove")) {
            int productId = Integer.parseInt(request.getParameter("id"));
            String variant = request.getParameter("variant");
            cartDAO.removeItem(userId, guestToken, productId, variant);
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");
        Integer userId = (user != null) ? user.getId() : null;
        String guestToken = (user == null) ? getGuestToken(request, response) : null;

        CartDAO cartDAO = new CartDAO();

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // 💡 BỔ SUNG: XỬ LÝ HỦY VOUCHER KHỎI ĐƠN HÀNG (Nếu người dùng bấm xóa mã)
        if (action.equals("removeVoucher")) {
            session.removeAttribute("appliedVoucher");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        int productId = Integer.parseInt(idRaw);
        String variant = request.getParameter("variant");

        if (action.equals("add")) {
            cartDAO.addToCart(userId, guestToken, productId, 1, variant);
            response.sendRedirect(request.getContextPath() + "/cart");

        } else if (action.equals("update")) {
            String quantityRaw = request.getParameter("quantity");
            if (quantityRaw != null && !quantityRaw.trim().isEmpty()) {
                int quantity = Integer.parseInt(quantityRaw);
                cartDAO.updateQuantity(userId, guestToken, productId, variant, quantity);

                // 💡 Khi thay đổi số lượng, tổng tiền đổi -> Xóa voucher cũ để bắt check lại điều kiện tiền tối thiểu
                session.removeAttribute("appliedVoucher");
            }
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
}
