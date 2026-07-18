<%-- 
    Document   : category-edit
    Created on : Jul 18, 2026, 10:04:05 PM
    Author     : LENOVO
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container my-5" style="max-width: 600px;">
    <div class="card shadow-sm border-0 p-4 bg-white rounded">
        <div class="mb-4 pb-2 border-bottom">
            <h3 class="fw-bold text-warning m-0">✏️ CHỈNH SỬA DANH MỤC</h3>
            <small class="text-muted">Cập nhật thông tin phân loại mã ID: ${category.id}</small>
        </div>

        <c:if test="${param.error == 'empty_name'}">
            <div class="alert alert-danger py-2 fw-bold">⚠ Lỗi: Tên danh mục không được bỏ trống!</div>
        </c:if>
        <c:if test="${param.error == 'update_fail'}">
            <div class="alert alert-danger py-2">⚠ Lỗi: Không thể cập nhật danh mục vào hệ thống!</div>
        </c:if>
        <c:if test="${param.error == 'duplicate_name'}">
            <div class="alert alert-warning py-2 fw-bold">⚠ Lỗi: Tên danh mục này đã được sử dụng bởi một phân loại khác! Vui lòng nhập tên khác.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/adminCategory?action=edit" method="post">
            <!-- Thẻ ẩn lưu mã ID của danh mục cần sửa -->
            <input type="hidden" name="id" value="${category.id}">

            <div class="mb-4">
                <label class="form-label fw-bold text-secondary">Tên danh mục</label>
                <input type="text" name="name" class="form-control form-control-lg fs-6" 
                       value="${category.name}" required autofocus>
            </div>

            <div class="d-flex gap-2 justify-content-end pt-3 border-top">
                <a href="${pageContext.request.contextPath}/adminProduct?action=list" class="btn btn-light fw-bold px-3">Hủy Bỏ</a>
                <button type="submit" class="btn btn-warning px-4 fw-bold shadow-sm">Cập Nhật</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />