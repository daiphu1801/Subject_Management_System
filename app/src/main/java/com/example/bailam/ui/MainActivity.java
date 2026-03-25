package com.example.bailam.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu; // Thêm cái này
import android.view.MenuItem; // Thêm cái này
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView; 
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.lifecycle.ViewModelProvider;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import com.example.bailam.worker.ReminderWorker;
import java.util.concurrent.TimeUnit;

import com.example.bailam.R;
import com.example.bailam.adapter.SubjectAdapter;
import com.example.bailam.database.Subject;
import com.example.bailam.database.SubjectWithProgress;
import com.example.bailam.databinding.ActivityMainBinding;
import com.example.bailam.databinding.DialogAddSubjectBinding;
import com.example.bailam.ui.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SubjectAdapter adapter;
    private List<SubjectWithProgress> subjectList = new ArrayList<>();
    private List<SubjectWithProgress> fullDataList = new ArrayList<>();
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }
        
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Yêu cầu quyền Thông báo trên Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Hẹn giờ Worker chạy mỗi 24 tiếng để kiểm tra Task
        PeriodicWorkRequest reminderRequest = new PeriodicWorkRequest.Builder(ReminderWorker.class, 24, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DailyReminder",
                ExistingPeriodicWorkPolicy.KEEP,
                reminderRequest
        );

        setupRecyclerView();
        
        // Theo dõi dữ liệu tự động thay đổi
        viewModel.getAllSubjects().observe(this, data -> {
            fullDataList.clear();
            fullDataList.addAll(data);
            adapter.setFilter(data);
            
            if (data.isEmpty()) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
            }
        });

        binding.btnAddSubject.setOnClickListener(v -> showAddSubjectDialog());
    }

    private void setupRecyclerView() {
        // Khởi tạo Adapter với Listener
        adapter = new SubjectAdapter(subjectList, new SubjectAdapter.OnSubjectClickListener() {
            @Override
            public void onSubjectClick(Subject subject) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("SUBJECT_ID", subject.getId());
                intent.putExtra("SUBJECT_NAME", subject.getSubjectName());
                startActivity(intent);
            }

            @Override
            public void onSubjectLongClick(Subject subject) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xóa môn học")
                        .setMessage("Bạn có chắc muốn xóa môn '" + subject.getSubjectName() + "'?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            viewModel.deleteSubject(subject);
                            Toast.makeText(MainActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        // QUAN TRỌNG: Phải gán LayoutManager và Adapter cho RecyclerView
        binding.rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSubjects.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            PopupMenu popup = new PopupMenu(this, binding.toolbar);

            SharedPreferences settings = getSharedPreferences("AppSettings", MODE_PRIVATE);
            boolean isDark = settings.getBoolean("isDarkMode", false);

            popup.getMenu().add(0, 1, 0, isDark ? "Chế độ Sáng" : "Chế độ Tối");
            popup.getMenu().add(0, 2, 1, "Đăng xuất");

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == 1) {
                    boolean newMode = !isDark;
                    settings.edit().putBoolean("isDarkMode", newMode).apply();
                    if (newMode) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                } else if (menuItem.getItemId() == 2) {
                    logout();
                    return true;
                }
                return false;
            });
            popup.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Không cần hàm filter và loadData cũ nữa, ta thu gọi lại:
    private void filter(String text) {
        List<SubjectWithProgress> filteredList = new ArrayList<>();
        for (SubjectWithProgress item : fullDataList) {
            if (item.subject.getSubjectName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setFilter(filteredList);
    }

    private void showAddSubjectDialog() {
        DialogAddSubjectBinding dialogBinding = DialogAddSubjectBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnSave.setOnClickListener(v -> {
            String name = dialogBinding.edtSubjectName.getText().toString().trim();
            String teacher = dialogBinding.edtTeacherName.getText().toString().trim();

            if (name.isEmpty() || teacher.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            Subject newSubject = new Subject(name, teacher);
            viewModel.insertSubject(newSubject);
            dialog.dismiss();
        });

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void logout() {
        // Xóa trạng thái đăng nhập
        getSharedPreferences("USER_SESSION", MODE_PRIVATE).edit().clear().apply();

        // Quay lại màn hình Login
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}