<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container my-5" style="max-width: 900px;">
    <div class="card shadow-sm border-0 p-4 bg-white rounded">
        <div class="mb-4 pb-2 border-bottom">
            <h3 class="fw-bold text-warning m-0">CHỈNH SỬA SẢN PHẨM</h3>
            <small class="text-muted">Cập nhật thông tin chi tiết mã sản phẩm: #${product.id}</small>
        </div>

        <c:if test="${param.error == 'empty_name'}">
            <div class="alert alert-danger py-2 fw-bold shadow-sm mb-3">
                ⚠ Lỗi: Tên sản phẩm không được bỏ trống!
            </div>
        </c:if>

        <c:if test="${param.error == 'duplicate_name'}">
            <div class="alert alert-warning py-2 fw-bold shadow-sm mb-3">
                ⚠ Lỗi: Tên sản phẩm này đã được sử dụng (trùng đường dẫn URL)! Vui lòng nhập tên khác.
            </div>
        </c:if>

        <c:if test="${param.error == 'exception'}">
            <div class="alert alert-danger py-2 shadow-sm mb-3">
                ⚠ Lỗi hệ thống: Không thể thực hiện thao tác lúc này!
            </div>
        </c:if>
        <!-- Form cập nhật, gửi kèm action=update và type=product -->
        <form action="${pageContext.request.contextPath}/adminProduct?action=update&type=product" method="post" id="productForm">
            <!-- Thẻ ẩn (hidden) lưu ID sản phẩm phục vụ cho câu lệnh WHERE trong SQL -->
            <input type="hidden" name="productId" value="${product.id}">

            <!-- 1. THÔNG TIN CHUNG SẢN PHẨM CHA -->
            <h5 class="fw-bold text-dark mb-3">1. Thông tin gốc</h5>
            <div class="row">
                <div class="col-12 col-md-4 mb-3">
                    <label class="form-label fw-bold">Danh mục ID</label>
                    <input type="number" name="categoryId" class="form-control" value="${product.categoryId}" min="1" required>
                </div>
                <div class="col-12 col-md-8 mb-3">
                    <label class="form-label fw-bold">Tên sản phẩm</label>
                    <input type="text" name="name" class="form-control" value="${product.name}" required>
                </div>
            </div>

            <div class="row">
                <div class="col-12 col-md-6 mb-3">
                    <label class="form-label fw-bold">Thương hiệu</label>
                    <input type="text" name="brand" class="form-control" value="${product.brand}" required>
                </div>
                <div class="col-12 col-md-6 mb-3">
                    <label class="form-label fw-bold">Giá bán gốc (đ)</label>
                    <input type="number" name="price" class="form-control" value="${product.displayPrice}" min="0" required>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label fw-bold">Đường dẫn hình ảnh</label>
                <input type="text" name="displayImageUrl" class="form-control" value="${product.displayImageUrl}" required>
            </div>

            <div class="mb-4">
                <label class="form-label fw-bold">Thông số & Mô tả</label>
                <textarea name="description" class="form-control" rows="3">${product.description}</textarea>
            </div>

            <!-- 2. DANH SÁCH BIẾN THỂ ĐANG CÓ (TỰ ĐỘNG ĐỔ RA TỪ CÙNG BẢNG MẢNG SONG SONG) -->
            <div class="d-flex justify-content-between align-items-center mb-3 mt-4">
                <h5 class="fw-bold text-secondary m-0">2. Các phiên bản biến thể</h5>
                <button type="button" class="btn btn-sm btn-outline-primary fw-bold px-3" onclick="addVariantRow()">+ Thêm dòng biến thể</button>
            </div>

            <div id="variant-container">
                <!-- Vòng lặp in ra toàn bộ đống biến thể cũ -->
                <c:forEach var="v" items="${variants}">
                    <div class="row g-2 mb-3 variant-row bg-light p-3 rounded position-relative border">
                        <!-- 1. Màu sắc -->
                        <div class="col-12 col-md-2">
                            <label class="form-label small fw-bold text-muted">Màu sắc</label>
                            <input type="text" name="colors" class="form-control" placeholder="${v.color}" required>
                        </div>

                        <!-- 2. Bộ nhớ -->
                        <div class="col-12 col-md-2">
                            <label class="form-label small fw-bold text-muted">Bộ nhớ</label>
                            <input type="text" name="capacities" class="form-control" placeholder="${v.storageCapacity}" required>
                        </div>

                        <!-- 3. Giá tiền (Đã xử lý format dấu chấm) -->
                        <div class="col-12 col-md-3">
                            <label class="form-label small fw-bold text-muted">Giá riêng dòng này</label>
                            <fmt:formatNumber value="${v.price}" pattern="#,###" var="formattedPrice" />
                            <input type="text" name="prices" class="form-control" placeholder="${v.price > 0 ? formattedPrice : 'Giá (đ)'}" 
                                   oninput="this.value = this.value.replace(/[^0-9.]/g, '')">
                        </div>

                        <!-- 4. Số lượng kho -->
                        <div class="col-12 col-md-2">
                            <label class="form-label small fw-bold text-muted">Số lượng kho</label>
                            <input type="number" name="stocks" class="form-control" placeholder="${v.stockQuantity}" min="0" required>
                        </div>

                        <!-- 5. Ảnh biến thể -->
                        <div class="col-12 col-md-2">
                            <label class="form-label small fw-bold text-muted">Ảnh màu này</label>
                            <input type="text" name="variantImages" class="form-control" placeholder="${not empty v.variantImage ? v.variantImage : 'Đường dẫn ảnh'}">
                        </div>

                        <!-- Nút xóa dòng -->
                        <div class="col-12 col-md-1 d-flex align-items-end">
                            <button type="button" class="btn btn-danger w-100" onclick="removeRow(this)">Xóa</button>
                        </div>
                    </div>
                </c:forEach>

                <!-- Nếu sản phẩm cũ chưa có biến thể nào thì hiện ra 1 ô trống mặc định -->
                <c:if test="${empty variants}">
                    <div class="row g-2 mb-3 variant-row bg-light p-3 rounded position-relative border">
                        <div class="col-12 col-md-2"><input type="text" name="colors" class="form-control" placeholder="Màu sắc" required></div>
                        <div class="col-12 col-md-2"><input type="text" name="capacities" class="form-control" placeholder="Dung lượng" required></div>
                        <div class="col-12 col-md-3"><input type="number" name="prices" class="form-control" min="0" placeholder="Giá"></div>
                        <div class="col-12 col-md-2"><input type="number" name="stocks" class="form-control" min="0" placeholder="Số kho" required></div>
                        <div class="col-12 col-md-2"><input type="text" name="variantImages" class="form-control" placeholder="Link ảnh"></div>
                        <div class="col-12 col-md-1 d-flex align-items-end"><button type="button" class="btn btn-danger w-100" onclick="removeRow(this)">Xóa</button></div>
                    </div>
                </c:if>
            </div>

            <!-- 3. HÀNH ĐỘNG -->
            <div class="d-flex flex-column flex-md-row gap-2 justify-content-end mt-4 pt-3 border-top">
                <a href="${pageContext.request.contextPath}/adminProduct?action=list" class="btn btn-secondary order-2 order-md-1">Hủy Bỏ</a>
                <button type="submit" class="btn btn-warning px-5 fw-bold order-1 order-md-2 shadow text-dark">Cập Nhật Ngay</button>
            </div>
        </form>
    </div>
</div>

<script>
    // Hàm sinh thêm dòng mới nếu Admin muốn thêm biến thể khi đang cập nhật sản phẩm
    function addVariantRow() {
        const container = document.getElementById('variant-container');
        const newRow = document.createElement('div');
        newRow.className = 'row g-2 mb-3 variant-row bg-light p-3 rounded position-relative border';
        newRow.innerHTML = `
            <div class="col-12 col-md-2"><input type="text" name="colors" class="form-control" placeholder="Màu mới" required></div>
            <div class="col-12 col-md-2"><input type="text" name="capacities" class="form-control" placeholder="Bộ nhớ" required></div>
            <div class="col-12 col-md-3"><input type="number" name="prices" class="form-control" min="0" placeholder="Giá"></div>
            <div class="col-12 col-md-2"><input type="number" name="stocks" class="form-control" min="0" placeholder="Số kho" required></div>
            <div class="col-12 col-md-2"><input type="text" name="variantImages" class="form-control" placeholder="Ảnh"></div>
            <div class="col-12 col-md-1 d-flex align-items-end"><button type="button" class="btn btn-danger w-100" onclick="removeRow(this)">Xóa</button></div>
        `;
        container.appendChild(newRow);
    }

    function removeRow(button) {
        const rows = document.getElementsByClassName('variant-row');
        if (rows.length > 1) {
            button.closest('.variant-row').remove();
        } else {
            alert("Không được xóa hết, sản phẩm tối thiểu phải có một dòng thông số chứ em!");
        }
    }
</script>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />