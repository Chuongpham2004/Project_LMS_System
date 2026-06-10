package utils;

import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        try {
            DBUtil.getConnection();
            System.out.println("✅ Kết nối đến cơ sở dữ liệu thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
