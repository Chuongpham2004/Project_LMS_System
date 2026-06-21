package business.impl;

import business.IEnrollmentService;
import dao.ICourseDAO;
import dao.IEnrollmentDAO;
import dao.impl.CourseDAOImpl;
import dao.impl.EnrollmentDAOImpl;
import entity.Course;
import entity.EnrollmentDetail;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnrollmentServiceImpl implements IEnrollmentService {
    private final IEnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final ICourseDAO courseDAO = new CourseDAOImpl();

    // Khởi tạo dịch vụ ghi nhật ký hệ thống ngầm
    private final AuditLogServiceImpl auditLogService = new AuditLogServiceImpl();

    @Override
    public Map<String, List<EnrollmentDetail>> getEnrollmentsGroupedByCourse() {
        return enrollmentDAO.getAllEnrollments().stream()
                .collect(Collectors.groupingBy(EnrollmentDetail::getCourseName));
    }

    @Override
    public void approveEnrollment(int enrollmentId) throws Exception {
        boolean isSuccess = enrollmentDAO.updateStatus(enrollmentId, "CONFIRM");
        if (!isSuccess) {
            throw new Exception("❌ Lỗi: ID đăng ký không tồn tại hoặc lỗi hệ thống!");
        }

        // [NHÚNG AUDIT LOG]: Ghi nhận hành động duyệt đơn của Admin
        auditLogService.log(
                "Admin",
                "APPROVE",
                "enrollment",
                enrollmentId,
                "Phê duyệt đơn đăng ký khóa học thành công (Trạng thái -> CONFIRM)"
        );
    }

    @Override
    public void removeEnrollment(int enrollmentId) throws Exception {
        boolean isSuccess = enrollmentDAO.deleteEnrollment(enrollmentId);
        if (!isSuccess) {
            throw new Exception("❌ Lỗi: ID đăng ký không tồn tại hoặc lỗi hệ thống!");
        }

        // [NHÚNG AUDIT LOG]: Ghi nhận hành động xóa đơn của Admin
        auditLogService.log(
                "Admin",
                "DELETE",
                "enrollment",
                enrollmentId,
                "Quản trị viên xóa đơn đăng ký khỏi hệ thống"
        );
    }

    @Override
    public void enrollCourse(int studentId, int courseId) throws Exception {
        // 1. Kiểm tra khóa học có tồn tại và đang mở (chưa bị xóa mềm) không?
        Course course = courseDAO.findById(courseId);
        if (course == null) {
            throw new Exception("❌ Lỗi: Khóa học không tồn tại hoặc đã ngừng giảng dạy!");
        }

        // 2. Kiểm tra trùng lặp (Fail-Fast)
        if (enrollmentDAO.isEnrolled(studentId, courseId)) {
            throw new Exception("❌ Từ chối: Bạn đã đăng ký khóa học này rồi! Vui lòng vào mục Lịch sử để kiểm tra.");
        }

        // 3. Tiến hành ghi nhận đơn
        if (!enrollmentDAO.insertEnrollment(studentId, courseId)) {
            throw new Exception("❌ Lỗi hệ thống: Không thể khởi tạo đơn đăng ký lúc này!");
        }

        // [NHÚNG AUDIT LOG]: Xác định Actor linh hoạt theo phiên đăng nhập của Học viên
        // Vì hàm insert trả về boolean chứ không trả về mã ID tự tăng của đơn mới, ta truyền targetId là 0 hoặc id khóa học để định danh
        auditLogService.log(
                "Học viên ID: " + studentId,
                "INSERT",
                "enrollment",
                courseId,
                "Học viên gửi yêu cầu đăng ký mới vào khóa học: " + course.getName() + " (Trạng thái mặc định: WAITING)"
        );
    }

    @Override
    public List<EnrollmentDetail> getMyEnrollments(int studentId) {
        return enrollmentDAO.getEnrollmentsByStudentId(studentId);
    }

    @Override
    public void cancelEnrollment(int studentId, int enrollmentId) throws Exception {
        // 1. Kiểm tra sự tồn tại và quyền sở hữu đơn
        String status = enrollmentDAO.getStatusByEnrollmentAndStudent(enrollmentId, studentId);

        if (status == null) {
            throw new Exception("❌ Lỗi: Không tìm thấy mã đơn đăng ký này trong tài khoản của bạn!");
        }

        // 2. CHẶN LỖI NGHIỆP VỤ: Chỉ được hủy khi đơn ở trạng thái WAITING
        if (!"WAITING".equals(status)) {
            throw new Exception("❌ Từ chối: Đơn đăng ký này đã được Quản trị viên phê duyệt (CONFIRM). Bạn không thể tự hủy, vui lòng liên hệ phòng đào tạo!");
        }

        // 3. Thực thi xóa đơn bằng hàm xóa cứng có sẵn của bạn
        if (!enrollmentDAO.deleteEnrollment(enrollmentId)) {
            throw new Exception("❌ Lỗi hệ thống: Không thể thực hiện thao tác hủy đơn lúc này!");
        }

        // [NHÚNG AUDIT LOG]: Ghi nhận học viên tự hủy đơn hợp lệ
        auditLogService.log(
                "Học viên ID: " + studentId,
                "DELETE",
                "enrollment",
                enrollmentId,
                "Học viên tự hủy đơn đăng ký khóa học thành công (Trạng thái trước khi hủy: WAITING)"
        );
    }

    @Override
    public List<EnrollmentDetail> getEnrollmentsByPage(int page, int pageSize) throws Exception {
        return enrollmentDAO.getEnrollmentsByPage(page, pageSize);
    }

    @Override
    public int getTotalEnrollmentsCount() throws Exception {
        return enrollmentDAO.getTotalEnrollmentsCount();
    }
}