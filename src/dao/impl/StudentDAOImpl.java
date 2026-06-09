package dao.impl;

import dao.IStudentDAO;
import entity.Student;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements IStudentDAO {

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        // Đã gài thêm điều kiện is_deleted = false để ẩn các tài khoản đã xóa
        String sql = "SELECT * FROM student WHERE role = 'STUDENT' AND is_deleted = false ORDER BY id ASC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy danh sách học viên: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return students;
    }

    @Override
    public Student findById(int id) {
        // Đã gài thêm điều kiện is_deleted = false
        String sql = "SELECT * FROM student WHERE id = ? AND role = 'STUDENT' AND is_deleted = false";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm kiếm học viên theo ID: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public Student findByEmail(String email) {
        // Đã gài thêm is_deleted = false để người bị xóa không thể đăng nhập
        String sql = "SELECT * FROM student WHERE email = ? AND is_deleted = false";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm kiếm học viên theo Email: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public boolean insert(Student student) {
        String sql = "INSERT INTO student (name, dob, email, sex, phone, password, role) VALUES (?, ?, ?, ?, ?, ?, 'STUDENT')";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, student.getName());
                pstmt.setDate(2, (Date) student.getDob());
                pstmt.setString(3, student.getEmail());
                pstmt.setInt(4, student.getSex());
                pstmt.setString(5, student.getPhone());
                pstmt.setString(6, student.getPassword());

                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi thêm mới học viên: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE student SET name = ?, dob = ?, email = ?, sex = ?, phone = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, student.getName());
                pstmt.setDate(2, (Date) student.getDob());
                pstmt.setString(3, student.getEmail());
                pstmt.setInt(4, student.getSex());
                pstmt.setString(5, student.getPhone());
                pstmt.setInt(6, student.getId());

                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi cập nhật học viên: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        // Thực thi Soft Delete: Cập nhật cờ is_deleted và lưu thời gian xóa
        String sql = "UPDATE student SET is_deleted = true, deleted_at = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi xóa mềm học viên: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    // Tùy chọn: Hàm này phục vụ cho tính năng "Truy vết" (Admin xem danh sách đã xóa)
    public List<Student> findDeletedStudents() {
        List<Student> deletedList = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE role = 'STUDENT' AND is_deleted = true ORDER BY deleted_at DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    deletedList.add(mapResultSetToStudent(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi truy vết dữ liệu: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return deletedList;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws Exception {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setDob(rs.getDate("dob"));
        student.setEmail(rs.getString("email"));
        student.setSex(rs.getInt("sex"));
        student.setPhone(rs.getString("phone"));
        student.setPassword(rs.getString("password"));
        student.setRole(rs.getString("role"));

        // Map thêm 2 trường phục vụ Soft Delete
        student.setDeleted(rs.getBoolean("is_deleted"));
        student.setDeletedAt(rs.getTimestamp("deleted_at"));

        return student;
    }
}