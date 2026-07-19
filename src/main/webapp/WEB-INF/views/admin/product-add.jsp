<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container my-5" style="max-width: 900px;">
    <div class="card shadow-sm border-0 p-4 bg-white rounded">
        <!-- Khu vực tiêu đề và trạng thái lưu bản nháp -->
        <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
            <div>
                <h3 class="fw-bold text-primary m-0">THÊM SẢN PHẨM CÔNG NGHỆ MỚI</h3>
                <small class="text-muted">Nhập thông tin sản phẩm cha và cấu hình các dòng thuộc tính con</small>
            </div>
            <span id="save-status" class="badge bg-secondary p-2 shadow-sm">Chưa lưu tạm</span>
        </div>
        
        <!-- Form trỏ về Servlet xử lý bằng phương thức POST -->
        <form action="${pageContext.request.contextPath}/adminProduct" method="post" id="productForm">
            <!-- 1. KHỐI THÔNG TIN CHUNG (Chuẩn Responsive Grid) -->
            <h5 class="fw-bold text-dark mb-3">1. Thông tin cơ bản</h5>
            <div class="row">
                <div class="col-12 col-md-4 mb-3">
                    <label class="form-label fw-bold">Danh mục ID</label>
                    <input type="number" name="categoryId" id="categoryId" class="form-control" value="4" min="1" step="1" required>
                </div>
                <div class="col-12 col-md-8 mb-3">
                    <label class="form-label fw-bold">Tên sản phẩm</label>
                    <input type="text" name="name" id="name" class="form-control" placeholder="Ví dụ: iPhone 16 Pro Max" required>
                </div>
            </div>

            <div class="row">
                <div class="col-12 col-md-6 mb-3">
                    <label class="form-label fw-bold">Thương hiệu</label>
                    <input type="text" name="brand" id="brand" class="form-control" value="Apple" required>
                </div>
                <div class="col-12 col-md-6 mb-3">
                    <label class="form-label fw-bold">Giá bán gốc (đ)</label>
                    <input type="number" name="price" id="price" class="form-control" min="0" step="1000" placeholder="Mức giá tối thiểu" required>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label fw-bold">Đường dẫn hình ảnh gốc sản phẩm</label>
                <input type="text" name="displayImageUrl" id="displayImageUrl" class="form-control" placeholder="Ví dụ: assets/images/products/... " required>
            </div>
            
            <div class="mb-4">
                <label class="form-label fw-bold">Thông số & Mô tả tóm tắt sản phẩm</label>
                <textarea name="description" id="description" class="form-control" rows="3" placeholder="Nhập đặc điểm nổi bật..."></textarea>
            </div>

            <!-- 2. KHỐI CẤU HÌNH BIẾN THỂ ĐỘNG -->
            <div class="d-flex justify-content-between align-items-center mb-3 mt-4">
                <h5 class="fw-bold text-secondary m-0">2. Thiết lập các phiên bản (Biến thể)</h5>
                <button type="button" class="btn btn-sm btn-outline-primary fw-bold px-3 shadow-sm" onclick="addVariantRow()">+ Thêm dòng biến thể</button>
            </div>

            <div id="variant-container">
                <!-- Dòng nhập biến thể mặc định ban đầu -->
                <div class="row g-2 mb-3 variant-row bg-light p-3 rounded position-relative border">
                    <div class="col-12 col-md-2">
                        <label class="form-label small fw-bold text-muted">Màu sắc</label>
                        <input type="text" name="colors" class="form-control" placeholder="Titan Sa Mạc" required>
                    </div>
                    <div class="col-12 col-md-2">
                        <label class="form-label small fw-bold text-muted">Bộ nhớ</label>
                        <input type="text" name="capacities" class="form-control" placeholder="256GB" required>
                    </div>
                    <div class="col-12 col-md-3">
                        <label class="form-label small fw-bold text-muted">Giá riêng dòng này</label>
                        <input type="number" name="prices" class="form-control" min="0" placeholder="Bằng giá gốc nếu để trống">
                    </div>
                    <div class="col-12 col-md-2">
                        <label class="form-label small fw-bold text-muted">Số lượng kho</label>
                        <input type="number" name="stocks" class="form-control" min="0" step="1" placeholder="10" required>
                    </div>
                    <div class="col-12 col-md-2">
                        <label class="form-label small fw-bold text-muted">Ảnh riêng màu này</label>
                        <input type="text" name="variantImages" class="form-control" placeholder="assets/...">
                    </div>
                    <div class="col-12 col-md-1 d-flex align-items-end">
                        <button type="button" class="btn btn-danger w-100" onclick="removeRow(this)">Xóa</button>
                    </div>
                </div>
            </div>

            <!-- 3. THANH ĐIỀU HƯỚNG TÁC VỤ (Responsive trật tự xếp khối dọc trên Mobile) -->
            <div class="d-flex flex-column flex-md-row gap-2 justify-content-end mt-4 pt-3 border-top">
                <button type="button" class="btn btn-outline-danger order-3 order-md-1 me-md-auto" onclick="clearDraft()">Xóa bản nháp gõ dở</button>
                <a href="${pageContext.request.contextPath}/adminProduct?action=list" class="btn btn-secondary order-2 order-md-2">Hủy bỏ</a>
                <button type="submit" class="btn btn-success px-5 fw-bold order-1 order-md-3 shadow">Lưu Hệ Thống</button>
            </div>
        </form>
    </div>
</div>

<!-- BLOCK JAVASCRIPT XỬ LÝ AUTO-SAVE & ĐỘNG FORM MẢNG SONG SONG -->
<script>
    // ----------------------------------------------------
    // XỬ LÝ 1: THÊM/XÓA ĐỘNG DÒNG BIẾN THỂ (MẢNG SONG SONG)
    // ----------------------------------------------------
    function addVariantRow() {
        const container = document.getElementById('variant-container');
        const newRow = document.createElement('div');
        newRow.className = 'row g-2 mb-3 variant-row bg-light p-3 rounded position-relative border';
        newRow.innerHTML = `
            <div class="col-12 col-md-2">
                <label class="form-label small fw-bold text-muted d-md-none">Màu sắc</label>
                <input type="text" name="colors" class="form-control" placeholder="Màu mới" required>
            </div>
            <div class="col-12 col-md-2">
                <label class="form-label small fw-bold text-muted d-md-none">Bộ nhớ</label>
                <input type="text" name="capacities" class="form-control" placeholder="Dung lượng" required>
            </div>
            <div class="col-12 col-md-3">
                <label class="form-label small fw-bold text-muted d-md-none">Giá dòng này</label>
                <input type="number" name="prices" class="form-control" min="0" placeholder="Giá">
            </div>
            <div class="col-12 col-md-2">
                <label class="form-label small fw-bold text-muted d-md-none">Số lượng kho</label>
                <input type="number" name="stocks" class="form-control" min="0" step="1" placeholder="Số kho" required>
            </div>
            <div class="col-12 col-md-2">
                <label class="form-label small fw-bold text-muted d-md-none">Ảnh riêng màu này</label>
                <input type="text" name="variantImages" class="form-control" placeholder="Đường dẫn ảnh">
            </div>
            <div class="col-12 col-md-1 d-flex align-items-end">
                <button type="button" class="btn btn-danger w-100" onclick="removeRow(this)">Xóa</button>
            </div>
        `;
        container.appendChild(newRow);
    }

    function removeRow(button) {
        const rows = document.getElementsByClassName('variant-row');
        if (rows.length > 1) {
            button.closest('.variant-row').remove();
        } else {
            alert("Sản phẩm bắt buộc phải cấu hình tối thiểu một biến thể để kinh doanh chứ em!");
        }
    }

    // ----------------------------------------------------
    // XỬ LÝ 2: LOCALSTORAGE ĐỂ GIỮ DỮ LIỆU KHI CÓ SỰ CỐ
    // ----------------------------------------------------
    const formFields = ['categoryId', 'name', 'brand', 'price', 'displayImageUrl', 'description'];
    const statusBadge = document.getElementById('save-status');
    const form = document.getElementById('productForm');

    // Khôi phục thông tin chung từ bản nháp khi tải trang
    window.addEventListener('DOMContentLoaded', () => {
        let hasDraft = false;
        formFields.forEach(fieldId => {
            const savedValue = localStorage.getItem('draft_prj_' + fieldId);
            if (savedValue) {
                document.getElementById(fieldId).value = savedValue;
                hasDraft = true;
            }
        });
        if (hasDraft) {
            statusBadge.innerText = "🔄 Đã tự động khôi phục bản nháp!";
            statusBadge.className = "badge bg-warning text-dark p-2 shadow-sm";
        }
    });

    // Lắng nghe sự kiện gõ chữ để lưu tạm thông tin chung
    form.addEventListener('input', () => {
        statusBadge.innerText = "✍️ Đang ghi nhận...";
        statusBadge.className = "badge bg-info text-dark p-2 shadow-sm";
        
        formFields.forEach(fieldId => {
            const value = document.getElementById(fieldId).value;
            localStorage.setItem('draft_prj_' + fieldId, value);
        });

        setTimeout(() => {
            statusBadge.innerText = "💾 Bản nháp an toàn ở trình duyệt";
            statusBadge.className = "badge bg-success p-2 shadow-sm";
        }, 400);
    });

    // Submit thành công -> Xóa sạch bản nháp để không bị điền trùng lần sau
    form.addEventListener('submit', () => {
        formFields.forEach(fieldId => {
            localStorage.removeItem('draft_prj_' + fieldId);
        });
    });

    // Hàm xóa bản nháp thủ công bằng nút bấm
    function clearDraft() {
        if(confirm("Bạn có chắc chắn muốn xóa sạch dữ liệu đang nhập dở để gõ lại từ đầu không?")) {
            formFields.forEach(fieldId => {
                localStorage.removeItem('draft_prj_' + fieldId);
            });
            form.reset();
            statusBadge.innerText = "Chưa lưu tạm";
            statusBadge.className = "badge bg-secondary p-2 shadow-sm";
        }
    }
</script>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />