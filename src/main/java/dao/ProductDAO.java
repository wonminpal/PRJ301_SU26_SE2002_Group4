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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Category;
import model.Product;
import model.ProductVariant;

/**
 *
 * @author ADMIN
 */
public class ProductDAO extends DBContext {

    // Hàm lấy danh sách sản phẩm mới nhất để hiển thị ở Trang chủ
    public List<Product> getLatestProducts(int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT TOP (?) p.id, p.name, p.slug, p.brand, p.category_id, "
                + "(SELECT MIN(price) FROM Product_Variants WHERE product_id = p.id AND stock_quantity > 0) AS display_price, "
                + "(SELECT TOP 1 image_url FROM Product_Images WHERE product_id = p.id AND is_thumbnail = 1) AS display_image "
                + "FROM Products p "
                + "WHERE p.status = 1 "
                + "ORDER BY p.created_at DESC";

        try {
            PreparedStatement statement = this.getConnection().prepareCall(sql);

            statement.setInt(1, limit);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setBrand(rs.getString("brand"));
                p.setSlug(rs.getString("slug"));
                double price = rs.getDouble("display_price");
                p.setDisplayPrice(rs.wasNull() ? 0 : price);

                p.setDisplayImageUrl(rs.getString("display_image"));

                list.add(p);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Lỗi tại getLatestProducts: " + ex.getMessage());
        }

        return list;
    }

    public Product getProductBySlug(String slug) {

        Product p = null;

        String sqlProduct = "select p.*, c.name AS category_name\n"
                + "from Products p \n"
                + "JOIN Categories c ON c.id = p.category_id\n"
                + "WHERE p.slug = ? AND p.status = 1";

        String sqlVariant = "SELECT * FROM Product_Variants WHERE product_id = ?";

        String sqlImages = "SELECT image_url FROM Product_Images WHERE product_id = ? ORDER BY is_thumbnail DESC";

        try {
            PreparedStatement statementProduct = this.getConnection().prepareCall(sqlProduct);

            statementProduct.setString(1, slug);

            ResultSet rsProduct = statementProduct.executeQuery();

            while (rsProduct.next()) {
                p.setId(rsProduct.getInt("id"));
                p.setCategoryId(rsProduct.getInt("category_id"));
                p.setName(rsProduct.getString("name"));
                p.setDescription(rsProduct.getString("description"));
                p.setBrand(rsProduct.getString("brand"));
                p.setSlug(rsProduct.getString("slug"));

                Category cat = new Category();
                cat.setId(rsProduct.getInt("category_id"));
                cat.setName(rsProduct.getString("category_name"));
                p.setCategory(cat);
            }

            if (p != null) {
                PreparedStatement statementVariant = this.getConnection().prepareCall(sqlVariant);

                statementVariant.setInt(1, p.getId());

                ResultSet rsVariant = statementVariant.executeQuery();

                List<ProductVariant> variants = new ArrayList<>();

                while (rsVariant.next()) {
                    ProductVariant pv = new ProductVariant();
                    pv.setId(rsVariant.getInt("id"));
                    pv.setProductId(rsVariant.getInt("product_id"));
                    pv.setSku(rsVariant.getString("sku"));
                    pv.setColor(rsVariant.getString("color"));
                    pv.setStorageCapacity(rsVariant.getString("storage_capacity"));
                    pv.setPrice(rsVariant.getDouble("price"));
                    pv.setStockQuantity(rsVariant.getInt("stock_quantity"));
                    pv.setVariantImage(rsVariant.getString("variant_image"));

                    variants.add(pv);
                }

                p.setVariants(variants);

                PreparedStatement statementImage = this.getConnection().prepareCall(sqlImages);

                statementImage.setInt(1, p.getId());

                ResultSet rsImage = statementImage.executeQuery();

                List<String> imageGallery = new ArrayList<>();

                while (rsImage.next()) {
                    imageGallery.add(rsImage.getString("image_url"));
                }

                p.setImages(imageGallery);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Lỗi tại getProductBySlug: " + ex.getMessage());

        }

        return p;
    }

    public List<Product> getProductByCategorySlug(String categorySlug, int page, int pageSize) {

        List<Product> list = new ArrayList<>();

        if (page < 1) {
            page = 1;
        }

        String sql = "SELECT p.id, p.[name], p.slug, p.brand,\n"
                + "       (SELECT MIN(pr.price) FROM Product_Variants pr WHERE pr.product_id = p.id AND pr.stock_quantity > 0) as diisplay_price,\n"
                + "       (SELECT TOP 1 proImg.image_url FROM Product_Images proImg WHERE proImg.product_id = p.id AND proImg.is_thumbnail = 1) as dislpay_image\n"
                + "FROM Products p\n"
                + "JOIN Categories c ON p.category_id = c.id\n"
                + "WHERE p.[status] = 1 AND c.slug = ?\n"
                + "ORDER BY p.created_at DESC\n"
                + "OFFSET ? ROWS\n"
                + "FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement statement = this.getConnection().prepareCall(sql);

            statement.setString(1, categorySlug);
            statement.setInt(2, (page - 1) * pageSize);
            statement.setInt(3, pageSize);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setSlug(rs.getString("slug"));
                product.setBrand(rs.getString("brand"));

                double price = rs.getDouble("display_price");
                if (rs.wasNull()) {
                    product.setDisplayPrice(0);
                } else {
                    product.setDisplayPrice(price);
                }

                product.setDisplayImageUrl(rs.getString("display_image"));

                list.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Lỗi tại getProductByCategorySlug: " + ex.getMessage());
        }

        return list;
    }

    public int getTotalProductsByCategorySlug(String categorySlug) {
        String sql = "SELECT COUNT(p.id) FROM Products p "
                + "JOIN Categories c ON p.category_id = c.id "
                + "WHERE p.status = 1 AND c.slug = ?";
        try {
            PreparedStatement statement = this.getConnection().prepareCall(sql);

            statement.setString(1, categorySlug);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm sản phẩm: " + e.getMessage());
        }
        return 0;
    }

    public List<Product> getRelatedProduct(int categoryId, int currentProductId, int limit) {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT TOP (?) p.id, p.[name], p.brand, p.slug, p.category_id,\n"
                + "        (SELECT MIN(price) from Product_Variants pr WHERE pr.product_id = p.id AND pr.stock_quantity > 0),\n"
                + "        (SELECT TOP 1 proImg.image_url FROM Product_Images proImg WHERE proImg.product_id = p.id AND proImg.is_thumbnail = 1)\n"
                + "FROM Products p\n"
                + "JOIN Categories c ON p.category_id = c.id\n"
                + "WHERE p.id = ? AND p.category_id = ? AND p.[status] = 1\n"
                + "ORDER BY NEWID()";

        try {
            PreparedStatement statement = this.getConnection().prepareCall(sql);

            statement.setInt(1, limit);
            statement.setInt(2, currentProductId);
            statement.setInt(3, categoryId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setName(rs.getString("name"));
                product.setBrand(rs.getString("brand"));
                product.setSlug(rs.getString("slug"));

                double price = rs.getDouble("display_price");
                if (rs.wasNull()) {
                    product.setDisplayPrice(0);
                } else {
                    product.setDisplayPrice(price);
                }

                product.setDisplayImageUrl(rs.getString("display_image"));
                list.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Lỗi tại getRelatedProducts: " + ex.getMessage());
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



    // Hàm lấy danh sách sản phẩm theo từ khóa (phân trang)
    public List<Product> searchProducts(String keyword, int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        // Lưu ý: Dùng alias display_price và display_image để khớp với JSP
        String sql = "SELECT p.id, p.name, " +
                     "(SELECT MIN(price) FROM Product_Variants WHERE product_id = p.id) as display_price, " +
                     "(SELECT TOP 1 image_url FROM Product_Images WHERE product_id = p.id AND is_thumbnail = 1) as display_image " +
                     "FROM Products p WHERE p.name LIKE ? AND p.status = 1 " +
                     "ORDER BY p.id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setDisplayPrice(rs.getDouble("display_price"));
                p.setDisplayImageUrl(rs.getString("display_image"));
                list.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Hàm lấy chi tiết sản phẩm theo ID
    public Product getProductById(int id) {
        String sql = "SELECT p.*, " +
                     "(SELECT MIN(price) FROM Product_Variants WHERE product_id = p.id) as display_price, " +
                     "(SELECT TOP 1 image_url FROM Product_Images WHERE product_id = p.id AND is_thumbnail = 1) as display_image " +
                     "FROM Products p WHERE p.id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setDisplayPrice(rs.getDouble("display_price"));
                p.setDisplayImageUrl(rs.getString("display_image"));
                return p;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    // Giữ nguyên các hàm khác của bạn Nhân (getLatestProducts, countSearchProducts...)
}
