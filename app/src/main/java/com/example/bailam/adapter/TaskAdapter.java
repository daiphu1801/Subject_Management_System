package com.example.bailam.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bailam.database.Task;
import com.example.bailam.databinding.ItemTaskBinding;

import android.graphics.Paint;
import android.graphics.Color;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskStatusChangeListener listener;

    // Interface để TaskActivity lắng nghe sự kiện thay đổi
    public interface OnTaskStatusChangeListener {
        void onStatusChange(Task task); // Khi tích chọn hoàn thành
        void onDelete(Task task);       // Khi nhấn nút xóa
    }

    public TaskAdapter(List<Task> taskList, OnTaskStatusChangeListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng View Binding cho từng dòng Task
        ItemTaskBinding binding = ItemTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.binding.tvTaskTitle.setText(task.getTaskTitle());
        holder.binding.cbTask.setChecked(task.isCompleted());
        
        if (task.isCompleted()) {
            holder.binding.tvTaskTitle.setPaintFlags(holder.binding.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.tvTaskTitle.setTextColor(Color.GRAY);
        } else {
            holder.binding.tvTaskTitle.setPaintFlags(holder.binding.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.binding.tvTaskTitle.setTextColor(Color.parseColor("#333333"));
        }

        // Xử lý khi người dùng tích vào Checkbox (SSP-73)
        holder.binding.cbTask.setOnClickListener(v -> {
            task.setCompleted(holder.binding.cbTask.isChecked());
            listener.onStatusChange(task);
        });

        // Xử lý khi người dùng nhấn nút Xóa
        holder.binding.btnDeleteTask.setOnClickListener(v -> {
            listener.onDelete(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        ItemTaskBinding binding;
        public TaskViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}