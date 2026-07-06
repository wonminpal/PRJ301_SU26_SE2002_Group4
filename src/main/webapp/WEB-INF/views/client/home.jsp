<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container mb-5" style="min-height: 70vh;">

    <!-- THANH DANH MỤC SẢN PHẨM (GIAO DIỆN TRẢI DÀI) -->
    <div class="bg-white p-3 rounded shadow-sm mb-4">
        <h5 class="fw-bold mb-3 text-dark">Danh mục nổi bật</h5>
        
        <!-- Thêm w-100 để đảm bảo thẻ div chiếm trọn 100% chiều rộng -->
        <div class="d-flex flex-wrap gap-2 w-100">
            
            <!-- Thêm class "flex-fill" và "text-center" vào từng nút -->
            
            <!-- Nút Tất cả -->
            <a href="${pageContext.request.contextPath}/home" class="btn ${empty currentCategory ? 'btn-danger' : 'btn-outline-danger'} fw-bold rounded-pill px-3 py-2 flex-fill category-pill text-center">
                Tất cả
            </a>
            
            <!-- Điện thoại -->
            <a href="${pageContext.request.contextPath}/home?category=dien-thoai" class="btn ${currentCategory == 'dien-thoai' ? 'btn-danger' : 'border'} fw-bold text-dark rounded-pill px-3 py-2 flex-fill category-pill text-center">
                <i class="fa-solid fa-mobile-screen-button text-primary"></i> Điện thoại
            </a>
            
            <!-- Laptop -->
            <a href="${pageContext.request.contextPath}/home?category=laptop" class="btn ${currentCategory == 'laptop' ? 'btn-danger' : 'border'} fw-bold text-dark rounded-pill px-3 py-2 flex-fill category-pill text-center">
                <i class="fa-solid fa-laptop text-success"></i> Laptop
            </a>
            
            <!-- Máy tính bảng -->
            <a href="${pageContext.request.contextPath}/home?category=tablet" class="btn ${currentCategory == 'tablet' ? 'btn-danger' : 'border'} fw-bold text-dark rounded-pill px-3 py-2 flex-fill category-pill text-center">
                <i class="fa-solid fa-tablet-screen-button text-secondary"></i> Máy tính bảng
            </a>
            
            <!-- Phụ kiện -->
            <a href="${pageContext.request.contextPath}/home?category=phu-kien" class="btn ${currentCategory == 'phu-kien' ? 'btn-danger' : 'border'} fw-bold text-dark rounded-pill px-3 py-2 flex-fill category-pill text-center">
                <i class="fa-solid fa-headphones text-info"></i> Phụ kiện
            </a>
            
            <!-- Tivi -->
            <a href="${pageContext.request.contextPath}/home?category=tivi" class="btn ${currentCategory == 'tivi' ? 'btn-danger' : 'border'} fw-bold text-dark rounded-pill px-3 py-2 flex-fill category-pill text-center">
                <i class="fa-solid fa-tv text-danger"></i> Tivi
            </a>
            
        </div>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="fw-bold text-uppercase text-dark mb-0">
            <c:choose>
                <c:when test="${not empty keyword}">Kết quả tìm kiếm cho: "${keyword}"</c:when>
                <c:otherwise>Sản phẩm Mới Nhất</c:otherwise>
            </c:choose>
        </h3>
    </div>

    <c:choose>
        <c:when test="${empty productList}">
            <div class="alert alert-warning text-center shadow-sm py-4">
                <i class="fa-solid fa-box-open fs-1 text-muted mb-2"></i>
                <h5>Không tìm thấy sản phẩm nào!</h5>
            </div>
        </c:when>

        <c:otherwise>
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-3">
                <c:forEach items="${productList}" var="p">
                    <div class="col">
                        <div class="card h-100 border-0 shadow-sm product-card p-2">
                            <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="text-center p-3">
                                <img src="${p.displayImageUrl}" class="card-img-top object-fit-contain" alt="${p.name}" style="height: 200px;">
                            </a>

                            <div class="card-body d-flex flex-column pt-0">
                                <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="text-decoration-none text-dark">
                                    <h6 class="card-title fw-bold" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; min-height: 40px;">
                                        ${p.name}
                                    </h6>
                                </a>

                                <div class="mt-auto text-center mb-3">
                                    <span class="text-danger fw-bold fs-5">
                                        <fmt:formatNumber value="${p.displayPrice}" pattern="#,###"/>₫
                                    </span>
                                </div>

                                
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${totalPages > 1}">
                <nav class="mt-5">
                    <ul class="pagination justify-content-center">
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link shadow-sm ${currentPage == i ? 'bg-danger border-danger text-white' : 'text-dark'}" 
                                   href="${pageContext.request.contextPath}/home?page=${i}&keyword=${keyword}&category=${currentCategory}">
                                    ${i}
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </c:if>
        </c:otherwise>
    </c:choose>

</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />