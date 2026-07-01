<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container mt-4" style="min-height: 65vh;">
    <h2 class="mb-4 fw-bold"><i class="fa-solid fa-clock-rotate-left"></i> Lịch sử đơn hàng của bạn</h2>

    <c:choose>
        <c:when test="${empty orderList}">
            <div class="alert alert-info text-center shadow-sm">
                Bạn chưa thực hiện đơn đặt hàng nào. <a href="${pageContext.request.contextPath}/home" class="alert-link">Mua sắm ngay!</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive shadow-sm rounded">
                <table class="table table-bordered table-hover text-center align-middle bg-white mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>Mã Đơn hàng</th>
                            <th>Ngày đặt hàng</th>
                            <th>Địa chỉ giao nhận</th>
                            <th>Số điện thoại</th>
                            <th>Tổng thanh toán</th>
                            <th>Trạng thái đơn</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${orderList}" var="order">
                            <tr>
                                <td class="fw-bold text-secondary">#ORD-${order.id}</td>
                                <td><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                        <td class="text-start">${order.shippingAddress}</td>
                        <td>${order.shippingPhone}</td>
                        <td class="text-danger fw-bold">$${order.finalAmount}</td>
                        <td>
                            <c:choose>
                                <c:when test="${order.status eq 'Chờ xác nhận'}">
                                    <span class="badge bg-warning text-dark">${order.status}</span>
                                </c:when>
                                <c:when test="${order.status eq 'Hoàn thành'}">
                                    <span class="badge bg-success">${order.status}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">${order.status}</span>
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