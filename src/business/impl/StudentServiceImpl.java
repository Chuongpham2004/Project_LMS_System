package business.impl;

import business.IStudentService;
import dao.IEnrollmentDAO;
import dao.IStudentDAO;
import dao.impl.EnrollmentDAOImpl;
import dao.impl.StudentDAOImpl;
import entity.Student;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StudentServiceImpl implements IStudentService {

    private final IStudentDAO studentDAO = new StudentDAOImpl();
    private final IEnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();

    // Khởi tạo dịch vụ ghi nhật ký hệ thống ngầm
    private final AuditLogServiceImpl auditLogService = new AuditLogServiceImpl();

    @Override
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    @Override
    public Student getStudentById(int id) {
        return studentDAO.findById(id);
    }

    @Override
    public void addStudent(Student student) throws Exception {
        // 1. Validate định dạng dữ liệu
        validateStudentInfo(student, true);

        // 2. Băm mật khẩu trước khi lưu xuống DB
        String hashedPassword = BCrypt.hashpw(student.getPassword(), BCrypt.gensalt(10));
        student.setPassword(hashedPassword);

        // 3. Gọi DAO thêm mới
        if (!studentDAO.insert(student)) {
            throw new Exception("❌ Thêm học viên thất bại do lỗi hệ thống!");
        }

        // [NHÚNG AUDIT LOG]: Ghi nhận hành động thêm mới học viên
        auditLogService.log(
                "Admin",
                "INSERT",
                "student",
                student.getId(),
                "Thêm mới học viên thành công: " + student.getName() + " (Email: " + student.getEmail() + ", SĐT: " + student.getPhone() + ")"
        );
    }

    @Override
    public void updateStudent(Student student) throws Exception {
        // Kiểm tra xem ID có tồn tại không
        Student existing = studentDAO.findById(student.getId());
        if (existing == null) {
            throw new Exception("❌ Lỗi: Không tìm thấy học viên với ID này!");
        }

        // Validate thông tin (truyền false vì lúc update không bắt nhập mật khẩu)
        validateStudentInfo(student, false);

        // [XỬ LÝ LOG CHI TIẾT]: Chụp lại thay đổi trước khi lưu xuống DB
        String details = String.format("Cập nhật thông tin học viên ID %d. [Cũ] Tên: %s, SĐT: %s, Phái: %s -> [Mới] Tên: %s, SĐT: %s, Phái: %s",
                student.getId(),
                existing.getName(), existing.getPhone(), (existing.getSex() == 1 ? "Nam" : "Nữ"),
                student.getName(), student.getPhone(), (student.getSex() == 1 ? "Nam" : "Nữ"));

        if (!studentDAO.update(student)) {
            throw new Exception("❌ Cập nhật thông tin thất bại!");
        }

        // [NHÚNG AUDIT LOG]: Actor linh hoạt vì hàm này có thể dùng bởi Admin hoặc chính Học viên tự sửa profile
        auditLogService.log(
                "Admin/Học viên ID: " + student.getId(),
                "UPDATE",
                "student",
                student.getId(),
                details
        );
    }

    @Override
    public void deleteStudent(int id) throws Exception {
        // 1. Kiểm tra tồn tại: ID có hợp lệ không và học viên này đã bị xóa mềm trước đó chưa?
        Student existingStudent = studentDAO.findById(id);
        if (existingStudent == null) {
            throw new Exception("❌ Lỗi: Không tìm thấy học viên với ID này, hoặc học viên đã bị xóa khỏi hệ thống!");
        }

        // 2. Kiểm tra nghiệp vụ (Business Rule): Học viên có đơn đăng ký không?
        boolean hasEnrollment = enrollmentDAO.hasEnrollmentByStudentId(id);
        if (hasEnrollment) {
            throw new Exception("❌ Từ chối xóa: Học viên này đang có đơn đăng ký khóa học. Bạn phải xóa đơn đăng ký trong [Quản lý Đăng ký] trước khi xóa tài khoản!");
        }

        // 3. Thực thi Soft Delete qua DAO
        if (!studentDAO.delete(id)) {
            throw new Exception("❌ Xóa học viên thất bại do lỗi hệ thống cơ sở dữ liệu!");
        }

        // [NHÚNG AUDIT LOG]: Ghi nhận hành động xóa mềm tài khoản của Admin
        auditLogService.log(
                "Admin",
                "DELETE",
                "student",
                id,
                "Xóa mềm tài khoản học viên: " + existingStudent.getName() + " (Email: " + existingStudent.getEmail() + ")"
        );
    }

    @Override
    public boolean checkCurrentPassword(int studentId, String rawPassword) {
        try {
            Student student = studentDAO.findById(studentId);
            if (student == null) return false;
            // So khớp mật khẩu người dùng gõ với mật khẩu băm trong DB
            return BCrypt.checkpw(rawPassword, student.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void changePassword(int studentId, String oldPassword, String newPassword) throws Exception {
        // 1. Không cần gọi BCrypt.checkpw kiểm tra pass cũ ở đây nữa!
        // Vì tầng View đã gọi checkCurrentPassword và chặn đứng từ vòng gửi xe rồi.

        // 2. Chỉ tập trung băm mật khẩu mới
        String hashedNewPassword = BCrypt.hashpw(newPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

        // 3. Gọi DAO chuyên dụng để nhét mật khẩu mới vào DB
        if (!studentDAO.updatePassword(studentId, hashedNewPassword)) {
            throw new Exception("❌ Lỗi hệ thống: Không thể lưu mật khẩu mới vào cơ sở dữ liệu!");
        }

        // [NHÚNG AUDIT LOG]: Ghi nhận học viên đổi mật khẩu thành công độc lập
        auditLogService.log(
                "Học viên ID: " + studentId,
                "CHANGE_PASSWORD",
                "student",
                studentId,
                "Học viên tự thay đổi mật khẩu đăng nhập thành công"
        );
    }

    @Override
    public List<Student> getStudentsByPage(String keyword, int page, int pageSize) throws Exception {
        return studentDAO.getStudentsByPage(keyword, page, pageSize);
    }

    @Override
    public int getTotalStudentsCount(String keyword) throws Exception {
        return studentDAO.getTotalStudentsCount(keyword);
    }

    // ================= XỬ LÝ STREAM API =================

    @Override
    public List<Student> searchStudents(String keyword) {
        String lowerKeyword = keyword.toLowerCase().trim();

        // Kéo toàn bộ danh sách, dùng Stream filter trên 3 trường cùng lúc
        return studentDAO.findAll().stream()
                .filter(s ->
                        // 1. Khớp Tên
                        s.getName().toLowerCase().contains(lowerKeyword) ||
                                // 2. Hoặc khớp Email
                                s.getEmail().toLowerCase().contains(lowerKeyword) ||
                                // 3. Hoặc khớp ID (Ép kiểu ID thành chuỗi để dùng hàm contains)
                                String.valueOf(s.getId()).contains(lowerKeyword)
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudents(String type, boolean isAscending) {
        List<Student> list = studentDAO.findAll();
        Comparator<Student> comparator;

        if ("NAME".equalsIgnoreCase(type)) {
            comparator = Comparator.comparing(Student::getName);
        } else {
            comparator = Comparator.comparing(Student::getId);
        }

        if (!isAscending) {
            comparator = comparator.reversed();
        }

        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

    // ================= HÀM VALIDATE CHUNG =================

    private void validateStudentInfo(Student student, boolean isNew) throws Exception {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new Exception("❌ Tên học viên không được để trống!");
        }

        if (student.getEmail() == null || !student.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("❌ Định dạng Email không hợp lệ!");
        }

        // CHỐNG TRÙNG EMAIL: Gọi DAO để check
        Student existingEmail = studentDAO.findByEmail(student.getEmail());
        if (existingEmail != null) {
            // Nếu là Thêm mới -> Cứ có existingEmail là lỗi
            // Nếu là Cập nhật -> Có existingEmail nhưng ID khác với ID người đang sửa thì mới là lỗi
            if (isNew || existingEmail.getId() != student.getId()) {
                throw new Exception("❌ Lỗi: Email này đã được sử dụng trong hệ thống!");
            }
        }

        if (student.getPhone() == null || !student.getPhone().matches("^[0-9]{10,11}$")) {
            throw new Exception("❌ Số điện thoại phải gồm 10-11 chữ số!");
        }

        if (isNew) { // Chỉ kiểm tra mật khẩu khi tạo mới
            String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
            if (student.getPassword() == null || !student.getPassword().matches(passwordRegex)) {
                throw new Exception("❌ Lỗi Bảo Mật: Mật khẩu phải từ 8 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt (@#$%^&+=!)");
            }
        }
    }
}