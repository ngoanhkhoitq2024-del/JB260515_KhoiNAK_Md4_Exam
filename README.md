### 1. Giải thích cơ chế Service Discovery: tại sao Gateway không gọi trực tiếp IP/P/Port?
Service Discovery là cơ chế cho phép các service tự đăng kí thông tin của mình với một hệ thống quản lí tập trung, ở bài này là Eureka Server.
Khi Student Service hoặc Course Service khởi động, chúng sẽ đăng kí với Eureka bằng tên service và thông tin địa chỉ đang hoạt động.
Khi ApiGateway nhận request, Gateway sử dụng Service ID để tìm service đích thông qua Eureka thay vì gọi trực tiếp một địa chỉ IP và port cố định.

* Tại sao Gateway không gọi trực tiếp IP/P/Port?
- **Giảm phụ thuộc vào địa chỉ cố định:** Khi IP hoặc Port của service thay đổi, không cần sửa lại cấu hình Gateway
- **Dễ mở rộng:** Khi có nhiều instance của cùng 1 service Eureka có thể quản lí danh sách các instance đó.
- **Dễ bảo trì:** Các service có thể restart hoặc thay đổi địa chỉ mà không cần cấu hình lại địa chỉ đích trong Gateway.
- **Load Balancing:** Gateway có thể phân phối request đến các instance đang được đăng kí, giúp chia tải.

### 2. Nếu số lượng request tăng đột biến, làm thế nào để scale Student Service mà không thay đổi cấu hình Gateway?
Sử dụng phương pháp mở rộng theo chiều ngang bằng cách triển khai nhiều instance của Student Service.
- Khởi chạy thêm nhiều instance Student Service trên các port khác nhau
- Các instance đó cùng đăng kí với Eureka bằng 1 Service ID thống nhất, vd: STUDENT-SERVICE
- Eureka sẽ cập nhật danh sách các instance đang hoạt động
- API Gateway sẽ cấu hình và Spring Cloud Balancer sẽ phân phối request đến các instance của Student Service

### 3. So sánh ưu/nhược điểm OpenFeign (Đồng bộ) vs Kafka (Bất ất đồng bộ) trong bài toán này
  **OpenFeign**
* Ưu điểm:
- Triển khai đơn giản, dễ hiểu và dễ kiểm thử
- Student Service có thể quyết định đăng kí thành công hay thất bại dựa trên kết quả kiểm tra
- Phù hợp với nghiệp vụ bài này vì cần biết kết quả ngay như đăng kí khóa học
* Nhược điểm:
- Student Service phải chờ Course Service phản hồi
- Nếu Course Service bị chậm hoặc không hoạt động thì quá trình đăng kí cũng sẽ bị ảnh hưởng
- Hai service sẽ bị phụ thuộc trong thời gian xử lí request

  **Kafka**
* Ưu điểm
- Hai service không cần chờ nhau xử lí trực tiếp
- Giảm sự phụ thuộc trực tiếp giữa 2 service
- Phù hợp với các nghiệp vụ xử lí lượng lớn event
* Nhược điểm
- Cấu hình và triển khai phức tạp hơn
- Cần quản lí topic, consumer, producer và cơ chế xử lí lỗi
- Kết quả không được trả về ngay lập tức
- Dữ liệu giữa các service có thể chưa đồng bộ tức thời.

-> Đối với bài toán trên, OpenFeign phù hợp hơn vì Student Service cần nhận kết quả kiểm tra từ Course Service trước khi xác nhận đăng kí.
OpenFeign giúp luồng xử lí rõ ràng, dễ triển khai và dễ kiểm thử. Kafka sẽ ohuf hợp với khi hệ thông có lượng event lớn, cần xử lí bất đồng bộ.

