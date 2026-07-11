USE master;
GO

-- =============================================
-- 1. ÉP ĐÓNG KẾT NỐI VÀ XÓA DATABASE CŨ
-- =============================================
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'PROJECT_PRJ301')
BEGIN
    ALTER DATABASE PROJECT_PRJ301 SET SINGLE_USER WITH ROLLBACK IMMEDIATE; 
    DROP DATABASE PROJECT_PRJ301;
END
GO

-- =============================================
-- 2. TẠO LẠI DATABASE MỚI
-- =============================================
CREATE DATABASE PROJECT_PRJ301;
GO

USE PROJECT_PRJ301;
GO

-- =============================================
-- 3. TẠO CẤU TRÚC CÁC BẢNG (CHUẨN MVC & KHÓA NGOẠI)
-- =============================================

-- MODULE 1: TÀI KHOẢN (Nguyễn Minh Phát)
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

-- MODULE 2: DANH MỤC & SẢN PHẨM (Nguyễn Trần Khả Nhân)
CREATE TABLE Categories (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    name NVARCHAR(100) NOT NULL, 
    description NVARCHAR(500), 
    parent_id INT FOREIGN KEY REFERENCES Categories(id) NULL, 
    slug VARCHAR(150) UNIQUE, 
    status BIT DEFAULT 1 -- 1: Hoạt động, 0: Khóa 
);

CREATE TABLE Products (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    category_id INT FOREIGN KEY REFERENCES Categories(id), 
    name NVARCHAR(200) NOT NULL, 
    description NVARCHAR(MAX), 
    price DECIMAL(18,2) NOT NULL, 
    brand NVARCHAR(100), 
    image_url VARCHAR(500), 
    slug VARCHAR(250) UNIQUE, 
    stock_quantity INT DEFAULT 0, 
    status INT DEFAULT 1, -- 1: Đang bán, 0: Ngừng kinh doanh, 2: Bản nháp 
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

-- MODULE 3: GIỎ HÀNG (Lê Nguyễn Thành Tài)
CREATE TABLE Carts (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    user_id INT FOREIGN KEY REFERENCES Users(id) UNIQUE, -- Mỗi user 1 giỏ hàng duy nhất 
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

-- MODULE 4: VOUCHER (Trương Anh Tuấn)
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

-- MODULE 5: ĐƠN HÀNG (Đỗ Trần Thiên Phúc)
CREATE TABLE Orders (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    user_id INT FOREIGN KEY REFERENCES Users(id), 
    voucher_id INT FOREIGN KEY REFERENCES Vouchers(id) NULL, 
    total_amount DECIMAL(18,2) NOT NULL, 
    final_amount DECIMAL(18,2) NOT NULL, 
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

-- MODULE 6: ĐÁNH GIÁ (Lương Trung Hiếu)
CREATE TABLE Reviews (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    user_id INT FOREIGN KEY REFERENCES Users(id), 
    product_id INT FOREIGN KEY REFERENCES Products(id), 
    rating INT CHECK (rating >= 1 AND rating <= 5), 
    comment NVARCHAR(MAX), 
    created_at DATETIME DEFAULT GETDATE() 
);

-- TẠO INDEX TỐI ƯU TRUY VẤN
CREATE INDEX idx_products_slug ON Products(slug); 
CREATE INDEX idx_categories_slug ON Categories(slug); 
CREATE INDEX idx_variants_price ON Product_Variants(price); 
GO

-- =============================================
-- 4. CHÈN DỮ LIỆU MẪU (SEED DATA)
-- =============================================

-- Tài khoản mẫu (Mật khẩu: 123456)
INSERT INTO Users (full_name, email, password, phone, address, role)
VALUES (N'Lê Nguyễn Thành Tài', 'tai@fpt.edu.vn', 'e10adc3949ba59abbe56e057f20f883e', '0909123456', N'Cần Thơ', 0); 

-- Danh mục Gốc (Level 1) & Con (Level 2)
INSERT INTO Categories (name, description, parent_id, slug, status) VALUES 
(N'Điện thoại', N'Smartphone chính hãng', NULL, 'dien-thoai', 1),    -- ID 1 
(N'Laptop', N'Máy tính xách tay', NULL, 'laptop', 1),                -- ID 2 
(N'Tablet', N'Máy tính bảng', NULL, 'tablet', 1),                    -- ID 3 
(N'Apple (iPhone)', N'Điện thoại iPhone', 1, 'apple-iphone', 1),     -- ID 4 
(N'Samsung', N'Điện thoại Samsung', 1, 'samsung-phone', 1),          -- ID 5 
(N'Laptop Gaming', N'Laptop cấu hình mạnh', 2, 'laptop-gaming', 1),  -- ID 6 
(N'MacBook', N'Máy tính Apple', 2, 'macbook', 1);                    -- ID 7 

-- Danh sách Sản phẩm mẫu
INSERT INTO Products (category_id, name, brand, description, price, image_url, slug, stock_quantity, status) VALUES
(4, N'iPhone 15 Pro Max', 'Apple', N'Thiết kế Titan tự nhiên siêu nhẹ, chip A17 Pro mạnh mẽ.', 25000000, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2023/9/13/638302146950240417_iphone-15-pro-max-titan-tu-nhien-1.jpg', 'iphone-15-pro-max', 20, 1), 
(4, N'iPhone 14', 'Apple', N'Cấu hình ổn định với chip A15 Bionic, thiết kế nhỏ gọn.', 14000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRoh3yt4lkcmClQZ2pqUIopBfOy1VmwQAZSxHRpr_rU4Q&s', 'iphone-14', 30, 1), 
(5, N'Samsung Galaxy S24 Ultra 5G', 'Samsung', N'Tích hợp Galaxy AI, khung viền Titan, camera 200MP.', 28000000, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2024/1/18/638411545624794697_samsung-galaxy-s24-ultra-xam-1.jpg', 'samsung-galaxy-s24-ultra', 15, 1), 
(6, N'Acer Nitro 5 Tiger', 'Acer', N'Intel Core i5 12500H, RTX 3050Ti, Màn hình 144Hz.', 18000000, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2023/6/5/638215682852230553_acer-nitro-gaming-an515-58-den-1.jpg', 'acer-nitro-5-tiger', 12, 1), 
(7, N'MacBook Air M3 2024', 'Apple', N'Chip Apple M3 mới nhất, siêu mỏng nhẹ, pin 18 tiếng.', 22000000, 'https://images.fpt.shop/unsafe/fit-in/240x240/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2024/3/5/638452445885061611_macbook-air-m3-13-inch-xanh-den-1.jpg', 'macbook-air-m3-2024', 10, 1); 
GO

-- Tự động đồng bộ Hình ảnh & Biến thể ban đầu cho tất cả Sản phẩm
INSERT INTO Product_Images (product_id, image_url, is_thumbnail)
SELECT id, image_url, 1 FROM Products; 

INSERT INTO Product_Variants (product_id, sku, color, storage_capacity, price, stock_quantity, variant_image)
SELECT id, 'SKU-' + CAST(id AS VARCHAR), N'Mặc định', 'Tiêu chuẩn', price, stock_quantity, image_url FROM Products; 
GO

DELETE FROM Product_Images WHERE product_id = 1;
DELETE FROM Product_Variants WHERE product_id = 1;
GO



INSERT INTO Product_Images (product_id, image_url, is_thumbnail) VALUES 
(1, 'assets/images/products/iphone-15-promax-den-main.webp', 1),
(1, 'assets/images/products/iphone-15-promax-den-nghieng.webp', 0),
(1, 'assets/images/products/iphone-15-promax-den-doc.webp', 0),
(1, 'assets/images/products/iphone-15-promax-den-cam.webp', 0);
GO

INSERT INTO Product_Variants (product_id, sku, color, storage_capacity, price, stock_quantity, variant_image) VALUES 
-- Màu Đen
(1, 'IP15PM-256-DEN', N'Titan Đen', '256GB', 28000000, 10, 'assets/images/products/iphone-15-pro-max/iphone-15-promax-den-main.webp'),
(1, 'IP15PM-512-DEN', N'Titan Đen', '512GB', 32000000, 5, 'assets/images/products/iphone-15-pro-max/iphone-15-promax-den-main.webp'),
(1, 'IP15PM-1TB-DEN', N'Titan Đen', '1TB', 36000000, 2, 'assets/images/products/iphone-15-pro-max/iphone-15-promax-den-main.webp'),

-- Màu Trắng
(1, 'IP15PM-256-TRANG', N'Titan Trắng', '256GB', 28000000, 10, 'assets/images/products/iphone-15-pro-max/iphone-15-promax-trang-main.webp'),
(1, 'IP15PM-512-TRANG', N'Titan Trắng', '512GB', 32000000, 8, 'assets/images/products/iphone-15-pro-max/iphone-15-promax-trang-main.webp'),
(1, 'IP15PM-1TB-TRANG', N'Titan Trắng', '1TB', 36000000, 3, 'assets/images/products/iphone-15-pro-max/iphone-15-promax-trang-main.webp');
GO

SELECT * from Product_Variants

INSERT INTO Vouchers (code, discount_percent, max_discount, min_order_value, expiry_date, usage_limit, used_count)
VALUES 
('CUUTUIROIMON', 15, 300000, 0, '2026-12-31 23:59:59', 99, 0),
('FEMBOY', 20, 500000, 0, '2026-12-31 23:59:59', 50, 0),
('GROUP410DIEM', 99, 9999999, 999000000, '2026-12-31 23:59:59', 1, 0),
('NLOVEP', 10, 100000, 0, '2026-12-31 23:59:59', 100, 0),
('NGHEO_CONGAT', 10, 100000, 0, '2026-12-31 23:59:59', 100, 0),
('BIGSALE30', 30, 1500000, 25000000, '2026-12-31 23:59:59', 20, 0),
('WELCOME5', 5, 100000, 0, '2026-12-31 23:59:59', 500, 0),
('SOLDOUT', 10, 200000, 0, '2026-12-31 23:59:59', 10, 10),
('EXPIRED50', 50, 2000000, 0, '2026-01-01 00:00:00', 50, 0);
GO