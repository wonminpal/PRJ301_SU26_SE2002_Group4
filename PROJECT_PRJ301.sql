-- Tạo Database
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
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    parent_id INT FOREIGN KEY REFERENCES Categories(id) NULL
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
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Product_Variants (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES Products(id) ON DELETE CASCADE,
    sku VARCHAR(50) UNIQUE, -- Mã định danh quản lý kho (Ví dụ: IP15PM-256GB-NATURAL)
    color NVARCHAR(50),
    storage_capacity VARCHAR(50), -- Ví dụ: 128GB, 256GB, 1TB (hoặc RAM/SSD cho Laptop)
    price DECIMAL(18,2) NOT NULL, -- Giá riêng cho từng biến thể
    stock_quantity INT DEFAULT 0, -- Kho riêng cho từng biến thể
    variant_image VARCHAR(500)    -- Ảnh riêng cho biến thể màu đó
);

CREATE TABLE Product_Images (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES Products(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    is_thumbnail BIT DEFAULT 0 -- 1: Ảnh đại diện hiển thị ở trang danh sách, 0: Ảnh trong bộ sưu tập
);

CREATE TABLE Product_Specifications (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES Products(id) ON DELETE CASCADE,
    spec_key NVARCHAR(100) NOT NULL,   -- Ví dụ: Kích thước màn hình, Dung lượng Pin, Hệ điều hành
    spec_value NVARCHAR(500) NOT NULL  -- Ví dụ: 6.7 inch, 4441 mAh, iOS 17
);

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