package controller;

import dao.VoucherDAO;
import model.Voucher; // Đã sửa thành model.Voucher theo đúng dự án của bạn
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "VoucherServlet", urlPatterns = {"/admin/voucher", "/voucher"})
public class VoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Có thể để trống hoặc dùng để điều hướng sang trang JSP nếu cần
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        VoucherDAO voucherDAO = new VoucherDAO();

        if ("create".equals(action)) {
            // 1. XỬ LÝ TẠO MÃ GIẢM GIÁ (ADMIN)
            String code = request.getParameter("code");
            double discountPercent = Double.parseDouble(request.getParameter("discountPercent"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            Date startDate = Date.valueOf(request.getParameter("startDate"));
            Date endDate = Date.valueOf(request.getParameter("endDate"));

            // Sử dụng lớp Voucher mới thay cho VoucherModel
            Voucher voucher = new Voucher(code, discountPercent, quantity, startDate, endDate);
            boolean isSuccess = voucherDAO.createVoucher(voucher);

            if (isSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/voucher?msg=success");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/voucher?msg=fail");
            }

        } else if ("apply".equals(action)) {
            // 2. XỬ LÝ ÁP DỤNG MÃ KHI ĐẶT HÀNG (AJAX)
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            String code = request.getParameter("code");
            Voucher voucher = voucherDAO.getVoucherByCode(code); // Sửa thành Voucher
            
            long currentDateMillis = System.currentTimeMillis();
            Date today = new Date(currentDateMillis);

            // Tự động nối chuỗi JSON thủ công để tránh bị lỗi đỏ do thiếu thư viện org.json
            if (voucher == null) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá không tồn tại!\"}");
            } else if (voucher.getQuantity() <= 0) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá đã hết lượt sử dụng!\"}");
            } else if (today.before(voucher.getStartDate()) || today.after(voucher.getEndDate())) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá đã hết hạn hoặc chưa được áp dụng!\"}");
            } else {
                // Hợp lệ -> Trả về phần trăm giảm và trạng thái thành công
                out.print("{\"status\":\"success\", \"discountPercent\":" + voucher.getDiscountPercent() + 
                          ", \"voucherId\":" + voucher.getId() + ", \"message\":\"Áp dụng mã thành công!\"}");
            }
            
            out.flush();
        }
    }
}