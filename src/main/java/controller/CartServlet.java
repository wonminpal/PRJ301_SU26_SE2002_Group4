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
                if (c.getName().equals("guest_token")) return c.getValue();
            }
        }
        String token = java.util.UUID.randomUUID().toString();
        Cookie cookie = new Cookie("guest_token", token);
        cookie.setMaxAge(60 * 60 * 24 * 30);
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
            request.setAttribute("cartItems", cartItems);
            request.getRequestDispatcher("/WEB-INF/cart.jsp").forward(request, response);
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
        int productId = Integer.parseInt(request.getParameter("id"));
        String variant = request.getParameter("variant");

        if (action.equals("add")) {
            cartDAO.addToCart(userId, guestToken, productId, 1, variant);
            response.sendRedirect(request.getContextPath() + "/cart");
        } else if (action.equals("update")) {
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            cartDAO.updateQuantity(userId, guestToken, productId, variant, quantity);
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
}