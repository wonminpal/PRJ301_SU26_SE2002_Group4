package controller;

import dao.ProductDAO;
import dao.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Product;

@WebServlet(name = "DetailServlet", urlPatterns = {"/detail"})
public class DetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy Slug từ URL thay vì ID (Tối ưu SEO cho dự án Web)
            String slug = request.getParameter("slug");

            if (slug == null || slug.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            ProductDAO dao = new ProductDAO();

            Product product = dao.getProductBySlug(slug);

            System.out.println("=== KIỂM TRA TỒN KHO CỦA " + product.getName() + " LÀ: " + product.getStockQuantity() + " ===");

            if (product != null) {
                // Đẩy dữ liệu Product qua detail.jsp để JSTL render
                System.out.println("=== [DEBUG] THÀNH CÔNG: Tìm thấy sản phẩm " + product.getName() + " ===");
                System.out.println("=== TỒN KHO GỐC BẢNG PRODUCT: " + product.getStockQuantity() + " ===");
                ReviewDAO reviewDAO = new ReviewDAO();
                // Giả định tên hàm trong ReviewDAO của bạn là getReviewsByProductId hoặc tương đương
                List<?> listReview = reviewDAO.getReviewsByProductId(product.getId()); 
                
                // 2. Đính kèm danh sách review vào request thuộc tính "reviewList"
                request.setAttribute("reviewList", listReview);
                request.setAttribute("product", product);
                request.getRequestDispatcher("/WEB-INF/views/client/product/detail.jsp").forward(request, response);
            } else {
                System.err.println("👉 [DEBUG] THẤT BẠI: Không tìm thấy sản phẩm nào có slug = '" + slug + "' trong DB!");
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } catch (Exception e) {
            System.err.println("👉 [DEBUG] HỆ THỐNG GẶP LỖI: ");
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Bỏ trống vì form thêm giỏ hàng đã trỏ trực tiếp đến CartServlet
    }

    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();

        System.out.println(dao.getProductBySlug("iphone-15-pro-max"));
    }
}
