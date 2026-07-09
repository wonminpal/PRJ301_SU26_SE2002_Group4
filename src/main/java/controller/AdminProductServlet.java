/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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

            ProductDAO dao = new ProductDAO();

            if (action == null || action.isEmpty()) {
                action = "list";
            }

            if (action.equals("list")) {
                List<Product> list = dao.getLatestProducts(8);

                request.setAttribute("adminProductList", list);

                request.getRequestDispatcher("/WEB-INF/views/admin/product-list.jsp").forward(request, response);
            } else if (action.equals("add")) {
                request.getRequestDispatcher("/WEB-INF/views/admin/product-add.jsp").forward(request, response);
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

        if (action.equals("action") && type.equals("product")) {
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

                ProductDAO dao = new ProductDAO();

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
        }
    }

}
