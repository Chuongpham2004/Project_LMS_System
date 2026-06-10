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
        // Bọc toàn bộ luồng đăng nhập vào một vòng lặp lớn
        while (true) {
            System.out.println("\n--- 🔐 ĐĂNG NHẬP HỆ THỐNG ---");
            String email = "";
            String password = "";

            // LỚP PHÒNG THỦ 1: Ép nhập Email không được để trống (Thêm phím 0 để thoát)
            while (true) {
                System.out.print("Nhập email (hoặc gõ '0' để quay lại Menu chính): ");
                email = scanner.nextLine().trim();

                if (email.equals("0")) {
                    System.out.println("ℹ️ Đã hủy đăng nhập.");
                    return; // Thoát hoàn toàn khỏi hàm showLogin, quay về Main Menu
                }

                if (email.isEmpty()) {
                    System.out.println("❌ Lỗi: Email không được để trống! Vui lòng nhập lại.");
                } else {
                    break; // Nhập hợp lệ thì thoát vòng lặp nhỏ
                }
            }

            // LỚP PHÒNG THỦ 2: Ép nhập Mật khẩu không được để trống
            while (true) {
                System.out.print("Nhập mật khẩu: ");
                password = scanner.nextLine().trim();
                if (password.isEmpty()) {
                    System.out.println("❌ Lỗi: Mật khẩu không được để trống! Vui lòng nhập lại.");
                } else {
                    break; // Nhập hợp lệ thì thoát vòng lặp nhỏ
                }
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
                    StudentView studentView = new StudentView(user);
                    studentView.showMenu(scanner);
                }

                // Trả về luồng điều khiển sau khi người dùng Đăng xuất khỏi AdminView/StudentView
                return;
            } else {
                // Đăng nhập thất bại: Báo lỗi và vòng lặp while(true) lớn sẽ tự động quay lại bắt nhập Email
                System.out.println("❌ Tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại!");
                System.out.println("-------------------------------------------");
            }
        }
    }

    public void showRegister(Scanner scanner) throws Exception {
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