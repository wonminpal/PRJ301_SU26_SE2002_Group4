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

        if ("login".equals(action) || "signin".equals(action)) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            User user = userDao.checkLogin(email, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("account", user);

                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                request.setAttribute("errorMessage", "Email hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("/WEB-INF/views/account/login.jsp").forward(request, response);
            }

        } else if ("register".equals(action) || "signup".equals(action)) {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (userDao.registerUser(fullName, email, password)) {
                response.sendRedirect(request.getContextPath() + "/auth?action=loginForm&success=true");
            } else {
                request.setAttribute("errorMessage", "Email đã tồn tại trong hệ thống!");
                request.getRequestDispatcher("/WEB-INF/views/account/register.jsp").forward(request, response);
            }
        }
    }
}
