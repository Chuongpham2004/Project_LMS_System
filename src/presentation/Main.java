package presentation;

import org.mindrot.jbcrypt.BCrypt;
import utils.DBUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DBUtil.getConnection();
        Scanner scanner = new Scanner(System.in);
        AuthView authView = new AuthView();

        while (true) {
            System.out.println("\n===========================================");
            System.out.println("  HỆ THỐNG QUẢN LÝ KHÓA HỌC VÀ HỌC VIÊN  ");
            System.out.println("===========================================");
            System.out.println("[1]. Đăng nhập");
            System.out.println("[2]. Đăng ký");
            System.out.println("[3]. Thoát chương trình");
            System.out.println("-------------------------------------------");
            System.out.print("👉 Mời bạn chọn chức năng (1-3): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        authView.showLogin(scanner);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "2":
                    authView.showRegister(scanner);
                    break;
                case "3":
                    System.out.println("👋 Cảm ơn bạn đã sử dụng hệ thống. Hẹn gặp lại!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng chọn lại (1-3).");
                    break;
            }
        }
    }
}