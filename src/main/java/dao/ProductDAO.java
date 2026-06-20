/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import model.Product;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */

public class ProductDAO extends DBContext {

    // Hàm lấy danh sách sản phẩm mới nhất để hiển thị ở Trang chủ
    public List<Product> getLatestProducts(int limit) {
        List<Product> list = new ArrayList<>();
        // Truy vấn lấy N sản phẩm mới nhất (sắp xếp theo ID hoặc created_at giảm dần)
        String sql = "SELECT TOP (?) * FROM Products ORDER BY id DESC";
        
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, limit); // Truyền số lượng muốn lấy vào dấu ?
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setImageUrl(rs.getString("image_url"));
                p.setStockQuantity(rs.getInt("stock_quantity"));
                p.setCreatedAt(rs.getDate("created_at"));
                
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tại getLatestProducts: " + e.getMessage());
        }
        return list;
    }
    // Đếm tổng số sản phẩm tìm được
    public int countSearchProducts(String keyword) {
        String sql = "SELECT COUNT(*) FROM Products WHERE name LIKE ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tìm kiếm có phân trang
    public List<Product> searchProducts(String keyword, int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        // Lệnh OFFSET FETCH dùng để bỏ qua các sản phẩm của trang trước, và lấy sản phẩm trang hiện tại
        String sql = "SELECT * FROM Products WHERE name LIKE ? ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, (page - 1) * pageSize); // Bỏ qua bao nhiêu dòng
            ps.setInt(3, pageSize);              // Lấy bao nhiêu dòng
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setImageUrl(rs.getString("image_url"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
