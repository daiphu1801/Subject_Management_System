package com.example.bailam.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bailam.database.Subject;
import com.example.bailam.database.SubjectWithProgress;
import com.example.bailam.databinding.ItemSubjectBinding;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<SubjectWithProgress> subjectList;
    private OnSubjectClickListener listener;

    // 1. Interface để xử lý sự kiện
    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
        void onSubjectLongClick(Subject subject);
    }

    // 2. Constructor
    // Trong SubjectAdapter.java
    private List<SubjectWithProgress> subjectListFull; // Danh sách gốc để lọc

    public SubjectAdapter(List<SubjectWithProgress> subjectList, OnSubjectClickListener listener) {
        this.subjectList = subjectList;
        this.subjectListFull = new ArrayList<>(subjectList); // Sao chép dữ liệu gốc
        this.listener = listener;
    }

    // Hàm cập nhật danh sách khi tìm kiếm
    public void setFilter(List<SubjectWithProgress> newList) {
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
        SubjectWithProgress item = subjectList.get(position);
        Subject subject = item.subject;
        holder.binding.tvSubjectName.setText(subject.getSubjectName());
        holder.binding.tvTeacherName.setText(subject.getTeacherName());

        // 2. Tính phần trăm bằng class POJO
        int percent = item.getPercent();

        // 3. Hiển thị lên UI
        holder.binding.pbSubjectProgress.setProgress(percent);
        holder.binding.tvProgressPercent.setText(percent + "%");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubjectClick(subject); // Gọi về MainActivity
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onSubjectLongClick(subject);
            }
            return true;
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