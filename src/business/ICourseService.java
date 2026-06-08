package business;

import entity.Course;

import java.sql.SQLException;
import java.util.List;

public interface ICourseService {
    void addCourse(Course course) throws Exception;

    void updateCourse(Course course) throws Exception;

    void deleteCourse(int id) throws Exception;

    List<Course> getAllCourses() throws Exception;

    Course getCourseById(int id) throws Exception;

    List<Course> searchByName(String keyword) throws Exception;

    List<Course> sortCourses(String type, boolean isAscending) throws Exception;
}
