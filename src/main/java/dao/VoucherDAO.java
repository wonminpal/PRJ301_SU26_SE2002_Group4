package dao;

import db.DBContext;
import model.Voucher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VoucherDAO extends DBContext {

    // CLIENT & AJAX: Tìm mã giảm giá để áp dụng
    public Voucher getVoucherByCode(String code) {
        String query = "SELECT * FROM Vouchers WHERE code = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Voucher voucher = new Voucher();
                    voucher.setId(rs.getInt("id"));
                    voucher.setCode(rs.getString("code"));
                    voucher.setDiscountPercent(rs.getInt("discount_percent"));
                    voucher.setMaxDiscount(rs.getDouble("max_discount"));
                    voucher.setMinOrderValue(rs.getDouble("min_order_value"));
                    voucher.setExpiryDate(rs.getTimestamp("expiry_date"));
                    voucher.setUsageLimit(rs.getInt("usage_limit"));
                    voucher.setUsedCount(rs.getInt("used_count"));
                    return voucher;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // TĂNG SỐ LẦN SỬ DỤNG: Khi đặt hàng thành công thì tăng used_count lên 1
    public boolean increaseUsedCount(int voucherId) {
        String query = "UPDATE Vouchers SET used_count = used_count + 1 WHERE id = ? AND used_count < usage_limit";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, voucherId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // CLIENT: Lấy toàn bộ danh sách voucher để hiển thị lên kho voucher
    public java.util.List<Voucher> getAllVouchers() {
    java.util.List<Voucher> list = new java.util.ArrayList<>();
    String query = "SELECT * FROM Vouchers";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Voucher voucher = new Voucher();
            voucher.setId(rs.getInt("id"));
            voucher.setCode(rs.getString("code"));
            voucher.setDiscountPercent(rs.getInt("discount_percent"));
            voucher.setMaxDiscount(rs.getDouble("max_discount"));
            voucher.setMinOrderValue(rs.getDouble("min_order_value"));
            voucher.setExpiryDate(rs.getTimestamp("expiry_date"));
            voucher.setUsageLimit(rs.getInt("usage_limit"));
            voucher.setUsedCount(rs.getInt("used_count"));
            list.add(voucher);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

    // Đếm số lượng voucher còn khả dụng (chưa hết lượt và chưa hết hạn)
    public int getAvailableVouchersCount() {
    // Ép kiểu chuỗi YYYY-MM-DD để SQL Server tự so sánh chuẩn, không sợ lệch múi giờ hệ thống
    String query = "SELECT COUNT(*) FROM Vouchers "
                 + "WHERE used_count < usage_limit "
                 + "AND (expiry_date IS NULL OR expiry_date > CONVERT(datetime, ?, 120))";
    
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        
        // Tạo chuỗi thời gian cố định dựa theo năm hiện tại
        // Định dạng chuẩn: YYYY-MM-DD HH:mm:ss
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowStr = sdf.format(new java.util.Date()); 
        
        ps.setString(1, nowStr);
        
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}
}
