
# Ứng dụng chấm công nhân viên bằng QR.
***Ứng dụng Chấm Công QR được thiết kế để đơn giản hóa quá trình chấm công nhân viên và cung cấp khả năng xuất dữ liệu chấm công ra file Excel. Dưới đây là mô tả chi tiết về cách sử dụng chức năng chấm công trong ứng dụng***
# Tính Năng
### Chấm Công Nhân Viên
* Khởi động ứng dụng trên thiết bị Android.
![Loading screen](https://drive.google.com/file/d/13lBQQur1Rr6HGR4slHntQifP9iCXUKRM/view?usp=drive_link)

* Có 2 cách để chấm công: Quét mã QR và chấm công bằng cách nhập mã
![Main menu](https://drive.google.com/file/d/1vqi_p0IZ3ua5ltOjX5dWyB94HCR_qEqg/view?usp=drive_link)

* Danh Sách Nhân Viên: Hiển thị danh sách nhân viên trong hộp chọn dạng danh sách.

![Danh sách nhân viên](https://drive.google.com/file/d/1A0vP77lNxuNBJyBqjeVhNEU8hFtBqnJL/view?usp=drive_link)

### Xuất Dữ Liệu Chấm Công Ra File Excel

* Quyền Truy Cập: Ứng dụng sẽ yêu cầu quyền ghi vào bộ nhớ để có thể tạo và lưu file Excel.

* Chọn Nút "Xuất Excel": Nếu quyền đã được cấp, nhấn nút "Xuất Excel" để bắt đầu quá trình xuất dữ liệu.

* Nhập Tên File Excel: Một hộp thoại sẽ xuất hiện, yêu cầu bạn nhập tên cho file Excel sẽ được lưu. Điều này giúp bạn có thể xác định dễ dàng nội dung của file sau này.

* Xuất Excel Thành Công: Sau khi nhập tên và nhấn "OK," ứng dụng sẽ tạo một file Excel chứa thông tin chi tiết về chấm công của nhân viên.
![Export Excel](https://drive.google.com/file/d/1shUX4cM7BuZUmMMfQNuJQL5TE4bwPfm2/view?usp=drive_link)

### Ghi Chú:
* Mỗi lần quét chấm công, một mức lương cơ bản sẽ được thêm vào dữ liệu để tính tổng lương.

* File Excel sẽ được lưu trong thư mục Downloads của bộ nhớ ngoại vi.

* Tổng lương của tất cả nhân viên cũng sẽ được thêm vào cuối file Excel.

### Yêu Cầu Quyền:
* Quyền ghi vào bộ nhớ để tạo và lưu file Excel.
### Cài Đặt và Sử Dụng:
* Clone repository và mở dự án bằng Android Studio.
* Chạy ứng dụng trên thiết bị Android hoặc máy ảo.
### Phiên Bản Android:
* Yêu cầu Android 5.0 (API level 21) hoặc mới hơn.
## Thư Viện Sử Dụng:
* Apache POI - Thư viện xử lý và tạo file Excel.


# Một Số Ví Dụ
**onCreate(Bundle savedInstanceState)**
Hàm này được gọi khi Activity được tạo ra. Nó được sử dụng để khởi tạo giao diện người dùng và thực hiện các công việc khởi tạo khác.
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Khởi tạo giao diện và các thành phần khác
    binding = ActivityAttendaceBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    // Cấu hình và hiển thị danh sách nhân viên
    setupEmployeeList();

    // Gán sự kiện cho nút xuất Excel
    binding.exportBtn.setOnClickListener(view -> {
        // Kiểm tra quyền và hiển thị hộp thoại nhập tên file
        requestStoragePermissionOrExport();
    });
}

```

**showFileNameDialog()**
Hàm này hiển thị một hộp thoại cho người dùng nhập tên cho file Excel sẽ được xuất.
```
private void showFileNameDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Nhập tên file Excel");

    final EditText input = new EditText(this);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    builder.setView(input);

    builder.setPositiveButton("OK", (dialog, which) -> {
        String fileName = input.getText().toString().trim();
        if (!TextUtils.isEmpty(fileName)) {
            FILE_NAME = fileName + ".xls";
            exportExcel();
        } else {
            makeToast("Tên file không được để trống.");
        }
    });

    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

    builder.show();
}
```
**exportExcel()**
Hàm này thực hiện quá trình xuất dữ liệu chấm công ra file Excel. Nó tính toán tổng lương và tạo file Excel tương ứng.
```
private void exportExcel() {
    // Lấy dữ liệu từ cơ sở dữ liệu
    ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
    arrayList.add(databaseHelper.getName());
    arrayList.add(databaseHelper.getDate());
    arrayList.add(databaseHelper.getAllText());

    // Xử lý dữ liệu và tính tổng lương
    ArrayList<ArrayList<String>> reformed = processAndCalculateSalary(arrayList);

    // Tạo file Excel trên thiết bị
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (createXLS(reformed)) {
            makeToast("Đã xuất file XLS thành công.");
        } else {
            makeToast("Không thể xuất file XLS.");
        }
    }
}

```
**createXLS(ArrayList<ArrayList<String>> list)**
Hàm này tạo một file Excel từ danh sách dữ liệu được truyền vào và lưu trữ nó trên bộ nhớ thiết bị.
```
@RequiresApi(api = Build.VERSION_CODES.Q)
public boolean createXLS(ArrayList<ArrayList<String>> list) {
    // Tạo workbook và sheet
    workbook = new HSSFWorkbook();
    sheet = workbook.createSheet(SHEET_NAME);

    // Tạo hàng và ô cho tiêu đề
    createTitleRow();

    // Tạo và định dạng các ô cho dữ liệu
    createDataRows(list);

    // Lưu file Excel vào bộ nhớ
    return saveExcelToFile();
}

```

**makeToast(String toast)**
Hàm này hiển thị thông báo ngắn (toast) trên giao diện người dùng.
```
public void makeToast(String toast) {
    Toast.makeText(AttendaceActivity.this, toast, Toast.LENGTH_LONG).show();
}

```
# Kết Luận

Ứng dụng Chấm Công QR là một giải pháp đơn giản và tiện ích cho việc quản lý điểm danh nhân viên, tuy nhiên, ứng dụng này cần phát triển thêm một vài yếu tố như tối ưu hóa giao diện, bảo mật, thống kê chi tiết, và tích hợp với các hệ thống khác sẽ nâng cao khả năng quản lý và trải nghiệm người dùng. Cần sự liên tục trong cập nhật và hỗ trợ khách hàng để đáp ứng đa dạng và phức tạp hóa của yêu cầu quản lý nhân sự.
# Authors

- [@Liêm Phạm](https://github.com/hieuliempham)
- [@Kiệt Đặng](https://github.com/kasiwanpb5)


