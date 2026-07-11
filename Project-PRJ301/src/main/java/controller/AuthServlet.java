/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import util.VerifyRecaptcha;
import dao.CartDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import model.CartItem;
import model.User;

/**
 *
 * @author Nguyen Minh Phat - CE201621
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

    private UserDAO userDao;

    @Override
    public void init() {
        userDao = new UserDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("auth?action=loginForm");
        } else if ("registerForm".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/views/account/register.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/account/login.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        if (!VerifyRecaptcha.verify(gRecaptchaResponse)) {
            request.setAttribute("errorMessage", "Vui lòng xác thực bạn không phải là Robot!");
            if ("register".equals(action) || "signup".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/views/account/register.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/views/account/login.jsp").forward(request, response);
            }
            return;
        }

        if ("login".equals(action) || "signin".equals(action)) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            User user = userDao.checkLogin(email, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("account", user);

                // Đồng bộ giỏ hàng
                Map<Integer, CartItem> sessionCart = (Map<Integer, CartItem>) session.getAttribute("sessionCart");
                if (sessionCart != null && !sessionCart.isEmpty()) {
                    CartDAO cartDAO = new CartDAO();
                    for (CartItem item : sessionCart.values()) {
                        cartDAO.addToCart(user.getId(), item.getProductId(), item.getQuantity());
                    }
                    session.removeAttribute("sessionCart");
                }

                // SỬA DÒNG NÀY CHUẨN ĐƯỜNG DẪN:
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                request.setAttribute("errorMessage", "Email hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("/WEB-INF/views/account/login.jsp").forward(request, response);
            }

            // SỬA Ở ĐÂY: Bắt cả 'register' và 'signup'
        } else if ("register".equals(action) || "signup".equals(action)) {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (userDao.registerUser(fullName, email, password)) {
                response.sendRedirect("auth?action=signinForm&success=true");
            } else {
                request.setAttribute("errorMessage", "Email đã tồn tại trong hệ thống!");
                request.getRequestDispatcher("/WEB-INF/views/account/register.jsp").forward(request, response);
            }
        }
    }
}
