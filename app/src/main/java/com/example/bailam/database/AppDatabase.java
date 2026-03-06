package com.example.bailam.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Khai báo các bảng dữ liệu và phiên bản database
@Database(entities = {Subject.class, Task.class,User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract AppDao appDao();


    // Sử dụng Singleton pattern để đảm bảo chỉ có 1 database duy nhất được tạo ra
    // Trong AppDatabase.java
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "bailam_db")
                    .fallbackToDestructiveMigration() // THÊM DÒNG NÀY
                    .allowMainThreadQueries()         // Đảm bảo có dòng này để chạy trên main thread
                    .build();
        }
        return instance;
    }

}