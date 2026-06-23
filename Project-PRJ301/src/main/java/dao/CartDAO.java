/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;
import model.Product;

/**
 *
 * @author ADMIN
 */
public class CartDAO extends DBContext {
    public int getOrCreateCartId(int userId) {
    int cartId = -1;
    try {
        // 1. Kiểm tra xem user này đã có giỏ hàng chưa
        String checkSql = "SELECT id FROM Carts WHERE user_id = ?";
        PreparedStatement ps = getConnection().prepareStatement(checkSql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            cartId = rs.getInt("id");
        } else {
            // 2. Nếu chưa có, tạo giỏ hàng mới cho user này
            String insertSql = "INSERT INTO Carts (user_id) VALUES (?)";
            PreparedStatement psInsert = getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            psInsert.setInt(1, userId);
            psInsert.executeUpdate();
            
            ResultSet rsGen = psInsert.getGeneratedKeys();
            if (rsGen.next()) {
                cartId = rsGen.getInt(1);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return cartId;
}
    public void addToCart(int userId, int productId, int quantity) {
    int cartId = getOrCreateCartId(userId);
    
    try {
        // 1. Kiểm tra sản phẩm đã có trong giỏ của user này chưa
        String checkItemSql = "SELECT id, quantity FROM Cart_Items WHERE cart_id = ? AND product_id = ?";
        PreparedStatement psCheck = getConnection().prepareStatement(checkItemSql);
        psCheck.setInt(1, cartId);
        psCheck.setInt(2, productId);
        ResultSet rs = psCheck.executeQuery();
        
        if (rs.next()) {
            // 2A. Nếu có rồi -> Cập nhật tăng số lượng
            int currentQty = rs.getInt("quantity");
            String updateSql = "UPDATE Cart_Items SET quantity = ? WHERE cart_id = ? AND product_id = ?";
            PreparedStatement psUpdate = getConnection().prepareStatement(updateSql);
            psUpdate.setInt(1, currentQty + quantity);
            psUpdate.setInt(2, cartId);
            psUpdate.setInt(3, productId);
            psUpdate.executeUpdate();
        } else {
            // 2B. Nếu chưa có -> Thêm dòng mới
            String insertSql = "INSERT INTO Cart_Items (cart_id, product_id, quantity) VALUES (?, ?, ?)";
            PreparedStatement psInsert = getConnection().prepareStatement(insertSql);
            psInsert.setInt(1, cartId);
            psInsert.setInt(2, productId);
            psInsert.setInt(3, quantity);
            psInsert.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    public List<CartItem> getCartItemsByUserId(int userId) {
    List<CartItem> list = new ArrayList<>();
    // JOIN 3 bảng: Carts, Cart_Items, Products
    String sql = "SELECT ci.id, ci.quantity, p.id AS p_id, p.name, p.price, p.image_url " +
                 "FROM Cart_Items ci " +
                 "JOIN Carts c ON ci.cart_id = c.id " +
                 "JOIN Products p ON ci.product_id = p.id " +
                 "WHERE c.user_id = ?";
                 
    try {
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            CartItem item = new CartItem();
            item.setId(rs.getInt("id"));
            item.setQuantity(rs.getInt("quantity"));
            
            // Đổ dữ liệu vào object Product lồng bên trong
            Product p = new Product();
            p.setId(rs.getInt("p_id"));
            p.setName(rs.getString("name"));
            p.setPrice(rs.getDouble("price"));
            p.setImageUrl(rs.getString("image_url"));
            
            item.setProduct(p);
            list.add(item);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
    // Hàm Cập nhật số lượng sản phẩm trong giỏ
    public void updateQuantity(int userId, int productId, int newQuantity) {
        int cartId = getOrCreateCartId(userId);
        String sql = "UPDATE Cart_Items SET quantity = ? WHERE cart_id = ? AND product_id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm Xóa sản phẩm khỏi giỏ hàng
    public void removeItem(int userId, int productId) {
        int cartId = getOrCreateCartId(userId);
        String sql = "DELETE FROM Cart_Items WHERE cart_id = ? AND product_id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
