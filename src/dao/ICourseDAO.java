package dao;

import entity.Course;

import java.util.List;

public interface ICourseDAO {
    List<Course> findAll();

    Course findById(int id);

    boolean insert(Course course);

    boolean update(Course course);

    boolean delete(int id);
}
