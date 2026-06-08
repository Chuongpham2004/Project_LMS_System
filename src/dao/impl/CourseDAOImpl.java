package dao.impl;

import dao.ICourseDAO;
import entity.Course;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements ICourseDAO {
    @Override
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "select * from course";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreateAt(rs.getDate("create_at"));
                courses.add(course);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy danh sách khóa học: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return courses;
    }

    @Override
    public Course findById(int id) {
        Course course = null;
        String sql = "select * from course where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreateAt(rs.getDate("create_at"));
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm kiếm khóa học theo ID: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return course;
    }

    @Override
    public boolean insert(Course course) {
        String sql = "insert into course(name, duration,instructor) values(?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, course.getName());
                pstmt.setInt(2, course.getDuration());
                pstmt.setString(3, course.getInstructor());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0; // Trả về true nếu có ít nhất 1 dòng bị ảnh hưởng
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi thêm khóa học: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    @Override
    public boolean update(Course course) {
        String sql = "update course set name = ?, duration = ?, instructor = ? where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, course.getName());
                pstmt.setInt(2, course.getDuration());
                pstmt.setString(3, course.getInstructor());
                pstmt.setInt(4, course.getId());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0; // Trả về true nếu có ít nhất 1
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật khóa học: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "delete from course where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0; // Trả về true nếu có ít nhất 1
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xóa khóa học: " + e.getMessage());
        }
        return false;
    }
}
