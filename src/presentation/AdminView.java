package presentation;

import business.impl.CourseServiceImpl;
import business.impl.EnrollmentServiceImpl;
import business.impl.StudentServiceImpl;
import dao.impl.StatisticDAO;
import entity.Course;
import entity.CourseStatDTO;
import entity.EnrollmentDetail;
import entity.Student;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AdminView {
    // Khởi tạo các Service cần thiết
    private final CourseServiceImpl courseService = new CourseServiceImpl();
    private final EnrollmentServiceImpl enrollmentService = new EnrollmentServiceImpl();
    private final StudentServiceImpl studentService = new StudentServiceImpl();
    private final StatisticDAO statisticDAO = new StatisticDAO();

    /**
     * MENU TỔNG (DASHBOARD) - Đây là nơi Admin vào đầu tiên sau khi Login
     */
    public void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===========================================");
            System.out.println("          HỆ THỐNG QUẢN TRỊ (ADMIN)        ");
            System.out.println("===========================================");
            System.out.println("[1]. Quản lý Khóa học");
            System.out.println("[2]. Quản lý Học viên");
            System.out.println("[3]. Quản lý Đăng ký khóa học");
            System.out.println("[4]. Thống kê & Báo cáo");
            System.out.println("[5]. Đăng xuất");
            System.out.println("-------------------------------------------");
            System.out.print("👉 Mời chọn phân hệ quản lý (1-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Rẽ nhánh vào Menu con của Khóa học
                    handleCourseManagement(scanner);
                    break;
                case "2":
                    // Rẽ nhánh vào Menu con của Học viên
                    handleStudentManagement(scanner);
                    break;
                case "3":
                    // Rẽ nhánh vào Menu con của Đăng ký
                    handleEnrollmentManagement(scanner);
                    break;
                case "4":
                    handleStatistics(scanner);
                    break;
                case "5":
                    System.out.println("🔒 Đã đăng xuất quyền Admin.");
                    return; // Thoát vòng lặp tổng, về màn hình Đăng nhập
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    /**
     * MENU CON 1: QUẢN LÝ KHÓA HỌC
     */
    private void handleCourseManagement(Scanner scanner) {
        while (true) {
            System.out.println("\n--- 📚 PHÂN HỆ QUẢN LÝ KHÓA HỌC ---");
            System.out.println("[1]. Xem danh sách | [2]. Thêm | [3]. Sửa | [4]. Xóa | [5]. Tìm kiếm | [6]. Sắp xếp | [7]. Quay lại Dashboard");
            System.out.print("👉 Lựa chọn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        displayCourseTable(courseService.getAllCourses());
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
                        System.out.println("❌ Lựa chọn không hợp lệ!");
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
            System.out.println("\n--- 👨‍🎓 PHÂN HỆ QUẢN LÝ HỌC VIÊN ---");
            System.out.println("[1]. Xem danh sách | [2]. Thêm | [3]. Sửa | [4]. Xóa | [5]. Tìm kiếm | [6]. Sắp xếp | [7]. Quay lại Dashboard");
            System.out.print("👉 Lựa chọn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        displayStudentTable(studentService.getAllStudents());
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
                        System.out.println("❌ Lựa chọn không hợp lệ!");
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
            System.out.println("\n--- 📋 PHÂN HỆ QUẢN LÝ ĐĂNG KÝ ---");
            System.out.println("[1]. Hiển thị danh sách sinh viên theo từng khóa | [2]. Duyệt sinh viên đăng ký | [3]. Xóa sinh viên khỏi khóa học | [4]. Quay lại Dashboard");
            System.out.print("👉 Lựa chọn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.println("\n--- DANH SÁCH SINH VIÊN THEO TỪNG KHÓA HỌC ---");
                        displayGroupedEnrollments();
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
                        System.out.println("❌ Lựa chọn không hợp lệ!");
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
            System.out.println("\n--- 📈 PHÂN HỆ THỐNG KÊ & BÁO CÁO ---");
            System.out.println("[1]. Tổng quan hệ thống (Tổng khóa học & Tổng học viên)");
            System.out.println("[2]. Tổng số học viên theo từng khóa học");
            System.out.println("[3]. Top 5 khóa học đông sinh viên nhất");
            System.out.println("[4]. Danh sách khóa học có trên 10 học viên");
            System.out.println("[5]. Quay lại Dashboard");
            System.out.print("👉 Lựa chọn: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    statisticDAO.printGeneralStatistics();
                    break;
                case "2":
                    System.out.println("\n📊 SỐ LƯỢNG HỌC VIÊN THEO TỪNG KHÓA:");
                    displayStatTable(statisticDAO.getStudentsPerCourse());
                    break;
                case "3":
                    System.out.println("\n🏆 TOP 5 KHÓA HỌC ĐÔNG SINH VIÊN NHẤT:");
                    displayStatTable(statisticDAO.getTop5Courses());
                    break;
                case "4":
                    System.out.println("\n🔥 CÁC KHÓA HỌC CÓ TRÊN 10 HỌC VIÊN:");
                    List<entity.CourseStatDTO> hotCourses = statisticDAO.getCoursesWithMoreThan10Students();
                    if (hotCourses.isEmpty()) {
                        System.out.println("ℹ️ Hiện tại chưa có khóa học nào vượt mốc 10 học viên.");
                    } else {
                        displayStatTable(hotCourses);
                    }
                    break;
                case "5":
                    return;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    // Hàm in bảng chung cho các báo cáo
    private void displayStatTable(List<CourseStatDTO> stats) {
        System.out.println("+------------------------------------------+------------------+");
        System.out.printf("| %-40s | %-16s |\n", "Tên khóa học", "Số lượng Học viên");
        System.out.println("+------------------------------------------+------------------+");
        for (CourseStatDTO stat : stats) {
            System.out.printf("| %-40s | %-16d |\n", stat.getCourseName(), stat.getStudentCount());
        }
        System.out.println("+------------------------------------------+------------------+");
    }

    private void displayCourseTable(List<Course> courses) {
        if (courses.isEmpty()) {
            System.out.println("ℹ️ Danh sách trống.");
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
        System.out.println("\n--- 🔍 TÌM KIẾM KHÓA HỌC ---");

        while (true) {
            System.out.print("Nhập từ khóa tên khóa học cần tìm (Hoặc gõ '0' để Quay lại): ");
            String keyword = scanner.nextLine().trim();

            if (keyword.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác tìm kiếm.");
                return;
            }

            // LỚP PHÒNG THỦ 1: Chặn tìm kiếm 1 ký tự (Tránh nhiễu dữ liệu)
            if (keyword.length() < 2) {
                System.out.println("❌ Lỗi: Vui lòng nhập ít nhất 2 ký tự để tìm kiếm hiệu quả hơn!");
                continue; // Bỏ qua phần dưới, quay lại đầu vòng lặp bắt nhập lại
            }

            // LỚP PHÒNG THỦ 2: Gọi Service và xử lý lỗi mượt mà (Không Crash)
            try {
                List<entity.Course> results = courseService.searchByName(keyword);

                // Hiển thị kết quả ra bảng
                displayCourseTable(results);

                break; // Xử lý xong xuôi thì phá vòng lặp để quay về Menu

            } catch (Exception e) {
                System.out.println("❌ Lỗi hệ thống: Quá trình tìm kiếm gặp sự cố.");
                break; // Nếu lỗi DB thì thoát vòng lặp trả về Menu, tránh kẹt vĩnh viễn
            }
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


    private void displayGroupedEnrollments() {
        Map<String, List<EnrollmentDetail>> groupedMap = enrollmentService.getEnrollmentsGroupedByCourse();
        if (groupedMap.isEmpty()) {
            System.out.println("ℹ️ Không có dữ liệu đăng ký nào trong hệ thống.");
            return;
        }
        // Duyệt qua từng entry trong Map (Mỗi Key là 1 Tên khóa học, Value là List sinh viên)
        for (java.util.Map.Entry<String, java.util.List<EnrollmentDetail>> entry : groupedMap.entrySet()) {
            System.out.println("\n📘 KHÓA HỌC: " + entry.getKey().toUpperCase());
            System.out.println("+----+----------------------+---------------------------+---------------------+----------+");
            System.out.printf("| %-2s | %-20s | %-25s | %-19s | %-8s |\n", "ID", "Tên Học viên", "Email", "Thời gian ĐK", "Status");
            System.out.println("+----+----------------------+---------------------------+---------------------+----------+");

            // In danh sách sinh viên thuộc khóa học đó
            for (EnrollmentDetail e : entry.getValue()) {
                System.out.printf("| %-2d | %-20s | %-25s | %-19s | %-8s |\n",
                        e.getEnrollmentId(),
                        e.getStudentName(),
                        e.getStudentEmail(),
                        e.getRegisteredAt().toString().substring(0, 19),
                        e.getStatus());
            }
            System.out.println("+----+----------------------+---------------------------+---------------------+----------+");
        }
    }

    private void handleApproveEnrollment(Scanner scanner) {
        System.out.println("\n--- ✔️ DUYỆT ĐƠN ĐĂNG KÝ KHÓA HỌC ---");
        int enrollmentId = 0;

        // Vòng lặp validate đầu vào phải là số nguyên
        while (true) {
            System.out.print("Nhập ID đơn đăng ký muốn DUYỆT: ");
            try {
                enrollmentId = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: ID đơn đăng ký phải là một số nguyên!");
            }
        }

        try {
            // Gọi Business xử lý cập nhật trạng thái thành CONFIRM
            enrollmentService.approveEnrollment(enrollmentId);
            System.out.println("✅ Đã duyệt đơn đăng ký có ID [" + enrollmentId + "] thành công!");
        } catch (Exception e) {
            // Bắt lỗi từ Business ném lên nếu không tìm thấy ID trong DB
            System.out.println(e.getMessage());
        }
    }

    private void handleRemoveEnrollment(Scanner scanner) {
        System.out.println("\n--- ❌ XÓA SINH VIÊN KHỎI KHÓA HỌC ---");
        int enrollmentId = 0;

        // Vòng lặp validate đầu vào phải là số nguyên
        while (true) {
            System.out.print("Nhập ID đơn đăng ký muốn XÓA: ");
            try {
                enrollmentId = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: ID đơn đăng ký phải là một số nguyên!");
            }
        }

        // Yêu cầu xác nhận trước khi thực hiện lệnh DELETE trong PostgreSQL
        System.out.print("⚠️ Bạn có chắc chắn muốn xóa sinh viên này khỏi khóa học? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if ("Y".equalsIgnoreCase(confirm)) {
            try {
                // Gọi Business xử lý xóa bản ghi
                enrollmentService.removeEnrollment(enrollmentId);
                System.out.println("✅ Đã xóa đơn đăng ký có ID [" + enrollmentId + "] khỏi hệ thống!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
            System.out.println("ℹ️ Danh sách học viên trống.");
            return;
        }
        System.out.println("\n+----+----------------------+---------------------------+-------------+------+------------+");
        System.out.printf("| %-2s | %-20s | %-25s | %-11s | %-4s | %-10s |\n", "ID", "Tên Học viên", "Email", "SĐT", "Phái", "Ngày Sinh");
        System.out.println("+----+----------------------+---------------------------+-------------+------+------------+");
        for (Student s : students) {
            String sexStr = s.getSex() == 1 ? "Nam" : "Nữ";
            System.out.printf("| %-2d | %-20s | %-25s | %-11s | %-4s | %-10s |\n",
                    s.getId(), s.getName(), s.getEmail(), s.getPhone(), sexStr, s.getDob().toString());
        }
        System.out.println("+----+----------------------+---------------------------+-------------+------+------------+\n");
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
                        student.setName(utils.InputUtil.getValidName(scanner));
                        break;
                    case "2":
                        student.setDob(utils.InputUtil.getValidDate(scanner));
                        break;
                    case "3":
                        student.setEmail(utils.InputUtil.getValidEmail(scanner));
                        break;
                    case "4":
                        student.setPhone(utils.InputUtil.getValidPhone(scanner));
                        break;
                    case "5":
                        student.setSex(utils.InputUtil.getValidSex(scanner));
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
        System.out.println("\n--- 🔍 TÌM KIẾM ĐA NĂNG (OMNI-SEARCH) ---");

        while (true) {
            System.out.print("Nhập từ khóa (Tên, Email hoặc ID) (Hoặc gõ '0' để Quay lại): ");
            String keyword = scanner.nextLine().trim();

            if (keyword.equals("0")) {
                System.out.println("ℹ️ Đã hủy thao tác tìm kiếm.");
                return;
            }

            // LỚP PHÒNG THỦ 1: Chặn chuỗi rỗng (Chống việc kéo toàn bộ DB lên do LIKE '%%')
            if (keyword.isEmpty()) {
                System.out.println("❌ Lỗi: Từ khóa tìm kiếm không được để trống! Vui lòng nhập lại.");
                continue; // Quay lại vòng lặp bắt nhập chữ
            }

            // (Tùy chọn) Có thể giữ luật tối thiểu 2 ký tự, nhưng vì có tìm ID (như ID 1, 2) nên isEmpty là hợp lý nhất ở đây

            // LỚP PHÒNG THỦ 2: Hứng lỗi an toàn
            try {
                List<entity.Student> results = studentService.searchStudents(keyword);

                // Hiển thị kết quả ra bảng
                displayStudentTable(results);

                break; // Tìm kiếm và in xong thì phá vòng lặp để quay về Menu

            } catch (Exception e) {
                System.out.println("❌ Lỗi hệ thống: Quá trình tìm kiếm gặp sự cố.");
                // System.out.println("Chi tiết: " + e.getMessage());
                break; // Thoát vòng lặp nếu có lỗi sâu từ DB
            }
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
}