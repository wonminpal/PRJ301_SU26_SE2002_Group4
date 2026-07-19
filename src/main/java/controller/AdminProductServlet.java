/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Category;
import model.Product;
import model.ProductVariant;

/**
 *
 * @author LENOVO
 */
public class AdminProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String action = request.getParameter("action");

            ProductDAO productDAO = new ProductDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            if (action == null || action.isEmpty()) {
                action = "list";
            }

            if (action.equals("list")) {
                List<Product> productList = productDAO.getLatestProducts(8, true);
                List<Category> categoryList = categoryDAO.getAllCategories();

                request.setAttribute("adminProductList", productList);
                request.setAttribute("adminCategoryList", categoryList);

                request.getRequestDispatcher("/WEB-INF/views/admin/inventory-management.jsp").forward(request, response);
            } else if (action.equals("add")) {
                request.getRequestDispatcher("/WEB-INF/views/admin/product-add.jsp").forward(request, response);
            } else if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));

                Product product = productDAO.getProductById(id);

                List<ProductVariant> variants = productDAO.getVariantsByProductId(id);

                if (product != null) {
                    request.setAttribute("product", product);
                    request.setAttribute("variants", variants);
                    request.getRequestDispatcher("/WEB-INF/views/admin/product-edit.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=not_found");
                }
            } else if (action.equals("delete")) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));

                    boolean isDeleted = productDAO.softDeleteProduct(id);

                    if (isDeleted) {
                        response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&message=soft_delete_success");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=delete_fail");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=exception");
                }
            } else if (action.equals("restore")) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));

                    // Gọi hàm phục hồi status = 1 trong DAO
                    boolean isRestored = productDAO.restoreProduct(id);

                    if (isRestored) {
                        // Khôi phục thành công, quay về trang list kèm thông báo thành công
                        response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&message=restore_success");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=restore_fail");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=exception");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Loi he thong: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String type = request.getParameter("type");
        ProductDAO dao = new ProductDAO();

        if (action.equals("add")) {
            try {
                // --- BƯỚC 2.1: LẤY THÔNG TIN SẢN PHẨM GỐC ---
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                String name = request.getParameter("name");
                String brand = request.getParameter("brand");
                String description = request.getParameter("description");
                double price = Double.parseDouble(request.getParameter("price"));
                String displayImageUrl = request.getParameter("displayImageUrl");

                // --- BƯỚC 2.2: RÀNG BUỘC CHẶN GIÁ TRỊ ÂM PHÍA BACK-END ---
                if (categoryId <= 0) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=invalid_category");
                    return;
                }
                if (price < 0) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=invalid_price");
                    return;
                }

                // Tự động tạo slug gạch ngang từ tên sản phẩm để tối ưu SEO[cite: 4, 5]
                String slug = name.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");

                // Đóng gói dữ liệu Product cha
                Product p = new Product();
                p.setCategoryId(categoryId);
                p.setName(name);
                p.setBrand(brand);
                p.setDescription(description);
                p.setDisplayPrice(price);
                p.setDisplayImageUrl(displayImageUrl);
                p.setSlug(slug);
                p.setStockQuantity(0);
                p.setStatus(1); // 1: Đang bán[cite: 5]

                // Thực thi chèn bảng Products và lấy ID tự tăng[cite: 5]
                int newProductId = dao.insertProduct(p);

                // --- BƯỚC 2.3: CHÈN THÀNH CÔNG CHA -> BẮT ĐẦU DUYỆT MẢNG CHÈN CON ---
                if (newProductId > 0) {
                    System.out.println("=== [ADMIN] Tạo thành công sản phẩm ID: " + newProductId + " ===");

                    // Lấy các mảng biến thể song song gửi lên từ form[cite: 5]
                    String[] colors = request.getParameterValues("colors");
                    String[] capacities = request.getParameterValues("capacities");
                    String[] prices = request.getParameterValues("prices");
                    String[] stocks = request.getParameterValues("stocks");
                    String[] variantImages = request.getParameterValues("variantImages");

                    if (colors != null) {
                        for (int i = 0; i < colors.length; i++) {
                            // Bỏ qua dòng trống nếu Admin lỡ bấm thêm dòng mà không gõ chữ
                            if (colors[i] != null && !colors[i].trim().isEmpty()) {

                                // Ràng buộc kiểm tra số âm cho từng dòng biến thể
                                double vPrice = (prices[i] != null && !prices[i].isEmpty()) ? Double.parseDouble(prices[i]) : price;
                                int vStock = (stocks[i] != null && !stocks[i].isEmpty()) ? Integer.parseInt(stocks[i]) : 0;

                                if (vPrice < 0 || vStock < 0) {
                                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=invalid_variant_value");
                                    return;
                                }

                                ProductVariant pv = new ProductVariant();
                                pv.setProductId(newProductId); // Nối khóa ngoại[cite: 5]
                                pv.setSku("SKU-" + newProductId + "-" + System.currentTimeMillis() + "-" + i); // Mã SKU độc nhất[cite: 5]
                                pv.setColor(colors[i]);
                                pv.setStorageCapacity(capacities[i]);
                                pv.setPrice(vPrice);
                                pv.setStockQuantity(vStock);
                                pv.setVariantImage((variantImages[i] != null && !variantImages[i].isEmpty()) ? variantImages[i] : displayImageUrl);

                                dao.insertVariant(pv);
                            }
                        }
                    }

                    if (displayImageUrl != null && !displayImageUrl.trim().isEmpty()) {
                        dao.insertProductImage(newProductId, displayImageUrl, true); // true: Làm ảnh Thumbnail chính[cite: 5]
                    }

                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list");

                } else {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=insert_fail");
                }

            } catch (NumberFormatException nfe) {
                System.err.println("=== [ADMIN VALIDATION] Lỗi định dạng số ===");
                response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=number_format");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=exception");
            }
        } else if (action.equals("update")) {
            try {
                int productId = Integer.parseInt(request.getParameter("productId"));
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                String name = request.getParameter("name");
                String brand = request.getParameter("brand");
                String description = request.getParameter("description");
                double price = Double.parseDouble(request.getParameter("price"));
                String displayImageUrl = request.getParameter("displayImageUrl");

                // Ràng buộc số âm ở Back-end
                if (categoryId <= 0 || price < 0) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=edit&id=" + productId + "&error=invalid_value");
                    return;
                }

                String slug = name.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");

                // Cập nhật thực thể cha
                Product p = new Product();
                p.setId(productId);
                p.setCategoryId(categoryId);
                p.setName(name);
                p.setBrand(brand);
                p.setDescription(description);
                p.setDisplayPrice(price);
                p.setDisplayImageUrl(displayImageUrl);
                p.setSlug(slug);

                // Gọi DAO cập nhật Products
                boolean isUpdated = dao.updateProduct(p);

                if (isUpdated) {
                    // Xử lý các mảng biến thể chỉnh sửa gửi lên
                    String[] colors = request.getParameterValues("colors");
                    String[] capacities = request.getParameterValues("capacities");
                    String[] prices = request.getParameterValues("prices");
                    String[] stocks = request.getParameterValues("stocks");
                    String[] variantImages = request.getParameterValues("variantImages");

                    // GIẢI PHÁP ĐƠN GIẢN VÀ AN TOÀN NHẤT: Xóa sạch biến thể cũ của sản phẩm này, rồi chèn mảng mới vào
                    dao.deleteAllVariantsByProductId(productId);

                    if (colors != null) {
                        for (int i = 0; i < colors.length; i++) {
                            if (colors[i] != null && !colors[i].trim().isEmpty()) {
                                double vPrice = (prices[i] != null && !prices[i].isEmpty()) ? Double.parseDouble(prices[i]) : price;
                                int vStock = (stocks[i] != null && !stocks[i].isEmpty()) ? Integer.parseInt(stocks[i]) : 0;

                                ProductVariant pv = new ProductVariant();
                                pv.setProductId(productId);
                                pv.setSku("SKU-" + productId + "-" + System.currentTimeMillis() + "-" + i);
                                pv.setColor(colors[i]);
                                pv.setStorageCapacity(capacities[i]);
                                pv.setPrice(vPrice);
                                pv.setStockQuantity(vStock);
                                pv.setVariantImage((variantImages[i] != null && !variantImages[i].isEmpty()) ? variantImages[i] : displayImageUrl);

                                dao.insertVariant(pv);
                            }
                        }
                    }
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&message=update_success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=edit&id=" + productId + "&error=update_fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=exception");
            }
        }
    }

}
