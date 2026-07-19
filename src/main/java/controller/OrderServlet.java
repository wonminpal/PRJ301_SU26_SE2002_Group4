package controller;

import dao.CartDAO;
import dao.OrderDAO;
import dao.VoucherDAO;
import model.*;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "OrderServlet", urlPatterns = {"/order", "/checkout"})
public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=loginForm");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        request.setAttribute("orderList", orderDAO.getOrdersByUserId(user.getId()));
        request.getRequestDispatcher("/WEB-INF/views/client/order/orderHistory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        String guestToken = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("guest_token")) {
                    guestToken = c.getValue();
                }
            }
        }

        CartDAO cartDAO = new CartDAO();
        OrderDAO orderDAO = new OrderDAO();
        VoucherDAO voucherDAO = new VoucherDAO();
        String path = request.getServletPath();

        // ===============================================
        // LUỒNG 1: HIỂN THỊ TRANG XÁC NHẬN THANH TOÁN
        // ===============================================
        if (path.equals("/checkout")) {
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth?action=loginForm");
                return;
            }

            List<CartItem> cartItems = cartDAO.getCartItems(user.getId(), guestToken);
            if (cartItems == null || cartItems.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            double totalPrice = cartItems.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
            double discountAmount = 0; // Biến tính số tiền mặt được giảm

            String voucherCodeRaw = request.getParameter("voucherCode");
            if (voucherCodeRaw != null && !voucherCodeRaw.trim().isEmpty()) {
                String[] codes = voucherCodeRaw.split(",");
                int totalDiscountPercent = 0;
                for (String code : codes) {
                    Voucher v = voucherDAO.getVoucherByCode(code.trim().toUpperCase());
                    if (v != null) {
                        totalDiscountPercent += v.getDiscountPercent();
                    }
                }
                if (totalDiscountPercent > 100) {
                    totalDiscountPercent = 100;
                }

                // Tính toán số tiền giảm và cập nhật lại tổng thanh toán
                discountAmount = totalPrice * totalDiscountPercent / 100;
                totalPrice = totalPrice - discountAmount;

                session.setAttribute("checkoutVouchers", voucherCodeRaw);
            } else {
                session.removeAttribute("checkoutVouchers");
            }

            request.setAttribute("cartItems", cartItems);
            request.setAttribute("totalPrice", totalPrice);
            request.setAttribute("discountAmount", discountAmount); // Đẩy số tiền giảm sang JSP hiển thị
            request.getRequestDispatcher("/WEB-INF/views/client/order/checkout.jsp").forward(request, response);
        } // ===============================================
        // LUỒNG 2: XỬ LÝ LƯU ĐƠN HÀNG XUỐNG DATABASE
        // ===============================================
        else if (path.equals("/order")) {
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth?action=loginForm");
                return;
            }

            int userId = user.getId();
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");

            try {
                List<CartItem> cartItems = cartDAO.getCartItems(userId, guestToken);

                if (cartItems == null || cartItems.isEmpty()) {
                    request.setAttribute("errorMsg", "Giỏ hàng rỗng, không thể tiến hành thanh toán!");
                    request.getRequestDispatcher("/WEB-INF/views/client/order/checkout.jsp").forward(request, response);
                    return;
                }

                // Thực thi Transaction lưu đơn
                boolean success = orderDAO.placeOrder(userId, address, phone, cartItems);

                if (success) {
                    // Cập nhật tăng used_count của voucher khi mua thành công
                    String appliedVouchers = (String) session.getAttribute("checkoutVouchers");
                    if (appliedVouchers != null && !appliedVouchers.trim().isEmpty()) {
                        String[] codes = appliedVouchers.split(",");
                        for (String code : codes) {
                            Voucher v = voucherDAO.getVoucherByCode(code.trim().toUpperCase());
                            if (v != null) {
                                voucherDAO.increaseUsedCount(v.getId());
                            }
                        }
                    }

                    session.removeAttribute("checkoutVouchers");
                    session.setAttribute("voucherCount", voucherDAO.getAvailableVouchersCount());

                    response.sendRedirect(request.getContextPath() + "/order");
                }
            } catch (Exception e) {
                request.setAttribute("errorMsg", e.getMessage());

                List<CartItem> cartItems = cartDAO.getCartItems(userId, guestToken);
                double totalPrice = cartItems.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
                double discountAmount = 0;

                String appliedVouchers = (String) session.getAttribute("checkoutVouchers");
                if (appliedVouchers != null && !appliedVouchers.trim().isEmpty()) {
                    String[] codes = appliedVouchers.split(",");
                    int totalDiscountPercent = 0;
                    for (String code : codes) {
                        Voucher v = voucherDAO.getVoucherByCode(code.trim().toUpperCase());
                        if (v != null) {
                            totalDiscountPercent += v.getDiscountPercent();
                        }
                    }
                    if (totalDiscountPercent > 100) {
                        totalDiscountPercent = 100;
                    }
                    discountAmount = totalPrice * totalDiscountPercent / 100;
                    totalPrice = totalPrice - discountAmount;
                }

                request.setAttribute("cartItems", cartItems);
                request.setAttribute("totalPrice", totalPrice);
                request.setAttribute("discountAmount", discountAmount);

                request.getRequestDispatcher("/WEB-INF/views/client/order/checkout.jsp").forward(request, response);
            }
        }
    }
}
