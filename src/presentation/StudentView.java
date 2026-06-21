package presentation;

import business.impl.EnrollmentServiceImpl;
import entity.Course;
import entity.EnrollmentDetail;
import entity.Student;
import business.impl.CourseServiceImpl;
import business.impl.StudentServiceImpl;
import utils.ConsoleUtils;
import utils.InputUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            ConsoleUtils.printMenuBox("🎓 HỆ THỐNG HỌC TẬP (STUDENT PORTAL)", new String[]{
                    "👤 Học viên: " + loggedInStudent.getName() + " | 📧 Email: " + loggedInStudent.getEmail(),
                    "[1]. Xem & Tìm kiếm danh sách khóa học",
                    "[2]. Đăng ký khóa học",
                    "[3]. Xem khóa học đã đăng ký (Sắp xếp)",
                    "[4]. Hủy đăng ký khóa học",
                    "[5]. Xem thông tin cá nhân",
                    "[6]. Cập nhật thông tin cá nhân",
                    "[7]. Đổi mật khẩu (Bảo mật 2 lớp)",
                    "[8]. Đăng xuất"
            });
            ConsoleUtils.printPrompt("👉 Lựa chọn của bạn (1-8): ");

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
                        ConsoleUtils.printlnInfo("🔒 Đã đăng xuất khỏi tài khoản Học viên.");
                        return;
                    default:
                        ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // --- CÁC HÀM XỬ LÝ CHỨC NĂNG ---

    /**
     * TÍNH NĂNG 1: XEM VÀ TÌM KIẾM KHÓA HỌC
     * HÀM XEM VÀ TÌM KIẾM KHÓA HỌC (TÍCH HỢP PHÂN TRANG & GỢI Ý NÂNG CAO)
     */
    private void handleViewAndSearchCourses(Scanner scanner) {
        while (true) {
            ConsoleUtils.printSubMenuTitle("📚 DANH SÁCH KHÓA HỌC");
            ConsoleUtils.printlnData("[1]. Hiển thị tất cả khóa học");
            ConsoleUtils.printlnData("[2]. Tìm kiếm khóa học theo tên");
            ConsoleUtils.printlnData("[3]. Quay lại Menu Học viên");
            ConsoleUtils.printPrompt("👉 Lựa chọn: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("3")) return;

            if (choice.equals("1")) {
                // Xem tất cả khóa học -> Truyền từ khóa rỗng ""
                displayPaginatedCoursesForStudent("", scanner);
            } else if (choice.equals("2")) {

                // LỚP PHÒNG THỦ: Ép nhập từ khóa tìm kiếm hợp lệ
                while (true) {
                    System.out.print("🔍 Nhập từ khóa tên khóa học (Hoặc gõ '0' để Hủy): ");
                    String keyword = scanner.nextLine().trim();

                    if (keyword.equals("0")) {
                        System.out.println("ℹ️ Đã hủy thao tác tìm kiếm.");
                        break;
                    }

                    if (keyword.isEmpty()) {
                        System.out.println("❌ Lỗi: Từ khóa tìm kiếm không được để trống! Vui lòng nhập lại.");
                        continue;
                    }

                    if (keyword.length() < 2) {
                        System.out.println("❌ Lỗi: Vui lòng nhập ít nhất 2 ký tự để tìm kiếm hiệu quả hơn!");
                        continue;
                    }

                    // Từ khóa đã sạch -> Bàn giao cho hàm phân trang hiển thị kết quả lọc
                    displayPaginatedCoursesForStudent(keyword, scanner);
                    break;
                }
            } else {
                System.out.println("❌ Lựa chọn không hợp lệ!");
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
    private void showMyEnrollments(Scanner scanner) {
        // 1. Kéo toàn bộ lịch sử học tập của sinh viên này lên
        List<EnrollmentDetail> myEnrollments = enrollmentService.getMyEnrollments(loggedInStudent.getId());

        if (myEnrollments.isEmpty()) {
            System.out.println("ℹ️ Bạn chưa đăng ký khóa học nào.");
            return;
        }

        while (true) {
            ConsoleUtils.printSubMenuTitle("📚 LỊCH SỬ ĐĂNG KÝ KHÓA HỌC");
            ConsoleUtils.printlnData("[1]. Xem danh sách gốc");
            ConsoleUtils.printlnData("[2]. Sắp xếp theo TÊN khóa học (A-Z)");
            ConsoleUtils.printlnData("[3]. Sắp xếp theo TÊN khóa học (Z-A)");
            ConsoleUtils.printlnData("[4]. Sắp xếp theo NGÀY đăng ký (Mới nhất trước)");
            ConsoleUtils.printlnData("[5]. Sắp xếp theo NGÀY đăng ký (Cũ nhất trước)");
            ConsoleUtils.printlnData("[6]. Quay lại");
            ConsoleUtils.printPrompt("👉 Lựa chọn: ");

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
            int[] widths = {7, 34, 19, 11};
            String[] headers = {"Mã Đơn", "Tên khóa học", "Ngày đăng ký", "Trạng thái"};
            List<String[]> rows = new ArrayList<>();
            for (EnrollmentDetail e : sortedList) {
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(e.getRegisteredAt());
                rows.add(new String[]{
                        String.valueOf(e.getEnrollmentId()),
                        e.getCourseName(),
                        formattedDate,
                        e.getStatus()
                });
            }
            System.out.println();
            ConsoleUtils.printTable(headers, widths, rows);
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
            ConsoleUtils.printlnInfo("ℹ️ Không tìm thấy khóa học nào.");
            return;
        }
        int[] widths = {2, 34, 8, 18};
        String[] headers = {"ID", "Tên khóa học", "Số giờ", "Giảng viên"};
        List<String[]> rows = new ArrayList<>();
        for (Course c : courses) {
            rows.add(new String[]{
                    String.valueOf(c.getId()),
                    c.getName(),
                    String.valueOf(c.getDuration()),
                    c.getInstructor()
            });
        }
        System.out.println();
        ConsoleUtils.printTable(headers, widths, rows);
        System.out.println();
    }

    private void displayCourseTable(List<Course> courses, String nameHeader) {
        if (courses.isEmpty()) {
            return;
        }
        int[] widths = {2, 34, 8, 18};
        String[] headers = {"ID", nameHeader, "Số giờ", "Giảng viên"};
        List<String[]> rows = new ArrayList<>();
        for (Course c : courses) {
            rows.add(new String[]{
                    String.valueOf(c.getId()),
                    c.getName(),
                    String.valueOf(c.getDuration()),
                    c.getInstructor()
            });
        }
        ConsoleUtils.printTable(headers, widths, rows);
    }

    private void displayPaginatedCoursesForStudent(String keyword, Scanner scanner) {
        int currentPage = 1;
        int pageSize = 5; // Hiển thị 5 khóa học trên mỗi trang

        while (true) {
            try {
                // 1. Lấy tổng số lượng bản ghi theo bộ lọc từ khóa
                int totalRecords = courseService.getTotalCoursesCount(keyword);
                if (totalRecords == 0) {
                    System.out.println("❌ Không tìm thấy khóa học nào phù hợp!");
                    return;
                }

                // 2. Tính tổng số trang
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                if (currentPage > totalPages) currentPage = totalPages;
                if (currentPage < 1) currentPage = 1;

                // 3. Kéo dữ liệu trang hiện tại lên
                List<Course> courses = courseService.getCoursesByPage(keyword, currentPage, pageSize);

                // =========================================================================
                // IN PHÂN ĐOẠN 1: ⭐ GỢI Ý KHÓA HỌC DÀNH RIÊNG CHO BẠN ⭐ (BONUS NÂNG CAO 2)
                // Điều kiện hiện: Chỉ hiện ở Trang 1 khi người dùng đang xem danh sách tổng quan (keyword rỗng)
                // =========================================================================
                if (keyword.isEmpty() && currentPage == 1) {
                    try {
                        List<Course> recommendations = courseService.getRecommendedCourses(loggedInStudent.getId(), 3);

                        if (!recommendations.isEmpty()) {
                            ConsoleUtils.printlnData("\n⭐ [GỢI Ý] CÓ THỂ BẠN SẼ THÍCH (Dựa trên các khóa học bạn và học viên cùng gu đã đăng ký):");
                            displayCourseTable(recommendations, "Tên khóa học gợi ý");
                        }
                    } catch (Exception e) {
                        // Phòng thủ: Nếu logic gợi ý lỗi (ví dụ DB rớt mạng), danh sách chính vẫn phải hiển thị mượt mà
                        System.out.println("ℹ️ Không thể tải mục gợi ý khóa học lúc này.");
                    }
                }

                // =========================================================================
                // IN PHÂN ĐOẠN 2: 📚 DANH SÁCH KHÓA HỌC CHÍNH (CÓ PHÂN TRANG)
                // =========================================================================
                String pageTitle = String.format("DANH SÁCH KHÓA HỌC HỆ THỐNG (Trang %d/%d) - Tổng: %d khóa", currentPage, totalPages, totalRecords);
                ConsoleUtils.printPageHeader(pageTitle);
                if (!keyword.isEmpty()) {
                    ConsoleUtils.printlnData("   🔍 Bộ lọc tìm kiếm từ khóa: [" + keyword + "]");
                }
                displayCourseTable(courses);
                ConsoleUtils.printDivider(73);

                // 4. MENU ĐIỀU HƯỚNG TẬP LỆNH LẬT TRANG
                ConsoleUtils.printlnData("Điều hướng: [N] Trang sau | [P] Trang trước | [Số] Nhảy tới trang | [0] Thoát");
                ConsoleUtils.printPrompt("👉 Nhập lựa chọn của bạn: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equals("0")) {
                    break; // Thoát màn hình phân trang, trả điều khiển về menu handleViewAndSearchCourses
                } else if (action.equals("N")) {
                    if (currentPage < totalPages) currentPage++;
                    else ConsoleUtils.printlnWarning("⚠️ Bạn đang ở trang cuối cùng!");
                } else if (action.equals("P")) {
                    if (currentPage > 1) currentPage--;
                    else ConsoleUtils.printlnWarning("⚠️ Bạn đang ở trang đầu tiên!");
                } else {
                    try {
                        int targetPage = Integer.parseInt(action);
                        if (targetPage >= 1 && targetPage <= totalPages) {
                            currentPage = targetPage;
                        } else {
                            System.out.println("❌ Trang không tồn tại!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chỉ nhập N, P, Số trang hoặc số 0.");
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi hệ thống: Không thể tải dữ liệu khóa học (" + e.getMessage() + ")");
                break;
            }
        }
    }

    private void showProfile() {
        ConsoleUtils.printSubMenuTitle("👤 THÔNG TIN CÁ NHÂN");
        ConsoleUtils.printlnData("- Họ và tên  : " + loggedInStudent.getName());
        ConsoleUtils.printlnData("- Ngày sinh  : " + loggedInStudent.getDob());
        ConsoleUtils.printlnData("- Email      : " + loggedInStudent.getEmail());
        ConsoleUtils.printlnData("- Điện thoại : " + loggedInStudent.getPhone());
        ConsoleUtils.printlnData("- Giới tính  : " + (loggedInStudent.getSex() == 1 ? "Nam" : "Nữ"));
    }

    private void updateProfile(Scanner scanner) {
        // Tạo một đối tượng tạm thời sao chép từ loggedInStudent
        // Điều này đảm bảo nếu người dùng sửa giữa chừng rồi ấn Hủy, dữ liệu gốc trong phiên đăng nhập không bị sai lệch
        Student tempStudent = new entity.Student();
        tempStudent.setId(loggedInStudent.getId());
        tempStudent.setName(loggedInStudent.getName());
        tempStudent.setDob(loggedInStudent.getDob());
        tempStudent.setPhone(loggedInStudent.getPhone());
        tempStudent.setSex(loggedInStudent.getSex());
        tempStudent.setEmail(loggedInStudent.getEmail());

        while (true) {
            ConsoleUtils.printSubMenuTitle("📝 CẬP NHẬT THÔNG TIN CÁ NHÂN");
            ConsoleUtils.printlnData("Thông tin hiện tại của bạn:");
            ConsoleUtils.printlnData("1. Họ và tên : " + tempStudent.getName());
            ConsoleUtils.printlnData("2. Ngày sinh  : " + tempStudent.getDob());
            ConsoleUtils.printlnData("3. Số điện thại: " + tempStudent.getPhone());
            ConsoleUtils.printlnData("4. Giới tính  : " + (tempStudent.getSex() == 1 ? "Nam" : "Nữ"));
            ConsoleUtils.printlnData("[5]. BẤM PHÍM 5 ĐỂ LƯU THAY ĐỔI");
            ConsoleUtils.printlnData("[0]. HỦY BỎ & QUAY LẠI (Không lưu)");
            ConsoleUtils.printPrompt("👉 Mời chọn mục cần chỉnh sửa (0-5): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("ℹ️ Đã hủy toàn bộ thay đổi thông tin cá nhân.");
                return;
            }

            try {
                switch (choice) {
                    case "1":
                        tempStudent.setName(InputUtil.getValidName(scanner));
                        break;
                    case "2":
                        tempStudent.setDob(InputUtil.getValidDate(scanner));
                        break;
                    case "3":
                        tempStudent.setPhone(InputUtil.getValidPhone(scanner));
                        break;
                    case "4":
                        tempStudent.setSex(InputUtil.getValidSex(scanner));
                        break;
                    case "5":
                        // Đồng bộ dữ liệu tạm thời vào đối tượng đăng nhập chính thức
                        loggedInStudent.setName(tempStudent.getName());
                        loggedInStudent.setDob(tempStudent.getDob());
                        loggedInStudent.setPhone(tempStudent.getPhone());
                        loggedInStudent.setSex(tempStudent.getSex());

                        // Gọi Service thực thi cập nhật xuống Database
                        studentService.updateStudent(loggedInStudent);
                        System.out.println("✅ Cập nhật thông tin cá nhân thành công!");
                        return; // Lưu xong thì thoát hàm
                    default:
                        ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi khi nhập liệu: " + e.getMessage());
            }
        }
    }
}