package presentation;

import business.impl.CourseServiceImpl;
import entity.Course;

import java.util.List;
import java.util.Scanner;

public class AdminView {
    private final CourseServiceImpl courseService = new CourseServiceImpl();

    public void showMenu(Scanner scanner) throws Exception {
        while (true) {
            System.out.println("\n===========================================");
            System.out.println("            MENU QUẢN TRỊ VIÊN             ");
            System.out.println("===========================================");
            System.out.println("[1]. Xem danh sách khóa học");
            System.out.println("[2]. Thêm mới khóa học");
            System.out.println("[3]. Chỉnh sửa thông tin khóa học");
            System.out.println("[4]. Xóa khóa học theo ID");
            System.out.println("[5]. Tìm kiếm khóa học theo tên");
            System.out.println("[6]. Sắp xếp danh sách khóa học");
            System.out.println("[7]. Đăng xuất (Quay lại Menu chính)");
            System.out.println("-------------------------------------------");
            System.out.print("👉 Mời chọn chức năng (1-7): ");

            String choice = scanner.nextLine();

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
                    System.out.println("🔒 Đã đăng xuất quyền Admin.");
                    return; // Thoát hàm showMenu, quay lại vòng lặp Main.java
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng nhập từ 1 đến 7.");
                    break;
            }
        }
    }

    /**
     * Tiện ích hiển thị danh sách dạng bảng thẳng hàng
     */
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
            while (true) {
                System.out.print("Nhập tên khóa học: ");
                String name = scanner.nextLine().trim(); // trim() để xóa dấu cách thừa
                if (!name.isEmpty()) {
                    course.setName(name);
                    break; // Nhập đúng thì thoát vòng lặp đi tiếp
                }
                System.out.println("❌ Lỗi: Không được để trống. Vui lòng nhập lại!");
            }

            // 2. Khóa vòng lặp bắt nhập Thời lượng (Phải là số nguyên > 0)
            while (true) {
                System.out.print("Nhập thời lượng (giờ): ");
                String durationStr = scanner.nextLine().trim();
                try {
                    int duration = Integer.parseInt(durationStr);
                    if (duration > 0) {
                        course.setDuration(duration);
                        break;
                    } else {
                        System.out.println("❌ Lỗi: Thời lượng phải lớn hơn 0!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Lỗi: Bạn phải nhập bằng số nguyên!");
                }
            }

            // 3. Khóa vòng lặp bắt nhập Giảng viên
            while (true) {
                System.out.print("Nhập tên giảng viên phụ trách: ");
                String instructor = scanner.nextLine().trim();
                if (!instructor.isEmpty()) {
                    course.setInstructor(instructor);
                    break;
                }
                System.out.println("❌ Lỗi: Không được để trống. Vui lòng nhập lại!");
            }

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

            System.out.println("Chọn thông tin cần sửa: [1] Tên | [2] Thời lượng | [3] Giảng viên");
            System.out.print("Lựa chọn của bạn: ");
            String subChoice = scanner.nextLine();

            switch (subChoice) {
                case "1":
                    while (true) {
                        System.out.print("Nhập tên mới: ");
                        String newName = scanner.nextLine().trim();
                        if (!newName.isEmpty()) {
                            course.setName(newName);
                            break;
                        }
                        System.out.println("❌ Lỗi: Tên không được để trống!");
                    }
                    break;
                case "2":
                    while (true) {
                        System.out.print("Nhập thời lượng mới (giờ): ");
                        try {
                            int newDuration = Integer.parseInt(scanner.nextLine().trim());
                            if (newDuration > 0) {
                                course.setDuration(newDuration);
                                break;
                            } else {
                                System.out.println("❌ Lỗi: Thời lượng phải lớn hơn 0!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Lỗi: Bạn phải nhập bằng số nguyên!");
                        }
                    }
                    break;
                case "3":
                    while (true) {
                        System.out.print("Nhập tên giảng viên mới: ");
                        String newInstructor = scanner.nextLine().trim();
                        if (!newInstructor.isEmpty()) {
                            course.setInstructor(newInstructor);
                            break;
                        }
                        System.out.println("❌ Lỗi: Tên giảng viên không được để trống!");
                    }
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

    private void handleIdSearch(Scanner scanner) throws Exception {
        System.out.println("\n--- 🔍 TÌM KIẾM KHÓA HỌC (STREAM API) ---");
        System.out.print("Nhập từ khóa tên khóa học cần tìm: ");
        String keyword = scanner.nextLine();

        List<Course> results = courseService.searchByName(keyword);
        displayCourseTable(results);
    }

    private void handleIdSort(Scanner scanner) throws Exception {
        System.out.println("\n--- 🔀 SẮP XẾP KHÓA HỌC (STREAM API) ---");
        System.out.println("Chọn tiêu chí: [1] Theo ID | [2] Theo Tên");
        String typeChoice = scanner.nextLine();
        String type = typeChoice.equals("2") ? "NAME" : "ID";

        System.out.println("Chọn chiều: [1] Tăng dần | [2] Giảm dần");
        String orderChoice = scanner.nextLine();
        boolean isAscending = !orderChoice.equals("2");

        List<Course> results = courseService.sortCourses(type, isAscending);
        displayCourseTable(results);
    }
}