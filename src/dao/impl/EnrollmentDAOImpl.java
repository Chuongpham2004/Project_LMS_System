package dao.impl;

import dao.IEnrollmentDAO;
import entity.EnrollmentDetail;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements IEnrollmentDAO {
    @Override
    public List<EnrollmentDetail> getAllEnrollments() {
        List<EnrollmentDetail> list = new ArrayList<>();
        String sql = "SELECT e.id AS enrollment_id, s.name AS student_name, s.email, " +
                "c.name AS course_name, e.registered_at, e.status " +
                "FROM enrollment e " +
                "JOIN student s ON e.student_id = s.id " +
                "JOIN course c ON e.course_id = c.id " +
                "ORDER BY c.name, e.registered_at DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    EnrollmentDetail enrollmentDetail = new EnrollmentDetail();
                    enrollmentDetail.setEnrollmentId(rs.getInt("enrollment_id"));
                    enrollmentDetail.setStudentName(rs.getString("student_name"));
                    enrollmentDetail.setStudentEmail(rs.getString("email"));
                    enrollmentDetail.setCourseName(rs.getString("course_name"));
                    enrollmentDetail.setRegisteredAt(rs.getTimestamp("registered_at"));
                    enrollmentDetail.setStatus(rs.getString("status"));
                    list.add(enrollmentDetail);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy danh sách đăng ký: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<EnrollmentDetail> getEnrollmentsByStudentId(int studentId) {
        List<EnrollmentDetail> list = new ArrayList<>();
        String sql = "SELECT e.id as enroll_id, s.name as student_name, s.email as student_email, " +
                "c.name as course_name, e.registered_at, e.status " +
                "FROM enrollment e " +
                "JOIN course c ON e.course_id = c.id " +
                "JOIN student s ON e.student_id = s.id " +
                "WHERE e.student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, studentId);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    EnrollmentDetail detail = new entity.EnrollmentDetail();
                    detail.setEnrollmentId(rs.getInt("enroll_id"));
                    detail.setStudentName(rs.getString("student_name"));
                    detail.setStudentEmail(rs.getString("student_email"));
                    detail.setCourseName(rs.getString("course_name"));
                    detail.setRegisteredAt(rs.getTimestamp("registered_at"));
                    detail.setStatus(rs.getString("status"));
                    list.add(detail);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy lịch sử đăng ký: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public boolean updateStatus(int enrollmentId, String newStatus) {
        String sql = "update enrollment set status = ?::enrollment_status where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, enrollmentId);
                int rowsAffected = pstmt.executeUpdate();
                return (rowsAffected > 0);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật trạng thái đăng ký: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    @Override
    public boolean deleteEnrollment(int enrollmentId) {
        String sql = "delete from enrollment where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, enrollmentId);
                int rowsAffected = pstmt.executeUpdate();
                return (rowsAffected > 0);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xóa đăng ký: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, null);
        }
        return false;
    }

    @Override
    public boolean hasEnrollmentByStudentId(int studentId) {
        // Truy vấn xem có bất kỳ đơn đăng ký nào của học viên này không
        String sql = "SELECT 1 FROM enrollment WHERE student_id = ? LIMIT 1";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = utils.DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, studentId);
                rs = pstmt.executeQuery();

                // Nếu rs.next() trả về true, nghĩa là tìm thấy ít nhất 1 đơn đăng ký
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra đơn đăng ký: " + e.getMessage());
        } finally {
            utils.DBUtil.closeResources(conn, pstmt, rs);
        }
        return false;
    }

    @Override
    public boolean isEnrolled(int studentId, int courseId) {
        String sql = "select 1 from enrollment where student_id = ? and course_id = ? limit 1";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                rs = pstmt.executeQuery();
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra trùng lặp đăng ký: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return false;
    }

    @Override
    public boolean insertEnrollment(int studentId, int courseId) {
        String sql = "INSERT INTO enrollment (student_id, course_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                int rowsAffected = pstmt.executeUpdate();
                return (rowsAffected > 0);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo đơn đăng ký mới: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return false;
    }

    @Override
    public String getStatusByEnrollmentAndStudent(int enrollmentId, int studentId) {
        String sql = "select status from enrollment where id = ? and student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, enrollmentId);
            pstmt.setInt(2, studentId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra trạng thái đơn: " + e.getMessage());
        } finally {
            DBUtil.closeResources(conn, pstmt, rs);
        }
        return null;
    }
}
