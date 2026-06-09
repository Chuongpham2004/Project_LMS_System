package dao;

import entity.EnrollmentDetail;

import java.util.List;

public interface IEnrollmentDAO {
    List<EnrollmentDetail> getAllEnrollments();

    boolean updateStatus(int enrollmentId, String newStatus);

    boolean deleteEnrollment(int enrollmentId);

    boolean hasEnrollmentByStudentId(int studentId);
}
