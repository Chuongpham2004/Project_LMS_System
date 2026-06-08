package business;

import entity.Student;

public interface IUserService {
    Student login(String email, String rawPassword);
    void register(Student newStudent) throws Exception;
}
