/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Category;

/**
 *
 * @author LENOVO
 */
public class CategoryDAO extends DBContext {

    public List<Category> getCategoryTree() {
        List<Category> rootCategories = new ArrayList<>();

        Map<Integer, Category> map = new LinkedHashMap();

        String sql = "SELECT * FROM Categories WHERE status = 1 ORDER BY parent_id ASC, id ASC";

        try {
            PreparedStatement statement = this.getConnection().prepareCall(sql);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setSlug(rs.getString("slug"));
                category.setStatus(rs.getBoolean("status"));

                int parentId = rs.getInt("parent_id");
                if (rs.wasNull()) {
                    parentId = 0;
                }
                category.setParentId(parentId);

                map.put(category.getId(), category);
            }

            for (Category currentCategory : map.values()) {
                if (currentCategory.getParentId() == 0) {
                    rootCategories.add(currentCategory);
                } else {
                    Category parentCategory = map.get(currentCategory.getParentId());
                    if (parentCategory != null) {
                        parentCategory.getChildren().add(currentCategory);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rootCategories;
    }

    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM Categories WHERE id = ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql);) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = new Category();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setSlug(rs.getString("slug"));
                    return c;
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(CategoryDAO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean addCategory(String name, String slug, int parentId) {
        String sql = "INSERT INTO Categories (name, slug, parent_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, slug);

            if (parentId > 0) {
                ps.setInt(3, parentId);
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories ORDER BY id ASC";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setSlug(rs.getString("slug"));
                c.setStatus(rs.getBoolean("status"));
                list.add(c);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public boolean checkCategoryExist(String name) {
        String sql = "SELECT COUNT(*) FROM Categories WHERE name = ?";
        // FIX: Đóng gói Connection và PreparedStatement an toàn
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {

            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateCategory(int id, String name, String slug) {
        String sql = "UPDATE Categories SET name = ?, slug = ? WHERE id = ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, slug);
            ps.setInt(3, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(CategoryDAO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean checkCategoryExistForUpdate(int id, String name, String slug) {
        // Thêm điều kiện kiểm tra (name = ? OR slug = ?)
        String sql = "SELECT COUNT(*) FROM Categories WHERE (name = ? OR slug = ?) AND id <> ?";
        try (java.sql.Connection conn = this.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setString(2, slug.trim());
            ps.setInt(3, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(CategoryDAO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean softDeleteCategory(int id) {
        String sql = "UPDATE Categories SET status = 0 WHERE id = ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean restoreCategory(int id) {
        String sql = "UPDATE Categories SET status = 1 WHERE id = ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean hasDependencies(int categoryId) {
        String sql = "SELECT "
                + "  (SELECT COUNT(*) FROM Products WHERE category_id = ? AND status = 1) "
                + "  + "
                + "  (SELECT COUNT(*) FROM Categories WHERE parent_id = ? AND status = 1) AS TotalDependencies";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalDependencies") > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public List<Category> getParentCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE parent_id IS NULL AND status = 1 ORDER BY id ASC";
        try (java.sql.Connection conn = this.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setSlug(rs.getString("slug"));
                list.add(c);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
