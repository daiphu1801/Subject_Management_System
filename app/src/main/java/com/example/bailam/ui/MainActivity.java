package com.example.bailam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu; // Thêm cái này
import android.view.MenuItem; // Thêm cái này
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView; // Thêm cái này
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bailam.R;
import com.example.bailam.adapter.SubjectAdapter;
import com.example.bailam.database.AppDatabase;
import com.example.bailam.database.Subject;
import com.example.bailam.databinding.ActivityMainBinding;
import com.example.bailam.databinding.DialogAddSubjectBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SubjectAdapter adapter;
    private List<Subject> subjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        loadData();

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
                            AppDatabase.getInstance(MainActivity.this).appDao().deleteSubject(subject);
                            loadData();
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
    // Trong MainActivity.java
    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi khi quay lại màn hình chính, tải lại dữ liệu để cập nhật phần trăm
        loadData();
    }

    private void filter(String text) {
        List<Subject> filteredList = new ArrayList<>();
        List<Subject> fullList = AppDatabase.getInstance(this).appDao().getAllSubjects();

        for (Subject item : fullList) {
            if (item.getSubjectName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setFilter(filteredList);
    }

    private void loadData() {
        List<Subject> data = AppDatabase.getInstance(this).appDao().getAllSubjects();
        subjectList.clear();
        subjectList.addAll(data);
        adapter.notifyDataSetChanged();

        if (subjectList.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
        }
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
            AppDatabase.getInstance(this).appDao().insertSubject(newSubject);

            loadData();
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