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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        VoucherDAO voucherDAO = new VoucherDAO();
        java.util.List<model.Voucher> listVouchers = voucherDAO.getAllVouchers();

        // Lấy thời gian hiện tại của hệ thống để so sánh bằng Java
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        int availableCount = 0;

        // Vòng lặp đếm các voucher thực sự hợp lệ
        if (listVouchers != null) {
            for (model.Voucher v : listVouchers) {
                if (v.getUsedCount() < v.getUsageLimit() && (v.getExpiryDate() == null || v.getExpiryDate().after(now))) {
                    availableCount++;
                }
            }
        }

        // Gửi dữ liệu xuống trang voucher.jsp
        request.setAttribute("vouchersList", listVouchers);
        request.setAttribute("availableCount", availableCount);

        // Cập nhật luôn cho menu ở header.jsp nhận số lượng mới nhất mà không cần đăng nhập lại
        request.getSession().setAttribute("voucherCount", availableCount);

        request.getRequestDispatcher("/WEB-INF/views/account/voucher.jsp").forward(request, response);
    }

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

            // Lấy tổng tiền hiện tại của giỏ hàng gửi từ giao diện lên
            double orderAmount = 0;
            try {
                orderAmount = Double.parseDouble(request.getParameter("orderAmount"));
            } catch (Exception e) {
                orderAmount = 0;
            }

            Voucher voucher = voucherDAO.getVoucherByCode(code);
            Timestamp now = new Timestamp(System.currentTimeMillis());

            if (voucher == null) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá không tồn tại!\"}");
            } else if (voucher.getUsedCount() >= voucher.getUsageLimit()) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá đã hết lượt sử dụng!\"}");
            } else if (voucher.getExpiryDate() != null && now.after(voucher.getExpiryDate())) {
                out.print("{\"status\":\"error\", \"message\":\"Mã giảm giá đã hết hạn sử dụng!\"}");
            } else if (orderAmount < voucher.getMinOrderValue()) {
                // 🛑 ĐIỀU KIỆN MỚI: Kiểm tra giá trị đơn hàng tối thiểu
                out.print("{\"status\":\"error\", \"message\":\"Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã này!\"}");
            } else {
                // Thỏa mãn mọi điều kiện -> Trả về thêm max_discount để tính số tiền giảm tối đa ở Front-end
                out.print("{\"status\":\"success\", "
                        + "\"discountPercent\":" + voucher.getDiscountPercent() + ", "
                        + "\"maxDiscount\":" + voucher.getMaxDiscount() + ", "
                        + "\"message\":\"Áp dụng mã thành công!\"}");
            }
            out.flush();
        }
    }
}
