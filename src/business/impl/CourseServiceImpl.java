package business.impl;

import business.ICourseService;
import dao.ICourseDAO;
import dao.impl.CourseDAOImpl;
import entity.Course;

import java.util.Comparator;
import java.util.List;

public class CourseServiceImpl implements ICourseService {
    private final ICourseDAO courseDAO = new CourseDAOImpl();

    @Override
    public void addCourse(Course course) throws Exception {
        validateCourseInfo(course);
        // Nếu qua ải validate, gọi DAO để lưu xuống DB
        boolean isSuccess = courseDAO.insert(course);
        if (!isSuccess) throw new Exception("❌ Thêm khóa học thất bại từ hệ thống!");
    }

    @Override
    public void updateCourse(Course course) throws Exception {
        Course existingCourse = courseDAO.findById(course.getId());
        if (existingCourse == null) {
            throw new Exception("❌ Lỗi: ID khóa học không tồn tại!");
        }
        validateCourseInfo(course);
        boolean isSuccess = courseDAO.update(course);
        if (!isSuccess) throw new Exception("❌ Cập nhật khóa học thất bại từ hệ thống!");
    }

    @Override
    public void deleteCourse(int id) throws Exception {
        Course existing = courseDAO.findById(id);
        if (existing == null) {
            throw new Exception("❌ Lỗi: ID khóa học không tồn tại!");
        }
        boolean isSuccess = courseDAO.delete(id);
        if (!isSuccess) throw new Exception("❌ Xóa khóa học thất bại từ hệ thống!");
    }

    @Override
    public List<Course> getAllCourses() throws Exception {
        return courseDAO.findAll();
    }

    @Override
    public Course getCourseById(int id) throws Exception {
        return courseDAO.findById(id);
    }

    @Override
    public List<Course> searchByName(String keyword) throws Exception {
        return courseDAO.findAll().stream()
                .filter(c->c.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    public List<Course> sortCourses(String type, boolean isAscending) throws Exception {
        List<Course> list = courseDAO.findAll();
        Comparator<Course> comparator;

        if("NAME".equalsIgnoreCase(type)){
            comparator = Comparator.comparing(Course::getName);
        }else{
            comparator = Comparator.comparing(Course::getId);
        }

        if(!isAscending){
            comparator = comparator.reversed();
        }
        return list.stream().sorted(comparator).toList();
    }

    public void validateCourseInfo(Course course) throws Exception {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new Exception("❌ Lỗi: Tên khóa học không được để trống!");
        }
        if (course.getName().length() < 3) {
            throw new Exception("❌ Lỗi: Tên khóa học phải có ít nhất 3 ký tự!");
        }
        if (course.getDuration() <= 0) {
            throw new Exception("❌ Lỗi: Thời lượng khóa học phải lớn hơn 0!");
        }
        if (course.getInstructor() == null || course.getInstructor().trim().isEmpty()) {
            throw new Exception("❌ Lỗi: Tên giảng viên không được để trống!");
        }
    }
}
