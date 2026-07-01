package dao;

import db.DBContext;
import model.CartItem;
import model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBContext {

    // 1. HÀM THANH TOÁN DÙNG TRANSACTION (QUAN TRỌNG)
    public boolean placeOrder(int userId, String address, String phone, List<CartItem> cartItems) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            double totalAmount = 0;

            // Bước A: Kiểm tra stock_quantity của từng sản phẩm
            String checkStockSql = "SELECT stock_quantity, name FROM Products WHERE id = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkStockSql);

            for (CartItem item : cartItems) {
                psCheck.setInt(1, item.getProduct().getId());
                ResultSet rs = psCheck.executeQuery();
                if (rs.next()) {
                    int currentStock = rs.getInt("stock_quantity");
                    if (currentStock < item.getQuantity()) {
                        // Nếu kho nhỏ hơn số lượng mua -> Ném Exception để Rollback
                        throw new Exception("Sản phẩm '" + rs.getString("name") + "' không đủ số lượng trong kho (Hiện còn: " + currentStock + ")");
                    }
                }
                totalAmount += item.getProduct().getPrice() * item.getQuantity();
            }

            // Bước B: Insert vào bảng Orders
            String insertOrderSql = "INSERT INTO Orders (user_id, total_amount, final_amount, shipping_address, shipping_phone) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, userId);
            psOrder.setDouble(2, totalAmount);
            psOrder.setDouble(3, totalAmount); // Tạm thời chưa tính voucher
            psOrder.setString(4, address);
            psOrder.setString(5, phone);
            psOrder.executeUpdate();

            // Lấy Order ID vừa tạo tự động
            int orderId = -1;
            ResultSet rsKeys = psOrder.getGeneratedKeys();
            if (rsKeys.next()) {
                orderId = rsKeys.getInt(1);
            }
            if (orderId == -1) {
                throw new Exception("Lỗi hệ thống: Không thể tạo đơn hàng.");
            }

            // Bước C: Insert bảng Order_Details và Cập nhật trừ số lượng trong kho
            String insertDetailSql = "INSERT INTO Order_Details (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(insertDetailSql);

            String updateStockSql = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE id = ?";
            PreparedStatement psUpdateStock = conn.prepareStatement(updateStockSql);

            for (CartItem item : cartItems) {
                // Thêm chi tiết đơn
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getProduct().getId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.setDouble(4, item.getProduct().getPrice());
                psDetail.executeUpdate();

                // Trừ kho tương ứng
                psUpdateStock.setInt(1, item.getQuantity());
                psUpdateStock.setInt(2, item.getProduct().getId());
                psUpdateStock.executeUpdate();
            }

            // Bước D: Xóa dữ liệu trong giỏ hàng (Cart_Items) của user này
            String clearCartSql = "DELETE FROM Cart_Items WHERE cart_id = (SELECT id FROM Carts WHERE user_id = ?)";
            PreparedStatement psClear = conn.prepareStatement(clearCartSql);
            psClear.setInt(1, userId);
            psClear.executeUpdate();

            // Hoàn thành mọi bước thành công -> Commit dữ liệu
            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback(); // Bắt buộc Rollback nếu có bất kỳ bước nào bị Exception lỗi
            }
            throw e; // Ném Exception ra ngoài để Servlet xử lý hiển thị giao diện
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // 2. HÀM TẢI TOÀN BỘ ĐƠN HÀNG THEO USER ID (SẮP XẾP MỚI NHẤT -> CŨ NHẤT)
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
}
