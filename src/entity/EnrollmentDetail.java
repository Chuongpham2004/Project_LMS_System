package entity;

import java.sql.Timestamp;

public class EnrollmentDetail {
    private int enrollmentId;
    private String studentName;
    private String studentEmail;
    private String courseName;
    private Timestamp registeredAt;
    private String status;

    public EnrollmentDetail() {
    }

    public EnrollmentDetail(int enrollmentId, String studentName, String studentEmail, String courseName, Timestamp registeredAt, String status) {
        this.enrollmentId = enrollmentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.courseName = courseName;
        this.registeredAt = registeredAt;
        this.status = status;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
