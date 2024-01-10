package com.example.nhom2_app_cham_cong_qr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nhom2_app_cham_cong_qr.databinding.ActivityMainBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DatabaseHelper databaseHelper;
    SimpleDateFormat simpleDateFormat;
    String date = null;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = simpleDateFormat.format(new Date());
        databaseHelper = new DatabaseHelper(MainActivity.this);

        binding.scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setPrompt("Nhấn phím tăng âm lượng để bật flash");
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(true);
                integrator.setCaptureActivity(Capture.class);
                integrator.initiateScan();
            }
        });

        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GenerateActivity.class));
            }
        });

        binding.recordedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AttendaceActivity.class);
                startActivity(i);
            }
        });

        // Cham thu cong

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Nhập mã nhân viên");

                // Tạo ô nhập
                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);

                // Xác nhận
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Lấy giá trị từ ô nhập
                        String employeeCode = input.getText().toString();
                        // Lưu dữ liệu vào cơ sở dữ liệu
                        // TODO: Lưu dữ liệu vào cơ sở dữ liệu
                        String trainee = input.getText().toString();
                        if (databaseHelper.addText(trainee, date)) {
                            Toast.makeText(MainActivity.this, "Đã ghi nhận thành công", Toast.LENGTH_LONG).show();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Mã nhân viên").
                                setMessage(input.getText()).
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).
                                show();
                    }
                });

                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                // Hiển thị dialog
                builder.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data
        );
        if (result.getContents() != null) {
            String trainee = result.getContents().toString();
            if (databaseHelper.addText(trainee, date)) {
                Toast.makeText(MainActivity.this, "Đã ghi nhận thành công", Toast.LENGTH_LONG).show();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Mã nhân viên").
                    setMessage(result.getContents()).
                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).
                    show();
        } else {
            Toast.makeText(this, "OOPS....có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }
}