package com.example.bailam.ui;

import android.os.Bundle;
import android.view.MenuItem; // Thêm dòng này
import android.widget.Toast;
import androidx.annotation.NonNull; // Thêm dòng này
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.bailam.adapter.TaskAdapter;
import com.example.bailam.database.Task;
import com.example.bailam.databinding.ActivityTaskBinding;
import com.example.bailam.databinding.DialogAddTaskBinding;
import com.example.bailam.ui.viewmodel.TaskViewModel;
import com.example.bailam.databinding.DialogAddTaskBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    private ActivityTaskBinding binding;
    private TaskAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private int subjectId;
    private TaskViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide default Action Bar if needed
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        subjectId = getIntent().getIntExtra("SUBJECT_ID", -1);
        String subjectName = getIntent().getStringExtra("SUBJECT_NAME");

        if (subjectName != null && !subjectName.isEmpty()) {
            binding.tvHeader.setText(subjectName);
        }

        setupRecyclerView();

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        
        // Observe LiveData instead of loadTasks() manually
        viewModel.getTasksBySubject(subjectId).observe(this, data -> {
            taskList.clear();
            taskList.addAll(data);
            adapter.notifyDataSetChanged();
        });

        binding.btnAddTask.setOnClickListener(v -> showAddTaskDialog());
        binding.btnBack.setOnClickListener(v -> finish());
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
                viewModel.updateTask(task);
            }

            @Override
            public void onDelete(Task task) {
                viewModel.deleteTask(task);
            }
        });
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTasks.setAdapter(adapter);
    }

    // Đã dùng LiveData nên không cần hàm loadTasks() cũ nữa

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
            viewModel.insertTask(newTask);

            dialog.dismiss();
            Toast.makeText(this, "Đã thêm công việc!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}