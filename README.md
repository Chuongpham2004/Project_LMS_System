# 🎓 Hệ thống Quản lý Khóa học và Học viên (Course & Student Management System)

Một ứng dụng Console Java chuẩn kiến trúc Doanh nghiệp, cung cấp giải pháp toàn diện để quản lý luồng học tập, đăng ký tín chỉ và quản trị thông tin học viên. Dự án áp dụng mô hình phân quyền chặt chẽ (RBAC) cùng với các kỹ thuật tối ưu hóa Database và xử lý nghiệp vụ sâu (Business Logic Validation).

## 🚀 Công nghệ sử dụng (Tech Stack)
* **Ngôn ngữ:** Java 8+ (Áp dụng mạnh mẽ Stream API)
* **Cơ sở dữ liệu:** PostgreSQL
* **Tương tác DB:** JDBC (Java Database Connectivity) nguyên bản
* **Bảo mật:** Thuật toán băm mật khẩu `jbcrypt` (BCrypt)
* **Kiến trúc:** Phân lớp chuẩn 3-Tier (Presentation/View - Business/Service - Data Access/DAO)

## 🎯 Tính năng nổi bật (Key Features)

Dự án được phân luồng chặt chẽ thông qua cơ chế **Role-Based Access Control (RBAC)**:

### 👑 Phân hệ Quản trị viên (Admin Portal)
* Quản lý Học viên và Khóa học toàn diện (CRUD).
* 🛡️ **Soft Delete (Xóa mềm):** Bảo toàn lịch sử dữ liệu thông qua cờ `is_deleted` và `deleted_at`, tích hợp chặn xóa cứng với các thực thể đang có quan hệ khóa ngoại (Đang có đơn đăng ký).
* Quản lý Đơn đăng ký học tập (Duyệt `WAITING` -> `CONFIRM` hoặc Hủy đơn).
* 📊 **Thống kê nâng cao:** Ứng dụng truy vấn SQL phức tạp (`LEFT JOIN`, `GROUP BY`, `HAVING`) để xuất báo cáo số lượng học viên theo từng môn và lọc khóa học theo định mức.

### 🎓 Phân hệ Học viên (Student Portal)
* Tra cứu và Tìm kiếm khóa học theo tên.
* Đăng ký tham gia khóa học với cơ chế **Fail-Fast Validation**: Chặn đăng ký trùng lặp ngay từ tầng Business.
* Xem lịch sử đăng ký với khả năng **Sắp xếp Động (Dynamic Sorting)** đa chiều (Theo tên, Ngày đăng ký) sử dụng `Java 8 Stream API` để tối ưu RAM thay vì Query DB nhiều lần.
* Hủy môn học (An toàn): Kiểm tra chặt chẽ quyền sở hữu mã đơn (Tránh lỗi IDOR) và chặn hủy nếu đơn đã được Admin phê duyệt.
* Quản lý hồ sơ cá nhân và Cập nhật mật khẩu bảo mật cục bộ (Xác thực 2 lớp qua Email/SĐT và check Pass cũ BCrypt).

## 🏗️ Kiến trúc Hệ thống (Architecture)

Dự án tuân thủ nghiêm ngặt nguyên lý **Single Responsibility (Đơn trách nhiệm)**, mọi luồng dữ liệu đều được "phòng thủ nhiều lớp":
1. **Tầng View (Presentation):** Đảm nhiệm UI Console và Format Validation (Bắt lỗi định dạng kiểu số/chữ bằng vòng lặp `while`). Tuyệt đối không chứa logic nghiệp vụ.
2. **Tầng Service (Business):** Chứa quy tắc nghiệp vụ cốt lõi (Check điều kiện xóa, kiểm tra trùng lặp khóa học, băm mật khẩu BCrypt).
3. **Tầng DAO (Data Access):** Trực tiếp tương tác với PostgreSQL thông qua PreparedStatement để ngăn chặn hoàn toàn lỗi SQL Injection.

## ⚙️ Hướng dẫn cài đặt (Installation)

1. Clone dự án về máy tính: git clone https://github.com/Chuongpham2004/Project_LMS_System.git
2. Mở PostgreSQL (hoặc pgAdmin) và chạy file script database.sql để khởi tạo cấu trúc 3 bảng (student, course, enrollment).
3. Đảm bảo đã nhúng thư viện jbcrypt-0.4.jar và postgresql-driver.jar vào cấu hình Project Structure.
4. Cập nhật thông tin kết nối Database tại utils/DBUtil.java (username, password, port)
5. Build và chạy file Main.java để bắt đầu.

👨‍💻 Tác giả (Author)
Phạm Hoàng Chương * Sinh viên chuyên ngành Kỹ thuật Phần mềm (Software Engineering) - Trường Quốc tế, Đại học Quốc gia Hà Nội (VNU-IS).

Định hướng phát triển: Java Backend Developer (Spring Boot / Microservices).
