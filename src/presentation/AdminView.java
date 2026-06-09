package presentation;

import business.impl.CourseServiceImpl;
import business.impl.EnrollmentServiceImpl;
import business.impl.StudentServiceImpl;
import dao.impl.StatisticDAO;
import entity.Course;
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
    private final StatisticDAO  statisticDAO = new StatisticDAO();

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
    private void displayStatTable(List<entity.CourseStatDTO> stats) {
        System.out.println("+------------------------------------------+------------------+");
        System.out.printf("| %-40s | %-16s |\n", "Tên khóa học", "Số lượng Học viên");
        System.out.println("+------------------------------------------+------------------+");
        for (entity.CourseStatDTO stat : stats) {
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
        try {
            Course course = new Course();
            System.out.print("Nhập tên khóa học: ");
            course.setName(scanner.nextLine());

            System.out.print("Nhập thời lượng (giờ): ");
            course.setDuration(Integer.parseInt(scanner.nextLine()));

            System.out.print("Nhập tên giảng viên phụ trách: ");
            course.setInstructor(scanner.nextLine());

            courseService.addCourse(course);
            System.out.println("✅ Thêm khóa học mới thành công!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: Thời lượng phải nhập bằng số nguyên!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleIdUpdate(Scanner scanner) {
        System.out.println("\n--- 📝 CHỈNH SỬA KHÓA HỌC ---");
        try {
            System.out.print("Nhập ID khóa học cần sửa: ");
            int id = Integer.parseInt(scanner.nextLine());

            Course course = courseService.getCourseById(id);
            if (course == null) {
                System.out.println("❌ ID khóa học không tồn tại, vui lòng kiểm tra lại!");
                return;
            }

            // Thiết lập menu con cho phép chọn thuộc tính cần sửa theo SRS
            System.out.println("Chọn thông tin cần sửa: [1] Tên | [2] Thời lượng | [3] Giảng viên");
            System.out.print("Lựa chọn của bạn: ");
            String subChoice = scanner.nextLine();

            switch (subChoice) {
                case "1":
                    System.out.print("Nhập tên mới: ");
                    course.setName(scanner.nextLine());
                    break;
                case "2":
                    System.out.print("Nhập thời lượng mới (giờ): ");
                    course.setDuration(Integer.parseInt(scanner.nextLine()));
                    break;
                case "3":
                    System.out.print("Nhập tên giảng viên mới: ");
                    course.setInstructor(scanner.nextLine());
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ. Hủy thao tác cập nhật.");
                    return;
            }

            courseService.updateCourse(course);
            System.out.println("✅ Cập nhật thông tin khóa học thành công!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: Định dạng số nhập vào không chính xác!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleIdDelete(Scanner scanner) {
        System.out.println("\n--- ❌ XÓA KHÓA HỌC ---");
        try {
            System.out.print("Nhập ID khóa học muốn xóa: ");
            int id = Integer.parseInt(scanner.nextLine());

            // Bắt buộc hiển thị câu hỏi xác nhận (Y/N) trước khi thực thi
            System.out.print("⚠️ Bạn có chắc chắn muốn xóa khóa học này không? (Y/N): ");
            String confirm = scanner.nextLine();

            if ("Y".equalsIgnoreCase(confirm)) {
                courseService.deleteCourse(id);
                System.out.println("✅ Đã xóa thành công khóa học có ID: " + id);
            } else {
                System.out.println("ℹ️ Đã hủy thao tác xóa.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: ID phải là số nguyên!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleIdSearch(Scanner scanner) {
        System.out.println("\n--- 🔍 TÌM KIẾM KHÓA HỌC (STREAM API) ---");
        System.out.print("Nhập từ khóa tên khóa học cần tìm: ");
        String keyword = scanner.nextLine();

        List<Course> results = null;
        try {
            results = courseService.searchByName(keyword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        displayCourseTable(results);
    }

    private void handleIdSort(Scanner scanner) {
        System.out.println("\n--- 🔀 SẮP XẾP KHÓA HỌC (STREAM API) ---");
        System.out.println("Chọn tiêu chí: [1] Theo ID | [2] Theo Tên");
        String typeChoice = scanner.nextLine();
        String type = typeChoice.equals("2") ? "NAME" : "ID";

        System.out.println("Chọn chiều: [1] Tăng dần | [2] Giảm dần");
        String orderChoice = scanner.nextLine();
        boolean isAscending = !orderChoice.equals("2");

        List<Course> results = null;
        try {
            results = courseService.sortCourses(type, isAscending);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        displayCourseTable(results);
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
        try {
            System.out.print("Nhập ID học viên cần sửa: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Student student = studentService.getStudentById(id);
            if (student == null) {
                System.out.println("❌ Không tìm thấy học viên với ID này!");
                return;
            }

            System.out.println("Chọn thông tin cần sửa: [1] Tên | [2] Ngày sinh | [3] Email | [4] SĐT | [5] Giới tính");
            System.out.print("Lựa chọn của bạn: ");
            String subChoice = scanner.nextLine().trim();

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
                    System.out.println("❌ Lựa chọn không hợp lệ. Hủy thao tác cập nhật.");
                    return;
            }

            // Gọi Business để check lại validate (đặc biệt là xem email mới có bị trùng không)
            studentService.updateStudent(student);
            System.out.println("✅ Cập nhật thông tin học viên thành công!");

        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: ID học viên phải là một số nguyên!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleStudentDelete(Scanner scanner) {
        System.out.println("\n--- ❌ XÓA HỌC VIÊN ---");
        try {
            System.out.print("Nhập ID học viên muốn xóa: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("⚠️ Bạn có chắc chắn muốn xóa học viên này không? (Y/N): ");
            if ("Y".equalsIgnoreCase(scanner.nextLine().trim())) {
                studentService.deleteStudent(id);
                System.out.println("✅ Đã xóa thành công học viên có ID: " + id);
            } else {
                System.out.println("ℹ️ Đã hủy thao tác xóa.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: ID phải là số nguyên!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleStudentSearch(Scanner scanner) {
        System.out.println("\n--- 🔍 TÌM KIẾM ĐA NĂNG (OMNI-SEARCH) ---");
        System.out.print("Nhập từ khóa (Tên, Email hoặc ID): ");
        String keyword = scanner.nextLine();

        List<Student> results = studentService.searchStudents(keyword);
        displayStudentTable(results);
    }

    private void handleStudentSort(Scanner scanner) {
        System.out.println("\n--- 🔀 SẮP XẾP HỌC VIÊN ---");
        System.out.println("Chọn tiêu chí: [1] Theo ID | [2] Theo Tên");
        String typeChoice = scanner.nextLine().trim();
        String type = typeChoice.equals("2") ? "NAME" : "ID";

        System.out.println("Chọn chiều: [1] Tăng dần | [2] Giảm dần");
        String orderChoice = scanner.nextLine().trim();
        boolean isAscending = !orderChoice.equals("2");

        List<Student> results = studentService.sortStudents(type, isAscending);
        displayStudentTable(results);
    }
}