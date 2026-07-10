<%-- 
    Document   : header
    Created on : Jun 20, 2026, 12:15:49 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>PRJ301 Store - Mua sắm an tâm</title>
        <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            /* CSS tùy chỉnh cho hiệu ứng hover sản phẩm */
            .product-card {
                transition: transform 0.3s ease, box-shadow 0.3s ease;
            }
            .product-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 10px 20px rgba(0,0,0,0.15) !important;
            }
            .category-pill {
                transition: 0.2s;
            }
            .category-pill:hover {
                background-color: #dc3545;
                color: white !important;
            }
        </style>
    </head>
    <body class="bg-light">

        <nav class="navbar navbar-expand-lg navbar-dark bg-danger mb-4 shadow">
            <div class="container">
                
                <a class="navbar-brand fw-bold fs-4" href="${pageContext.request.contextPath}/home">
                    <i class="fa-solid fa-shop me-2"></i>Trang Chủ
                </a>

                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav me-auto">
                        </ul>
                    
                    <form class="d-flex mx-auto position-relative" action="${pageContext.request.contextPath}/home" method="get" style="width: 450px;">
                        <input class="form-control rounded-pill pe-5" type="search" name="keyword" placeholder="Nhập tên điện thoại, laptop, phụ kiện..." value="${keyword}">
                        <button class="btn border-0 position-absolute end-0 top-50 translate-middle-y text-danger" type="submit">
                            <i class="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </form>

                    <ul class="navbar-nav align-items-center">
                        <li class="nav-item me-3">
                            <a class="btn btn-warning fw-bold text-dark rounded-pill px-3" href="${pageContext.request.contextPath}/cart">
                                <i class="fa-solid fa-cart-shopping"></i> Giỏ hàng
                            </a>
                        </li>
                        <li class="nav-item me-3">
                            <a class="nav-link text-white" href="${pageContext.request.contextPath}/order">
                                <i class="fa-solid fa-file-invoice"></i> Lịch sử đơn
                            </a>
                        </li>

                        <c:choose>
                            <%-- Nếu ĐÃ ĐĂNG NHẬP --%>
                            <c:when test="${not empty sessionScope.account}">
                                <li class="nav-item ms-2 dropdown">
                                    <a class="nav-link text-white fw-bold dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                                        <i class="fa-solid fa-circle-user fs-5 align-middle"></i> ${sessionScope.account.fullName}
                                    </a>
                                    <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">Thông tin tài khoản</a></li>
                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item text-danger fw-bold" href="${pageContext.request.contextPath}/auth?action=logout">Đăng xuất</a></li>
                                    </ul>
                                </li>
                            </c:when>

                            <%-- Nếu CHƯA ĐĂNG NHẬP --%>
                            <c:otherwise>
                                <li class="nav-item ms-2">
                                    <a class="btn btn-outline-light btn-sm fw-bold rounded-pill px-3" href="${pageContext.request.contextPath}/auth?action=signinForm">Đăng nhập</a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>
        </nav>