package presentation;

import business.impl.AuditLogServiceImpl;
import business.impl.CourseServiceImpl;
import business.impl.EnrollmentServiceImpl;
import business.impl.StudentServiceImpl;
import dao.impl.StatisticDAO;
import entity.*;

import utils.ConsoleUtils;
import utils.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AdminView {
    // Khởi tạo các Service cần thiết
    private final CourseServiceImpl courseService = new CourseServiceImpl();
    private final EnrollmentServiceImpl enrollmentService = new EnrollmentServiceImpl();
    private final StudentServiceImpl studentService = new StudentServiceImpl();
    private final StatisticDAO statisticDAO = new StatisticDAO();
    private final AuditLogServiceImpl auditLogService = new AuditLogServiceImpl();

    /**
     * MENU TỔNG (DASHBOARD) - Đây là nơi Admin vào đầu tiên sau khi Login
     */
    public void showMenu(Scanner scanner) {
        while (true) {
            ConsoleUtils.printMenuBox("HỆ THỐNG QUẢN TRỊ (ADMIN)", new String[]{
                    "[1]. Quản lý Khóa học",
                    "[2]. Quản lý Học viên",
                    "[3]. Quản lý Đăng ký khóa học",
                    "[4]. Thống kê & Báo cáo",
                    "[5]. Nhật ký hệ thống (Audit Log)",
                    "[6]. Đăng xuất"
            });
            ConsoleUtils.printPrompt("👉 Mời chọn phân hệ quản lý (1-6): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleCourseManagement(scanner);
                    break;
                case "2":
                    handleStudentManagement(scanner);
                    break;
                case "3":
                    handleEnrollmentManagement(scanner);
                    break;
                case "4":
                    handleStatistics(scanner);
                    break;
                case "5":
                    handleViewAuditLogs(scanner);
                    break;
                case "6":
                    ConsoleUtils.printlnInfo("🔒 Đã đăng xuất quyền Admin.");
                    return; // Thoát vòng lặp tổng, về màn hình Đăng nhập
                default:
                    ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    /**
     * MENU CON 1: QUẢN LÝ KHÓA HỌC
     */
    private void handleCourseManagement(Scanner scanner) {
        while (true) {
            ConsoleUtils.printSubMenuTitle("📚 PHÂN HỆ QUẢN LÝ KHÓA HỌC");
            ConsoleUtils.printlnData("[1]. Xem danh sách | [2]. Thêm | [3]. Sửa | [4]. Xóa | [5]. Tìm kiếm | [6]. Sắp xếp | [7]. Quay lại Dashboard");
            ConsoleUtils.printPrompt("👉 Lựa chọn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        displayPaginatedCourses("", scanner);
                        break;
                    case "2":
                        handleIdInsert(scanner);
                        break;
                    case "3":
                        handleIdUpdate(scanner);
                        break;
                    case "4":
                        handleIdDelete(scanner);
                        break;
                    case "5":
                        handleIdSearch(scanner);
                        break;
                    case "6":
                        handleIdSort(scanner);
                        break;
                    case "7":
                        return; // Thoát menu con, quay về Dashboard
                    default:
                        ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * MENU CON 2: QUẢN LÝ HỌC VIÊN
     */
    private void handleStudentManagement(Scanner scanner) {
        while (true) {
            ConsoleUtils.printSubMenuTitle("👨‍🎓 PHÂN HỆ QUẢN LÝ HỌC VIÊN");
            ConsoleUtils.printlnData("[1]. Xem danh sách | [2]. Thêm | [3]. Sửa | [4]. Xóa | [5]. Tìm kiếm | [6]. Sắp xếp | [7]. Quay lại Dashboard");
            ConsoleUtils.printPrompt("👉 Lựa chọn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        displayPaginatedStudents("", scanner);
                        break;
                    case "2":
                        handleStudentInsert(scanner);
                        break;
                    case "3":
                        handleStudentUpdate(scanner);
                        break;
                    case "4":
                        handleStudentDelete(scanner);
                        break;
                    case "5":
                        handleStudentSearch(scanner);
                        break;
                    case "6":
                        handleStudentSort(scanner);
                        break;
                    case "7":
                        return; // Quay về Dashboard
                    default:
                        ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * MENU CON 3: QUẢN LÝ ĐĂNG KÝ
     */
    private void handleEnrollmentManagement(Scanner scanner) {
        while (true) {
            ConsoleUtils.printSubMenuTitle("📋 PHÂN HỆ QUẢN LÝ ĐĂNG KÝ");
            ConsoleUtils.printlnData("[1]. Hiển thị danh sách sinh viên theo từng khóa | [2]. Duyệt sinh viên đăng ký | [3]. Xóa sinh viên khỏi khóa học | [4]. Quay lại Dashboard");
            ConsoleUtils.printPrompt("👉 Lựa chọn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        ConsoleUtils.printSubMenuTitle("DANH SÁCH SINH VIÊN THEO TỪNG KHÓA HỌC");
                        displayPaginatedEnrollments(scanner);
                        break;
                    case "2":
                        handleApproveEnrollment(scanner);
                        break;
                    case "3":
                        handleRemoveEnrollment(scanner);
                        break;
                    case "4":
                        return;
                    default:
                        ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: Định dạng số nhập vào không chính xác!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * MENU CON 4: THỐNG KÊ & BÁO CÁO
     */
    private void handleStatistics(Scanner scanner) {
        while (true) {
            ConsoleUtils.printSubMenuTitle("📈 PHÂN HỆ THỐNG KÊ & BÁO CÁO");
            ConsoleUtils.printlnData("[1]. Tổng quan hệ thống (Tổng khóa học & Tổng học viên)");
            ConsoleUtils.printlnData("[2]. Tổng số học viên theo từng khóa học");
            ConsoleUtils.printlnData("[3]. Top 5 khóa học đông sinh viên nhất");
            ConsoleUtils.printlnData("[4]. Danh sách khóa học có trên 10 học viên");
            ConsoleUtils.printlnData("[5]. Quay lại Dashboard");
            ConsoleUtils.printPrompt("👉 Lựa chọn: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    statisticDAO.printGeneralStatistics();
                    break;
                case "2":
                    ConsoleUtils.printlnData("\n📊 SỐ LƯỢNG HỌC VIÊN THEO TỪNG KHÓA:");
                    displayStatTable(statisticDAO.getStudentsPerCourse());
                    break;
                case "3":
                    ConsoleUtils.printlnData("\n🏆 TOP 5 KHÓA HỌC ĐÔNG SINH VIÊN NHẤT:");
                    displayStatTable(statisticDAO.getTop5Courses());
                    break;
                case "4":
                    ConsoleUtils.printlnData("\n🔥 CÁC KHÓA HỌC CÓ TRÊN 10 HỌC VIÊN:");
                    List<CourseStatDTO> hotCourses = statisticDAO.getCoursesWithMoreThan10Students();
                    if (hotCourses.isEmpty()) {
                        ConsoleUtils.printlnInfo("ℹ️ Hiện tại chưa có khóa học nào vượt mốc 10 học viên.");
                    } else {
                        displayStatTable(hotCourses);
                    }
                    break;
                case "5":
                    return;
                default:
                    ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    private void displayStatTable(List<CourseStatDTO> stats) {
        int[] widths = {40, 16};
        String[] headers = {"Tên khóa học", "Số lượng Học viên"};
        List<String[]> rows = new ArrayList<>();
        for (CourseStatDTO stat : stats) {
            rows.add(new String[]{stat.getCourseName(), String.valueOf(stat.getStudentCount())});
        }
        ConsoleUtils.printTable(headers, widths, rows);
    }

    private void displayCourseTable(List<Course> courses) {
        if (courses.isEmpty()) {
            ConsoleUtils.printlnInfo("ℹ️ Danh sách trống.");
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

    private void displayPaginatedCourses(String keyword, Scanner scanner) {
        int currentPage = 1;
        int pageSize = 5; // Bạn có thể chỉnh sửa số lượng khóa học trên 1 trang ở đây

        while (true) {
            try {
                // Gọi Service để lấy tổng số lượng
                int totalRecords = courseService.getTotalCoursesCount(keyword);
                if (totalRecords == 0) {
                    System.out.println("❌ Không tìm thấy khóa học nào phù hợp!");
                    return;
                }

                // Tính tổng số trang
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                // Đảm bảo currentPage không bị vượt giới hạn khi dữ liệu thay đổi
                if (currentPage > totalPages) currentPage = totalPages;
                if (currentPage < 1) currentPage = 1;

                // Lấy dữ liệu của trang hiện tại
                List<Course> courses = courseService.getCoursesByPage(keyword, currentPage, pageSize);

                String pageTitle = String.format("DANH SÁCH KHÓA HỌC (Trang %d/%d) - Tổng: %d khóa", currentPage, totalPages, totalRecords);
                ConsoleUtils.printPageHeader(pageTitle);
                if (!keyword.isEmpty()) {
                    ConsoleUtils.printlnData("   🔍 Đang lọc theo từ khóa: [" + keyword + "]");
                }
                displayCourseTable(courses);
                ConsoleUtils.printDivider(73);

                ConsoleUtils.printlnData("Điều hướng: [N] Trang sau | [P] Trang trước | [Số] Nhảy tới trang | [0] Thoát");
                ConsoleUtils.printPrompt("👉 Nhập lựa chọn: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equals("0")) {
                    break; // Thoát khỏi màn hình phân trang
                } else if (action.equals("N")) {
                    if (currentPage < totalPages) currentPage++;
                    else ConsoleUtils.printlnWarning("⚠️ Bạn đang ở trang cuối cùng!");
                } else if (action.equals("P")) {
                    if (currentPage > 1) currentPage--;
                    else ConsoleUtils.printlnWarning("⚠️ Bạn đang ở trang đầu tiên!");
                } else {
                    // Xử lý nếu người dùng gõ số để nhảy trang trực tiếp
                    try {
                        int targetPage = Integer.parseInt(action);
                        if (targetPage >= 1 && targetPage <= totalPages) {
                            currentPage = targetPage;
                        } else {
                            System.out.println("❌ Trang không tồn tại!");
                        }
                    } catch (NumberFormatException e) {
                        ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi truy xuất dữ liệu phân trang: " + e.getMessage());
                break;
            }
        }
    }

    private void handleIdInsert(Scanner scanner) {
        System.out.println("\n--- ➕ THÊM MỚI KHÓA HỌC ---");
        entity.Course course = new entity.Course();

        // LỚP PHÒNG THỦ 1: Ép nhập Tên khóa học (Không để trống)
        while (true) {
            System.out.print("Nhập tên khóa học (Hoặc gõ '0' để Hủy): ");
            String name = scanner.nextLine().trim();

            if (name.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác thêm khóa học.");
                return;
            }
            if (name.isEmpty()) {
                System.out.println("❌ Lỗi: Tên khóa học không được để trống! Vui lòng nhập lại.");
            } else {
                course.setName(name);
                break; // Hợp lệ -> Đi tiếp sang nhập thời lượng
            }
        }

        // LỚP PHÒNG THỦ 2: Ép nhập Thời lượng (Phải là số nguyên và > 0)
        while (true) {
            System.out.print("Nhập thời lượng (giờ) (Hoặc gõ '0' để Hủy): ");
            String inputDuration = scanner.nextLine().trim();

            if (inputDuration.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác thêm khóa học.");
                return;
            }
            try {
                int duration = Integer.parseInt(inputDuration);
                if (duration <= 0) {
                    System.out.println("❌ Lỗi: Thời lượng khóa học phải lớn hơn 0! Vui lòng nhập lại.");
                } else {
                    course.setDuration(duration);
                    break; // Số chuẩn -> Đi tiếp sang nhập giảng viên
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: Thời lượng phải nhập bằng số nguyên! Vui lòng nhập lại.");
            }
        }

        // LỚP PHÒNG THỦ 3: Ép nhập Tên giảng viên phụ trách (Không để trống)
        while (true) {
            System.out.print("Nhập tên giảng viên phụ trách (Hoặc gõ '0' để Hủy): ");
            String instructor = scanner.nextLine().trim();

            if (instructor.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác thêm khóa học.");
                return;
            }
            if (instructor.isEmpty()) {
                System.out.println("❌ Lỗi: Tên giảng viên không được để trống! Vui lòng nhập lại.");
            } else {
                course.setInstructor(instructor);
                break; // Hợp lệ -> Kết thúc quá trình nhập liệu
            }
        }

        // LỚP PHÒNG THỦ 4: Gọi Service thực thi SQL
        try {
            courseService.addCourse(course);
            System.out.println("✅ Thêm khóa học mới thành công!");
        } catch (Exception e) {
            System.out.println("❌ Lỗi hệ thống: Không thể thêm khóa học lúc này.");
            // System.out.println("Chi tiết lỗi: " + e.getMessage());
        }
    }

    private void handleIdUpdate(Scanner scanner) {
        System.out.println("\n--- 📝 CHỈNH SỬA KHÓA HỌC ---");
        int id = -1;

        // LỚP PHÒNG THỦ 1: Vòng lặp ép nhập đúng định dạng ID khóa học
        while (true) {
            System.out.print("Nhập ID khóa học cần sửa (Hoặc gõ '0' để Hủy): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác cập nhật.");
                return;
            }

            try {
                id = Integer.parseInt(input);
                break; // Vượt qua kiểm tra định dạng -> Thoát vòng lặp
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: Định dạng số nhập vào không chính xác! Vui lòng nhập lại.");
            }
        }

        try {
            // LỚP PHÒNG THỦ 2: Kiểm tra ID có tồn tại trong DB không
            Course course = courseService.getCourseById(id);
            if (course == null) {
                System.out.println("❌ ID khóa học không tồn tại, vui lòng kiểm tra lại!");
                return;
            }

            // Thiết lập menu con cho phép chọn thuộc tính cần sửa theo SRS
            System.out.println("Chọn thông tin cần sửa: [1] Tên | [2] Thời lượng | [3] Giảng viên");
            System.out.print("Lựa chọn của bạn: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    System.out.print("Nhập tên mới: ");
                    course.setName(scanner.nextLine().trim());
                    break;
                case "2":
                    // LỚP PHÒNG THỦ 3: Ép nhập đúng định dạng số cho Thời lượng
                    while (true) {
                        System.out.print("Nhập thời lượng mới (giờ): ");
                        try {
                            int duration = Integer.parseInt(scanner.nextLine().trim());
                            course.setDuration(duration);
                            break; // Thoát vòng lặp nếu nhập số đúng
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Lỗi: Thời lượng phải là một số nguyên! Vui lòng nhập lại.");
                        }
                    }
                    break;
                case "3":
                    System.out.print("Nhập tên giảng viên mới: ");
                    course.setInstructor(scanner.nextLine().trim());
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ. Hủy thao tác cập nhật.");
                    return;
            }

            // Gọi Service để update xuống DB
            courseService.updateCourse(course);
            System.out.println("✅ Cập nhật thông tin khóa học thành công!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleIdDelete(Scanner scanner) {
        System.out.println("\n--- ❌ XÓA KHÓA HỌC ---");
        int id = -1;

        // LỚP PHÒNG THỦ 1: Vòng lặp ép nhập đúng định dạng ID
        while (true) {
            System.out.print("Nhập ID khóa học muốn xóa (Hoặc gõ '0' để Hủy): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác xóa.");
                return;
            }

            try {
                id = Integer.parseInt(input);
                break; // Vượt qua kiểm tra định dạng -> Thoát vòng lặp
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: ID phải là số nguyên! Vui lòng nhập lại.");
            }
        }

        try {
            // LỚP PHÒNG THỦ 2: Kiểm tra tồn tại trước khi hỏi xác nhận (Đáp ứng TC-02.7)
            Course course = courseService.getCourseById(id);
            if (course == null) {
                System.out.println("❌ ID khóa học không tồn tại, vui lòng kiểm tra lại!");
                return;
            }

            // In ra tên khóa học để Admin check lại lần cuối cho chắc chắn
            System.out.println("🔍 Đang chuẩn bị xóa khóa học: [" + course.getName() + "]");

            // Bắt buộc hiển thị câu hỏi xác nhận (Y/N) trước khi thực thi
            System.out.print("⚠️ Bạn có chắc chắn muốn xóa khóa học này không? (Y/N): ");
            String confirm = scanner.nextLine().trim();

            if ("Y".equalsIgnoreCase(confirm)) {
                // Gọi Service thực thi Xóa mềm
                courseService.deleteCourse(id);
                System.out.println("✅ Đã xóa thành công khóa học có ID: " + id);
            } else {
                System.out.println("ℹ️ Đã hủy thao tác xóa.");
            }
        } catch (Exception e) {
            // Nơi này sẽ hứng các lỗi nghiệp vụ như: "Không thể xóa vì đang có học viên đăng ký..."
            System.out.println(e.getMessage());
        }
    }

    private void handleIdSearch(Scanner scanner) {
        System.out.println("\n--- 🔍 TÌM KIẾM KHÓA HỌC (CÓ PHÂN TRANG) ---");

        while (true) {
            System.out.print("Nhập từ khóa tên khóa học cần tìm (Hoặc gõ '0' để Quay lại): ");
            String keyword = scanner.nextLine().trim();

            if (keyword.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác tìm kiếm.");
                return;
            }

            // Chặn tìm kiếm rỗng
            if (keyword.isEmpty()) {
                System.out.println("❌ Lỗi: Từ khóa không được để trống! Vui lòng nhập lại.");
                continue;
            }

            // Gọi hàm phân trang hiển thị kết quả lọc theo keyword
            displayPaginatedCourses(keyword, scanner);
            break; // Hiển thị xong, khi Admin bấm '0' thoát phân trang thì sẽ quay về Menu chính
        }
    }

    private void handleIdSort(Scanner scanner) {
        System.out.println("\n--- 🔀 SẮP XẾP KHÓA HỌC (STREAM API) ---");

        String type = "";
        boolean isAscending = true;

        // LỚP PHÒNG THỦ 1: Ép nhập đúng tiêu chí (ID hoặc Tên)
        while (true) {
            System.out.println("Chọn tiêu chí: [1] Theo ID | [2] Theo Tên (Hoặc gõ '0' để Quay lại)");
            System.out.print("👉 Lựa chọn của bạn: ");
            String typeChoice = scanner.nextLine().trim();

            if (typeChoice.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác sắp xếp.");
                return;
            } else if (typeChoice.equals("1")) {
                type = "ID";
                break;
            } else if (typeChoice.equals("2")) {
                type = "NAME";
                break;
            } else {
                System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chọn 1 hoặc 2.");
            }
        }

        // LỚP PHÒNG THỦ 2: Ép nhập đúng chiều sắp xếp (Tăng hoặc Giảm)
        while (true) {
            System.out.println("Chọn chiều: [1] Tăng dần | [2] Giảm dần (Hoặc gõ '0' để Quay lại)");
            System.out.print("👉 Lựa chọn của bạn: ");
            String orderChoice = scanner.nextLine().trim();

            if (orderChoice.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác sắp xếp.");
                return;
            } else if (orderChoice.equals("1")) {
                isAscending = true;
                break;
            } else if (orderChoice.equals("2")) {
                isAscending = false;
                break;
            } else {
                System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chọn 1 hoặc 2.");
            }
        }

        // LỚP PHÒNG THỦ 3: Gọi Service và hứng lỗi an toàn
        try {
            List<entity.Course> results = courseService.sortCourses(type, isAscending);
            displayCourseTable(results);
        } catch (Exception e) {
            System.out.println("❌ Lỗi hệ thống: Quá trình sắp xếp gặp sự cố.");
            // System.out.println("Chi tiết lỗi: " + e.getMessage());
        }
    }

    private void displayEnrollmentTable(List<EnrollmentDetail> enrollments) {
        int[] widths = {2, 30, 20, 25, 11};
        String[] headers = {"ID", "Khóa học", "Tên Học viên", "Email", "Trạng thái"};
        List<String[]> rows = new ArrayList<>();
        for (EnrollmentDetail e : enrollments) {
            rows.add(new String[]{
                    String.valueOf(e.getEnrollmentId()),
                    e.getCourseName(),
                    e.getStudentName(),
                    e.getStudentEmail(),
                    e.getStatus()
            });
        }
        ConsoleUtils.printTable(headers, widths, rows);
    }

    private void displayPaginatedEnrollments(Scanner scanner) {
        int currentPage = 1;
        int pageSize = 5;

        while (true) {
            try {
                int totalRecords = enrollmentService.getTotalEnrollmentsCount();
                if (totalRecords == 0) {
                    System.out.println("ℹ️ Không có dữ liệu đăng ký nào trong hệ thống.");
                    return;
                }

                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                if (currentPage > totalPages) currentPage = totalPages;
                if (currentPage < 1) currentPage = 1;

                List<EnrollmentDetail> enrollments = enrollmentService.getEnrollmentsByPage(currentPage, pageSize);

                String pageTitle = String.format("DANH SÁCH ĐƠN ĐĂNG KÝ (Trang %d/%d) - Tổng: %d đơn (Xếp mới nhất lên đầu)", currentPage, totalPages, totalRecords);
                ConsoleUtils.printPageHeader(pageTitle);
                displayEnrollmentTable(enrollments);

                ConsoleUtils.printlnData("Điều hướng: [N] Trang sau | [P] Trang trước | [Số] Nhảy tới trang | [0] Thoát");
                ConsoleUtils.printPrompt("👉 Nhập lựa chọn: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equals("0")) break;
                else if (action.equals("N")) {
                    if (currentPage < totalPages) currentPage++;
                    else ConsoleUtils.printlnWarning("⚠️ Bạn đang ở trang cuối cùng!");
                } else if (action.equals("P")) {
                    if (currentPage > 1) currentPage--;
                    else ConsoleUtils.printlnWarning("⚠️ Bạn đang ở trang đầu tiên!");
                } else {
                    try {
                        int targetPage = Integer.parseInt(action);
                        if (targetPage >= 1 && targetPage <= totalPages) currentPage = targetPage;
                        else System.out.println("❌ Trang không tồn tại!");
                    } catch (NumberFormatException e) {
                        ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi truy xuất dữ liệu phân trang: " + e.getMessage());
                break;
            }
        }
    }

    private void handleApproveEnrollment(Scanner scanner) {
        System.out.println("\n--- ✔️ DUYỆT ĐƠN ĐĂNG KÝ KHÓA HỌC ---");

        // BƯỚC ĐỘ UX: Gọi hiển thị danh sách phân trang để Admin nhìn tận mắt các ID đang chờ duyệt
        displayPaginatedEnrollments(scanner);

        int enrollmentId = 0;

        // Vòng lặp validate đầu vào phải là số nguyên (Có lối thoát 0)
        while (true) {
            System.out.print("\n👉 Nhập ID đơn đăng ký muốn DUYỆT (Hoặc gõ '0' để Quay lại): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác duyệt đơn.");
                return;
            }

            try {
                enrollmentId = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: ID đơn đăng ký phải là một số nguyên! Vui lòng nhập lại.");
            }
        }

        try {
            // Gọi Business xử lý cập nhật trạng thái thành CONFIRM
            enrollmentService.approveEnrollment(enrollmentId);
            System.out.println("✅ Đã duyệt đơn đăng ký có ID [" + enrollmentId + "] thành công!");
        } catch (Exception e) {
            // Bắt lỗi từ Business ném lên (ID không tồn tại, hoặc đơn này đã được duyệt từ trước rồi)
            System.out.println("❌ Lỗi nghiệp vụ: " + e.getMessage());
        }
    }

    private void handleRemoveEnrollment(Scanner scanner) {
        System.out.println("\n--- ❌ XÓA SINH VIÊN KHỎI KHÓA HỌC ---");

        // Gọi hiển thị danh sách phân trang trực quan
        displayPaginatedEnrollments(scanner);

        int enrollmentId = 0;

        // Vòng lặp validate đầu vào
        while (true) {
            System.out.print("\n👉 Nhập ID đơn đăng ký muốn XÓA (Hoặc gõ '0' để Quay lại): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác xóa đơn.");
                return;
            }

            try {
                enrollmentId = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: ID đơn đăng ký phải là một số nguyên! Vui lòng nhập lại.");
            }
        }

        // Yêu cầu xác nhận kép (Double Confirmation) chống bấm nhầm
        System.out.print("⚠️ Bạn có chắc chắn muốn xóa đơn đăng ký có ID [" + enrollmentId + "] không? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if ("Y".equalsIgnoreCase(confirm)) {
            try {
                // Gọi Business xử lý xóa bản ghi dưới DB
                enrollmentService.removeEnrollment(enrollmentId);
                System.out.println("✅ Đã xóa đơn đăng ký có ID [" + enrollmentId + "] khỏi hệ thống!");
            } catch (Exception e) {
                System.out.println("❌ Lỗi hệ thống: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ Đã hủy thao tác xóa đơn đăng ký.");
        }
    }

    /**
     * Tiện ích hiển thị danh sách học viên
     */
    private void displayStudentTable(List<Student> students) {
        if (students.isEmpty()) {
            ConsoleUtils.printlnInfo("ℹ️ Danh sách học viên trống.");
            return;
        }
        int[] widths = {2, 20, 25, 11, 4, 10};
        String[] headers = {"ID", "Tên Học viên", "Email", "SĐT", "Phái", "Ngày Sinh"};
        List<String[]> rows = new ArrayList<>();
        for (Student s : students) {
            String sexStr = s.getSex() == 1 ? "Nam" : "Nữ";
            rows.add(new String[]{
                    String.valueOf(s.getId()),
                    s.getName(),
                    s.getEmail(),
                    s.getPhone(),
                    sexStr,
                    s.getDob().toString()
            });
        }
        System.out.println();
        ConsoleUtils.printTable(headers, widths, rows);
        System.out.println();
    }

    private void handleStudentInsert(Scanner scanner) {
        System.out.println("\n--- ➕ THÊM MỚI HỌC VIÊN ---");
        try {
            Student student = new Student();

            student.setName(utils.InputUtil.getValidName(scanner));
            student.setDob(utils.InputUtil.getValidDate(scanner));
            student.setEmail(utils.InputUtil.getValidEmail(scanner));
            student.setSex(utils.InputUtil.getValidSex(scanner));
            student.setPhone(utils.InputUtil.getValidPhone(scanner));
            student.setPassword(utils.InputUtil.getValidPassword(scanner));

            // Gọi Business để validate tất cả và lưu (bao gồm check trùng Email, băm mật khẩu)
            studentService.addStudent(student);
            System.out.println("✅ Thêm học viên mới thành công!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayPaginatedStudents(String keyword, Scanner scanner) {
        int currentPage = 1;
        int pageSize = 5;

        while (true) {
            try {
                int totalRecords = studentService.getTotalStudentsCount(keyword);
                if (totalRecords == 0) {
                    System.out.println("❌ Không tìm thấy học viên nào phù hợp!");
                    return;
                }

                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                if (currentPage > totalPages) currentPage = totalPages;
                if (currentPage < 1) currentPage = 1;

                List<Student> students = studentService.getStudentsByPage(keyword, currentPage, pageSize);

                String pageTitle = String.format("DANH SÁCH HỌC VIÊN (Trang %d/%d) - Tổng: %d học viên", currentPage, totalPages, totalRecords);
                ConsoleUtils.printPageHeader(pageTitle);
                if (!keyword.isEmpty()) {
                    ConsoleUtils.printlnData("   🔍 Đang lọc theo từ khóa: [" + keyword + "]");
                }
                displayStudentTable(students);
                ConsoleUtils.printDivider(90);

                ConsoleUtils.printlnData("Điều hướng: [N] Trang sau | [P] Trang trước | [Số] Nhảy tới trang | [0] Thoát");
                ConsoleUtils.printPrompt("👉 Nhập lựa chọn: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equals("0")) break;
                else if (action.equals("N")) {
                    if (currentPage < totalPages) currentPage++;
                    else ConsoleUtils.printlnWarning("⚠️ Bạn đang ở trang cuối cùng!");
                } else if (action.equals("P")) {
                    if (currentPage > 1) currentPage--;
                    else ConsoleUtils.printlnWarning("⚠️ Bạn đang ở trang đầu tiên!");
                } else {
                    try {
                        int targetPage = Integer.parseInt(action);
                        if (targetPage >= 1 && targetPage <= totalPages) currentPage = targetPage;
                        else System.out.println("❌ Trang không tồn tại!");
                    } catch (NumberFormatException e) {
                        ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ!");
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi truy xuất dữ liệu phân trang: " + e.getMessage());
                break;
            }
        }
    }

    private void handleStudentUpdate(Scanner scanner) {
        System.out.println("\n--- 📝 CHỈNH SỬA THÔNG TIN HỌC VIÊN ---");
        int id = -1;

        // LỚP PHÒNG THỦ 1: Ép nhập đúng định dạng ID học viên (Kèm nút thoát)
        while (true) {
            System.out.print("Nhập ID học viên cần sửa (Hoặc gõ '0' để Hủy): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác cập nhật.");
                return;
            }

            try {
                id = Integer.parseInt(input);
                break; // Vượt qua kiểm tra định dạng -> Thoát vòng lặp
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: ID học viên phải là một số nguyên! Vui lòng nhập lại.");
            }
        }

        try {
            // LỚP PHÒNG THỦ 2: Kiểm tra sự tồn tại của học viên trong DB
            Student student = studentService.getStudentById(id);
            if (student == null) {
                System.out.println("❌ Không tìm thấy học viên với ID này!");
                return;
            }

            // In tên học viên ra để Admin kiểm tra chéo (Cross-check) tránh sửa nhầm người
            System.out.println("🔍 Đang chuẩn bị cập nhật thông tin cho học viên: [" + student.getName() + "]");

            // LỚP PHÒNG THỦ 3: Vòng lặp chống nhập sai lựa chọn Menu con
            while (true) {
                System.out.println("Chọn thông tin cần sửa: [1] Tên | [2] Ngày sinh | [3] Email | [4] SĐT | [5] Giới tính (Hoặc '0' để Hủy)");
                System.out.print("👉 Lựa chọn của bạn: ");
                String subChoice = scanner.nextLine().trim();

                if (subChoice.equals("0")) {
                    System.out.println("ℹ️ Đã hủy thao tác cập nhật.");
                    return;
                }

                boolean isValidChoice = true;
                switch (subChoice) {
                    case "1":
                        student.setName(InputUtil.getValidName(scanner));
                        break;
                    case "2":
                        student.setDob(InputUtil.getValidDate(scanner));
                        break;
                    case "3":
                        student.setEmail(InputUtil.getValidEmail(scanner));
                        break;
                    case "4":
                        student.setPhone(InputUtil.getValidPhone(scanner));
                        break;
                    case "5":
                        student.setSex(InputUtil.getValidSex(scanner));
                        break;
                    default:
                        System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chọn từ 1 đến 5.");
                        isValidChoice = false; // Đánh dấu là sai để vòng lặp chạy lại
                        break;
                }

                // Nếu Admin chọn đúng tính năng (1-5) và InputUtil xử lý xong, thoát vòng lặp Menu
                if (isValidChoice) {
                    break;
                }
            }

            // Gọi Business để thực thi (Business sẽ check xem Email mới đổi có bị trùng với ai khác không)
            studentService.updateStudent(student);
            System.out.println("✅ Cập nhật thông tin học viên thành công!");

        } catch (Exception e) {
            // Nơi này sẽ hứng các lỗi Business dội lên như "Email đã tồn tại trên hệ thống"
            System.out.println(e.getMessage());
        }
    }

    private void handleStudentDelete(Scanner scanner) {
        System.out.println("\n--- ❌ XÓA HỌC VIÊN ---");
        int id = -1;

        // LỚP PHÒNG THỦ 1: Vòng lặp ép nhập đúng định dạng ID (Kèm nút thoát)
        while (true) {
            System.out.print("Nhập ID học viên muốn xóa (Hoặc gõ '0' để Hủy): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác xóa.");
                return;
            }

            try {
                id = Integer.parseInt(input);
                break; // Vượt qua kiểm tra định dạng -> Thoát vòng lặp
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: ID phải là số nguyên! Vui lòng nhập lại.");
            }
        }

        try {
            // LỚP PHÒNG THỦ 2: Kiểm tra tồn tại trước khi hỏi xác nhận
            entity.Student student = studentService.getStudentById(id);
            if (student == null) {
                System.out.println("❌ Không tìm thấy học viên với ID này, vui lòng kiểm tra lại!");
                return;
            }

            // In ra Tên và Email để Admin "nhìn tận mắt" trước khi ra tay
            System.out.println("🔍 Đang chuẩn bị xóa học viên: [" + student.getName() + " - " + student.getEmail() + "]");

            // Bắt buộc hiển thị câu hỏi xác nhận (Y/N)
            System.out.print("⚠️ Bạn có chắc chắn muốn xóa học viên này không? (Y/N): ");
            String confirm = scanner.nextLine().trim();

            if ("Y".equalsIgnoreCase(confirm)) {
                // Gọi Service thực thi Xóa (Xóa mềm hoặc kiểm tra RBAC)
                studentService.deleteStudent(id);
                System.out.println("✅ Đã xóa thành công học viên có ID: " + id);
            } else {
                System.out.println("ℹ️ Đã hủy thao tác xóa.");
            }

        } catch (Exception e) {
            // Hứng các lỗi nghiệp vụ từ Service/DAO (Ví dụ: Học viên đang có đơn đăng ký, không được xóa cứng)
            System.out.println(e.getMessage());
        }
    }

    private void handleStudentSearch(Scanner scanner) {
        System.out.println("\n--- 🔍 TÌM KIẾM ĐA NĂNG (OMNI-SEARCH CÓ PHÂN TRANG) ---");

        while (true) {
            System.out.print("Nhập từ khóa (Tên, Email hoặc ID) (Hoặc gõ '0' để Quay lại): ");
            String keyword = scanner.nextLine().trim();

            if (keyword.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác tìm kiếm.");
                return;
            }

            // LỚP PHÒNG THỦ 1: Chặn chuỗi rỗng (Chống việc kéo toàn bộ DB do LIKE '%%')
            if (keyword.isEmpty()) {
                System.out.println("❌ Lỗi: Từ khóa tìm kiếm không được để trống! Vui lòng nhập lại.");
                continue;
            }

            // LỚP PHÒNG THỦ 2: Bàn giao từ khóa sạch cho hàm hiển thị phân trang
            displayPaginatedStudents(keyword, scanner);

            // Sau khi Admin thoát khỏi màn hình phân trang (gõ 0),
            // vòng lặp này phá vỡ để đưa Admin quay về Menu con Quản lý học viên
            break;
        }
    }

    private void handleStudentSort(Scanner scanner) {
        System.out.println("\n--- 🔀 SẮP XẾP HỌC VIÊN ---");

        String type = "";
        boolean isAscending = true;

        // LỚP PHÒNG THỦ 1: Ép nhập đúng tiêu chí (ID hoặc Tên)
        while (true) {
            System.out.println("Chọn tiêu chí: [1] Theo ID | [2] Theo Tên (Hoặc gõ '0' để Quay lại)");
            System.out.print("👉 Lựa chọn của bạn: ");
            String typeChoice = scanner.nextLine().trim();

            if (typeChoice.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác sắp xếp.");
                return;
            } else if (typeChoice.equals("1")) {
                type = "ID";
                break;
            } else if (typeChoice.equals("2")) {
                type = "NAME";
                break;
            } else {
                System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chọn 1 hoặc 2.");
            }
        }

        // LỚP PHÒNG THỦ 2: Ép nhập đúng chiều sắp xếp (Tăng hoặc Giảm)
        while (true) {
            System.out.println("Chọn chiều: [1] Tăng dần | [2] Giảm dần (Hoặc gõ '0' để Quay lại)");
            System.out.print("👉 Lựa chọn của bạn: ");
            String orderChoice = scanner.nextLine().trim();

            if (orderChoice.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác sắp xếp.");
                return;
            } else if (orderChoice.equals("1")) {
                isAscending = true;
                break;
            } else if (orderChoice.equals("2")) {
                isAscending = false;
                break;
            } else {
                System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chọn 1 hoặc 2.");
            }
        }

        // LỚP PHÒNG THỦ 3: Gọi Service và hứng lỗi an toàn
        try {
            List<entity.Student> results = studentService.sortStudents(type, isAscending);
            displayStudentTable(results);
        } catch (Exception e) {
            System.out.println("❌ Lỗi hệ thống: Quá trình sắp xếp gặp sự cố.");
            // System.out.println("Chi tiết lỗi: " + e.getMessage());
        }
    }

    private void displayAuditLogTable(List<AuditLog> logs) {
        int[] widths = {2, 18, 9, 10, 47, 19};
        String[] headers = {"ID", "Người thực hiện", "Thao tác", "Bảng nguồn", "Chi tiết thay đổi dữ liệu", "Thời gian ghi nhận"};
        List<String[]> rows = new ArrayList<>();
        for (AuditLog log : logs) {
            rows.add(new String[]{
                    String.valueOf(log.getId()),
                    log.getActorInfo(),
                    log.getActionType(),
                    log.getTargetTable(),
                    log.getActionDetails(),
                    log.getCreatedAt().toString().substring(0, 19)
            });
        }
        ConsoleUtils.printTable(headers, widths, rows);
    }

    /**
     * TÍNH NĂNG MỚI: XEM NHẬT KÝ HỆ THỐNG (AUDIT TRAIL)
     */
    private void handleViewAuditLogs(Scanner scanner) {
        int currentLimit = 20; // Mặc định hiển thị 20 logs mới nhất

        while (true) {
            try {
                // Gọi Service lấy danh sách log gần đây
                List<AuditLog> logs = auditLogService.getRecentSystemLogs(currentLimit);

                ConsoleUtils.printPageHeader("📬 LỊCH SỬ HOẠT ĐỘNG HỆ THỐNG (Hiển thị " + currentLimit + " log mới nhất)");

                if (logs.isEmpty()) {
                    ConsoleUtils.printlnInfo("ℹ️ Hệ thống chưa ghi nhận nhật ký nào.");
                } else {
                    displayAuditLogTable(logs);
                }

                // Menu điều khiển cấu hình số lượng hiển thị
                ConsoleUtils.printlnData("Tùy chọn: [Nhập số lớn hơn 0] Thay đổi số lượng log muốn xem | [0] Quay lại Dashboard");
                ConsoleUtils.printPrompt("👉 Lựa chọn của bạn: ");
                String input = scanner.nextLine().trim();

                if (input.equals("0")) {
                    return; // Thoát hàm quay về Dashboard chính
                }

                try {
                    int newLimit = Integer.parseInt(input);
                    if (newLimit > 0) {
                        currentLimit = newLimit; // Cập nhật lại số lượng hiển thị để vòng lặp tải lại
                    } else {
                        ConsoleUtils.printlnError("❌ Lỗi: Số lượng log muốn xem phải lớn hơn 0!");
                    }
                } catch (NumberFormatException e) {
                    ConsoleUtils.printlnError("❌ Lựa chọn không hợp lệ! Vui lòng nhập số hoặc gõ '0'.");
                }

            } catch (Exception e) {
                ConsoleUtils.printlnError("❌ Lỗi truy xuất nhật ký hệ thống: " + e.getMessage());
                return;
            }
        }
    }
}