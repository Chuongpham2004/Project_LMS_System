package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // Khối static này sẽ chạy ngay khi class DBUtil được gọi lần đầu tiên
    static {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("database.properties")) {
            properties.load(fis);
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("❌ Lỗi: Không tìm thấy file database.properties hoặc không thể đọc file!");
            System.err.println("💡 Mẹo: Hãy copy file database.properties.example thành database.properties và cấu hình lại.");
            e.printStackTrace();
        }
    }

    /**
     * Khởi tạo và trả về một Connection tới PostgreSQL.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Hàm dùng để test kết nối ngay khi khởi động Main.java
     */
    public static void testConnection() {
        System.out.println("Đang kiểm tra kết nối cơ sở dữ liệu...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Kết nối PostgreSQL thành công!");
            } else {
                System.err.println("❌ Kết nối thất bại. Vui lòng kiểm tra lại cấu hình trong file database.properties.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Xảy ra lỗi trong quá trình kiểm tra kết nối: " + e.getMessage());
        }
    }

    /**
     * Tiện ích đóng các tài nguyên JDBC
     */
    public static void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) rs.close();
            if (pstmt != null && !pstmt.isClosed()) pstmt.close();
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đóng tài nguyên database: " + e.getMessage());
        }
    }
}