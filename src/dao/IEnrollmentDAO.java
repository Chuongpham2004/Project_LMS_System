package dao;

import entity.EnrollmentDetail;

import java.util.List;

public interface IEnrollmentDAO {
    List<EnrollmentDetail> getAllEnrollments();

    boolean updateStatus(int enrollmentId, String newStatus);

    boolean deleteEnrollment(int enrollmentId);

    boolean hasEnrollmentByStudentId(int studentId);

    boolean isEnrolled(int studentId, int courseId);

    boolean insertEnrollment(int studentId, int courseId);

    List<EnrollmentDetail> getEnrollmentsByStudentId(int studentId);

    String getStatusByEnrollmentAndStudent(int enrollmentId, int studentId);

    int getTotalEnrollmentsCount() throws Exception;

    List<EnrollmentDetail> getEnrollmentsByPage(int page, int pageSize) throws Exception;
}
