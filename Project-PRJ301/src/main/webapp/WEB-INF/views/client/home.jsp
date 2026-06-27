<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/include/header.jsp" />

<div class="container mt-4 mb-5">
    <h2 class="mb-4 text-center fw-bold">SẢN PHẨM MỚI NHẤT</h2>
    <div class="row">
        
        <c:forEach items="${productList}" var="p">
            <div class="col-md-3 mb-4">
                <div class="card h-100 shadow-sm border-0">
                    <img src="${p.imageUrl}" class="card-img-top p-3" alt="${p.name}" style="height: 250px; object-fit: contain;">
                    
                    <div class="card-body d-flex flex-column text-center">
                        <h6 class="card-title text-truncate">${p.name}</h6>
                        <p class="card-text text-danger fw-bold mt-auto">$${p.price}</p>
                    </div>
                    
                    <div class="card-footer bg-white border-0 pb-3">
                        <form action="${pageContext.request.contextPath}/cart" method="post">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="id" value="${p.id}">
                            <button type="submit" class="btn btn-primary w-100 fw-bold">Thêm vào giỏ</button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>
        
  
</div>

    <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation" class="mt-5">
            <ul class="pagination justify-content-center">
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/home?keyword=${keyword}&page=${i}">${i}</a>
                    </li>
                </c:forEach>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/WEB-INF/include/footer.jsp" />