/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject; // Nhớ thêm thư viện Gson vào pom.xml hoặc thư mục lib
import com.google.gson.JsonParser;
import java.io.BufferedReader;

/**
 *
 * @author Nguyen Minh Phat - CE201621
 */
public class VerifyRecaptcha {

    // Thay YOUR_GOOGLE_SECRET_KEY bằng mã Secret Key thật của bạn
    public static final String SECRET_KEY = "6LfN6DctAAAAACbxlKQ0DM4oshwbGYVbwDb1sBQ_";

    public static boolean verify(String recaptchaResponse) {
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
            return false;
        }
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            String params = "secret=" + SECRET_KEY + "&response=" + recaptchaResponse;

            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream out = http.getOutputStream()) {
                out.write(params.getBytes("UTF-8"));
            }

            try (InputStream res = http.getInputStream(); BufferedReader rd = new BufferedReader(new InputStreamReader(res, "UTF-8"))) {

                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }

                // Trả về true nếu kết quả JSON chứa "success": true
                return sb.toString().contains("\"success\": true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
