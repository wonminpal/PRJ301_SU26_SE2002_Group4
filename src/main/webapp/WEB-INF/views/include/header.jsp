<%-- 
    Document   : header
    Created on : Jun 20, 2026, 12:15:49 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Bán hàng điện tử - PRJ301</title>
        <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>

        <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4 shadow-sm">
            <div class="container">
                <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home">PRJ301_Store</a>

                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav me-auto">
                        <li class="nav-item">
                            <a class="nav-link active" href="${pageContext.request.contextPath}/home">Trang Chủ</a>
                        </li>
                    </ul>
                    <form class="d-flex mx-auto" action="${pageContext.request.contextPath}/home" method="get" style="width: 400px;">
                        <input class="form-control me-2" type="search" name="keyword" placeholder="Tìm tên sản phẩm..." value="${keyword}">
                        <button class="btn btn-outline-warning text-dark fw-bold" type="submit">Tìm</button>
                    </form>

                    <ul class="navbar-nav">
                        <li class="nav-item me-3">
                            <a class="nav-link text-warning fw-bold" href="${pageContext.request.contextPath}/cart">
                                🛒 Giỏ hàng
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/order">Lịch sử đơn</a>
                        </li>

                        <c:choose>
                            <%-- Nếu ĐÃ ĐĂNG NHẬP (Session 'account' có tồn tại) --%>
                            <c:when test="${not empty sessionScope.account}">
                                <li class="nav-item ms-2">
                                    <a class="nav-link text-info fw-bold" href="${pageContext.request.contextPath}/profile">
                                        👤 Chào, ${sessionScope.account.fullName}
                                    </a>
                                </li>
                                <%-- Nút Đăng xuất --%>
                                <li class="nav-item ms-2">
                                    <a class="btn btn-danger btn-sm mt-1 fw-bold" href="${pageContext.request.contextPath}/auth?action=logout">Đăng xuất</a>
                                </li>
                            </c:when>

                            <%-- Nếu CHƯA ĐĂNG NHẬP --%>
                            <c:otherwise>
                                <li class="nav-item ms-2">
                                    <a class="btn btn-outline-light btn-sm mt-1 fw-bold" href="${pageContext.request.contextPath}/auth?action=signinForm">Đăng nhập</a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>
        </nav>
