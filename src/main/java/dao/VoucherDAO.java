package dao;

import db.DBContext; // Đã sửa từ context.DBContext thành db.DBContext
import model.Voucher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Kế thừa DBContext giống hệt CartDAO của dự án
public class VoucherDAO extends DBContext {

    // 1. ADMIN: Tạo mã giảm giá mới
    public boolean createVoucher(Voucher voucher) { 
        String query = "INSERT INTO Vouchers (code, discount_percent, quantity, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        // Gọi thẳng getConnection() trực tiếp nhờ cơ chế kế thừa
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, voucher.getCode());
            ps.setDouble(2, voucher.getDiscountPercent());
            ps.setInt(3, voucher.getQuantity());
            ps.setDate(4, voucher.getStartDate());
            ps.setDate(5, voucher.getEndDate());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. CLIENT & SERVLET: Tìm kiếm voucher theo Code để kiểm tra điều kiện áp dụng
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
                    // Bạn chú ý check lại tên cột discount_percent trong DB của bạn nha
                    voucher.setDiscountPercent(rs.getDouble("discount_percent")); 
                    voucher.setQuantity(rs.getInt("quantity"));
                    voucher.setStartDate(rs.getDate("start_date"));
                    voucher.setEndDate(rs.getDate("end_date"));
                    return voucher;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. ĐẶT HÀNG THÀNH CÔNG: Trừ đi 1 số lượng của mã voucher đó
    public boolean decreaseQuantity(int voucherId) {
        String query = "UPDATE Vouchers SET quantity = quantity - 1 WHERE id = ? AND quantity > 0";
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