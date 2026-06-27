USE master;
GO

-- Kểm tra nếu DB tồn tại thì ép đóng mọi kết nối và xóa
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'PROJECT_PRJ301')
BEGIN
    ALTER DATABASE PROJECT_PRJ301 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE PROJECT_PRJ301;
END
GO

-- =======================================================
-- BƯỚC 2: TẠO LẠI DATABASE MỚI TINH
-- =======================================================
CREATE DATABASE PROJECT_PRJ301;
GO
USE PROJECT_PRJ301;
GO

-- ==========================================
-- 1. MODULE TÀI KHOẢN (Nguyễn Minh Phát)
-- ==========================================
CREATE TABLE Users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    address NVARCHAR(255),
    role INT DEFAULT 0, -- 0: Customer, 1: Admin
    created_at DATETIME DEFAULT GETDATE()
);
-- ==========================================
-- 2. MODULE SẢN PHẨM & DANH MỤC (Nguyễn Trần Khả Nhân)
-- ==========================================
CREATE TABLE Categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    [name] NVARCHAR(100) NOT NULL,
    [description] NVARCHAR(500),
    parent_id INT FOREIGN KEY REFERENCES Categories(id) NULL,
    slug VARCHAR(150) UNIQUE,
    status BIT DEFAULT 1 -- 1: Hoạt động (Hiển thị), 0: Khóa (Ẩn)
);

CREATE TABLE Products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    category_id INT FOREIGN KEY REFERENCES Categories(id),
    [name] NVARCHAR(200) NOT NULL,
    [description] NVARCHAR(MAX),
    brand NVARCHAR(100),
    slug VARCHAR(250) UNIQUE, 
    [status] INT DEFAULT 1, -- 1: Đang bán, 0: Ngừng kinh doanh, 2: Bản nháp
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Product_Variants (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES Products(id) ON DELETE CASCADE,
    sku VARCHAR(50) UNIQUE, 
    color NVARCHAR(50),
    storage_capacity VARCHAR(50), 
    price DECIMAL(18,2) NOT NULL, 
    stock_quantity INT DEFAULT 0, 
    variant_image VARCHAR(500)    
);

CREATE TABLE Product_Images (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES Products(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    is_thumbnail BIT DEFAULT 0 
);

CREATE TABLE Product_Specifications (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES Products(id) ON DELETE CASCADE,
    spec_key NVARCHAR(100) NOT NULL,   
    spec_value NVARCHAR(500) NOT NULL  
);

-- Tạo INDEX tối ưu truy vấn
CREATE INDEX idx_products_slug ON Products(slug);
CREATE INDEX idx_categories_slug ON Categories(slug);
CREATE INDEX idx_variants_price ON Product_Variants(price);

-- ==========================================
-- 3. MODULE GIỎ HÀNG (Lê Nguyễn Thành Tài)
-- ==========================================
-- Lưu ý: Giỏ hàng có thể lưu ở Session, nhưng dùng DB sẽ giữ được giỏ hàng khi user đăng nhập lại
CREATE TABLE Carts (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(id) UNIQUE, -- Mỗi user 1 giỏ hàng duy nhất
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Cart_Items (
    id INT IDENTITY(1,1) PRIMARY KEY,
    cart_id INT FOREIGN KEY REFERENCES Carts(id),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    quantity INT NOT NULL DEFAULT 1
);

-- ==========================================
-- 4. MODULE VOUCHER & TRẠNG THÁI (Trương Anh Tuấn)
-- ==========================================
CREATE TABLE Vouchers (
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percent INT NOT NULL, -- Ví dụ: 10, 15, 20 (%)
    max_discount DECIMAL(18,2),    -- Giảm tối đa bao nhiêu tiền
    min_order_value DECIMAL(18,2), -- Đơn tối thiểu để áp dụng
    expiry_date DATETIME,
    usage_limit INT DEFAULT 100,   -- Số lượt dùng tối đa
    used_count INT DEFAULT 0
);

-- ==========================================
-- 5. MODULE ĐƠN HÀNG (Đỗ Trần Thiên Phúc)
-- ==========================================
CREATE TABLE Orders (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(id),
    voucher_id INT FOREIGN KEY REFERENCES Vouchers(id) NULL, -- Có thể không xài mã
    total_amount DECIMAL(18,2) NOT NULL, -- Tổng tiền ban đầu
    final_amount DECIMAL(18,2) NOT NULL, -- Tiền sau khi áp voucher
    status NVARCHAR(50) DEFAULT N'Chờ xác nhận', -- Chờ xác nhận, Đang giao, Hoàn thành, Đã hủy
    shipping_address NVARCHAR(500) NOT NULL,
    shipping_phone VARCHAR(15) NOT NULL,
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Order_Details (
    order_id INT FOREIGN KEY REFERENCES Orders(id),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL, -- Lưu giá tại thời điểm mua (lỡ sau này sp tăng/giảm giá)
    PRIMARY KEY (order_id, product_id)
);

-- ==========================================
-- 6. MODULE ĐÁNH GIÁ (Lương Trung Hiếu)
-- ==========================================
CREATE TABLE Reviews (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(id),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);

-- Danh mục Gốc (Level 1)
INSERT INTO Categories (name, description, parent_id, slug, status) VALUES 
(N'Điện thoại', N'Smartphone chính hãng', NULL, 'dien-thoai', 1),    -- ID 1
(N'Laptop', N'Máy tính xách tay', NULL, 'laptop', 1),                -- ID 2
(N'Tablet', N'Máy tính bảng', NULL, 'tablet', 1);                    -- ID 3

-- Danh mục Con (Level 2)
INSERT INTO Categories (name, description, parent_id, slug, status) VALUES 
(N'Apple (iPhone)', N'Điện thoại iPhone', 1, 'apple-iphone', 1),     -- ID 4 (Con của 1)
(N'Samsung', N'Điện thoại Samsung', 1, 'samsung-phone', 1),          -- ID 5 (Con của 1)
(N'Laptop Gaming', N'Laptop cấu hình mạnh', 2, 'laptop-gaming', 1),  -- ID 6 (Con của 2)
(N'MacBook', N'Máy tính Apple', 2, 'macbook', 1);                    -- ID 7 (Con của 2)

INSERT INTO Products (category_id, name, brand, description, slug, status) VALUES 
-- Nhóm iPhone (Category ID = 4)
(4, N'iPhone 15 Pro Max', 'Apple', N'Thiết kế Titan tự nhiên siêu nhẹ, chip A17 Pro mạnh mẽ, camera zoom quang 5x.', 'iphone-15-pro-max', 1), -- ID 1
(4, N'iPhone 14', 'Apple', N'Cấu hình ổn định với chip A15 Bionic, thiết kế nhỏ gọn.', 'iphone-14', 1), -- ID 2

-- Nhóm Samsung (Category ID = 5)
(5, N'Samsung Galaxy S24 Ultra 5G', 'Samsung', N'Tích hợp Galaxy AI, khung viền Titan, camera 200MP, bút S-Pen.', 'samsung-galaxy-s24-ultra', 1), -- ID 3

-- Nhóm Laptop Gaming (Category ID = 6)
(6, N'Acer Nitro 5 Tiger', 'Acer', N'Intel Core i5 12500H, RTX 3050Ti, Màn hình 144Hz.', 'acer-nitro-5-tiger', 1), -- ID 4

-- Nhóm MacBook (Category ID = 7)
(7, N'MacBook Air M3 2024', 'Apple', N'Chip Apple M3 mới nhất, siêu mỏng nhẹ, pin 18 tiếng.', 'macbook-air-m3-2024', 1); -- ID 5

INSERT INTO Product_Images (product_id, image_url, is_thumbnail) VALUES 
-- Ảnh iPhone 15 Pro Max
(1, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2023/9/13/638302146950240417_iphone-15-pro-max-titan-tu-nhien-1.jpg', 1),
(1, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2023/9/13/638302146950240417_iphone-15-pro-max-titan-tu-nhien-2.jpg', 0),

-- Ảnh Samsung S24 Ultra
(3, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2024/1/18/638411545624794697_samsung-galaxy-s24-ultra-xam-1.jpg', 1),

-- Ảnh Laptop Acer
(4, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2023/6/5/638215682852230553_acer-nitro-gaming-an515-58-den-1.jpg', 1),

-- Ảnh MacBook Air M3
(5, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2024/3/5/638452445885061611_macbook-air-m3-13-inch-xanh-den-1.jpg', 1);

select p.*, c.name AS category_name
from Products p 
JOIN Categories c ON c.id = p.category_id
WHERE p.slug = ? AND p.status = 1

SELECT p.id, p.[name], p.slug, p.brand,
       (SELECT MIN(pr.price) FROM Product_Variants pr WHERE pr.product_id = p.id AND pr.stock_quantity > 0) as diisplay_price,
       (SELECT TOP 1 proImg.image_url FROM Product_Images proImg WHERE proImg.product_id = p.id AND proImg.is_thumbnail = 1) as dislpay_image
FROM Products p
JOIN Categories c ON p.category_id = c.id
WHERE p.[status] = 1 AND c.slug = ?
ORDER BY p.created_at DESC
OFFSET ? ROWS
FETCH NEXT ? ROWS ONLY

SELECT TOP (?) p.id, p.[name], p.brand, p.slug, p.category_id,
        (SELECT MIN(price) from Product_Variants pr WHERE pr.product_id = p.id AND pr.stock_quantity > 0),
        (SELECT TOP 1 proImg.image_url FROM Product_Images proImg WHERE proImg.product_id = p.id AND proImg.is_thumbnail = 1)
FROM Products p
JOIN Categories c ON p.category_id = c.id
WHERE p.id = ? AND p.category_id = ? AND p.[status] = 1
ORDER BY NEWID();



