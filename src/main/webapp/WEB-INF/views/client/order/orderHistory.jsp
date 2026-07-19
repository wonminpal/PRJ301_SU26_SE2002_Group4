<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container mt-4 mb-5" style="min-height: 65vh;">
    <h2 class="mb-4 fw-bold text-center text-primary">
        <i class="fa-solid fa-history me-2"></i>LỊCH SỬ ĐƠN HÀNG CỦA BẠN
    </h2>

    <c:choose>
        <%-- TRƯỜNG HỢP: KHÔNG CÓ ĐƠN HÀNG NÀO --%>
        <c:when test="${empty orderList}">
            <div class="alert alert-info text-center shadow-sm py-4">
                <p class="mb-3 fs-5 text-secondary">Bạn chưa có đơn hàng nào trong hệ thống.</p>
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary fw-bold">MUA SẮM NGAY</a>
            </div>
        </c:when>

        <%-- TRƯỜNG HỢP: CÓ DANH SÁCH ĐƠN HÀNG --%>
        <c:otherwise>
            <div class="table-responsive bg-white rounded shadow-sm p-3">
                <table class="table table-bordered table-hover text-center align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th style="width: 10%;">Mã Đơn</th>
                            <th style="width: 20%;">Ngày đặt</th>
                            <th class="text-center" style="width: 30%;">Địa chỉ giao hàng</th>
                            <th style="width: 15%;">Số điện thoại</th>
                            <th style="width: 13%;">Tổng tiền</th>
                            <th style="width: 12%;">Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${orderList}" var="order">
                            <tr>
                                <!-- 1. Mã Đơn -->
                                <td class="fw-bold text-secondary">#ORD-${order.id}</td>

                                <!-- 2. Ngày đặt -->
                                <td>
                                    <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                </td>

                                <!-- 3. Địa chỉ giao hàng (Đã căn giữa hoàn toàn) -->
                                <td class="text-center">${order.shippingAddress}</td>

                                <!-- 4. Số điện thoại -->
                                <td>${order.shippingPhone}</td>

                                <!-- 5. Tổng tiền (Đã sửa hiển thị giá trị sau khi giảm giá của Voucher) -->
                                <td class="text-danger fw-bold">
                                    <fmt:formatNumber value="${order.finalAmount}" type="number" pattern="#,###" />đ
                                </td>

                                <!-- 6. Trạng thái đơn hàng kèm Badge màu sắc sinh động -->
                                <td>
                                    <c:choose>
                                        <c:when test="${order.status eq 'Chờ xác nhận'}">
                                            <span class="badge bg-warning text-dark px-3 py-2">Chờ xác nhận</span>
                                        </c:when>
                                        <c:when test="${order.status eq 'Đang giao'}">
                                            <span class="badge bg-info text-white px-3 py-2">Đang giao</span>
                                        </c:when>
                                        <c:when test="${order.status eq 'Đã giao'}">
                                            <span class="badge bg-success text-white px-3 py-2">Đã giao</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary text-white px-3 py-2">${order.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />