package business.impl;

import business.IEnrollmentService;
import dao.IEnrollmentDAO;
import dao.impl.EnrollmentDAOImpl;
import entity.EnrollmentDetail;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnrollmentServiceImpl implements IEnrollmentService {
    private final IEnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();

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
}
