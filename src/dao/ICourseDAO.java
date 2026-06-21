package dao;

import entity.Course;

import java.util.List;

public interface ICourseDAO {
    List<Course> findAll();

    Course findById(int id);

    boolean insert(Course course);

    boolean update(Course course);

    boolean delete(int id);

    int getTotalCoursesCount(String keyword) throws Exception;

    List<Course> getCoursesByPage(String keyword, int page, int pageSize) throws Exception;

    List<Course> getRecommendedCourses(int studentId, int limit) throws Exception;
}
