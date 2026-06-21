package business;

import entity.EnrollmentDetail;

import java.util.List;
import java.util.Map;

public interface IEnrollmentService {

    Map<String, List<EnrollmentDetail>> getEnrollmentsGroupedByCourse();

    void approveEnrollment(int enrollmentId) throws Exception;


    void removeEnrollment(int enrollmentId) throws Exception;

    void enrollCourse(int studentId, int courseId) throws Exception;

    List<EnrollmentDetail> getMyEnrollments(int studentId);

    void cancelEnrollment(int studentId, int enrollmentId) throws Exception;

    int getTotalEnrollmentsCount() throws Exception;

    List<EnrollmentDetail> getEnrollmentsByPage(int page, int pageSize) throws Exception;
}
