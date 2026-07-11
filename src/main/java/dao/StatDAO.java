package dao;

import db.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatDAO extends DBContext {

    // 1. Thống kê doanh thu theo tháng / năm (ĐÃ ĐÚNG)
    public List<Map<String, Object>> getMonthlyRevenue() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT YEAR(created_at) AS yr, MONTH(created_at) AS mth, SUM(final_amount) AS total "
                   + "FROM Orders WHERE status = N'Hoàn thành' "
                   + "GROUP BY YEAR(created_at), MONTH(created_at) "
                   + "ORDER BY yr DESC, mth DESC";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("month", rs.getInt("mth"));
                map.put("year", rs.getInt("yr"));
                map.put("revenue", rs.getDouble("total"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thống kê Top sản phẩm bán chạy nhất (ĐÃ SỬA TÊN BẢNG THÀNH Order_Details)
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        // Sửa OrderDetails thành Order_Details ở dòng dưới đây
        String sql = "SELECT TOP (?) p.id, p.name, SUM(od.quantity) AS total_qty "
                   + "FROM Order_Details od "
                   + "JOIN Products p ON od.product_id = p.id "
                   + "JOIN Orders o ON od.order_id = o.id "
                   + "WHERE o.status = N'Hoàn thành' "
                   + "GROUP BY p.id, p.name "
                   + "ORDER BY total_qty DESC";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("name", rs.getString("name"));
                map.put("totalSold", rs.getInt("total_qty"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}