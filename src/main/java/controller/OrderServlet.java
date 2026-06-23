package controller;

import dao.CartDAO;
import dao.OrderDAO;
import model.CartItem;
import model.Order;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "OrderServlet", urlPatterns = {"/order", "/checkout"})
public class OrderServlet extends HttpServlet {

    // 1. XEM LỊCH SỬ ĐƠN HÀNG (GET /order)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Giả lập tài khoản đăng nhập để tránh lỗi 404/Null khi chưa có module Login
        if (session.getAttribute("account") == null) {
            // Tạo một object giả lập (Dùng tạm class hoặc xử lý thuộc tính trực tiếp)
            // Khi bạn Phát làm xong phân hệ Login, bạn chỉ cần xóa đoạn giả lập này đi
            session.setAttribute("mockUser", "Khách Hàng Mẫu");
        }

        int userId = 1; // Giả lập userId = 1 giống CartServlet để test

        OrderDAO orderDAO = new OrderDAO();
        List<Order> orderList = orderDAO.getOrdersByUserId(userId);

        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/WEB-INF/orderHistory.jsp").forward(request, response);
    }

    // 2. XỬ LÝ ĐIỀU HƯỚNG SANG TRANG CHECKOUT HOẶC THỰC THI TRANSACTON (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath(); // Lấy đường dẫn url đang gọi (/checkout hoặc /order)
        int userId = 1; // Giả lập userId mẫu để đồng bộ với Database dữ liệu có sẵn

        CartDAO cartDAO = new CartDAO();
        OrderDAO orderDAO = new OrderDAO();

        // LUỒNG A: Người dùng bấm "Thanh toán" từ Giỏ hàng -> Chuyển sang Trang chủ thanh toán
        if (path.equals("/checkout")) {
            List<CartItem> cartItems = cartDAO.getCartItemsByUserId(userId);

            if (cartItems == null || cartItems.isEmpty()) {
                request.setAttribute("errorMsg", "Giỏ hàng của bạn đang trống, không thể thanh toán!");
                request.getRequestDispatcher("/WEB-INF/cart.jsp").forward(request, response);
                return;
            }

            // Tính tổng tiền của giỏ hàng để hiển thị bên trang thanh toán
            double totalPrice = 0;
            for (CartItem item : cartItems) {
                totalPrice += item.getProduct().getPrice() * item.getQuantity();
            }

            // Đẩy dữ liệu sang trang checkout.jsp
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("totalPrice", totalPrice);
            request.getRequestDispatcher("/WEB-INF/checkout.jsp").forward(request, response);

        } // LUỒNG B: Người dùng điền thông tin xong và bấm "Xác nhận đặt hàng" từ trang checkout.jsp
        else if (path.equals("/order")) {
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");

            try {
                List<CartItem> cartItems = cartDAO.getCartItemsByUserId(userId);

                // Thực thi JDBC Transaction (Kiểm tra kho -> Thêm Đơn -> Trừ kho -> Xóa giỏ)
                boolean success = orderDAO.placeOrder(userId, address, phone, cartItems);

                if (success) {
                    // Đặt hàng thành công -> Chuyển hướng về trang lịch sử đơn hàng để xem kết quả
                    response.sendRedirect(request.getContextPath() + "/order");
                }
            } catch (Exception e) {
                // Nếu dính lỗi bất kỳ (Ví dụ: Sản phẩm vượt quá số lượng tồn kho) -> Trả về trang checkout để báo lỗi
                request.setAttribute("errorMsg", e.getMessage());

                // Tải lại dữ liệu để hiển thị lại giao diện checkout không bị trống
                List<CartItem> cartItems = cartDAO.getCartItemsByUserId(userId);
                double totalPrice = 0;
                for (CartItem item : cartItems) {
                    totalPrice += item.getProduct().getPrice() * item.getQuantity();
                }
                request.setAttribute("cartItems", cartItems);
                request.setAttribute("totalPrice", totalPrice);

                request.getRequestDispatcher("/WEB-INF/checkout.jsp").forward(request, response);
            }
        }
    }
}
