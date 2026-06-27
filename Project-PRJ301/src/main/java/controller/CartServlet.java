/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CartDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.CartItem;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "view"; // Mặc định là xem giỏ hàng
        }

        CartDAO cartDAO = new CartDAO();

        // GIẢ LẬP: Mặc định userId = 1 (Tài khoản mẫu)
        // Sau này bạn Phát làm Login xong, em đổi thành code lấy từ Session nhé:
        // User user = (User) request.getSession().getAttribute("account");
        // int userId = user.getId();
        int userId = 1;

        if (action.equals("view")) {
            // Lấy danh sách sản phẩm và đẩy sang cart.jsp tính tiền
            List<CartItem> cartItems = cartDAO.getCartItemsByUserId(userId);
            request.setAttribute("cartItems", cartItems);
            request.getRequestDispatcher("/WEB-INF/views//client/cart/cart.jsp").forward(request, response);

        } else if (action.equals("remove")) {
            // Lấy ID sản phẩm và gọi hàm xóa
            int productId = Integer.parseInt(request.getParameter("id"));
            cartDAO.removeItem(userId, productId);

            // Xóa xong thì load lại trang giỏ hàng
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    // Hàm xử lý việc THÊM và CẬP NHẬT SỐ LƯỢNG (Form submit)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        CartDAO cartDAO = new CartDAO();
        int userId = 1; // Giả lập user 1

        if (action.equals("add")) {
            int productId = Integer.parseInt(request.getParameter("id"));
            int quantity = 1; // Bấm nút "Thêm vào giỏ" ngoài Home thì mặc định là 1

            cartDAO.addToCart(userId, productId, quantity);
            response.sendRedirect(request.getContextPath() + "/cart");

        } else if (action.equals("update")) {
            int productId = Integer.parseInt(request.getParameter("id"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            if (quantity > 0) {
                cartDAO.updateQuantity(userId, productId, quantity);
            } else {
                // Nếu khách cố tình nhập số lượng = 0 thì tự động xóa món đó
                cartDAO.removeItem(userId, productId);
            }
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
}
