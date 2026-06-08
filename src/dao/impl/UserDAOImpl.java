package dao.impl;

import dao.IUserDAO;
import entity.Student;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAOImpl implements IUserDAO {

    @Override
    public Student findByEmail(String email) {
        Student user = null;
        String sql = "SELECT * FROM student WHERE email = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email); // Truyền email vào dấu ? chống SQL Injection
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    user = new Student();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setDob(rs.getDate("dob"));
                    user.setEmail(rs.getString("email"));
                    user.setSex(rs.getInt("sex"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    user.setPassword(rs.getString("password"));
                    user.setCreateAt(rs.getDate("create_at"));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm User: " + e.getMessage());
        } finally {
            // Luôn nhớ đóng kết nối
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return user; // Trả về đối tượng Student nếu thấy, không thấy thì trả về null
    }

    @Override
    public boolean registerStudent(Student student) {
        String sql = "INSERT INTO student (name, dob, email, sex, phone, password) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, student.getName());
                pstmt.setDate(2, (Date) student.getDob()); // Lưu ý: Ngày sinh phải là java.sql.Date
                pstmt.setString(3, student.getEmail());
                pstmt.setInt(4, student.getSex());
                pstmt.setString(5, student.getPhone());
                pstmt.setString(6, student.getPassword()); // Mật khẩu lúc này đã được băm từ tầng Business

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi đăng ký tài khoản: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }
}