package controller;

import dao.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.Review;
import model.User;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/review"})
public class ReviewServlet extends HttpServlet {
    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String productIdStr = request.getParameter("productId");
    String slug = request.getParameter("slug");

    if (productIdStr != null && !productIdStr.isEmpty()) {
        int productId = Integer.parseInt(productIdStr);
        ReviewDAO reviewDAO = new ReviewDAO();
        
        // Lấy danh sách review
        List<Review> reviewList = reviewDAO.getReviewsByProductId(productId);
        request.setAttribute("reviewList", reviewList);
        
        // Forward sang trang review.jsp
        request.getRequestDispatcher("/WEB-INF/views/client/product/review.jsp").forward(request, response);
    } else {
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        // Kiểm tra xem user đã đăng nhập chưa
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=loginForm");
            return;
        }

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");
            String productSlug = request.getParameter("productSlug"); // Dùng để redirect về trang chi tiết

            Review review = new Review();
            review.setUserId(user.getId());
            review.setProductId(productId);
            review.setRating(rating);
            review.setComment(comment);

            ReviewDAO reviewDAO = new ReviewDAO();
            reviewDAO.addReview(review);

            // Quay trở lại trang chi tiết sản phẩm sau khi đánh giá thành công
            response.sendRedirect(request.getContextPath() + "/detail?slug=" + productSlug);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}