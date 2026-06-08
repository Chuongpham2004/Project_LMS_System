package business.impl;

import business.IUserService;
import dao.IUserDAO;
import dao.impl.UserDAOImpl;
import entity.Student;
import org.mindrot.jbcrypt.BCrypt;

public class UserServiceImpl implements IUserService {
    private IUserDAO userDAO = new UserDAOImpl();

    @Override
    public Student login(String email, String rawPassword) {
        Student user = userDAO.findByEmail(email);
        if (user != null) {
            try {
                boolean isPasswordMatch = BCrypt.checkpw(rawPassword, user.getPassword());

                if (isPasswordMatch) {
                    return user;// Dang nhap thanh cong
                }
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Lỗi khi kiểm tra mật khẩu: " + e.getMessage());
            }
        }
        return null; // Dang nhap that bai
    }

    @Override
    public void register(Student newStudent) throws Exception {
        // 1. Kiểm tra Email đã tồn tại chưa
        Student existingUser = userDAO.findByEmail(newStudent.getEmail());
        if (existingUser != null) {
            throw new Exception("❌ Email này đã được đăng ký trong hệ thống!");
        }

        // 2. Validate cơ bản
        if (newStudent.getPassword() == null || newStudent.getPassword().length() < 6) {
            throw new Exception("❌ Mật khẩu phải có ít nhất 6 ký tự!");
        }

        // 3. Băm mật khẩu bằng BCrypt
        String hashedPassword = BCrypt.hashpw(newStudent.getPassword(), BCrypt.gensalt(10));
        newStudent.setPassword(hashedPassword);

        // 4. Gọi DAO để lưu
        boolean isSuccess = userDAO.registerStudent(newStudent);
        if (!isSuccess) {
            throw new Exception("❌ Đăng ký thất bại do lỗi hệ thống, vui lòng thử lại sau!");
        }
    }
}
