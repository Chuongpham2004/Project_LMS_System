package dao;

import entity.Student;

public interface IUserDAO {
    Student findByEmail(String email);
    boolean registerStudent(Student student);
}
