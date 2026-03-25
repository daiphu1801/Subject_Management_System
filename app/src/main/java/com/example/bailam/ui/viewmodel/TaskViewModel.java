package com.example.bailam.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bailam.database.AppRepository;
import com.example.bailam.database.Task;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private AppRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public LiveData<List<Task>> getTasksBySubject(int subjectId) {
        return repository.getTasksBySubject(subjectId);
    }

    public void insertTask(Task task) {
        repository.insertTask(task);
    }

    public void updateTask(Task task) {
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }
}
