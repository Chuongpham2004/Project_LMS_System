package utils;

import java.sql.Date;
import java.util.Scanner;

public class InputUtil {

    public static String getValidName(Scanner scanner) {
        while (true) {
            System.out.print("Nhập họ và tên: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) return name;
            System.out.println("❌ Lỗi: Họ tên không được để trống!");
        }
    }

    public static Date getValidDate(Scanner scanner) {
        while (true) {
            System.out.print("Nhập ngày sinh (yyyy-mm-dd): ");
            try {
                return Date.valueOf(scanner.nextLine().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Lỗi: Ngày sinh phải đúng định dạng yyyy-mm-dd (Ví dụ: 2000-01-01)!");
            }
        }
    }

    public static String getValidEmail(Scanner scanner) {
        while (true) {
            System.out.print("Nhập email: ");
            String email = scanner.nextLine().trim();
            if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return email;
            System.out.println("❌ Lỗi: Định dạng Email không hợp lệ!");
        }
    }

    public static String getValidPhone(Scanner scanner) {
        while (true) {
            System.out.print("Nhập số điện thoại: ");
            String phone = scanner.nextLine().trim();
            if (phone.matches("^[0-9]{10,11}$")) return phone;
            System.out.println("❌ Lỗi: Số điện thoại phải gồm 10-11 chữ số!");
        }
    }

    public static int getValidSex(Scanner scanner) {
        while (true) {
            System.out.print("Nhập giới tính (1: Nam, 0: Nữ): ");
            try {
                int sex = Integer.parseInt(scanner.nextLine().trim());
                if (sex == 0 || sex == 1) return sex;
                System.out.println("❌ Lỗi: Chỉ nhập 1 (Nam) hoặc 0 (Nữ)!");
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: Vui lòng nhập số!");
            }
        }
    }

    public static String getValidPassword(Scanner scanner) {
        while (true) {
            System.out.print("Nhập mật khẩu: ");
            String password = scanner.nextLine().trim();
            String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
            if (password.matches(passwordRegex)) return password;
            System.out.println("❌ Lỗi Bảo Mật: Mật khẩu phải từ 8 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt (@#$%^&+=!)");
        }
    }
}