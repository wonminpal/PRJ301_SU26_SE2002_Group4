<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/include/header.jsp" />
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<div class="container mt-4 mb-5" style="min-height: 65vh;">
    <h2 class="mb-4 fw-bold text-center text-uppercase text-primary">Trang Chủ Thanh Toán</h2>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger shadow-sm alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-triangle-exclamation"></i> ${errorMsg}
        </div>
    </c:if>

    <div class="row">
        <div class="col-md-5 mb-4">
            <div class="card shadow-sm border-0 bg-light p-4">
                <h4 class="mb-3 fw-bold text-secondary"><i class="fa-solid fa-truck-fast"></i> Thông tin giao hàng</h4>
                <hr>
                <form action="${pageContext.request.contextPath}/order" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Tên người nhận:</label>
                        <input type="text" class="form-control" value="Khách Hàng Mẫu (Giả lập)" disabled bg-white>
                        <small class="text-muted">* Tên tài khoản đồng bộ sau khi đăng nhập.</small>
                    </div>

                    <div class="mb-3">
                        <label for="address" class="form-label fw-bold">Địa chỉ nhận hàng nhận:</label>
                        <input type="text" class="form-control" id="address" name="address" required 
                               placeholder="Nhập số nhà, tên đường, quận/huyện...">
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label fw-bold">Số điện thoại liên lạc:</label>
                        <input type="tel" class="form-control" id="phone" name="phone" required 
                               placeholder="Nhập số điện thoại người nhận hàng">
                    </div>

                    <div class="d-grid gap-2 mt-4">
                        <button type="submit" class="btn btn-success btn-lg fw-bold py-3 shadow">
                            <i class="fa-solid fa-circle-check"></i> XÁC NHẬN ĐẶT HÀNG
                        </button>
                        <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary btn-sm">Quay lại giỏ hàng</a>
                    </div>
                </form>
            </div>
        </div>

        <div class="col-md-7">
            <div class="card shadow-sm border-0 p-4 bg-white">
                <h4 class="mb-3 fw-bold text-secondary"><i class="fa-solid fa-receipt"></i> Tóm tắt đơn hàng</h4>
                <hr>
                <div class="table-responsive">
                    <table class="table align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Đơn giá</th>
                                <th>Số lượng</th>
                                <th>Tổng cộng</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${cartItems}" var="item">
                                <tr>
                                    <td class="text-start">
                                        <img src="${item.product.imageUrl}" width="40" class="me-2 rounded" alt="${item.product.name}">
                                        <span class="fw-bold text-truncate" style="max-width: 200px; display: inline-block;">${item.product.name}</span>
                                    </td>
                                    <td class="align-middle fw-bold text-danger">
                                        <fmt:formatNumber value="${item.product.displayPrice}" pattern="#,###"/>₫
                                    </td>


                                    <td class="fw-bold">x${item.quantity}</td>
                                    <td class="align-middle fw-bold text-danger">
                                        <fmt:formatNumber value="${item.product.displayPrice * item.quantity}" pattern="#,###"/>₫
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="p-3 bg-light border rounded mt-3 text-end">
                    <h4 class="mb-0 fw-bold">Tổng tiền thanh toán: 
                        <span class="text-danger">
                            <fmt:formatNumber value="${totalPrice}" pattern="#,###"/>₫
                        </span>
                    </h4>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />