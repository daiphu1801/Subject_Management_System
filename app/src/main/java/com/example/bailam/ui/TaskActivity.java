package com.example.bailam.ui;

import android.os.Bundle;
import android.view.MenuItem; // Thêm dòng này
import android.widget.Toast;
import androidx.annotation.NonNull; // Thêm dòng này
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bailam.adapter.TaskAdapter;
import com.example.bailam.database.AppDatabase;
import com.example.bailam.database.Task;
import com.example.bailam.databinding.ActivityTaskBinding;
import com.example.bailam.databinding.DialogAddTaskBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    private ActivityTaskBinding binding;
    private TaskAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private int subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập nút Back trên thanh tiêu đề
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        subjectId = getIntent().getIntExtra("SUBJECT_ID", -1);
        String subjectName = getIntent().getStringExtra("SUBJECT_NAME");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(subjectName);
        }

        setupRecyclerView();
        loadTasks();

        binding.btnAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    // Xử lý sự kiện khi bấm vào nút Back (mũi tên)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskStatusChangeListener() {
            @Override
            public void onStatusChange(Task task) {
                AppDatabase.getInstance(TaskActivity.this).appDao().updateTask(task);
            }

            @Override
            public void onDelete(Task task) {
                AppDatabase.getInstance(TaskActivity.this).appDao().deleteTask(task);
                loadTasks();
            }
        });
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTasks.setAdapter(adapter);
    }

    private void loadTasks() {
        List<Task> data = AppDatabase.getInstance(this).appDao().getTasksBySubject(subjectId);
        taskList.clear();
        taskList.addAll(data);
        adapter.notifyDataSetChanged();
    }

    private void showAddTaskDialog() {
        DialogAddTaskBinding dialogBinding = DialogAddTaskBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnSaveTask.setOnClickListener(v -> {
            String title = dialogBinding.edtTaskTitle.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            Task newTask = new Task(subjectId, title, false);
            AppDatabase.getInstance(this).appDao().insertTask(newTask);

            loadTasks();
            dialog.dismiss();
            Toast.makeText(this, "Đã thêm công việc!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}