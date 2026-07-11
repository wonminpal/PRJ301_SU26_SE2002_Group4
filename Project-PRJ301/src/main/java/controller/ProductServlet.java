/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Product;

/**
 *
 * @author LENOVO
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/product"})
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        ProductDAO dao = new ProductDAO();

        if (action.equals("detail")) {

        }
    }

    private void showProductDetail(HttpServletRequest request, HttpServletResponse response, ProductDAO dao)
            throws ServletException, IOException {
        String slug = request.getParameter("slug");

        if (slug == null || slug.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        Product product = dao.getProductBySlug(slug);

        if (product == null) {
            request.getRequestDispatcher("WEB-INF/views/error/404.jsp").forward(request, response);
            return;
        }

        List<Product> relatedProducts = dao.getRelatedProduct(product.getCategoryId(), product.getId(), 4);

        request.setAttribute("product", product);
        request.setAttribute("relatedProducts", relatedProducts);

        request.getRequestDispatcher("/WEB-INF/views/product/detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
