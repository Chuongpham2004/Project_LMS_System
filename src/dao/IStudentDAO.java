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
}