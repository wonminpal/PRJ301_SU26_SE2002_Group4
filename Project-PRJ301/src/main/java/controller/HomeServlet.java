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
        
        // Nếu không có keyword (vừa vào web), gán bằng chuỗi rỗng để lệnh LIKE '%%' lấy tất cả
        if (keyword == null) {
            keyword = "";
        }
        
        int page = 1;
        // THẦY CHỈNH LẠI BẰNG 4 ĐỂ EM DỄ TEST PHÂN TRANG NHÉ:
        int pageSize = 8; 
        
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        // Luôn luôn đếm và tính số trang (dù có search hay không)
        int totalProducts = dao.countSearchProducts(keyword);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        
        // Lấy danh sách sản phẩm theo trang
        List<Product> list = dao.searchProducts(keyword, page, pageSize);
        
        request.setAttribute("productList", list);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("keyword", keyword); // Để in lại chữ đã gõ vào ô tìm kiếm
        
        request.getRequestDispatcher("/WEB-INF/home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
