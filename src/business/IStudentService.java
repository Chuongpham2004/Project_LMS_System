package business;

import entity.Student;

import java.util.List;

public interface IStudentService {
    List<Student> getAllStudents();

    Student getStudentById(int id);

    void addStudent(Student student) throws Exception;

    void updateStudent(Student student) throws Exception;

    void deleteStudent(int id) throws Exception;

    // Hàm Omni-search 3 trong 1
    List<Student> searchStudents(String keyword);

    // Hàm sắp xếp
    List<Student> sortStudents(String type, boolean isAscending);

    void changePassword(int studentId, String oldPassword, String newPassword) throws Exception;

    boolean checkCurrentPassword(int studentId, String rawPassword);

    int getTotalStudentsCount(String keyword) throws Exception;

    List<Student> getStudentsByPage(String keyword, int page, int pageSize) throws Exception;
}
