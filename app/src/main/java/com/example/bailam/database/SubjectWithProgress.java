package com.example.bailam.database;

import androidx.room.Embedded;

public class SubjectWithProgress {
    @Embedded
    public Subject subject;
    
    public int totalTasks;
    public int completedTasks;

    public int getPercent() {
        if (totalTasks == 0) return 0;
        return (completedTasks * 100) / totalTasks;
    }
}
