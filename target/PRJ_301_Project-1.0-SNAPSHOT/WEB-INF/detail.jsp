<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/include/header.jsp" />

<div class="container mt-5 mb-5">
    <div class="row">
        <div class="col-md-6 text-center">
            <img src="${product.imageUrl}" class="img-fluid rounded shadow-sm border" style="max-height: 500px;" alt="${product.name}">
        </div>
        
        <div class="col-md-6">
            <h1 class="fw-bold">${product.name}</h1>
            <h3 class="text-danger mt-3 fw-bold">$${product.price}</h3>
            <hr>
            
            <p class="mt-4 text-muted">Mô tả sản phẩm:</p>
            <p>${product.description}</p>
            
            <div class="mt-4">
                <p>Tình trạng kho: 
                    <c:choose>
                        <c:when test="${product.stockQuantity > 0}">
                            <span class="badge bg-success">Còn hàng (${product.stockQuantity})</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-danger">Hết hàng</span>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>

            <form action="${pageContext.request.contextPath}/cart" method="post" class="mt-4">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="id" value="${product.id}">
                <button type="submit" class="btn btn-primary btn-lg px-5 py-3 fw-bold shadow-sm" 
                        <c:if test="${product.stockQuantity <= 0}">disabled</c:if>>
                    THÊM VÀO GIỎ HÀNG
                </button>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/include/footer.jsp" />

### 2. File `checkout.jsp` (Trang thông tin giao hàng)
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

<jsp:include page="/WEB-INF/include/footer.jsp" />