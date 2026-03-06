package com.example.bailam.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bailam.database.AppDatabase;
import com.example.bailam.database.Subject;
import com.example.bailam.databinding.ItemSubjectBinding;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<Subject> subjectList;
    private OnSubjectClickListener listener;

    // 1. Interface để xử lý sự kiện
    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
        void onSubjectLongClick(Subject subject);
    }

    // 2. Constructor
    // Trong SubjectAdapter.java
    private List<Subject> subjectListFull; // Danh sách gốc để lọc

    public SubjectAdapter(List<Subject> subjectList, OnSubjectClickListener listener) {
        this.subjectList = subjectList;
        this.subjectListFull = new ArrayList<>(subjectList); // Sao chép dữ liệu gốc
        this.listener = listener;
    }

    // Hàm cập nhật danh sách khi tìm kiếm
    public void setFilter(List<Subject> newList) {
        this.subjectList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSubjectBinding binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SubjectViewHolder(binding);
    }

    // Trong SubjectAdapter.java
    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.binding.tvSubjectName.setText(subject.getSubjectName());
        holder.binding.tvTeacherName.setText(subject.getTeacherName());

        // 1. Lấy dữ liệu từ Database (sử dụng DAO thông qua context của view)
        AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
        int total = db.appDao().getTotalTasks(subject.getId());
        int completed = db.appDao().getCompletedTasks(subject.getId());

        // 2. Tính phần trăm
        int percent = 0;
        if (total > 0) {
            percent = (completed * 100) / total;
        }

        // 3. Hiển thị lên UI
        holder.binding.pbSubjectProgress.setProgress(percent);
        holder.binding.tvProgressPercent.setText(percent + "%");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubjectClick(subject); // Gọi về MainActivity
            }
        });

        // Thêm dòng này để có hiệu ứng gợn sóng khi chạm vào (tăng trải nghiệm)
        holder.itemView.setClickable(true);
        holder.itemView.setFocusable(true);

        // Giữ nguyên các listener click và long click của bạn...
    }

    @Override
    public int getItemCount() {
        return subjectList != null ? subjectList.size() : 0;
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        ItemSubjectBinding binding;
        public SubjectViewHolder(ItemSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}