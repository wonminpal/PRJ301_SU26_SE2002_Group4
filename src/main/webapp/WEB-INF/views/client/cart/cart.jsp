<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/include/header.jsp" />

<div class="container mt-4" style="min-height: 60vh;">
    <h2 class="mb-4 fw-bold">Giỏ hàng của bạn</h2>

    <c:choose>
        <c:when test="${empty cartItems}">
            <div class="alert alert-warning text-center">
                Giỏ hàng đang trống. <a href="${pageContext.request.contextPath}/home" class="alert-link">Quay lại mua sắm ngay!</a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered table-hover text-center align-middle bg-white shadow-sm">
                <thead class="table-dark">
                    <tr>
                        <th>Ảnh</th>
                        <th>Sản phẩm</th>
                        <th>Đơn giá</th>
                        <th>Số lượng</th>
                        <th>Tổng cộng</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:set var="totalPrice" value="0" />
                    <c:forEach items="${cartItems}" var="item">
                        <tr>
                            <td><img src="${item.product.imageUrl}" width="70" alt="${item.product.name}"></td>
                            <td class="text-start fw-bold">${item.product.name}</td>
                            <td class="align-middle fw-bold text-danger">
                                <fmt:formatNumber value="${item.product.price}" type="number" pattern="#,###" />đ
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex justify-content-center">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="id" value="${item.product.id}">
                                    <input type="hidden" name="variant" value="${item.variant}">
                                    <input type="number" name="quantity" value="${item.quantity}" min="1" class="form-control form-control-sm text-center" style="width: 70px;">
                                    <button type="submit" class="btn btn-sm btn-outline-secondary ms-1">Cập nhật</button>
                                </form>
                            </td>
                            <td class="align-middle fw-bold text-danger">
                                <fmt:formatNumber value="${item.product.displayPrice * item.quantity}" pattern="#,###"/>₫
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/cart?action=remove&id=${item.product.id}&variant=${item.variant}" class="btn btn-sm btn-outline-danger">Xóa</a>
                            </td>
                        </tr>

                        <c:set var="totalPrice" value="${totalPrice + (item.product.displayPrice * item.quantity)}" />

                    </c:forEach>
                </tbody>
            </table>

            <!-- Khu vực Voucher hỗ trợ cộng dồn và xem lịch sử -->
            <div class="row justify-content-end mt-4">
                <div class="col-md-5 col-lg-4">
                    <div class="card p-3 shadow-sm bg-white border position-relative">
                        <label for="voucherInput" class="form-label fw-bold text-secondary mb-2">Mã giảm giá (Voucher)</label>
                        <div class="input-group">
                            <input type="text" id="voucherInput" class="form-control text-uppercase" placeholder="Nhập mã..." autocomplete="off" onfocus="showVoucherHistory()">
                            <button type="button" id="btnApplyVoucher" class="btn btn-danger fw-bold" onclick="applyVoucher()">Áp dụng</button>
                        </div>

                        <!-- DANH SÁCH GỢI Ý MÃ ĐÃ NHẬP (Dropdown History) -->
                        <div id="voucherHistoryDropdown" class="list-group shadow-sm position-absolute w-100" 
                             style="display: none; top: 100%; left: 0; z-index: 1050; max-height: 200px; overflow-y: auto; padding: 0 15px; margin-top: 5px;">
                            <div class="bg-white border rounded" id="historyItemsContainer">
                                <!-- Render danh sách mã giảm giá từ localStorage tại đây -->
                            </div>
                        </div>

                        <!-- Vùng hiển thị thông báo lỗi hoặc tag các Voucher đang dùng -->
                        <div id="voucherMessage" class="small fw-bold mt-2" style="display: none;"></div>
                    </div>
                </div>
            </div>

            <!-- Khối hiển thị tổng tiền -->
            <div class="d-flex justify-content-between align-items-center mt-3 p-3 bg-light border rounded">
                <h4 class="mb-0 fw-bold">Tổng thanh toán: 
                    <span class="text-danger" id="totalPriceDisplay">
                        <fmt:formatNumber value="${totalPrice}" pattern="#,###"/>₫
                    </span>
                </h4>
                <form action="${pageContext.request.contextPath}/checkout" method="post">
                    <!-- Chuỗi các mã ngăn cách bằng dấu phẩy sẽ được gán vào đây để gửi lên Servlet -->
                    <input type="hidden" id="appliedVoucherCode" name="voucherCode" value="">
                    <button type="submit" class="btn btn-success btn-lg fw-bold px-5">THANH TOÁN</button>
                </form>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
    var originalPrice = ${totalPrice != null ? totalPrice : 0};
    var activeVouchers = []; // Mảng chứa các voucher đang được cộng dồn

    // Ẩn dropdown lịch sử mã khi nhấn ra ngoài vùng nhập
    document.addEventListener("click", function (e) {
        var dropdown = document.getElementById("voucherHistoryDropdown");
        var voucherInput = document.getElementById("voucherInput");
        if (e.target !== voucherInput && dropdown && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });

    // Hiển thị lịch sử các mã đã từng dùng thành công từ bộ nhớ trình duyệt
    function showVoucherHistory() {
        var history = JSON.parse(localStorage.getItem("voucherHistory")) || [];
        var dropdown = document.getElementById("voucherHistoryDropdown");
        var container = document.getElementById("historyItemsContainer");

        if (history.length === 0) {
            dropdown.style.display = "none";
            return;
        }

        var html = "";
        history.forEach(function (code) {
            var isApplied = activeVouchers.some(function (v) {
                return v.code === code;
            });
            var badge = isApplied ? '<span class="badge bg-success">Đang dùng</span>' : '';

            html += '<div class="list-group-item list-group-item-action d-flex justify-content-between align-items-center py-2" style="cursor: pointer;">' +
                    '  <span onclick="selectVoucher(\'' + code + '\')" class="w-100 fw-bold ' + (isApplied ? 'text-muted' : 'text-dark') + '">' + code + ' ' + badge + '</span>' +
                    '  <button type="button" class="btn-close small" style="font-size: 10px;" onclick="deleteVoucherHistory(\'' + code + '\', event)"></button>' +
                    '</div>';
        });

        container.innerHTML = html;
        dropdown.style.display = "block";
    }

    function selectVoucher(code) {
        if (activeVouchers.some(function (v) {
            return v.code === code;
        }))
            return;
        document.getElementById("voucherInput").value = code;
        document.getElementById("voucherHistoryDropdown").style.display = "none";
    }

    function deleteVoucherHistory(code, event) {
        event.stopPropagation();
        var history = JSON.parse(localStorage.getItem("voucherHistory")) || [];
        history = history.filter(function (item) {
            return item !== code;
        });
        localStorage.setItem("voucherHistory", JSON.stringify(history));
        showVoucherHistory();
    }

    function saveToHistory(code) {
        var history = JSON.parse(localStorage.getItem("voucherHistory")) || [];
        if (!history.includes(code)) {
            history.push(code);
            localStorage.setItem("voucherHistory", JSON.stringify(history));
        }
    }

    // Gửi yêu cầu áp dụng mã giảm giá và tính toán cộng dồn
    function applyVoucher() {
        var voucherInput = document.getElementById("voucherInput");
        var voucherCode = voucherInput.value.trim().toUpperCase();
        var messageDiv = document.getElementById("voucherMessage");

        if (voucherCode === "") {
            messageDiv.style.display = "block";
            messageDiv.className = "small fw-bold mt-2 text-danger";
            messageDiv.innerText = "Vui lòng nhập mã giảm giá!";
            return;
        }

        if (activeVouchers.some(function (v) {
            return v.code === voucherCode;
        })) {
            messageDiv.style.display = "block";
            messageDiv.className = "small fw-bold mt-2 text-warning";
            messageDiv.innerText = "Mã này đang được áp dụng rồi!";
            return;
        }

        // 🔴 ĐÃ CẬP NHẬT: Gửi thêm orderAmount = originalPrice lên Servlet để check min_order_value
        fetch("${pageContext.request.contextPath}/voucher?action=apply", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: "code=" + encodeURIComponent(voucherCode) + "&orderAmount=" + originalPrice
        })
                .then(function (response) {
                    return response.json();
                })
                .then(function (data) {
                    messageDiv.style.display = "block";
                    if (data.status === "success") {
                        // Thêm mã mới vào danh sách đang áp dụng
                        activeVouchers.push({
                            code: voucherCode,
                            discount: data.discountPercent
                        });

                        // Tính lại giá và cập nhật giao diện hiển thị các tag mã
                        updateCartSummary();
                        saveToHistory(voucherCode);

                        voucherInput.value = ""; // Dọn sạch ô nhập để sẵn sàng nhập mã tiếp theo
                    } else {
                        messageDiv.className = "small fw-bold mt-2 text-danger";
                        messageDiv.innerText = data.message;
                    }
                })
                .catch(function (error) {
                    console.error("Error:", error);
                });
    }

    // Cập nhật lại tổng số tiền và render danh sách tag Voucher đang hoạt động
    function updateCartSummary() {
        var priceDisplay = document.getElementById("totalPriceDisplay");
        var hiddenInput = document.getElementById("appliedVoucherCode");
        var messageDiv = document.getElementById("voucherMessage");

        if (activeVouchers.length === 0) {
            priceDisplay.innerText = originalPrice.toLocaleString('vi-VN') + "₫";
            hiddenInput.value = "";
            messageDiv.innerHTML = "";
            messageDiv.style.display = "none";
            return;
        }

        // Tính tổng phần trăm giảm giá tích lũy từ các mã
        var totalDiscountPercent = activeVouchers.reduce(function (sum, v) {
            return sum + v.discount;
        }, 0);
        if (totalDiscountPercent > 100)
            totalDiscountPercent = 100;

        // Tính toán lại giá trị thanh toán mới sau khi giảm
        var finalPrice = originalPrice - (originalPrice * totalDiscountPercent / 100);
        priceDisplay.innerText = finalPrice.toLocaleString('vi-VN') + "₫";

        // Gộp các mã thành chuỗi phân tách bằng dấu phẩy (Ví dụ: "PRJ10,SAMSUNG20") gửi lên Controller
        hiddenInput.value = activeVouchers.map(function (v) {
            return v.code;
        }).join(",");

        // Tạo giao diện các khối tag Voucher có nút hủy nhanh từng mã
        var tagsHtml = '<div class="mt-2 fw-bold text-secondary small">Mã đang dùng (Cộng dồn): </div>' +
                '<div class="d-flex flex-wrap gap-1 mt-1">';
        activeVouchers.forEach(function (v, index) {
            tagsHtml += '<span class="badge bg-danger d-flex align-items-center gap-1 py-1 px-2 text-white">' +
                    v.code + ' (-' + v.discount + '%)' +
                    '<span style="cursor:pointer; font-size: 14px; font-weight:bold; margin-left: 5px;" onclick="removeSpecificVoucher(' + index + ')">&times;</span>' +
                    '</span>';
        });
        tagsHtml += '</div>';

        messageDiv.innerHTML = tagsHtml;
        messageDiv.style.display = "block";
    }

    // Hủy bỏ trạng thái áp dụng của một mã giảm giá cụ thể trong danh sách cộng dồn
    function removeSpecificVoucher(index) {
        activeVouchers.splice(index, 1);
        updateCartSummary();
    }
</script>

<jsp:include page="/WEB-INF/views/include/footer.jsp" />