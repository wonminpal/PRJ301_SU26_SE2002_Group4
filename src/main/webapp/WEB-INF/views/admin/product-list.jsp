<%-- 
    Document   : product-list
    Created on : Jul 6, 2026, 10:13:46 PM
    Author     : LENOVO
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container mt-5 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-3 bg-white p-3 rounded shadow-sm">
        <div>
            <h2 class="fw-bold text-dark m-0">Hệ thống quản trị</h2>
            <small class="text-muted">Quản lý kho hàng và danh mục phân loại</small>
        </div>
        <div class="dropdown">
            <button class="btn btn-primary btn-lg dropdown-toggle fw-bold shadow-sm" type="button" id="adminAddDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                + Thêm Mới
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow border-0" aria-labelledby="adminAddDropdown">
                <li>
                    <a class="dropdown-menu-item dropdown-item py-2 fw-bold" href="${pageContext.request.contextPath}/adminProduct?action=add">
                        📦 Thêm Sản Phẩm Mới
                    </a>
                </li>
                <li><hr class="dropdown-divider"></li>
                <!-- LỰA CHỌN 2: Đi tới Form thêm Danh mục (Em sẽ phát triển tiếp Servlet/Action cho nó) -->
                <li>
                    <a class="dropdown-menu-item dropdown-item py-2 fw-bold text-success" href="${pageContext.request.contextPath}/adminProduct?action=addCategory">
                        📁 Thêm Danh Mục Mới
                    </a>
                </li>
            </ul>
        </div>

    </div>
</div>


<div class="card shadow-sm border-0">
    <div class="card-body p-0">
        <table class="table table-hover mb-0 align-middle">
            <thead class="table-dark text-center fs-5">
                <th width="30%">Hình ảnh</th>
                <th width="20%">Tên Sản Phẩm</th>
                <th width="15%">Thương Hiệu</th>
                <th width="15%">Giá Cơ Sở</th>
                <th width="20%">Hành Động</th>
            </thead>
            <tbody>
                <c:forEach items="${adminProductList}" var="p">
                    <tr>
                        <td class="text-center">
                            <img src="${pageContext.request.contextPath}/${p.displayImageUrl}" alt="${p.slug}" class="img-thumbnail shadow-sm rounded" 
                                 style="width: 200px; height: 200px; object-fit: cover; background-color: #fff;">
                        </td>
                        <td>
                            <span class="fw-bold text-primary">${p.name}</span>
                            <br>
                            <small class="text-muted">Slug: ${p.slug}</small>
                        </td>
                        <td class="text-center">
                            <span class="badge bg-secondary px-2 py-1">${p.brand}</span>
                        </td>
                        <td class="text-danger fw-bold">
                            <fmt:formatNumber value="${p.displayPrice}" pattern="#,###"/>₫
                        </td>
                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/adminProduct?action=edit&id=${p.id}" class="btn btn-warning btn-sm fw-bold">Sửa</a>
                            <a href="#" class="btn btn-danger btn-sm fw-bold" onclick="return confirm('Bạn có chắc chắn muốn xóa không?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty adminProductList}">
                    <tr>
                        <td colspan="5" class="text-center p-5 text-muted">
                            🌟 Chưa có sản phẩm nào trong hệ thống!
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />