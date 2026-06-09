package entity;

public class CourseStatDTO {
    private String courseName;
    private int studentCount;

    public CourseStatDTO() {
    }

    public CourseStatDTO(String courseName, int studentCount) {
        this.courseName = courseName;
        this.studentCount = studentCount;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }
}
