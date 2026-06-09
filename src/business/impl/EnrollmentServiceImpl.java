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
    }

    @Override
    public void removeEnrollment(int enrollmentId) throws Exception {
        boolean isSuccess = enrollmentDAO.deleteEnrollment(enrollmentId);
        if (!isSuccess) {
            throw new Exception("❌ Lỗi: ID đăng ký không tồn tại hoặc lỗi hệ thống!");
        }
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
    }

    @Override
    public List<entity.EnrollmentDetail> getMyEnrollments(int studentId) {
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
    }
}
