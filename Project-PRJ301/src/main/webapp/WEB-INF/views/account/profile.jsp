<%-- 
    Document   : profile
    Created on : Jun 22, 2026, 11:14:47 PM
    Author     : Nguyen Minh Phat - CE201621
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/view/include/header.jsp" />

<div class="container my-5" style="min-height: 65vh;">
    <div class="row">
        <div class="col-md-3 mb-4">
            <div class="card shadow border-0 text-center py-4">
                <div class="bg-dark text-white rounded-circle d-inline-flex justify-content-center align-items-center mx-auto mb-3" style="width: 80px; height: 80px; font-size: 2rem;">
                    👤
                </div>
                <h5 class="fw-bold">${user.fullName}</h5>
                <p class="text-muted small">${user.email}</p>
                <a href="${pageContext.request.contextPath}/auth?action=logout" class="btn btn-outline-dark btn-sm mx-3 fw-bold">Đăng xuất</a>
            </div>
        </div>

        <div class="col-md-9">
            <div class="card shadow border-0">
                <div class="card-header bg-white border-bottom pt-4 pb-0">
                    <ul class="nav nav-tabs border-0" id="profileTabs">
                        <li class="nav-item">
                            <a class="nav-link active text-dark fw-bold border-0 border-bottom border-dark border-3" data-bs-toggle="tab" href="#info">Thông tin cá nhân</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link text-muted fw-bold border-0" data-bs-toggle="tab" href="#security">Bảo mật</a>
                        </li>
                    </ul>
                </div>

                <div class="card-body p-4">
                    <div class="tab-content">
                        <div class="tab-pane fade show active" id="info">
                            <% if (request.getAttribute("successMsg") != null) {%>
                            <div class="alert alert-success small"><%= request.getAttribute("successMsg")%></div>
                            <% } %>

                            <form action="${pageContext.request.contextPath}/profile?action=update" method="POST">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label small fw-bold">Họ và tên</label>
                                        <input type="text" name="fullName" class="form-control" value="${user.fullName}" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label small fw-bold">Email (Không thể đổi)</label>
                                        <input type="email" class="form-control bg-light" value="${user.email}" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label small fw-bold">Số điện thoại</label>
                                        <input type="text" name="phone" class="form-control" value="${user.phone}">
                                    </div>
                                    <div class="col-12">
                                        <label class="form-label small fw-bold">Địa chỉ giao hàng</label>
                                        <textarea name="address" class="form-control" rows="2">${user.address}</textarea>
                                    </div>
                                    <div class="col-12 text-end mt-4">
                                        <button type="submit" class="btn btn-dark px-4 fw-bold">Lưu thay đổi</button>
                                    </div>
                                </div>
                            </form>
                        </div>

                        <div class="tab-pane fade" id="security">
                            <% if (request.getAttribute("successPassMsg") != null) {%>
                            <div class="alert alert-success small"><%= request.getAttribute("successPassMsg")%></div>
                            <% } %>
                            <% if (request.getAttribute("errorPassMsg") != null) {%>
                            <div class="alert alert-danger small"><%= request.getAttribute("errorPassMsg")%></div>
                            <% }%>

                            <form action="${pageContext.request.contextPath}/profile?action=updatePassword" method="POST">
                                <div class="mb-3">
                                    <label class="form-label small fw-bold">Mật khẩu hiện tại</label>
                                    <input type="password" name="oldPassword" class="form-control" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label small fw-bold">Mật khẩu mới</label>
                                    <input type="password" name="newPassword" class="form-control" required minlength="6">
                                </div>
                                <div class="mb-4">
                                    <label class="form-label small fw-bold">Xác nhận mật khẩu mới</label>
                                    <input type="password" name="confirmPassword" class="form-control" required minlength="6">
                                </div>
                                <div class="text-end">
                                    <button type="submit" class="btn btn-danger px-4 fw-bold">Cập nhật mật khẩu</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/include/footer.jsp" />