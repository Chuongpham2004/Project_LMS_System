package presentation;

import business.IUserService;
import business.impl.UserServiceImpl;
import entity.Student;
import utils.InputUtil;

import java.sql.Date;
import java.util.Scanner;

public class AuthView {
    // Khởi tạo Service để xử lý logic
    private final IUserService userService = new UserServiceImpl();

    public void showLogin(Scanner scanner) throws Exception {
        System.out.println("\n--- 🔐 ĐĂNG NHẬP HỆ THỐNG ---");
        System.out.print("Nhập email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine().trim();

        // Validate cơ bản tại View
        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Lỗi: Không được để trống thông tin đăng nhập!");
            return;
        }

        // Gọi Business để xác thực
        Student user = userService.login(email, password);

        if (user != null) {
            System.out.println("✅ Đăng nhập thành công! Xin chào " + user.getName());

            // Điều hướng menu dựa theo Role (Phân quyền)
            if ("ADMIN".equals(user.getRole())) {
                System.out.println("👉 Chuyển hướng tới Menu Quản trị viên (Admin)...");
                AdminView adminView = new AdminView();
                adminView.showMenu(scanner);

            } else {
                System.out.println("👉 Chuyển hướng tới Menu Học viên (Student)...");
                // TODO: Gọi StudentView ở đây trong Buổi 4
                // StudentView studentView = new StudentView(user);
                // studentView.showMenu(scanner);
            }
        } else {
            System.out.println("❌ Tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại!");
        }
    }

    public void showRegister(Scanner scanner) {
        System.out.println("\n--- 📝 ĐĂNG KÝ TÀI KHOẢN ---");
        Student newStudent = new Student();

        try {

            newStudent.setName(utils.InputUtil.getValidName(scanner));
            newStudent.setDob(utils.InputUtil.getValidDate(scanner));
            newStudent.setEmail(utils.InputUtil.getValidEmail(scanner));
            newStudent.setSex(utils.InputUtil.getValidSex(scanner));
            newStudent.setPhone(utils.InputUtil.getValidPhone(scanner));
            newStudent.setPassword(utils.InputUtil.getValidPassword(scanner));

            // Gọi Business để kiểm tra (chống trùng email) và lưu DB + Băm mật khẩu
            userService.register(newStudent);
            System.out.println("🎉 Đăng ký tài khoản thành công! Bạn có thể tiến hành đăng nhập ngay.");

        } catch (Exception e) {
            // Bắt các lỗi throw từ tầng Business (như trùng email)
            System.out.println(e.getMessage());
            System.out.println("⚠️ Đăng ký thất bại. Vui lòng thử lại!");
        }
    }
}