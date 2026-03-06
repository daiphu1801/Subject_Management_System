package com.example.bailam.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bailam.database.AppDatabase;
import com.example.bailam.database.User;
import com.example.bailam.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Kiểm tra trạng thái đã đăng nhập chưa (Auto Login)
        SharedPreferences pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        if (pref.getBoolean("isLoggedIn", false)) {
            goToMainActivity();
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Nút Đăng nhập
        binding.btnLogin.setOnClickListener(v -> {
            String user = binding.edtUsername.getText().toString().trim();
            String pass = binding.edtPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tài khoản và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra trong Room Database
            User u = AppDatabase.getInstance(this).appDao().login(user, pass);
            if (u != null) {
                // Lưu phiên đăng nhập
                pref.edit().putBoolean("isLoggedIn", true).putString("username", user).apply();
                goToMainActivity();
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Chuyển sang màn hình Đăng ký
        binding.tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Đóng LoginActivity để không quay lại được bằng nút Back
    }
}