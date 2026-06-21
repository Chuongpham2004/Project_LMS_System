package dao;

import entity.Student;

import java.util.List;

public interface IStudentDAO {
    List<Student> findAll();

    Student findById(int id);

    Student findByEmail(String email); // Vũ khí để chặn trùng email

    boolean insert(Student student);

    boolean update(Student student);

    boolean delete(int id);

    boolean updatePassword(int id, String newHashedPassword);

    int getTotalStudentsCount(String keyword) throws Exception;

    List<Student> getStudentsByPage(String keyword, int page, int pageSize) throws Exception;
}