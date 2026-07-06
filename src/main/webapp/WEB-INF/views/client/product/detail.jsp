<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container my-5 bg-white p-4 rounded shadow-sm" style="min-height: 65vh;">
    <div class="row">

        <div class="col-12 col-md-5 mb-4 text-center">
            <div class="card border-0 shadow-sm p-3">
                <img id="main-product-image" src="${product.imageUrl}" class="img-fluid rounded" style="max-height: 400px; object-fit: contain;" alt="${product.name}">
            </div>
        </div>

        <div class="col-12 col-md-7">
            <h2 class="fw-bold text-dark">${product.name}</h2>
            <p class="text-muted mb-2">Thương hiệu: <span class="text-primary fw-bold">${product.brand}</span></p>
            <h3 class="text-danger fw-bold mt-3">
                <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="$"/>
            </h3>
            <hr>

            <h5 class="fw-bold mt-4">Mô tả sản phẩm:</h5>
            <p class="text-secondary" style="line-height: 1.8;">${product.description}</p>

            <p class="mt-3">
                <strong>Tình trạng:</strong> 
                <c:choose>
                    <c:when test="${product.stockQuantity > 0}">
                        <span class="badge bg-success">Còn hàng (${product.stockQuantity})</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-danger">Hết hàng</span>
                    </c:otherwise>
                </c:choose>
            </p>

            <form action="${pageContext.request.contextPath}/cart" method="post" class="mt-4 p-3 bg-light rounded border">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="id" value="${product.id}">

                <div class="d-flex align-items-center mb-3">
                    <label class="form-label fw-bold mb-0 me-3">Số lượng:</label>
                    <input type="number" name="quantity" value="1" min="1" max="${product.stockQuantity}" class="form-control text-center" style="width: 100px;">
                </div>

                <button type="submit" class="btn btn-primary btn-lg fw-bold px-5" 
                        <c:if test="${product.stockQuantity <= 0}">disabled</c:if>>
                            <i class="fa-solid fa-cart-plus"></i> THÊM VÀO GIỎ HÀNG
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
                if (qtyInput.value > vStock)
                    qtyInput.value = vStock;
                btnCart.disabled = false;
            } else {
                qtyInput.max = 0;
                qtyInput.value = 0;
                btnCart.disabled = true;
            }
        }
    </script>
    <style>
        .variant-btn.active {
            background-color: #dc3545;
            color: white;
        }
    </style>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />