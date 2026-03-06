package com.example.bailam.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects") // Định nghĩa tên bảng
public class Subject {
    @PrimaryKey(autoGenerate = true) // Tự động tăng ID
    private int id;

    private String subjectName;
    private String teacherName;

    // Constructor để tạo đối tượng mới (dùng cho task SSP-70)
    public Subject(String subjectName, String teacherName) {
        this.subjectName = subjectName;
        this.teacherName = teacherName;
    }

    // Các hàm Getter và Setter (Room bắt buộc phải có)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
}