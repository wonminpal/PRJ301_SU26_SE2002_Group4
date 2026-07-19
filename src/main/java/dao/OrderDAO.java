package dao;

import db.DBContext;
import model.CartItem;
import model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBContext {

    // 1. HÀM XỬ LÝ ĐẶT HÀNG TRANSACTION HOÀN CHỈNH (KHỚP 100% CẤU TRÚC SQL MỚI)
    public boolean placeOrder(int userId, String address, String phone, List<CartItem> cartItems, double finalTotalPrice) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        PreparedStatement psFindCart = null;
        PreparedStatement psClearCartItems = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Bắt đầu chuỗi giao dịch an toàn (Transaction)

            // Tính tổng tiền gốc trước khi giảm
            double totalAmountOriginal = cartItems.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();

            // Lệnh chèn dữ liệu vào bảng Orders (Khớp hoàn toàn cột trong SQL của bạn)
            String insertOrderQuery = "INSERT INTO Orders (user_id, total_amount, final_amount, status, shipping_address, shipping_phone, created_at) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
            
            psOrder = conn.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, userId);
            psOrder.setDouble(2, totalAmountOriginal); // total_amount
            psOrder.setDouble(3, finalTotalPrice);      // final_amount
            psOrder.setString(4, "Chờ xác nhận");
            psOrder.setString(5, address);             // shipping_address
            psOrder.setString(6, phone);               // shipping_phone

            psOrder.executeUpdate();

            // Lấy ID tự động tăng của Đơn hàng vừa tạo
            rs = psOrder.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // Lệnh chèn vào bảng Order_Details (Đã sửa tên bảng có dấu gạch dưới)
            String insertDetailQuery = "INSERT INTO Order_Details (order_id, product_id, quantity, price, variant) VALUES (?, ?, ?, ?, ?)";
            psDetail = conn.prepareStatement(insertDetailQuery);

            // Lệnh trừ kho ở bảng Products (Cột số lượng tồn kho của bạn là stock_quantity)
            String updateStockQuery = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
            psUpdateStock = conn.prepareStatement(updateStockQuery);

            for (CartItem item : cartItems) {
                // a. Thêm vào chi tiết đơn hàng
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getProduct().getId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.setDouble(4, item.getProduct().getPrice());
                psDetail.setString(5, item.getVariant());
                psDetail.addBatch();

                // b. Cập nhật trừ số lượng kho
                psUpdateStock.setInt(1, item.getQuantity());
                psUpdateStock.setInt(2, item.getProduct().getId());
                psUpdateStock.setInt(3, item.getQuantity());
                
                int affectedRows = psUpdateStock.executeUpdate();
                if (affectedRows == 0) {
                    throw new Exception("Sản phẩm '" + item.getProduct().getName() + "' đã hết hàng hoặc không đủ số lượng tồn kho!");
                }
            }
            
            psDetail.executeBatch(); 

            // --- TIẾN HÀNH DỌN SẠCH CÁC MÓN TRONG GIỎ HÀNG CỦA USER ---
            // Tìm cart_id dựa vào user_id từ bảng Carts
            String findCartSql = "SELECT id FROM Carts WHERE user_id = ?";
            psFindCart = conn.prepareStatement(findCartSql);
            psFindCart.setInt(1, userId);
            ResultSet rsCart = psFindCart.executeQuery();
            
            if (rsCart.next()) {
                int cartId = rsCart.getInt("id");
                // Xóa các sản phẩm nằm trong Cart_Items thuộc giỏ hàng này
                String clearCartItemsSql = "DELETE FROM Cart_Items WHERE cart_id = ?";
                psClearCartItems = conn.prepareStatement(clearCartItemsSql);
                psClearCartItems.setInt(1, cartId);
                psClearCartItems.executeUpdate();
            }
            if (rsCart != null) rsCart.close();

            conn.commit(); // Hoàn tất thành công toàn bộ giao dịch đặt hàng!
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            // Đóng toàn bộ kết nối tránh tràn dữ liệu hệ thống
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (psOrder != null) psOrder.close(); } catch (Exception e) {}
            try { if (psDetail != null) psDetail.close(); } catch (Exception e) {}
            try { if (psUpdateStock != null) psUpdateStock.close(); } catch (Exception e) {}
            try { if (psFindCart != null) psFindCart.close(); } catch (Exception e) {}
            try { if (psClearCartItems != null) psClearCartItems.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return false;
    }

    // 2. HÀM TẢI TOÀN BỘ ĐƠN HÀNG THEO USER ID
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE user_id = ? ORDER BY created_at DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setUserId(rs.getInt("user_id"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setFinalAmount(rs.getDouble("final_amount"));
                o.setStatus(rs.getString("status"));
                o.setShippingAddress(rs.getString("shipping_address"));
                o.setShippingPhone(rs.getString("shipping_phone"));
                o.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(o);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG
    public boolean updateStatus(int orderId, String status) {
       String query = "UPDATE Orders SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setNString(1, status); // Dùng setNString thay vì setString để lưu chuẩn Unicode N'...'
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}