<%-- 
    Document   : category-add
    Created on : Jul 18, 2026, 8:53:01 PM
    Author     : LENOVO
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container my-5" style="max-width: 600px;">
    <div class="card shadow-sm border-0 p-4 bg-white rounded">
        <div class="mb-4 pb-2 border-bottom">
            <h3 class="fw-bold text-success m-0">📁 THÊM DANH MỤC MỚI</h3>
            <small class="text-muted">Tạo mới phân loại hàng hóa cho hệ thống cửa hàng</small>
        </div>

        <c:if test="${param.error == 'empty_name'}">
            <div class="alert alert-danger py-2 fw-bold">⚠ Lỗi: Tên danh mục không được để trống!</div>
        </c:if>
        <c:if test="${param.error == 'exception'}">
            <div class="alert alert-danger py-2">⚠ Lỗi hệ thống: Đã xảy ra lỗi ngoài ý muốn. Vui lòng thử lại!</div>
        </c:if>
        <c:if test="${param.error == 'duplicate_name'}">
            <div class="alert alert-warning py-2 fw-bold">⚠ Lỗi: Tên danh mục này đã tồn tại trong hệ thống! Vui lòng chọn tên khác.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/adminCategory?action=add" method="post">
            <div class="mb-4">
                <label class="form-label fw-bold text-secondary">Tên danh mục</label>
                <input type="text" name="name" class="form-control form-control-lg fs-6" 
                       placeholder="Nhập tên (Ví dụ: Điện thoại, Laptop, Phụ kiện...)" required autofocus>
            </div>

            <div class="d-flex gap-2 justify-content-end pt-3 border-top">
                <!-- Nút Hủy quay trở lại trang tổng quan kho hàng -->
                <a href="${pageContext.request.contextPath}/adminProduct?action=list" class="btn btn-light fw-bold px-3">Hủy Bỏ</a>
                <button type="submit" class="btn btn-success px-4 fw-bold shadow-sm">Lưu Danh Mục</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />