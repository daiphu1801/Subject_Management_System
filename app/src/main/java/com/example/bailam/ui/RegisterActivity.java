package com.example.bailam.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bailam.database.AppDatabase;
import com.example.bailam.database.User;
import com.example.bailam.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo View Binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Xử lý sự kiện khi nhấn nút Đăng ký
        binding.btnRegisterSubmit.setOnClickListener(v -> {
            String user = binding.edtRegUsername.getText().toString().trim();
            String pass = binding.edtRegPassword.getText().toString().trim();

            // 1. Kiểm tra không được để trống
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Kiểm tra xem tài khoản đã tồn tại trong Database chưa
            User existingUser = AppDatabase.getInstance(this).appDao().checkUserExists(user);

            if (existingUser != null) {
                // Nếu đã có user này rồi thì báo lỗi
                Toast.makeText(this, "Tài khoản này đã tồn tại, vui lòng chọn tên khác!", Toast.LENGTH_SHORT).show();
            } else {
                // 3. Nếu chưa có thì tiến hành lưu User mới (SSP-69 logic tương tự)
                User newUser = new User(user, pass);
                AppDatabase.getInstance(this).appDao().registerUser(newUser);

                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                // Đóng màn hình này để quay lại màn hình Login
                finish();
            }
        });
    }
}