package com.example.bailam.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.bailam.database.User;
import com.example.bailam.databinding.ActivityRegisterBinding;
import com.example.bailam.ui.viewmodel.LoginViewModel;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Lắng nghe kết quả đăng ký
        viewModel.getRegisterResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Tài khoản này đã tồn tại, vui lòng chọn tên khác!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Back
        binding.btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện khi nhấn nút Đăng ký
        binding.btnRegisterSubmit.setOnClickListener(v -> {
            String user = binding.edtRegUsername.getText().toString().trim();
            String pass = binding.edtRegPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển việc tạo và lưu cho ViewModel
            viewModel.registerUserWithCheck(user, pass);
        });
    }
}