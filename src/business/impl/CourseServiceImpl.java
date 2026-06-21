package business.impl;

import business.ICourseService;
import dao.ICourseDAO;
import dao.impl.CourseDAOImpl;
import entity.Course;

import java.util.Comparator;
import java.util.List;

public class CourseServiceImpl implements ICourseService {
    private final ICourseDAO courseDAO = new CourseDAOImpl();

    // Khởi tạo dịch vụ ghi nhật ký hệ thống ngầm
    private final AuditLogServiceImpl auditLogService = new AuditLogServiceImpl();

    @Override
    public void addCourse(Course course) throws Exception {
        validateCourseInfo(course);
        // Nếu qua ải validate, gọi DAO để lưu xuống DB
        boolean isSuccess = courseDAO.insert(course);
        if (!isSuccess) throw new Exception("❌ Thêm khóa học thất bại từ hệ thống!");

        // [NHÚNG AUDIT LOG]: Ghi nhận hành động thêm mới
        auditLogService.log(
                "Admin",
                "INSERT",
                "course",
                course.getId(),
                "Thêm mới khóa học thành công: " + course.getName() + " (" + course.getDuration() + " giờ, GV: " + course.getInstructor() + ")"
        );
    }

    @Override
    public void updateCourse(Course course) throws Exception {
        Course existingCourse = courseDAO.findById(course.getId());
        if (existingCourse == null) {
            throw new Exception("❌ Lỗi: ID khóa học không tồn tại!");
        }
        validateCourseInfo(course);
        boolean isSuccess = courseDAO.update(course);
        if (!isSuccess) throw new Exception("❌ Cập nhật khóa học thất bại từ hệ thống!");

        // [NHÚNG AUDIT LOG]: Đối chiếu chi tiết dữ liệu cũ -> dữ liệu mới (Rất chuẩn Enterprise)
        String details = String.format("Cập nhật khóa học ID %d. [Cũ] Tên: %s, Giờ: %d, GV: %s -> [Mới] Tên: %s, Giờ: %d, GV: %s",
                course.getId(),
                existingCourse.getName(), existingCourse.getDuration(), existingCourse.getInstructor(),
                course.getName(), course.getDuration(), course.getInstructor());

        auditLogService.log("Admin", "UPDATE", "course", course.getId(), details);
    }

    @Override
    public void deleteCourse(int id) throws Exception {
        Course existing = courseDAO.findById(id);
        if (existing == null) {
            throw new Exception("❌ Lỗi: ID khóa học không tồn tại!");
        }
        boolean isSuccess = courseDAO.delete(id);
        if (!isSuccess) throw new Exception("❌ Xóa khóa học thất bại từ hệ thống!");

        // [NHÚNG AUDIT LOG]: Ghi lại thông tin khóa học trước khi bị xóa khỏi màn hình hiển thị
        auditLogService.log(
                "Admin",
                "DELETE",
                "course",
                id,
                "Xóa khóa học hệ thống: " + existing.getName() + " (Giảng viên phụ trách: " + existing.getInstructor() + ")"
        );
    }

    @Override
    public List<Course> getAllCourses() throws Exception {
        return courseDAO.findAll();
    }

    @Override
    public Course getCourseById(int id) throws Exception {
        return courseDAO.findById(id);
    }

    @Override
    public List<Course> searchByName(String keyword) throws Exception {
        return courseDAO.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    public List<Course> sortCourses(String type, boolean isAscending) throws Exception {
        List<Course> list = courseDAO.findAll();
        Comparator<Course> comparator;

        if ("NAME".equalsIgnoreCase(type)) {
            comparator = Comparator.comparing(Course::getName);
        } else {
            comparator = Comparator.comparing(Course::getId);
        }

        if (!isAscending) {
            comparator = comparator.reversed();
        }
        return list.stream().sorted(comparator).toList();
    }

    @Override
    public int getTotalCoursesCount(String keyword) throws Exception {
        return courseDAO.getTotalCoursesCount(keyword);
    }

    @Override
    public List<Course> getCoursesByPage(String keyword, int page, int pageSize) throws Exception {
        return courseDAO.getCoursesByPage(keyword, page, pageSize);
    }

    @Override
    public List<Course> getRecommendedCourses(int studentId, int limit) throws Exception {
        return courseDAO.getRecommendedCourses(studentId, limit);
    }

    public void validateCourseInfo(Course course) throws Exception {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new Exception("❌ Lỗi: Tên khóa học không được để trống!");
        }
        if (course.getName().length() < 3) {
            throw new Exception("❌ Lỗi: Tên khóa học phải có ít nhất 3 ký tự!");
        }
        if (course.getDuration() <= 0) {
            throw new Exception("❌ Lỗi: Thời lượng khóa học phải lớn hơn 0!");
        }
        if (course.getInstructor() == null || course.getInstructor().trim().isEmpty()) {
            throw new Exception("❌ Lỗi: Tên giảng viên không được để trống!");
        }
    }
}