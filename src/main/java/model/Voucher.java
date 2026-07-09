package model;

import java.sql.Date;

public class Voucher {
    private int id;
    private String code;
    private double discountPercent; // % giảm giá (ví dụ: 10, 20...)
    private int quantity;           // Số lượng mã còn lại
    private Date startDate;         // Ngày bắt đầu áp dụng
    private Date endDate;           // Ngày hết hạn

    // 1. Constructor không tham số
    public Voucher() {
    }

    // 2. Constructor dùng khi Admin tạo mới Voucher (không cần id vì DB tự tăng)
    public Voucher(String code, double discountPercent, int quantity, Date startDate, Date endDate) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // 3. Constructor đầy đủ tham số
    public Voucher(int id, String code, double discountPercent, int quantity, Date startDate, Date endDate) {
        this.id = id;
        this.code = code;
        this.discountPercent = discountPercent;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // 4. Các hàm Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", discountPercent=" + discountPercent +
                ", quantity=" + quantity +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}