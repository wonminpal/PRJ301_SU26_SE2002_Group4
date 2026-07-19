<%-- 
    Document   : voucher
    Created on : Jun 23, 2026
    Author     : Trương Anh Tuấn CE201233
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- THÊM DÒNG NÀY ĐỂ SỬ DỤNG ĐƯỢC THẺ <c:forEach> VÀ <c:choose> --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container my-5" style="min-height: 65vh;">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <!-- Tiêu đề trang -->
            <div class="d-flex align-items-center mb-4 border-bottom pb-3">
                <h3 class="fw-bold mb-0 text-dark">
                    <i class="fa-solid fa-ticket text-danger me-2"></i> Kho Voucher Của Bạn
                </h3>
                <%-- Số lượng voucher sẽ lấy động theo size của list --%>
                <span class="badge bg-danger ms-3 fs-6">${availableCount} Voucher khả dụng</span>            </div>

            <!-- 🔴 BẮT ĐẦU THAY THẾ TỪ ĐÂY ĐẾN HẾT </div row> 🔴 -->
            <!-- Danh sách Voucher Động từ Database -->
            <div class="row g-3">
                <c:forEach var="v" items="${vouchersList}">
                    <div class="col-md-6">
                        <div class="card border-start border-danger border-4 shadow-sm h-100">
                            <div class="card-body d-flex justify-content-between align-items-center py-4">
                                <div>
                                    <h5 class="text-danger fw-bold mb-1">GIẢM ${v.discountPercent}% TỔNG ĐƠN</h5>
                                    <p class="text-muted small mb-2">
                                        Giảm tối đa: ${v.maxDiscount}đ • Đơn tối thiểu: ${v.minOrderValue}đ
                                    </p>
                                    <p class="text-muted small mb-2">HSD: ${v.expiryDate}</p>
                                    <span class="badge bg-light text-dark border">Mã: <strong class="text-danger">${v.code}</strong></span>
                                </div>

                                <c:choose>
                                    <c:when test="${v.usedCount >= v.usageLimit}">
                                        <button class="btn btn-outline-secondary btn-sm fw-bold px-3 ms-2" disabled>Hết lượt</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-dark btn-sm fw-bold px-3 ms-2" onclick="copyCode('${v.code}')">Sao chép</button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <!-- 🔴 KẾT THÚC ĐOẠN THAY THẾ 🔴 -->

            <!-- Nút quay lại mua sắm -->
            <div class="text-center mt-5">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-danger fw-bold px-4 py-2 rounded-pill">
                    <i class="fa-solid fa-arrow-left me-2"></i> Quay lại mua sắm
                </a>
            </div>
        </div>
    </div>
</div>

<script>
    function copyCode(code) {
        navigator.clipboard.writeText(code).then(() => {
            alert("Đã sao chép mã voucher: " + code);
        }).catch(err => {
            console.error("Không thể sao chép mã: ", err);
        });
    }
</script>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />