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

            <!-- Ô nhập Tên danh mục (Chắc em đã có sẵn) -->
            <div class="mb-3">
                <label class="form-label fw-bold">Tên danh mục mới</label>
                <input type="text" name="name" class="form-control" required placeholder="Ví dụ: Laptop Gaming">
            </div>

            <!-- THÊM Ô CHỌN DANH MỤC CHA VÀO ĐÂY -->
            <div class="mb-4">
                <label class="form-label fw-bold">Trực thuộc danh mục cha</label>
                <select name="parentId" class="form-select shadow-sm">
                    <option value="0">-- 🌟 Đặt làm danh mục gốc (Level 1) --</option>

                    <!-- Vòng lặp in ra các danh mục cha lấy từ Servlet -->
                    <c:forEach items="${parentCategories}" var="pCat">
                        <option value="${pCat.id}">📁 ${pCat.name}</option>
                    </c:forEach>
                </select>
                <small class="text-muted">Chọn "Đặt làm danh mục gốc" nếu đây là danh mục lớn (như Laptop, Điện thoại).</small>
            </div>

            <button type="submit" class="btn btn-success fw-bold px-4">Lưu Danh Mục</button>
            <a href="${pageContext.request.contextPath}/adminProduct?action=list" class="btn btn-secondary">Hủy bỏ</a>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />