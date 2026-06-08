package presentation;

import business.IUserService;
import business.impl.UserServiceImpl;
import entity.Student;

import java.sql.Date;
import java.util.Scanner;

public class AuthView {
    // Khởi tạo Service để xử lý logic
    private final IUserService userService = new UserServiceImpl();

    public void showLogin(Scanner scanner) throws Exception {
        System.out.println("\n--- 🔐 ĐĂNG NHẬP HỆ THỐNG ---");
        System.out.print("Nhập email: ");
        String email = scanner.nextLine();

        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();

        // Validate cơ bản tại View
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
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
            }
        } else {
            System.out.println("❌ Tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại!");
        }
    }

    public void showRegister(Scanner scanner) {
        System.out.println("\n--- 📝 ĐĂNG KÝ TÀI KHOẢN ---");
        Student newStudent = new Student();

        try {
            System.out.print("Nhập họ và tên: ");
            newStudent.setName(scanner.nextLine());

            System.out.print("Nhập ngày sinh (yyyy-mm-dd): ");
            // Ép kiểu chuỗi sang java.sql.Date
            newStudent.setDob(Date.valueOf(scanner.nextLine()));

            System.out.print("Nhập email: ");
            newStudent.setEmail(scanner.nextLine());

            System.out.print("Nhập giới tính (1: Nam, 0: Nữ): ");
            newStudent.setSex(Integer.parseInt(scanner.nextLine()));

            System.out.print("Nhập số điện thoại: ");
            newStudent.setPhone(scanner.nextLine());

            System.out.print("Nhập mật khẩu (tối thiểu 6 ký tự): ");
            newStudent.setPassword(scanner.nextLine());

            // Gọi Business để kiểm tra và lưu DB
            userService.register(newStudent);
            System.out.println("✅ Đăng ký thành công! Bạn có thể tiến hành đăng nhập ngay.");

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Lỗi: Sai định dạng dữ liệu (Ngày sinh phải là yyyy-mm-dd, Giới tính phải là số).");
        } catch (Exception e) {
            // Bắt các lỗi throw từ tầng Business (như trùng email, mật khẩu ngắn)
            System.out.println(e.getMessage());
        }
    }
}