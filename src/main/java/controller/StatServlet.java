package controller;

import dao.StatDAO;
import model.User; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class StatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        HttpSession session = request.getSession(false);

        // 1. Kiểm tra session với đúng tên biến "account" của nhóm bạn
        if (session == null || session.getAttribute("account") == null) {
            // Đá về đúng đường dẫn trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/auth?action=signinForm");
            return;
        }

        // 2. Ép kiểu và kiểm tra quyền (role = 1 là Admin)
        User acc = (User) session.getAttribute("account"); 
        if (acc.getRole() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này!");
            return;
        }

        // 3. Nếu đúng là Admin thì gọi DB và trả về giao diện Dashboard
        StatDAO statDAO = new StatDAO();
        List<Map<String, Object>> revenueList = statDAO.getMonthlyRevenue();
        List<Map<String, Object>> topProducts = statDAO.getTopSellingProducts(10);

        request.setAttribute("revenueList", revenueList);
        request.setAttribute("topProducts", topProducts);

        request.getRequestDispatcher("/WEB-INF/views/admin/admin_dashboard.jsp").forward(request, response);
    }
}