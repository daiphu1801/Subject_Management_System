package com.example.bailam.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.bailam.database.User;
import com.example.bailam.databinding.ActivityLoginBinding;
import com.example.bailam.ui.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

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
        
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Lắng nghe kết quả login
        viewModel.getLoginResult().observe(this, u -> {
            if (u != null) {
                // Lưu phiên đăng nhập
                pref.edit().putBoolean("isLoggedIn", true).putString("username", u.getUsername()).apply();
                goToMainActivity();
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Nút Đăng nhập
        binding.btnLogin.setOnClickListener(v -> {
            String user = binding.edtUsername.getText().toString().trim();
            String pass = binding.edtPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tài khoản và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi ViewModel thay vì gọi thẳng DB
            viewModel.login(user, pass);
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