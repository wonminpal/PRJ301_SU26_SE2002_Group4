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
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
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
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, voucherId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}