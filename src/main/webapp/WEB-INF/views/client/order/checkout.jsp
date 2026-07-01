<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container mt-4 mb-5" style="min-height: 65vh;">
    <h2 class="mb-4 fw-bold text-center text-primary">XÁC NHẬN THANH TOÁN</h2>

    <%-- Hiển thị lỗi nếu quá trình Transaction thất bại (VD: Hết hàng) --%>
    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger shadow-sm">
            <strong>Lỗi:</strong> ${errorMsg}
        </div>
    </c:if>

    <div class="row">
        <div class="col-md-5 mb-4">
            <div class="card shadow-sm border-0 bg-light p-4">
                <h4 class="mb-3 fw-bold text-secondary">Thông tin giao hàng</h4>
                <hr>
                <form action="${pageContext.request.contextPath}/order" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Người nhận:</label>
                        <input type="text" class="form-control" value="${account.fullName}" disabled bg-white>
                    </div>

                    <div class="mb-3">
                        <label for="address" class="form-label fw-bold">Địa chỉ giao hàng:</label>
                        <input type="text" class="form-control" id="address" name="address" required 
                               value="${account.address}" placeholder="Nhập địa chỉ cụ thể...">
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label fw-bold">Số điện thoại liên lạc:</label>
                        <input type="tel" class="form-control" id="phone" name="phone" required 
                               value="${account.phone}" placeholder="Nhập số điện thoại...">
                    </div>

                    <div class="d-grid mt-4">
                        <button type="submit" class="btn btn-success btn-lg fw-bold shadow">HOÀN TẤT ĐẶT HÀNG</button>
                        <a href="${pageContext.request.contextPath}/cart" class="btn btn-link text-secondary mt-2">Quay lại giỏ hàng</a>
                    </div>
                </form>
            </div>
        </div>

        <div class="col-md-7">
            <div class="card shadow-sm border-0 p-4">
                <h4 class="mb-3 fw-bold text-secondary">Tóm tắt đơn hàng</h4>
                <hr>
                <table class="table align-middle">
                    <tbody>
                        <c:forEach items="${cartItems}" var="item">
                            <tr>
                                <td>
                                    <span class="fw-bold text-truncate" style="max-width: 250px; display: inline-block;">${item.product.name}</span>
                                </td>
                                <td>$${item.product.price}</td>
                                <td class="fw-bold">x${item.quantity}</td>
                                <td class="text-danger fw-bold text-end">$${item.product.price * item.quantity}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="p-3 bg-light border rounded mt-3 text-end">
                    <h5 class="mb-0 fw-bold">Tổng thanh toán: 
                        <span class="text-danger fs-4">$${totalPrice}</span>
                    </h5>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />