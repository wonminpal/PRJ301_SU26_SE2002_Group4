<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container my-5 bg-white p-4 rounded shadow-sm">
    <div class="row">
        
        <div class="col-12 col-md-5 mb-4">
            <div class="card border-0 shadow-sm mb-3 text-center p-3">
                <c:choose>
                    <c:when test="${not empty product.images}">
                        <img id="main-product-image" src="${pageContext.request.contextPath}/${product.images[0]}" class="img-fluid rounded" style="max-height: 400px; object-fit: contain;">
                    </c:when>
                    <c:otherwise>
                        <img id="main-product-image" src="https://via.placeholder.com/400?text=No+Image" class="img-fluid rounded">
                    </c:otherwise>
                </c:choose>
            </div>

            <c:if test="${not empty product.images}">
                <div class="d-flex gap-2 overflow-auto py-2">
                    <c:forEach items="${product.images}" var="img">
                        <img src="${pageContext.request.contextPath}/${img}" class="img-thumbnail variant-btn" 
                             style="width: 80px; height: 80px; object-fit: contain; cursor: pointer;" 
                             onclick="document.getElementById('main-product-image').src = this.src">
                    </c:forEach>
                </div>
            </c:if>
        </div>

        <div class="col-12 col-md-7">
            <h2 class="fw-bold mb-2">${product.name}</h2>
            <p class="text-muted mb-2">Thương hiệu: <span class="fw-bold text-dark">${product.brand}</span> | Danh mục: <span class="fw-bold text-dark">${product.category.name}</span></p>
            
            <h3 class="text-danger fw-bold my-3" id="display-price">
                <fmt:formatNumber value="${product.displayPrice}" pattern="#,##0"/> đ
            </h3>

            <div class="card border-0 bg-light p-3 mb-4">
                <p class="mb-0">${product.description}</p>
            </div>

            <h6 class="fw-bold mb-3 mt-4">Chọn phiên bản:</h6>
            <div class="d-flex flex-wrap gap-2 mb-4">
                <c:forEach items="${product.variants}" var="v" varStatus="loop">
                    <button type="button" class="btn btn-outline-danger variant-btn ${loop.first ? 'active' : ''}" 
                            data-id="${v.id}" data-price="${v.price}" data-stock="${v.stockQuantity}"
                            onclick="selectVariant(this)">
                        ${v.storageCapacity} - ${v.color}
                    </button>
                </c:forEach>
            </div>

            <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex align-items-center gap-3">
                <input type="hidden" name="action" value="add">
                
                <input type="hidden" name="productId" value="${product.id}">
                <input type="hidden" name="variantId" id="selected-variant-id" value="${product.variants[0].id}">

                <div class="input-group" style="width: 130px;">
                    <span class="input-group-text bg-white">SL</span>
                    <input type="number" name="quantity" id="order-quantity" class="form-control text-center" value="1" min="1" max="${product.variants[0].stockQuantity}">
                </div>

                <button type="submit" class="btn btn-danger btn-lg px-4 fw-bold shadow-sm" id="btn-add-cart">
                    THÊM VÀO GIỎ HÀNG
                </button>
            </form>

        </div>
    </div>
</div>

<script>
    function selectVariant(btnElement) {
        let allBtns = document.querySelectorAll('.variant-btn');
        allBtns.forEach(b => b.classList.remove('active'));
        btnElement.classList.add('active');

        let vId = btnElement.getAttribute('data-id');
        let vPrice = parseFloat(btnElement.getAttribute('data-price'));
        let vStock = parseInt(btnElement.getAttribute('data-stock'));

        document.getElementById('selected-variant-id').value = vId;
        document.getElementById('display-price').innerText = new Intl.NumberFormat('vi-VN').format(vPrice) + ' đ';

        let qtyInput = document.getElementById('order-quantity');
        let btnCart = document.getElementById('btn-add-cart');
        
        if (vStock > 0) {
            qtyInput.max = vStock;
            if (qtyInput.value > vStock) qtyInput.value = vStock;
            btnCart.disabled = false;
        } else {
            qtyInput.max = 0;
            qtyInput.value = 0;
            btnCart.disabled = true;
        }
    }
</script>
<style>
    .variant-btn.active { background-color: #dc3545; color: white; }
</style>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />

<%--## 2. File `checkout.jsp` (Trang thông tin giao hàng)
Trang này chỉ hiện ra khi `CheckoutServlet` xác nhận user đã đăng nhập. Nếu chưa đăng nhập, Servlet sẽ điều hướng thẳng về `login.jsp` kèm thông báo lỗi.

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/include/header.jsp" />

<div class="container mt-5" style="max-width: 800px; min-height: 70vh;">
    <h2 class="mb-4 text-center fw-bold">XÁC NHẬN THANH TOÁN</h2>
    
    <div class="card shadow-sm p-4 border-0 bg-light">
        <h5 class="mb-4 text-primary">Chào mừng quay trở lại, <strong>${account.fullName}</strong>!</h5>
        
        <form action="${pageContext.request.contextPath}/order" method="post">
            <div class="mb-3">
                <label for="address" class="form-label fw-bold">Địa chỉ nhận hàng:</label>
                <input type="text" class="form-control" id="address" name="address" required 
                       placeholder="Ví dụ: 123 Đường 3/2, Quận Ninh Kiều, Cần Thơ" value="${account.address}">
            </div>
            
            <div class="mb-3">
                <label for="phone" class="form-label fw-bold">Số điện thoại liên lạc:</label>
                <input type="tel" class="form-control" id="phone" name="phone" required 
                       placeholder="Nhập số điện thoại nhận hàng" value="${account.phone}">
            </div>

            <div class="alert alert-info mt-4">
                <p class="mb-0">Tổng tiền cần thanh toán: <strong class="text-danger fs-4">$${totalPrice}</strong></p>
                <small class="text-muted">(Đã bao gồm thuế và các loại phí dịch vụ)</small>
            </div>

            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-success btn-lg fw-bold py-3 shadow">HOÀN TẤT ĐẶT HÀNG</button>
                <a href="${pageContext.request.contextPath}/cart" class="btn btn-link text-secondary">Quay lại giỏ hàng</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/include/include/footer.jsp" />
--%>