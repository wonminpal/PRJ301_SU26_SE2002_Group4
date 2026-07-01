package dao;

import db.DBContext;
import java.sql.*;
import java.util.*;
import model.*;

public class CartDAO extends DBContext {

    public int getOrCreateCartId(Integer userId, String guestToken) {
        try {
            String sql = (userId != null) ? "SELECT id FROM Carts WHERE user_id = ?" : "SELECT id FROM Carts WHERE guest_token = ?";
            PreparedStatement ps = getConnection().prepareStatement(sql);
            if (userId != null) ps.setInt(1, userId); else ps.setString(1, guestToken);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");

            String insertSql = (userId != null) ? "INSERT INTO Carts (user_id) VALUES (?)" : "INSERT INTO Carts (guest_token) VALUES (?)";
            PreparedStatement psInsert = getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            if (userId != null) psInsert.setInt(1, userId); else psInsert.setString(1, guestToken);
            psInsert.executeUpdate();
            
            ResultSet rsGen = psInsert.getGeneratedKeys();
            if (rsGen.next()) return rsGen.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void addToCart(Integer userId, String guestToken, int productId, int quantity, String variant) {
        int cartId = getOrCreateCartId(userId, guestToken);
        if (cartId == -1) return; // Bảo vệ lỗi

        try {
            // Xử lý thông minh: Nếu variant bị NULL thì dùng lệnh IS NULL
            String checkItemSql;
            if (variant == null || variant.isEmpty()) {
                checkItemSql = "SELECT id, quantity FROM Cart_Items WHERE cart_id = ? AND product_id = ? AND variant IS NULL";
            } else {
                checkItemSql = "SELECT id, quantity FROM Cart_Items WHERE cart_id = ? AND product_id = ? AND variant = ?";
            }
            
            PreparedStatement psCheck = getConnection().prepareStatement(checkItemSql);
            psCheck.setInt(1, cartId);
            psCheck.setInt(2, productId);
            if (variant != null && !variant.isEmpty()) psCheck.setString(3, variant);
            
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int currentQty = rs.getInt("quantity");
                String updateSql;
                if (variant == null || variant.isEmpty()) {
                    updateSql = "UPDATE Cart_Items SET quantity = ? WHERE cart_id = ? AND product_id = ? AND variant IS NULL";
                } else {
                    updateSql = "UPDATE Cart_Items SET quantity = ? WHERE cart_id = ? AND product_id = ? AND variant = ?";
                }
                PreparedStatement psUpdate = getConnection().prepareStatement(updateSql);
                psUpdate.setInt(1, currentQty + quantity);
                psUpdate.setInt(2, cartId);
                psUpdate.setInt(3, productId);
                if (variant != null && !variant.isEmpty()) psUpdate.setString(4, variant);
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

    public List<CartItem> getCartItems(Integer userId, String guestToken) {
        int cartId = getOrCreateCartId(userId, guestToken);
        List<CartItem> list = new ArrayList<>();
        if (cartId == -1) return list;

        String sql = "SELECT ci.*, p.name, p.price, p.image_url FROM Cart_Items ci JOIN Products p ON ci.product_id = p.id WHERE ci.cart_id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setDisplayPrice(rs.getDouble("price"));
                p.setDisplayImageUrl(rs.getString("image_url"));
                
                CartItem item = new CartItem(p, rs.getInt("quantity"), rs.getString("variant"));
                item.setId(rs.getInt("id"));
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void updateQuantity(Integer userId, String guestToken, int productId, String variant, int newQuantity) {
        int cartId = getOrCreateCartId(userId, guestToken);
        if (cartId == -1) return;
        
        String sql;
        if (variant == null || variant.isEmpty()) {
            sql = "UPDATE Cart_Items SET quantity = ? WHERE cart_id = ? AND product_id = ? AND variant IS NULL";
        } else {
            sql = "UPDATE Cart_Items SET quantity = ? WHERE cart_id = ? AND product_id = ? AND variant = ?";
        }
        
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartId);
            ps.setInt(3, productId);
            if (variant != null && !variant.isEmpty()) ps.setString(4, variant);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void removeItem(Integer userId, String guestToken, int productId, String variant) {
        int cartId = getOrCreateCartId(userId, guestToken);
        if (cartId == -1) return;
        
        String sql;
        if (variant == null || variant.isEmpty()) {
            sql = "DELETE FROM Cart_Items WHERE cart_id = ? AND product_id = ? AND variant IS NULL";
        } else {
            sql = "DELETE FROM Cart_Items WHERE cart_id = ? AND product_id = ? AND variant = ?";
        }
        
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            if (variant != null && !variant.isEmpty()) ps.setString(3, variant);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}