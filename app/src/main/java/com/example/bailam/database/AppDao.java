package com.example.bailam.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface AppDao {
    // --- Các lệnh cho Môn học (Subject) ---
    @Insert
    void insertSubject(Subject subject);
    @Insert
    void registerUser(User user);

    // Tìm user dựa trên tài khoản và mật khẩu (Dùng cho Đăng nhập)
    @Query("SELECT * FROM users WHERE username = :user AND password = :pass LIMIT 1")
    User login(String user, String pass);

    // Kiểm tra xem tên tài khoản đã tồn tại chưa (Dùng cho Đăng ký)
    @Query("SELECT * FROM users WHERE username = :user LIMIT 1")
    User checkUserExists(String user);

    @Query("SELECT * FROM subjects")
    List<Subject> getAllSubjects();
    // Trong AppDao.java
    @Query("SELECT COUNT(*) FROM tasks WHERE subjectId = :sId")
    int getTotalTasks(int sId);

    @Query("SELECT COUNT(*) FROM tasks WHERE subjectId = :sId AND isCompleted = 1")
    int getCompletedTasks(int sId);

    @Delete
    void deleteSubject(Subject subject);

    // --- Các lệnh cho Công việc (Task) ---
    @Insert
    void insertTask(Task task);

    // Lấy toàn bộ task của một môn học dựa trên ID môn học
    @Query("SELECT * FROM tasks WHERE subjectId = :sId")
    List<Task> getTasksBySubject(int sId);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}