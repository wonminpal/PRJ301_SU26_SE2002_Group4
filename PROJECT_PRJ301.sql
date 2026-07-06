USE master;
GO

-- =============================================
-- 1. ÉP ĐÓNG KẾT NỐI VÀ XÓA DATABASE CŨ (TRÁNH LỖI)
-- =============================================
-- Kiểm tra nếu DB tồn tại thì ép đóng mọi kết nối và xóa
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

-- =============================================
-- 3. TẠO CẤU TRÚC CÁC BẢNG (Chuẩn MVC & Khóa Ngoại)
-- =============================================

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
    price DECIMAL(18,2) NOT NULL,
    brand NVARCHAR(100),
    image_url VARCHAR(500),
    stock_quantity INT DEFAULT 0,
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
    variant NVARCHAR(255),
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
GO

-- =============================================
-- 4. CHÈN DỮ LIỆU MẪU (SEED DATA - ĐÃ CHUẨN HÓA THEO DANH MỤC UI)
-- =============================================

-- 4.1 Tài khoản mẫu (Mật khẩu: 123456)
INSERT INTO Users (full_name, email, password, phone, address, role)
VALUES (N'Lê Nguyễn Thành Tài', 'tai@fpt.edu.vn', 'e10adc3949ba59abbe56e057f20f883e', '0909123456', N'Cần Thơ', 0);

-- 4.2 Danh mục Gốc (Level 1 - Chuẩn 100% khớp với href trên header)
-- Bỏ danh mục con, gom hết về 1 cấp để lọc cho chuẩn
INSERT INTO Categories (name, description, slug, status) VALUES 
(N'Điện thoại', N'Smartphone các hãng', 'dien-thoai', 1),    -- ID 1
(N'Laptop', N'Máy tính xách tay', 'laptop', 1),                -- ID 2
(N'Máy tính bảng', N'iPad, Android Tablet', 'tablet', 1),      -- ID 3
(N'Phụ kiện', N'Tai nghe, sạc dự phòng, ốp lưng', 'phu-kien', 1), -- ID 4
(N'Tivi', N'Smart Tivi 4K, OLED', 'tivi', 1);                  -- ID 5

-- 4.3 Danh sách Sản phẩm (Đã nhét đúng vào từng Category)
INSERT INTO Products (category_id, name, brand, description, slug, price, image_url, stock_quantity, status) VALUES 
-- ================= NHÓM 1: ĐIỆN THOẠI (Apple, Samsung, Xiaomi...) =================
(1, N'iPhone 15 Pro Max', 'Apple', N'Thiết kế Titan tự nhiên siêu nhẹ, chip A17 Pro mạnh mẽ.', 'iphone-15-pro-max', 29000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSO5SEZJ_M3jJxyGTAypI_y4QUP6sK86WcjY4DTim5h-A&s=10', 20, 1),
(1, N'iPhone 14', 'Apple', N'Cấu hình ổn định với chip A15 Bionic, thiết kế nhỏ gọn.', 'iphone-14', 18000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRoh3yt4lkcmClQZ2pqUIopBfOy1VmwQAZSxHRpr_rU4Q&s', 15, 1),
(1, N'Samsung Galaxy S24 Ultra 5G', 'Samsung', N'Tích hợp Galaxy AI, khung viền Titan, bút S-Pen.', 'samsung-galaxy-s24-ultra', 28000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRPPGbkp9j0vJUaWBb-GMgbOjTbyupHOvlUPQR1nJ7SLQ&s=10', 15, 1),
(1, N'Xiaomi 14', 'Xiaomi', N'Camera Leica đỉnh cao, thiết kế viền mỏng.', 'xiaomi-14', 19000000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-14-pre-den.png', 10, 1),

-- ================= NHÓM 2: LAPTOP (MacBook, Acer, Asus...) =================
(2, N'MacBook Air M3 2024', 'Apple', N'Chip Apple M3 mới nhất, siêu mỏng nhẹ, pin 18 tiếng.', 'macbook-air-m3-2024', 27000000, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0uwNWfYQ1fP6W9vVUZZC4J_g1kIcRJWxPAlTHniBp3g&s=10', 10, 1),
(2, N'Acer Nitro 5 Tiger', 'Acer', N'Intel Core i5 12500H, RTX 3050Ti, Màn hình 144Hz.', 'acer-nitro-5-tiger', 18000000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_d_i_1__4_8.png', 12, 1),
(2, N'Laptop Asus Vivobook', 'Asus', N'Core i5, 8GB RAM, mỏng nhẹ phù hợp văn phòng.', 'asus-vivobook', 15000000, 'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/asus_gaming_vivobook_k3605_black_1_6dec3a2e8f.png', 20, 1),

-- ================= NHÓM 3: MÁY TÍNH BẢNG (MỚI) =================
(3, N'iPad Pro M4 11-inch (2024)', 'Apple', N'Chip M4 siêu mạnh, màn hình OLED rực rỡ, thiết kế cực mỏng.', 'ipad-pro-m4-11', 28500000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/f/r/frame_100_1_2__2_2.png', 10, 1),
(3, N'Samsung Galaxy Tab S9', 'Samsung', N'Chống nước IP68, màn hình Dynamic AMOLED 2X, kèm S-Pen.', 'galaxy-tab-s9', 19500000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-tab-s9_1_.png', 15, 1),

-- ================= NHÓM 4: PHỤ KIỆN (MỚI) =================
(4, N'Tai nghe không dây Baseus Bowie WM01', 'Baseus', N'Âm thanh sắc nét, pin dùng liên tục 5 giờ, thiết kế nhỏ gọn nhẹ tai.', 'baseus-bowie-wm01', 350000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/g/r/group_161_2.png', 50, 1),
(4, N'AirPods Pro 2', 'Apple', N'Chống ồn chủ động xuất sắc, âm thanh không gian 3D.', 'airpods-pro-2', 5800000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/a/p/apple-airpods-pro-2-usb-c_1_.png', 30, 1),

-- ================= NHÓM 5: TIVI (MỚI) =================
(5, N'Smart Tivi Samsung 4K 65 inch', 'Samsung', N'Độ phân giải 4K sắc nét, công nghệ màu PurColor rực rỡ.', 'tivi-samsung-4k-65', 12900000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/m/smart-tivi-samsung-qled-65q6fa-4k-65-inch.png', 8, 1),
(5, N'Smart Tivi LG 4K 55 inch', 'LG', N'Công nghệ AI ThinQ, chuột bay Magic Remote siêu tiện lợi.', 'tivi-lg-4k-55', 10500000, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/m/smart-tivi-lg-uhd-55ua7350-4k-55-inch-2025_1_.png', 5, 1);
GO

-- =============================================
-- 5. TỰ ĐỘNG KHỞI TẠO BIẾN THỂ & HÌNH ẢNH 
-- =============================================
INSERT INTO Product_Images (product_id, image_url, is_thumbnail)
SELECT id, image_url, 1 FROM Products;

INSERT INTO Product_Variants (product_id, sku, color, storage_capacity, price, stock_quantity)
SELECT 
    id, 'SKU-' + CAST(id AS VARCHAR), N'Mặc định', 'Tiêu chuẩn', price, stock_quantity 
FROM Products;
GO