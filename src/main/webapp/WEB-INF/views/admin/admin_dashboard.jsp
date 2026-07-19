<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container mt-4 mb-5" style="min-height: 70vh;">
    <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="fw-bold m-0">Tổng quan hệ thống</h2>
    <a href="${pageContext.request.contextPath}/adminProduct?action=list" class="btn btn-primary fw-bold shadow-sm d-flex align-items-center gap-2">
        <span>📦</span>
        <span>Quản Lý Kho Hàng</span>
    </a>
</div>
    <div class="row">
        <div class="col-12 col-lg-7 mb-4">
            <h5 class="fw-bold mb-3 text-secondary">Biểu đồ doanh thu theo tháng</h5>
            <div class="card shadow-sm border-0 p-3 bg-white rounded h-100">
                <canvas id="revenueChart" style="width:100%; max-height: 400px;"></canvas>
            </div>
        </div>

        <div class="col-12 col-lg-5 mb-4">
            <h5 class="fw-bold mb-3 text-secondary">Top Sản Phẩm Bán Chạy</h5>
            <div class="table-responsive shadow-sm rounded bg-white">
                <table class="table table-bordered table-hover text-center align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th style="width: 15%;">Mã SP</th>
                            <th class="text-start">Tên sản phẩm</th>
                            <th style="width: 25%;">Đã bán</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${topProducts}" var="item">
                            <tr>
                                <td class="text-muted fw-bold">#${item.id}</td>
                                <td class="text-start fw-bold text-dark">${item.name}</td>
                                <td class="text-success fw-bold fs-6">${item.totalSold}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty topProducts}">
                            <tr><td colspan="3" class="text-muted">Chưa có dữ liệu bán hàng</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    const labels = [];
    const data = [];

    // Nhúng data từ Java vào Javascript
    <c:forEach items="${revenueList}" var="item">
        labels.push("T${item.month}/${item.year}");
        data.push(${item.revenue});
    </c:forEach>

    // Lật ngược mảng để vẽ từ quá khứ đến hiện tại
    labels.reverse();
    data.reverse();

    // Vẽ biểu đồ
    new Chart(document.getElementById("revenueChart"), {
        type: "line",
        data: {
            labels: labels,
            datasets: [{
                label: "Doanh thu (VNĐ)",
                data: data,
                borderColor: "#0d6efd", // Màu xanh dương Bootstrap
                backgroundColor: "rgba(13, 110, 253, 0.1)",
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: "#0d6efd",
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false } // Ẩn legend vì chỉ có 1 đường
            },
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
</script>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />