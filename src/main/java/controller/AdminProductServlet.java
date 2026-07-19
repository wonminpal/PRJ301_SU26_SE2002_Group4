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
                int filterCategoryId = 0;
                String catIdParam = request.getParameter("categoryId");
                if (catIdParam != null && !catIdParam.isEmpty()) {
                    try {
                        filterCategoryId = Integer.parseInt(catIdParam);
                    } catch (NumberFormatException e) {
                        filterCategoryId = 0;
                    }
                }

                List<Product> productList = productDAO.getAdminProducts(filterCategoryId);
                List<Category> categoryList = categoryDAO.getAllCategories();

                request.setAttribute("currentCategoryId", filterCategoryId);
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

                    boolean isRestored = productDAO.restoreProduct(id);

                    if (isRestored) {
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
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                String name = request.getParameter("name");
                String brand = request.getParameter("brand");
                String description = request.getParameter("description");
                double price = Double.parseDouble(request.getParameter("price"));
                String displayImageUrl = request.getParameter("displayImageUrl");

                if (categoryId <= 0) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=invalid_category");
                    return;
                }
                if (price < 0) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=invalid_price");
                    return;
                }

                String slug = name.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");

                ProductDAO productDAO = new ProductDAO();

                if (productDAO.checkProductSlugExist(slug)) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=add&error=duplicate_name");
                    return;
                }

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
                            if (colors[i] != null && !colors[i].trim().isEmpty()) {

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

                if (categoryId <= 0 || price < 0) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=edit&id=" + productId + "&error=invalid_value");
                    return;
                }

                String slug = name.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");

                ProductDAO productDAO = new ProductDAO();

                if (productDAO.checkProductSlugExistForUpdate(productId, slug)) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=edit&id=" + productId + "&error=duplicate_name");
                    return;
                }

                Product p = new Product();
                p.setId(productId);
                p.setCategoryId(categoryId);
                p.setName(name);
                p.setBrand(brand);
                p.setDescription(description);
                p.setDisplayPrice(price);
                p.setDisplayImageUrl(displayImageUrl);
                p.setSlug(slug);

                p.setStockQuantity(0);
                p.setStatus(1);

                // Gọi DAO cập nhật Products
                boolean isUpdated = dao.updateProduct(p);

                if (isUpdated) {
                    String[] colors = request.getParameterValues("colors");
                    String[] capacities = request.getParameterValues("capacities");
                    String[] prices = request.getParameterValues("prices");
                    String[] stocks = request.getParameterValues("stocks");
                    String[] variantImages = request.getParameterValues("variantImages");

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
