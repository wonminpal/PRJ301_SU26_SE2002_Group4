package dao;

import db.DBContext;
import java.sql.*;
import java.util.*;
import model.*;

/**
 * @author ADMIN
 */
public class CartDAO extends DBContext {

    // --- 1. Hàm tìm hoặc tạo giỏ hàng (Dùng cho cả User và Guest) ---
    public int getOrCreateCartId(Integer userId, String guestToken) {
        try {
            String sql = (userId != null) ? "SELECT id FROM Carts WHERE user_id = ?" : "SELECT id FROM Carts WHERE guest_token = ?";
            PreparedStatement ps = getConnection().prepareStatement(sql);
            if (userId != null) ps.setInt(1, userId); else ps.setString(1, guestToken);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");

            // Nếu chưa có thì tạo mới
            String insertSql = (userId != null) ? "INSERT INTO Carts (user_id) VALUES (?)" : "INSERT INTO Carts (guest_token) VALUES (?)";
            PreparedStatement psInsert = getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            if (userId != null) psInsert.setInt(1, userId); else psInsert.setString(1, guestToken);
            psInsert.executeUpdate();
            
            ResultSet rsGen = psInsert.getGeneratedKeys();
            if (rsGen.next()) return rsGen.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    
    // --- 2. Hàm thêm sản phẩm vào giỏ ---
    public void addToCart(Integer userId, String guestToken, int productId, int quantity, String variant) {
        int cartId = getOrCreateCartId(userId, guestToken);
        try {
            String checkItemSql = "SELECT id, quantity FROM Cart_Items WHERE cart_id = ? AND product_id = ? AND variant = ?";
            PreparedStatement psCheck = getConnection().prepareStatement(checkItemSql);
            psCheck.setInt(1, cartId);
            psCheck.setInt(2, productId);
            psCheck.setString(3, variant);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int currentQty = rs.getInt("quantity");
                String updateSql = "UPDATE Cart_Items SET quantity = ? WHERE cart_id = ? AND product_id = ? AND variant = ?";
                PreparedStatement psUpdate = getConnection().prepareStatement(updateSql);
                psUpdate.setInt(1, currentQty + quantity);
                psUpdate.setInt(2, cartId);
                psUpdate.setInt(3, productId);
                psUpdate.setString(4, variant);
                psUpdate.executeUpdate();
            } else {
                String insertSql = "INSERT INTO Cart_Items (cart_id, product_id, quantity, variant) VALUES (?, ?, ?, ?)";
                PreparedStatement psInsert = getConnection().prepareStatement(insertSql);
                psInsert.setInt(1, cartId);
                psInsert.setInt(2, productId);
                psInsert.setInt(3, quantity);
                psInsert.setString(4, variant);
                psInsert.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- 3. Hàm lấy danh sách sản phẩm trong giỏ ---
    public List<CartItem> getCartItems(Integer userId, String guestToken) {
        int cartId = getOrCreateCartId(userId, guestToken);
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT ci.*, p.name, p.price, p.image_url FROM Cart_Items ci JOIN Products p ON ci.product_id = p.id WHERE ci.cart_id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Tạo đối tượng Product từ dữ liệu DB
                Product p = new Product();
                p.setId(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setDisplayPrice(rs.getDouble("price"));
                p.setDisplayImageUrl(rs.getString("image_url"));
                
                // Tạo CartItem
                CartItem item = new CartItem(p, rs.getInt("quantity"), rs.getString("variant"));
                item.setId(rs.getInt("id"));
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 4. Hàm cập nhật số lượng ---
    public void updateQuantity(Integer userId, String guestToken, int productId, String variant, int newQuantity) {
        int cartId = getOrCreateCartId(userId, guestToken);
        String sql = "UPDATE Cart_Items SET quantity = ? WHERE cart_id = ? AND product_id = ? AND variant = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartId);
            ps.setInt(3, productId);
            ps.setString(4, variant);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- 5. Hàm xóa sản phẩm ---
    public void removeItem(Integer userId, String guestToken, int productId, String variant) {
        int cartId = getOrCreateCartId(userId, guestToken);
        String sql = "DELETE FROM Cart_Items WHERE cart_id = ? AND product_id = ? AND variant = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            ps.setString(3, variant);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}