package com.example.bailam.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users") // Khai báo đây là một bảng tên "users" trong Database
public class User {

    @PrimaryKey(autoGenerate = true) // ID tự động tăng
    private int id;

    private String username;
    private String password;

    // Constructor để tạo User mới (thường dùng khi Đăng ký)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter và Setter (Bắt buộc phải có để Room truy cập dữ liệu)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}