/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.UserDAO;
import dao.CartDAO;
import model.User;
import model.CartItem;
import java.io.IOException;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    // HÀM GỌI API GOOGLE ĐỂ LẤY ĐIỂM BONUS
    private boolean verifyRecaptcha(String recaptchaResponse) {
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
            return false;
        }
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            // TODO: Bạn (Phát) cần thay thế Secret Key thật lấy từ Google reCAPTCHA Admin
            String secret = "YOUR_GOOGLE_SECRET_KEY";
            String params = "secret=" + secret + "&response=" + recaptchaResponse;

            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream out = http.getOutputStream()) {
                out.write(params.getBytes("UTF-8"));
            }

            try (InputStream res = http.getInputStream(); BufferedReader rd = new BufferedReader(new InputStreamReader(res, "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                // Nếu JSON trả về chứa "success": true tức là người thật (không phải bot)
                return sb.toString().contains("\"success\": true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

        // Lấy token reCAPTCHA từ form gửi lên
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        // KIỂM TRA BOT (Bỏ comment dòng dưới khi có key thật để bắt đầu tính năng)
        /*
        if (!verifyRecaptcha(gRecaptchaResponse)) {
            request.setAttribute("errorMessage", "Vui lòng xác thực bạn không phải là Robot!");
            if ("register".equals(action)) {
                request.getRequestDispatcher("/views/account/register.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/views/account/login.jsp").forward(request, response);
            }
            return;
        }
         */
        if ("login".equals(action) || "signin".equals(action)) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            User user = userDao.checkLogin(email, password);

            // Trong đoạn // Đồng bộ giỏ hàng của AuthServlet
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("account", user);

                // Lấy guestToken từ cookie để đồng bộ
                String guestToken = null;
                if (request.getCookies() != null) {
                    for (Cookie c : request.getCookies()) {
                        if (c.getName().equals("guest_token")) {
                            guestToken = c.getValue();
                        }
                    }
                }

                // Đồng bộ giỏ hàng từ Guest sang User
                if (guestToken != null) {
                    CartDAO cartDAO = new CartDAO();
                    // Cập nhật Database: Gán user_id cho giỏ hàng của guest
                    String updateSql = "UPDATE Carts SET user_id = ?, guest_token = NULL WHERE guest_token = ?";
                    try (PreparedStatement ps = cartDAO.getConnection().prepareStatement(updateSql)) {
                        ps.setInt(1, user.getId());
                        ps.setString(2, guestToken);
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                response.sendRedirect(request.getContextPath() + "/home");

            }
        }
    }
}
