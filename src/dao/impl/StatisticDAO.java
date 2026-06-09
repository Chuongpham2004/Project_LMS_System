package dao.impl;

import entity.CourseStatDTO;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {

    // 1. Thống kê tổng số lượng khóa học và tổng số học viên
    public void printGeneralStatistics() {
        String sqlStudent = "SELECT COUNT(*) FROM student WHERE role = 'STUDENT' AND is_deleted = false";
        String sqlCourse = "SELECT COUNT(*) FROM course WHERE is_deleted = false";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pst1 = conn.prepareStatement(sqlStudent);
             PreparedStatement pst2 = conn.prepareStatement(sqlCourse)) {

            ResultSet rsStudent = pst1.executeQuery();
            ResultSet rsCourse = pst2.executeQuery();

            int totalStudents = rsStudent.next() ? rsStudent.getInt(1) : 0;
            int totalCourses = rsCourse.next() ? rsCourse.getInt(1) : 0;

            System.out.println("📊 TỔNG QUAN HỆ THỐNG:");
            System.out.println("- Tổng số Học viên: " + totalStudents);
            System.out.println("- Tổng số Khóa học: " + totalCourses);

        } catch (Exception e) {
            System.err.println("❌ Lỗi thống kê tổng quan: " + e.getMessage());
        }
    }

    // Cơ chế dùng chung cho 3 chức năng còn lại (GROUP BY và JOIN)
    private List<CourseStatDTO> getCourseStatistics(String condition, int limit) {
        List<CourseStatDTO> list = new ArrayList<>();
        // LEFT JOIN để lấy cả những khóa học chưa có ai đăng ký (0 học viên)
        String sql = "SELECT c.name, COUNT(e.student_id) as total " +
                "FROM course c " +
                "LEFT JOIN enrollment e ON c.id = e.course_id " +
                "LEFT JOIN student s ON e.student_id = s.id AND s.is_deleted = false " +
                "WHERE c.is_deleted = false " +
                "GROUP BY c.id, c.name " +
                condition;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (limit > 0) pstmt.setMaxRows(limit);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new CourseStatDTO(rs.getString("name"), rs.getInt("total")));
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi truy xuất thống kê: " + e.getMessage());
        }
        return list;
    }

    // 2. Thống kê tổng số học viên theo từng khóa
    public List<CourseStatDTO> getStudentsPerCourse() {
        return getCourseStatistics("ORDER BY total DESC, c.name ASC", 0);
    }

    // 3. Thống kê top 5 khóa học đông sinh viên nhất
    public List<CourseStatDTO> getTop5Courses() {
        return getCourseStatistics("ORDER BY total DESC", 5); // Set limit = 5
    }

    // 4. Liệt kê các khóa học có trên 10 học viên
    public List<CourseStatDTO> getCoursesWithMoreThan10Students() {
        return getCourseStatistics("HAVING COUNT(e.student_id) > 10 ORDER BY total DESC", 0);
    }
}