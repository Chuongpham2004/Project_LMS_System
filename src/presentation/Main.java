package presentation;

import utils.ConsoleUtils;
import utils.DBUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DBUtil.getConnection();
        Scanner scanner = new Scanner(System.in);
        AuthView authView = new AuthView();

        while (true) {
            ConsoleUtils.printMenuBox("HỆ THỐNG QUẢN LÝ KHÓA HỌC VÀ HỌC VIÊN", new String[]{
                    "[1]. Đăng nhập",
                    "[2]. Đăng ký",
                    "[3]. Thoát chương trình"
            });
            ConsoleUtils.printPrompt("👉 Mời bạn chọn chức năng (1-3): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        authView.showLogin(scanner);
                    } catch (Exception e) {
                        ConsoleUtils.printlnError("❌ Lỗi hệ thống: Quá trình đăng nhập gặp sự cố. Vui lòng thử lại!");
                    }
                    break;
                case "2":
                    try {
                        authView.showRegister(scanner);
                    } catch (Exception e) {
                        ConsoleUtils.printlnError("❌ Lỗi hệ thống: Quá trình đăng ký gặp sự cố. Vui lòng thử lại!");
                    }
                    break;
                case "3":
                    ConsoleUtils.printlnInfo("👋 Cảm ơn bạn đã sử dụng hệ thống. Hẹn gặp lại!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ. Vui lòng chọn lại (1-3).");
                    break;
            }
        }
    }
}