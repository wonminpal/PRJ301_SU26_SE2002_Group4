/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.ProductDAO;
import model.Product;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author ADMIN
 */
// Đặt đường dẫn là /home và / để người dùng vừa vào web là thấy luôn
@WebServlet(name = "HomeServlet", urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDAO dao = new ProductDAO();
        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category"); // LẤY THÊM CATEGORY TỪ URL

        if (keyword == null) {
            keyword = "";
        }

        int page = 1;
        int pageSize = 8;

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        int totalProducts = 0;
        List<Product> list = null;

        // KIỂM TRA LOGIC: Lọc theo Danh mục hay Tìm kiếm
        if (category != null && !category.isEmpty()) {
            // Lấy sản phẩm theo Danh mục
            totalProducts = dao.countProductsByCategory(category);
            list = dao.getProductsByCategory(category, page, pageSize);
        } else {
            // Lấy sản phẩm theo Keyword (Nếu rỗng thì là lấy tất cả)
            totalProducts = dao.countSearchProducts(keyword);
            list = dao.searchProducts(keyword, page, pageSize);
        }

        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        request.setAttribute("productList", list);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentCategory", category); // Truyền danh mục hiện tại xuống JSP để làm sáng nút

        request.getRequestDispatcher("/WEB-INF/views/client/home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
