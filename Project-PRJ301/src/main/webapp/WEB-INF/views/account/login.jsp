<%-- 
    Document   : login
    Created on : Jun 22, 2026, 11:19:30 PM
    Author     : Nguyen Minh Phat - CE201621
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/view/include/header.jsp" />

<div class="container my-5" style="min-height: 65vh;">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow border-0">
                <div class="card-header bg-dark text-white text-center py-3">
                    <h4 class="mb-0 fw-bold">ĐĂNG NHẬP</h4>
                </div>
                <div class="card-body p-4">
                    <% if (request.getParameter("success") != null) { %>
                    <div class="alert alert-success small">Đăng ký thành công! Vui lòng đăng nhập.</div>
                    <% } %>
                    <% if (request.getAttribute("errorMessage") != null) {%>
                    <div class="alert alert-danger small"><%= request.getAttribute("errorMessage")%></div>
                    <% }%>

                    <form action="${pageContext.request.contextPath}/auth" method="POST">
                        <input type="hidden" name="action" value="signin">
                        <div class="mb-3">
                            <label class="form-label fw-bold small">Email</label>
                            <input type="email" name="email" class="form-control" required>
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-bold small">Mật khẩu</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>

                        <div class="g-recaptcha mb-3" data-sitekey="6LfN6DctAAAAAOItk7tox7vwWdgWcMXp5VUM17nT"></div>

                        <button type="submit" class="btn btn-dark w-100 fw-bold">Đăng nhập</button>

                        <div class="text-center mt-3 small">
                            Chưa có tài khoản? <a href="${pageContext.request.contextPath}/auth?action=registerForm" class="text-decoration-none fw-bold text-dark">Đăng ký ngay</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/include/footer.jsp" />
