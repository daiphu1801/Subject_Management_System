package com.example.bailam.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = Subject.class,
                parentColumns = "id",
                childColumns = "subjectId",
                onDelete = ForeignKey.CASCADE)) // Xóa môn học sẽ tự xóa hết task liên quan
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int taskId;

    private int subjectId; // Khóa ngoại liên kết với ID của Subject
    private String taskTitle;
    private boolean isCompleted;

    public Task(int subjectId, String taskTitle, boolean isCompleted) {
        this.subjectId = subjectId;
        this.taskTitle = taskTitle;
        this.isCompleted = isCompleted;
    }

    // Getter và Setter cho các trường dữ liệu
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}