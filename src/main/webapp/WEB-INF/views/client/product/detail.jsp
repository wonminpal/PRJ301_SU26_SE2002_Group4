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

            <h6 class="fw-bold mb-2 mt-4">Chọn dung lượng:</h6>
            <div class="d-flex flex-wrap gap-2 mb-3" id="capacity-container">
            </div>

            <h6 class="fw-bold mb-2">Chọn màu sắc:</h6>
            <div class="d-flex flex-wrap gap-2 mb-4" id="color-container">
            </div>

            <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex align-items-center gap-3">
                <input type="hidden" name="action" value="add">

                <!-- SỬA "productId" THÀNH "id" ĐỂ KHỚP VỚI CARTSERVLET -->
                <input type="hidden" name="id" value="${product.id}">

                <!-- Đảm bảo tên biến variant khớp với CartDAO (nếu CartServlet dùng request.getParameter("variant")) -->
                <input type="hidden" name="variant" id="selected-variant-id" value="">

                <div class="input-group" style="width: 130px;">
                    <span class="input-group-text bg-white">SL</span>
                    <!-- Nhớ cho phép người dùng chọn số lượng lớn hơn 1 nhé, bỏ max="1" đi -->
                    <input type="number" name="quantity" id="order-quantity" class="form-control text-center" value="1" min="1">
                </div>

                <button type="submit" class="btn btn-danger btn-lg px-4 fw-bold shadow-sm" id="btn-add-cart">
                    THÊM VÀO GIỎ HÀNG
                </button>
            </form>

        </div>
    </div>
</div>
        <div class="mt-4 pt-3 border-top">
                <a href="${pageContext.request.contextPath}/review?productId=${product.id}&slug=${product.slug}" class="btn btn-outline-dark w-100 fw-bold">
                    ⭐ Xem & Viết Đánh Giá Sản Phẩm
                </a>
            </div>        
<script>
    // Cấu trúc danh sách các biến thể sản phẩm
    const variants = [
    <c:forEach items="${product.variants}" var="v" varStatus="status">
    {
    id: ${v.id},
            color: "${v.color}",
            capacity: "${v.storageCapacity}",
            price: ${v.price},
            stock: ${v.stockQuantity}
    }
        <c:if test="${not status.last}">,</c:if>
    </c:forEach>
    ];
</script>

<script src="${pageContext.request.contextPath}/assets/js/product-detail.js"></script>

<style>
    .variant-btn.active {
        background-color: #dc3545;
        color: white;
    }
</style>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />

