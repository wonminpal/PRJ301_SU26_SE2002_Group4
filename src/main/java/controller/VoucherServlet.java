package controller;

import dao.VoucherDAO;
import model.Voucher;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "VoucherServlet", urlPatterns = {"/voucher"})
public class VoucherServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        VoucherDAO voucherDAO = new VoucherDAO();

        if ("apply".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            String code = request.getParameter("code");
            Voucher voucher = voucherDAO.getVoucherByCode(code);
            
            Timestamp now = new Timestamp(System.currentTimeMillis());

            if (voucher == null) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá không tồn tại!\"}");
            } else if (voucher.getUsedCount() >= voucher.getUsageLimit()) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá đã hết lượt sử dụng!\"}");
            } else if (voucher.getExpiryDate() != null && now.after(voucher.getExpiryDate())) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá đã hết hạn sử dụng!\"}");
            } else {
                // Trả về dữ liệu hợp lệ
                out.print("{\"status\":\"success\", \"discountPercent\":" + voucher.getDiscountPercent() + 
                          ", \"message\":\"Áp dụng mã thành công!\"}");
            }
            out.flush();
        }
    }
}