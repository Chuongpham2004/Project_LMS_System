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
        // ĐIỂM MẤU CHỐT: Chỉ lấy khóa học chưa bị xóa (is_deleted = false)
        String sql = "SELECT * FROM course WHERE is_deleted = false ORDER BY id ASC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
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
        // Chỉ tìm kiếm trong các khóa học chưa bị xóa
        String sql = "SELECT * FROM course WHERE id = ? AND is_deleted = false";
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
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm khóa học theo ID: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public boolean insert(Course course) {
        String sql = "INSERT INTO course (name, duration, instructor) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, course.getName());
                pstmt.setInt(2, course.getDuration());
                pstmt.setString(3, course.getInstructor());

                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi thêm mới khóa học: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    @Override
    public boolean update(Course course) {
        String sql = "UPDATE course SET name = ?, duration = ?, instructor = ? WHERE id = ? AND is_deleted = false";
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

                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi cập nhật khóa học: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        // CHUYỂN ĐỔI: Không dùng lệnh DELETE, chuyển sang UPDATE trạng thái xóa mềm
        String sql = "UPDATE course SET is_deleted = true, deleted_at = NOW() WHERE id = ?";
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
            System.err.println("❌ Lỗi xóa mềm khóa học: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    // Hàm phục vụ tính năng rà soát lịch sử (Kéo các khóa học đã ẩn lên)
    public List<Course> findDeletedCourses() {
        List<Course> deletedList = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE is_deleted = true ORDER BY deleted_at DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    deletedList.add(mapResultSetToCourse(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi truy vết dữ liệu khóa học: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return deletedList;
    }

    /**
     * Hàm tiện ích map dữ liệu từ DB lên Đối tượng Course
     */
    private Course mapResultSetToCourse(ResultSet rs) throws Exception {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setName(rs.getString("name"));
        course.setDuration(rs.getInt("duration"));
        course.setInstructor(rs.getString("instructor"));

        // Đọc thêm 2 trường phục vụ cơ chế Soft Delete
        course.setDeleted(rs.getBoolean("is_deleted"));
        course.setDeletedAt(rs.getTimestamp("deleted_at"));

        return course;
    }
}