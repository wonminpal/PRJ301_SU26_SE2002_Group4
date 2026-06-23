/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author Nguyen Minh Phat - CE201621
 */
public class UserDAO extends DBContext {

    private String hashMD5(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mess = md.digest(raw.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : mess) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public User checkLogin(String email, String password) {
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, hashMD5(password)); // So sánh bằng chuỗi đã băm

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"), rs.getString("full_name"), rs.getString("email"),
                        rs.getString("password"), rs.getString("phone"), rs.getString("address"), rs.getInt("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkEmailExist(String email) {
        String query = "SELECT id FROM Users WHERE email = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String fullName, String email, String password) {
        if (checkEmailExist(email)) {
            return false;
        }
        String query = "INSERT INTO Users (full_name, email, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, hashMD5(password)); // Lưu vào DB chuỗi đã băm
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM Users WHERE email = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"), rs.getString("full_name"), rs.getString("email"),
                        rs.getString("password"), rs.getString("phone"), rs.getString("address"), rs.getInt("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(String email, String fullName, String phone, String address) {
        String query = "UPDATE Users SET full_name = ?, phone = ?, address = ? WHERE email = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        User user = checkLogin(email, oldPassword);
        if (user == null) {
            return false;
        }

        String query = "UPDATE Users SET password = ? WHERE email = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, hashMD5(newPassword));
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
