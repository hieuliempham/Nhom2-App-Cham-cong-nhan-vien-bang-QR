package com.example.nhom2_app_cham_cong_qr;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.nhom2_app_cham_cong_qr.databinding.ActivityAttendaceBinding;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.OutputStream;
import java.util.ArrayList;

public class AttendaceActivity extends AppCompatActivity {

    private static final String SHEET_NAME = "Sheet1";
    private static final String NAME_COL_HEADER = "Tên/Mã NV";
    private static final String DATE_COL_HEADER = "Ngày chấm công";
    private static final String CONCAT_COL_HEADER = "Nội dung";

    private static String FILE_NAME = "attendance.xls";




    HSSFWorkbook workbook;
    HSSFSheet sheet;
    HSSFRow row;
    HSSFCell cell;
    private static final int REQUEST_CODE = 1;
    ActivityAttendaceBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(AttendaceActivity.this);
        ArrayList<String> arrayList = databaseHelper.getName();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        binding.attendaceBox.setAdapter(arrayAdapter);

        binding.attendaceBox.setOnItemLongClickListener((adapterView, view, i, l) -> {
            copyText(arrayList.get(i));
            return false;
        });

        binding.exportBtn.setOnClickListener(View -> {
            if (ContextCompat.checkSelfPermission(AttendaceActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showFileNameDialog();
            } else {
                ActivityCompat.requestPermissions(AttendaceActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CODE);
            }
        });
    }

//    private void showFileNameDialog() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String currentDateAndTime = sdf.format(Calendar.getInstance().getTime());
//
//        FILE_NAME = "attendance_" + currentDateAndTime + ".xls";
//
//        exportExcel();
//    }
private void showFileNameDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Nhập tên file Excel");

    final EditText input = new EditText(this);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    builder.setView(input);

    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String fileName = input.getText().toString().trim();
            if (!TextUtils.isEmpty(fileName)) {
                FILE_NAME = fileName + ".xls";
                exportExcel();
            } else {
                makeToast("Tên file không được để trống.");
            }
        }
    });

    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    });

    builder.show();
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFileNameDialog();
            } else {
                Toast.makeText(AttendaceActivity.this, "Cung cấp quyền truy cập", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void copyText(String text) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("AttendanceItem", text);
        manager.setPrimaryClip(clipData);
        Toast.makeText(AttendaceActivity.this, "Lưu vào clipboard", Toast.LENGTH_LONG).show();
    }

    private void exportExcel() {
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        arrayList.add(databaseHelper.getName());
        arrayList.add(databaseHelper.getDate());
        arrayList.add(databaseHelper.getAllText());

        ArrayList<ArrayList<String>> reformed = new ArrayList<>();
        int totalSalary = 0;

        for (int i = 0; i < arrayList.get(0).size(); i++) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(arrayList.get(0).get(i)); // Tên
            temp.add(arrayList.get(1).get(i)); // Ngày
            temp.add(arrayList.get(2).get(i)); // Dữ liệu điểm danh
            temp.add("200000"); // Thêm cột Lương (200k mỗi lần quét)

            int salary = Integer.parseInt(temp.get(3)); // Lương cơ bản
            temp.add(String.valueOf(salary)); // Cột Lương cơ bản
            totalSalary += salary; // Cộng Lương cơ bản vào tổng

            reformed.add(temp);
        }

        // Thêm Tổng vào cuối danh sách
        ArrayList<String> emptyRow = new ArrayList<>();
        emptyRow.add(""); // Tên
        emptyRow.add(""); // Ngày
        emptyRow.add(""); // Dữ liệu điểm danh
        emptyRow.add(""); // Lương cơ bản
        emptyRow.add("");
        reformed.add(emptyRow);

        ArrayList<String> totalRow = new ArrayList<>();
        totalRow.add("Tổng lương"); // Tên
        totalRow.add(""); // Ngày
        totalRow.add(""); // Dữ liệu điểm danh
        totalRow.add(""); // Lương cơ bản
        totalRow.add(String.valueOf(totalSalary)); // Tổng
        reformed.add(totalRow);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (createXLS(reformed)) {
                makeToast("Đã xuất file XLS thành công.");
            } else {
                makeToast("Không thể xuất file XLS.");
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean createXLS(ArrayList<ArrayList<String>> list) {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(SHEET_NAME);
        row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.AQUA.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        cell = row.createCell(0);
        cell.setCellValue(NAME_COL_HEADER);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(DATE_COL_HEADER);
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue(CONCAT_COL_HEADER);
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("Lương cơ bản");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("Tổng");
        cell.setCellStyle(style);

        CellStyle nameStyle = workbook.createCellStyle();
        nameStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        nameStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        dateStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        CellStyle concatStyle = workbook.createCellStyle();
        concatStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
        concatStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        for (int i = 0; i < list.size(); i++) {
            ArrayList<String> temp = list.get(i);
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(temp.get(0));
            cell.setCellStyle(nameStyle);

            cell = row.createCell(1);
            cell.setCellValue(temp.get(1));
            cell.setCellStyle(dateStyle);

            cell = row.createCell(2);
            cell.setCellValue(temp.get(2));
            cell.setCellStyle(concatStyle);

            cell = row.createCell(3);
            cell.setCellValue(temp.get(3));
            cell.setCellStyle(concatStyle);

            cell = row.createCell(4);
            cell.setCellValue(temp.get(4));
            cell.setCellStyle(concatStyle);
        }

        Uri externalUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        String relativeLocation = Environment.DIRECTORY_DOWNLOADS;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, FILE_NAME);
        values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/excel");
        values.put(MediaStore.Files.FileColumns.TITLE, FILE_NAME);
        values.put(MediaStore.Files.FileColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, relativeLocation);
        values.put(MediaStore.Files.FileColumns.DATE_TAKEN, System.currentTimeMillis());

        Uri fileUri = getContentResolver().insert(externalUri, values);
        try {
            OutputStream stream = getContentResolver().openOutputStream(fileUri);
            workbook.write(stream);
            stream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void makeToast(String toast) {
        Toast.makeText(AttendaceActivity.this, toast, Toast.LENGTH_LONG).show();
    }
}
