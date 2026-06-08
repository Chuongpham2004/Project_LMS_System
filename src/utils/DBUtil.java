package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/student_course_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "200426";

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
     * Hàm dùng để test kết nối ngay khi khởi động Main.java (Buổi 1)
     */
    public static void testConnection() {
        System.out.println("Đang kiểm tra kết nối cơ sở dữ liệu...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Kết nối PostgreSQL thành công!");
            } else {
                System.err.println("❌ Kết nối thất bại. Vui lòng kiểm tra lại URL, USER hoặc PASSWORD.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Xảy ra lỗi trong quá trình kiểm tra kết nối: " + e.getMessage());
        }
    }

    /**
     * Tiện ích đóng các tài nguyên JDBC để tránh rò rỉ bộ nhớ (Memory Leak)
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