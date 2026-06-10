package presentation;

import business.impl.EnrollmentServiceImpl;
import entity.Course;
import entity.EnrollmentDetail;
import entity.Student;
import business.impl.CourseServiceImpl;
import business.impl.StudentServiceImpl;
import utils.InputUtil;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StudentView {
    private final Student loggedInStudent;

    // Khởi tạo Service
    private final CourseServiceImpl courseService = new CourseServiceImpl();
    private final StudentServiceImpl studentService = new StudentServiceImpl();
    private final EnrollmentServiceImpl enrollmentService = new EnrollmentServiceImpl();

    public StudentView(Student student) {
        this.loggedInStudent = student;
    }

    public void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===========================================");
            System.out.println("     🎓 HỆ THỐNG HỌC TẬP (STUDENT PORTAL)  ");
            System.out.println("===========================================");
            System.out.println("👤 Học viên: " + loggedInStudent.getName() + " | 📧 Email: " + loggedInStudent.getEmail());
            System.out.println("-------------------------------------------");
            System.out.println("[1]. Xem & Tìm kiếm danh sách khóa học");
            System.out.println("[2]. Đăng ký khóa học");
            System.out.println("[3]. Xem khóa học đã đăng ký (Sắp xếp)");
            System.out.println("[4]. Hủy đăng ký khóa học");
            System.out.println("[5]. Xem thông tin cá nhân");
            System.out.println("[6]. Cập nhật thông tin cá nhân");
            System.out.println("[7]. Đổi mật khẩu (Bảo mật 2 lớp)");
            System.out.println("[8]. Đăng xuất");
            System.out.println("-------------------------------------------");
            System.out.print("👉 Lựa chọn của bạn (1-8): ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        handleViewAndSearchCourses(scanner);
                        break;
                    case "2":
                        enrollCourse(scanner);
                        break;
                    case "3":
                        showMyEnrollments(scanner);
                        break;
                    case "4":
                        cancelEnrollment(scanner);
                        break;
                    case "5":
                        showProfile();
                        break;
                    case "6":
                        updateProfile(scanner);
                        break;
                    case "7":
                        handleChangePassword(scanner);
                        break;
                    case "8":
                        System.out.println("🔒 Đã đăng xuất khỏi tài khoản Học viên.");
                        return;
                    default:
                        System.out.println("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // --- CÁC HÀM XỬ LÝ CHỨC NĂNG ---

    /**
     * TÍNH NĂNG 1: XEM VÀ TÌM KIẾM KHÓA HỌC
     * Đáp ứng yêu cầu: "Xem danh sách khóa học đang có" & "Tìm kiếm khóa học theo tên"
     */
    private void handleViewAndSearchCourses(Scanner scanner) {
        while (true) {
            System.out.println("\n--- 📚 DANH SÁCH KHÓA HỌC ---");
            System.out.println("[1]. Hiển thị tất cả khóa học");
            System.out.println("[2]. Tìm kiếm khóa học theo tên");
            System.out.println("[3]. Quay lại");
            System.out.print("👉 Lựa chọn: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("3")) return;

            try {
                if (choice.equals("1")) {
                    displayCourseTable(courseService.getAllCourses());
                } else if (choice.equals("2")) {

                    // LỚP PHÒNG THỦ: Chặn chuỗi rỗng và ép nhập đúng từ khóa
                    while (true) {
                        System.out.print("🔍 Nhập từ khóa tên khóa học (Hoặc gõ '0' để Hủy): ");
                        String keyword = scanner.nextLine().trim();

                        if (keyword.equals("0")) {
                            System.out.println("ℹ️ Đã hủy thao tác tìm kiếm.");
                            break; // Thoát vòng lặp nhập từ khóa, quay lại menu danh sách
                        }

                        if (keyword.isEmpty()) {
                            System.out.println("❌ Lỗi: Từ khóa tìm kiếm không được để trống! Vui lòng nhập lại.");
                            continue; // Bắt nhập lại
                        }

                        // (Tùy chọn) Bỏ comment đoạn này nếu bạn muốn ép nhập ít nhất 2 ký tự

                        if (keyword.length() < 2) {
                            System.out.println("❌ Lỗi: Vui lòng nhập ít nhất 2 ký tự để tìm kiếm hiệu quả hơn!");
                            continue;
                        }

                        // Nếu từ khóa hợp lệ thì gọi Service và in bảng
                        displayCourseTable(courseService.searchByName(keyword));
                        break; // Thoát vòng lặp sau khi tìm kiếm thành công
                    }

                } else {
                    System.out.println("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi truy xuất khóa học: " + e.getMessage());
            }
        }
    }

    /**
     * TÍNH NĂNG 2: ĐĂNG KÝ KHÓA HỌC
     */
    private void enrollCourse(Scanner scanner) {
        System.out.println("\n--- 📝 ĐĂNG KÝ KHÓA HỌC ---");

        // 1. Liệt kê danh sách khóa học
        System.out.println("Danh sách khóa học đang mở:");
        try {
            displayCourseTable(courseService.getAllCourses());
        } catch (Exception e) {
            System.out.println("❌ Lỗi hệ thống: Không thể tải danh sách khóa học lúc này.");
            return; // Nếu không tải được danh sách thì thoát luôn, không cho đăng ký
        }

        int courseId = -1;

        // 2. LỚP PHÒNG THỦ 1 (View Validation): Ép nhập đúng định dạng số bằng vòng lặp
        while (true) {
            System.out.print("👉 Nhập ID khóa học bạn muốn đăng ký (Hoặc nhập '0' để Hủy): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác đăng ký.");
                return; // Thoát hàm hoàn toàn
            }

            try {
                courseId = Integer.parseInt(input);
                break; // Vượt qua bài test định dạng -> Phá vòng lặp đi tiếp
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: ID khóa học phải là một số nguyên! Vui lòng nhập lại.");
            }
        }

        // 3. LỚP PHÒNG THỦ 2 (Business Validation): Kiểm tra logic nghiệp vụ dưới DB
        try {
            enrollmentService.enrollCourse(loggedInStudent.getId(), courseId);

            System.out.println("✅ Đăng ký thành công!");
            System.out.println("ℹ️ Đơn đăng ký của bạn đang ở trạng thái CHỜ DUYỆT (WAITING). Quản trị viên sẽ sớm xác nhận cho bạn.");

        } catch (Exception e) {
            // Nơi này sẽ hứng các lỗi như: ID không tồn tại, Đã đăng ký môn này rồi...
            System.out.println(e.getMessage());
        }
    }

    /**
     * TÍNH NĂNG 3: XEM VÀ SẮP XẾP KHÓA HỌC ĐÃ ĐĂNG KÝ
     */
    private void  showMyEnrollments(Scanner scanner) {
        // 1. Kéo toàn bộ lịch sử học tập của sinh viên này lên
        List<EnrollmentDetail> myEnrollments = enrollmentService.getMyEnrollments(loggedInStudent.getId());

        if (myEnrollments.isEmpty()) {
            System.out.println("ℹ️ Bạn chưa đăng ký khóa học nào.");
            return;
        }

        while (true) {
            System.out.println("\n--- 📚 LỊCH SỬ ĐĂNG KÝ KHÓA HỌC ---");
            System.out.println("[1]. Xem danh sách gốc");
            System.out.println("[2]. Sắp xếp theo TÊN khóa học (A-Z)");
            System.out.println("[3]. Sắp xếp theo TÊN khóa học (Z-A)");
            System.out.println("[4]. Sắp xếp theo NGÀY đăng ký (Mới nhất trước)");
            System.out.println("[5]. Sắp xếp theo NGÀY đăng ký (Cũ nhất trước)");
            System.out.println("[6]. Quay lại");
            System.out.print("👉 Lựa chọn: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("6")) return;

            List<EnrollmentDetail> sortedList;

            // 2. Sử dụng Java 8 Stream API để sắp xếp ngay trên RAM (Cực kỳ tối ưu)
            switch (choice) {
                case "1":
                    sortedList = myEnrollments;
                    break;
                case "2":
                    sortedList = myEnrollments.stream()
                            .sorted(Comparator.comparing(EnrollmentDetail::getCourseName))
                            .collect(Collectors.toList());
                    break;
                case "3":
                    sortedList = myEnrollments.stream()
                            .sorted(Comparator.comparing(EnrollmentDetail::getCourseName).reversed())
                            .collect(Collectors.toList());
                    break;
                case "4":
                    sortedList = myEnrollments.stream()
                            .sorted(Comparator.comparing(EnrollmentDetail::getRegisteredAt).reversed())
                            .collect(Collectors.toList());
                    break;
                case "5":
                    sortedList = myEnrollments.stream()
                            .sorted(Comparator.comparing(entity.EnrollmentDetail::getRegisteredAt))
                            .collect(Collectors.toList());
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
                    continue; // Bỏ qua lượt lặp này, bắt chọn lại
            }

            // 3. In kết quả đã sắp xếp ra bảng
            System.out.println("\n+---------+------------------------------------+---------------------+-------------+");
            System.out.printf("| %-7s | %-34s | %-19s | %-11s |\n", "Mã Đơn", "Tên khóa học", "Ngày đăng ký", "Trạng thái");
            System.out.println("+---------+------------------------------------+---------------------+-------------+");
            for (EnrollmentDetail e : sortedList) {
                // Format lại Timestamp cho đẹp (bỏ phần mili-giây)
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(e.getRegisteredAt());

                System.out.printf("| %-7d | %-34s | %-19s | %-11s |\n",
                        e.getEnrollmentId(), e.getCourseName(), formattedDate, e.getStatus());
            }
            System.out.println("+---------+------------------------------------+---------------------+-------------+");
        }
    }

    /**
     * TÍNH NĂNG 4: HỦY ĐĂNG KÝ KHÓA HỌC
     */
    private void cancelEnrollment(Scanner scanner) {
        System.out.println("\n--- ❌ HỦY ĐĂNG KÝ KHÓA HỌC ---");
        System.out.println("ℹ️ Gợi ý: Bạn nên vào mục [3] để kiểm tra chính xác 'Mã Đơn' của mình trước.");

        int enrollmentId = -1;

        // LỚP PHÒNG THỦ 1 (View Validation): Ép nhập đúng định dạng số
        while (true) {
            System.out.print("👉 Nhập Mã Đơn bạn muốn hủy (Hoặc gõ '0' để Quay lại): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác.");
                return;
            }

            try {
                enrollmentId = Integer.parseInt(input);
                break; // Hợp lệ định dạng -> Thoát vòng lặp nhập liệu
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: Mã đơn phải là một số nguyên! Vui lòng nhập lại.");
            }
        }

        // LỚP PHÒNG THỦ 2 (Business Validation): Kiểm tra logic sở hữu và trạng thái dưới DB
        try {
            enrollmentService.cancelEnrollment(loggedInStudent.getId(), enrollmentId);
            System.out.println("✅ Hủy đơn đăng ký khóa học thành công! Bản ghi đã được giải phóng khỏi hệ thống.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * TÍNH NĂNG 7: ĐỔI MẬT KHẨU (Bảo mật 2 yếu tố cục bộ)
     * Đáp ứng yêu cầu: "Đổi mật khẩu tài khoản (có xác thực qua email hoặc sđt và mật khẩu cũ)"
     */
    private void handleChangePassword(Scanner scanner) {
        System.out.println("\n--- 🔐 ĐỔI MẬT KHẨU TÀI KHOẢN ---");

        // 1. Xác thực Email hoặc SĐT
        System.out.print("Nhập Email hoặc Số điện thoại để xác minh: ");
        String verifyInput = scanner.nextLine().trim();

        if (!verifyInput.equals(loggedInStudent.getEmail()) && !verifyInput.equals(loggedInStudent.getPhone())) {
            System.out.println("❌ Lỗi: Email hoặc Số điện thoại xác minh không khớp với dữ liệu hệ thống!");
            return;
        }

        // 2. Xác thực Mật khẩu cũ NGAY LẬP TỨC (Fail-Fast)
        System.out.print("Nhập mật khẩu HIỆN TẠI: ");
        String oldPassword = scanner.nextLine().trim();

        // GỌI SERVICE KIỂM TRA LUÔN Ở ĐÂY
        if (!studentService.checkCurrentPassword(loggedInStudent.getId(), oldPassword)) {
            System.out.println("❌ Lỗi: Mật khẩu hiện tại không chính xác. Hủy thao tác đổi mật khẩu!");
            return; // Hất văng người dùng về menu chính luôn
        }

        // 3. Nếu vượt qua ải trên, mới cho phép nhập mật khẩu mới
        System.out.println("✅ Xác thực thành công!");
        System.out.println("Nhập mật khẩu MỚI...");
        String newPassword = InputUtil.getValidPassword(scanner);

        try {
            studentService.changePassword(loggedInStudent.getId(), oldPassword, newPassword);
            System.out.println("✅ Đổi mật khẩu thành công! Vui lòng sử dụng mật khẩu mới cho lần đăng nhập sau.");

            // Cập nhật lại session hiện tại
            loggedInStudent.setPassword(newPassword);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // --- CÁC HÀM TIỆN ÍCH HIỂN THỊ ĐÃ LÀM TỪ TRƯỚC ---
    private void displayCourseTable(List<Course> courses) {
        if (courses.isEmpty()) {
            System.out.println("ℹ️ Không tìm thấy khóa học nào.");
            return;
        }
        System.out.println("\n+----+------------------------------------+----------+--------------------+");
        System.out.printf("| %-2s | %-34s | %-8s | %-18s |\n", "ID", "Tên khóa học", "Số giờ", "Giảng viên");
        System.out.println("+----+------------------------------------+----------+--------------------+");
        for (Course c : courses) {
            System.out.printf("| %-2d | %-34s | %-8d | %-18s |\n", c.getId(), c.getName(), c.getDuration(), c.getInstructor());
        }
        System.out.println("+----+------------------------------------+----------+--------------------+\n");
    }

    private void showProfile() {
        System.out.println("\n--- 👤 THÔNG TIN CÁ NHÂN ---");
        System.out.println("- Họ và tên  : " + loggedInStudent.getName());
        System.out.println("- Ngày sinh  : " + loggedInStudent.getDob());
        System.out.println("- Email      : " + loggedInStudent.getEmail());
        System.out.println("- Điện thoại : " + loggedInStudent.getPhone());
        System.out.println("- Giới tính  : " + (loggedInStudent.getSex() == 1 ? "Nam" : "Nữ"));
        System.out.println("----------------------------");
    }

    private void updateProfile(Scanner scanner) {
        System.out.println("\n--- 📝 CẬP NHẬT THÔNG TIN ---");
        System.out.println("⚠️ Lưu ý: Email không được phép thay đổi.");
        try {
            loggedInStudent.setName(InputUtil.getValidName(scanner));
            loggedInStudent.setDob(InputUtil.getValidDate(scanner));
            loggedInStudent.setPhone(InputUtil.getValidPhone(scanner));
            loggedInStudent.setSex(InputUtil.getValidSex(scanner));

            studentService.updateStudent(loggedInStudent);
            System.out.println("✅ Cập nhật thông tin cá nhân thành công!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}