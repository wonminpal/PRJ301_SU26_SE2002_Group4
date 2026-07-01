package controller;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Product;

@WebServlet(name = "DetailServlet", urlPatterns = {"/detail"})
public class DetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy Slug từ URL thay vì ID (Tối ưu SEO cho dự án Web)
            String slug = request.getParameter("slug");
            ProductDAO dao = new ProductDAO();

            Product product = dao.getProductBySlug(slug);

            if (product != null) {
                // Đẩy dữ liệu Product qua detail.jsp để JSTL render
                request.setAttribute("product", product);
                request.getRequestDispatcher("/WEB-INF/views/client/product/detail.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Bỏ trống vì form thêm giỏ hàng đã trỏ trực tiếp đến CartServlet
    }
}
