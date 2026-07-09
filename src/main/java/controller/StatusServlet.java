package controller;

import dao.OrderDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "StatusServlet", urlPatterns = {"/admin/order"})
public class StatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Có thể dùng để hiển thị trang admin_order_status.jsp danh sách đơn hàng
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("update".equals(action)) {
            // XỬ LÝ ĐỔI TRẠNG THÁI ĐƠN HÀNG (ADMIN)
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String status = request.getParameter("status"); // 0, 1, hoặc 2

                // Gọi OrderDAO để cập nhật vào database
                OrderDAO orderDAO = new OrderDAO();
                boolean isUpdated = orderDAO.updateStatus(orderId, status);

                if (isUpdated) {
                    // Cập nhật thành công, quay về trang quản lý đơn hàng
                    response.sendRedirect(request.getContextPath() + "/admin/orders-list?status=success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/orders-list?status=fail");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/orders-list?status=invalid");
            }
        }
    }
}