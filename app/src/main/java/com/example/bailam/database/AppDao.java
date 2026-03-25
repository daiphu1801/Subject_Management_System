package com.example.bailam.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;
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

    // Lấy môn học kèm phần trăm hoàn thành bằng 1 câu truy vấn Room
    @Query("SELECT s.*, " +
           "(SELECT COUNT(*) FROM tasks t WHERE t.subjectId = s.id) AS totalTasks, " +
           "(SELECT COUNT(*) FROM tasks t WHERE t.subjectId = s.id AND t.isCompleted = 1) AS completedTasks " +
           "FROM subjects s")
    LiveData<List<SubjectWithProgress>> getAllSubjectsWithProgress();

    // Giữ lại methods này nếu cần dùng đồng bộ ở đâu đó, nếu không có thể xoá.
    @Query("SELECT * FROM subjects")
    List<Subject> getAllSubjectsRaw();

    @Delete
    void deleteSubject(Subject subject);

    // --- Các lệnh cho Công việc (Task) ---
    @Insert
    void insertTask(Task task);

    // Lấy toàn bộ task của một môn học dựa trên ID môn học
    @Query("SELECT * FROM tasks WHERE subjectId = :sId")
    LiveData<List<Task>> getTasksBySubject(int sId);

    // Dùng cho hệ thống nhắc nhở Notification
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    int getIncompleteTaskCount();

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}