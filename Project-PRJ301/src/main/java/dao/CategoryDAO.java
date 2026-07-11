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

                    if (currentCategory != null) {
                        parentCategory.getChildren().add(parentCategory);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rootCategories;
    }
}
