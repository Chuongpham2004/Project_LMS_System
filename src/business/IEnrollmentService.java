package business;

import entity.EnrollmentDetail;

import java.util.List;
import java.util.Map;

public interface IEnrollmentService {

    Map<String,List<EnrollmentDetail>> getEnrollmentsGroupedByCourse();

    void approveEnrollment(int enrollmentId) throws Exception;


    void removeEnrollment(int enrollmentId) throws Exception;
}
