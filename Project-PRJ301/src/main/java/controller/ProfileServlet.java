/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

/**
 *
 * @author Nguyen Minh Phat - CE201621
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private UserDAO userDao;

    @Override
    public void init() {
        userDao = new UserDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("account") : null;

        if (sessionUser == null) {
            response.sendRedirect("auth?action=loginForm");
            return;
        }

        // Cập nhật lại thông tin mới nhất từ DB
        User currentUser = userDao.getUserByEmail(sessionUser.getEmail());
        request.setAttribute("user", currentUser);
        request.getRequestDispatcher("/WEB-INF/views/account/profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("account") : null;

        if (sessionUser == null) {
            response.sendRedirect("auth?action=loginForm");
            return;
        }

        String action = request.getParameter("action");
        String email = sessionUser.getEmail();

        if ("updateInfo".equals(action)) {
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");

            if (userDao.updateProfile(email, fullName, phone, address)) {
                request.setAttribute("successMsg", "Cập nhật hồ sơ thành công!");
            } else {
                request.setAttribute("errorMsg", "Cập nhật thất bại.");
            }
        } else if ("changePassword".equals(action)) {
            String oldPass = request.getParameter("oldPassword");
            String newPass = request.getParameter("newPassword");
            String confirmPass = request.getParameter("confirmPassword");

            if (!newPass.equals(confirmPass)) {
                request.setAttribute("errorPassMsg", "Mật khẩu xác nhận không khớp!");
            } else if (userDao.changePassword(email, oldPass, newPass)) {
                request.setAttribute("successPassMsg", "Đổi mật khẩu thành công!");
            } else {
                request.setAttribute("errorPassMsg", "Mật khẩu cũ không chính xác!");
            }
        }

        // Nạp lại dữ liệu hiển thị
        request.setAttribute("user", userDao.getUserByEmail(email));
        request.getRequestDispatcher("/WEB-INF/views/account/profile.jsp").forward(request, response);
    }
}
