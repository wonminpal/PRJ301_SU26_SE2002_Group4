<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container my-5">
    <div class="mb-4">
        <a href="${pageContext.request.contextPath}/detail?slug=${param.slug}" class="text-decoration-none text-dark fw-bold">
            ← Quay lại trang sản phẩm
        </a>
    </div>

    <div class="row">
        <div class="col-md-5 mb-4">
            <h4 class="fw-bold mb-3">Viết Đánh Giá</h4>
            <div class="card border-0 shadow-sm p-4 bg-light">
                <c:choose>
                    <c:when test="${not empty sessionScope.account}">
                        <form action="${pageContext.request.contextPath}/review" method="post">
                            <input type="hidden" name="productId" value="${param.productId}">
                            <input type="hidden" name="productSlug" value="${param.slug}">
                            
                            <div class="mb-3">
                                <label class="fw-bold form-label">Số sao:</label>
                                <select name="rating" class="form-select">
                                    <option value="5">⭐⭐⭐⭐⭐ (5 Sao)</option>
                                    <option value="4">⭐⭐⭐⭐ (4 Sao)</option>
                                    <option value="3">⭐⭐⭐ (3 Sao)</option>
                                    <option value="2">⭐⭐ (2 Sao)</option>
                                    <option value="1">⭐ (1 Sao)</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="fw-bold form-label">Nhận xét:</label>
                                <textarea name="comment" class="form-control" rows="4" placeholder="Chia sẻ cảm nhận của bạn..." required></textarea>
                            </div>
                            <button type="submit" class="btn btn-dark w-100 fw-bold">Gửi Đánh Giá</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning text-center mb-0">
                            Vui lòng <a href="${pageContext.request.contextPath}/auth?action=loginForm" class="fw-bold">Đăng nhập</a> để đánh giá.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="col-md-7">
            <h4 class="fw-bold mb-3">Khách hàng nhận xét</h4>
            <div class="card border-0 shadow-sm p-4">
                <c:choose>
                    <c:when test="${not empty reviewList}">
                        <c:forEach items="${reviewList}" var="r">
                            <div class="mb-3 pb-3 border-bottom">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <strong class="text-primary">${r.userFullName}</strong>
                                    <small class="text-muted"><fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy HH:mm"/></small>
                                </div>
                                <div class="text-warning mb-2">${r.rating} ⭐</div>
                                <p class="mb-0 text-dark">${r.comment}</p>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted text-center my-4">Sản phẩm này chưa có đánh giá nào.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />