<%-- 
    Document   : cart
    Created on : Jun 20, 2026, 12:20:13 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/include/header.jsp" />

<div class="container mt-4" style="min-height: 60vh;">
    <h2 class="mb-4 fw-bold">Giỏ hàng của bạn</h2>
    
    <c:choose>
        <c:when test="${empty cartItems}">
            <div class="alert alert-warning text-center">
                Giỏ hàng đang trống. <a href="${pageContext.request.contextPath}/home" class="alert-link">Quay lại mua sắm ngay!</a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered table-hover text-center align-middle bg-white shadow-sm">
                <thead class="table-dark">
                    <tr>
                        <th>Ảnh</th>
                        <th>Sản phẩm</th>
                        <th>Đơn giá</th>
                        <th>Số lượng</th>
                        <th>Tổng cộng</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:set var="totalPrice" value="0" />
                    <c:forEach items="${cartItems}" var="item">
                        <tr>
                            <td><img src="${item.product.imageUrl}" width="70" alt="${item.product.name}"></td>
                            <td class="text-start fw-bold">${item.product.name}</td>
                            <td class="text-danger">$${item.product.price}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex justify-content-center">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="id" value="${item.product.id}">
                                    <input type="number" name="quantity" value="${item.quantity}" min="1" class="form-control form-control-sm text-center" style="width: 70px;">
                                    <button type="submit" class="btn btn-sm btn-outline-secondary ms-1">Cập nhật</button>
                                </form>
                            </td>
                            <td class="text-danger fw-bold">$${item.product.price * item.quantity}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/cart?action=remove&id=${item.product.id}" class="btn btn-sm btn-outline-danger">Xóa</a>
                            </td>
                        </tr>
                        <c:set var="totalPrice" value="${totalPrice + (item.product.price * item.quantity)}" />
                    </c:forEach>
                </tbody>
            </table>
            
            <div class="d-flex justify-content-between align-items-center mt-4 p-3 bg-light border rounded">
                <h4 class="mb-0">Tổng thanh toán: <span class="text-danger fw-bold fs-3">$${totalPrice}</span></h4>
                
                <form action="${pageContext.request.contextPath}/checkout" method="post">
                    <button type="submit" class="btn btn-success btn-lg fw-bold px-5">THANH TOÁN</button>
                </form>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/include/footer.jsp" />
