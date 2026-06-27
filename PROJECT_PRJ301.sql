USE master;
GO

-- =============================================
-- 1. ÉP ĐÓNG KẾT NỐI VÀ XÓA DATABASE CŨ (TRÁNH LỖI)
-- =============================================
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'PROJECT_PRJ301')
BEGIN
    ALTER DATABASE PROJECT_PRJ301 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE PROJECT_PRJ301;
END
GO

-- =============================================
-- 2. TẠO LẠI DATABASE
-- =============================================
CREATE DATABASE PROJECT_PRJ301;
GO
USE PROJECT_PRJ301;
GO

-- =============================================
-- 3. TẠO CẤU TRÚC CÁC BẢNG (Chuẩn MVC & Khóa Ngoại)
-- =============================================
CREATE TABLE Users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    address NVARCHAR(255),
    role INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    slug VARCHAR(100) UNIQUE, 
    parent_id INT NULL FOREIGN KEY REFERENCES Categories(id),
    status BIT DEFAULT 1
);

CREATE TABLE Products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    category_id INT FOREIGN KEY REFERENCES Categories(id),
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    price DECIMAL(18,2) NOT NULL,
    brand NVARCHAR(100),
    image_url VARCHAR(500),
    stock_quantity INT DEFAULT 0,
    status INT DEFAULT 1, 
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Product_Variants (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES Products(id) ON DELETE CASCADE,
    sku VARCHAR(50),
    color NVARCHAR(50),
    storage_capacity NVARCHAR(50),
    price DECIMAL(18,2),
    stock_quantity INT,
    variant_image VARCHAR(500)
);

CREATE TABLE Product_Images (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES Products(id) ON DELETE CASCADE,
    image_url VARCHAR(500),
    is_thumbnail BIT DEFAULT 0
);

CREATE TABLE Carts (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(id), -- Bỏ UNIQUE để khách vãng lai không bị lỗi
    guest_token VARCHAR(100),
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Cart_Items (
    id INT IDENTITY(1,1) PRIMARY KEY,
    cart_id INT FOREIGN KEY REFERENCES Carts(id),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    quantity INT NOT NULL DEFAULT 1,
    variant NVARCHAR(255)
);

CREATE TABLE Vouchers (
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percent INT NOT NULL,
    max_discount DECIMAL(18,2),
    min_order_value DECIMAL(18,2),
    expiry_date DATETIME,
    usage_limit INT DEFAULT 100,
    used_count INT DEFAULT 0
);

CREATE TABLE Orders (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(id),
    total_amount DECIMAL(18,2) NOT NULL,
    status NVARCHAR(50) DEFAULT N'Chờ xác nhận',
    shipping_address NVARCHAR(500) NOT NULL,
    shipping_phone VARCHAR(15) NOT NULL,
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Order_Details (
    order_id INT FOREIGN KEY REFERENCES Orders(id),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    variant NVARCHAR(255),
    PRIMARY KEY (order_id, product_id)
);
GO

-- =============================================
-- 4. CHÈN DỮ LIỆU MẪU (SEED DATA)
-- =============================================
-- Tài khoản mẫu (Mật khẩu: 123456)
INSERT INTO Users (full_name, email, password, phone, address, role)
VALUES (N'Lê Nguyễn Thành Tài', 'tai@fpt.edu.vn', 'e10adc3949ba59abbe56e057f20f883e', '0909123456', N'Cần Thơ', 0);

-- Danh mục sản phẩm
INSERT INTO Categories (name, slug) VALUES 
(N'Điện thoại', 'dien-thoai'), 
(N'Laptop', 'laptop');

-- Danh sách 10 Sản phẩm (Đã sử dụng link Google để không bị chặn ảnh)
INSERT INTO Products (category_id, name, description, price, image_url, stock_quantity, status) VALUES
(1, N'iPhone 15 Pro', N'Titan tự nhiên, 128GB', 25000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSO5SEZJ_M3jJxyGTAypI_y4QUP6sK86WcjY4DTim5h-A&s=10', 20, 1),
(1, N'Samsung Galaxy S24 Ultra', N'AI phone, 256GB', 28000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRPPGbkp9j0vJUaWBb-GMgbOjTbyupHOvlUPQR1nJ7SLQ&s=10', 15, 1),
(1, N'Xiaomi 14', N'Camera Leica, 512GB', 19000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS-XKPnmwnUN6Pa0O68w61VIZN2cKP_kgG628WW8EhdIQ&s=10', 10, 1),
(1, N'iPhone 13', N'Vẫn rất tốt trong tầm giá', 14000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRoh3yt4lkcmClQZ2pqUIopBfOy1VmwQAZSxHRpr_rU4Q&s', 30, 1),
(1, N'Oppo Reno11 5G', N'Chụp ảnh chân dung đẹp', 9000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQI2tbR31lnnHiPN8s_h5OjK60MndNQcV_HqV8W-9Tslg&s=10', 25, 1),
(2, N'MacBook Air M2', N'Chip Apple M2, 8GB RAM', 22000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0uwNWfYQ1fP6W9vVUZZC4J_g1kIcRJWxPAlTHniBp3g&s=10', 10, 1),
(2, N'Laptop Asus Vivobook', N'Core i5, 8GB RAM', 15000000, 'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/asus_gaming_vivobook_k3605_black_1_6dec3a2e8f.png', 20, 1),
(2, N'Laptop Acer Aspire 7', N'Gaming tầm trung', 18000000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_d_i_1__4_8.png', 12, 1),
(2, N'Laptop HP Pavilion 15', N'Thiết kế sang trọng', 16000000, 'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_8_28_638288277858898172_hp-pavilion-15-eg3111tu-i5-1335u-bac-5.jpg', 15, 1),
(2, N'Laptop Lenovo Ideapad', N'Cấu hình ổn định', 13000000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ssss_1__1_113_1.png', 18, 1);
GO

-- =============================================
-- 5. TỰ ĐỘNG KHỞI TẠO BIẾN THỂ & HÌNH ẢNH 
-- =============================================
-- Tự động chép link ảnh từ bảng Products sang Product_Images cho TOÀN BỘ sản phẩm
INSERT INTO Product_Images (product_id, image_url, is_thumbnail)
SELECT id, image_url, 1 
FROM Products;

-- Tự động tạo 1 biến thể (Mặc định) lấy theo giá trị từ bảng Products cho TOÀN BỘ sản phẩm
INSERT INTO Product_Variants (product_id, sku, color, storage_capacity, price, stock_quantity)
SELECT 
    id, 
    'SKU-' + CAST(id AS VARCHAR), 
    N'Mặc định', 
    'Tiêu chuẩn', 
    price, 
    stock_quantity 
FROM Products;
GO