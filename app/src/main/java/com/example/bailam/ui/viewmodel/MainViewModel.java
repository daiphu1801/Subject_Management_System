package com.example.bailam.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bailam.database.AppRepository;
import com.example.bailam.database.Subject;
import com.example.bailam.database.SubjectWithProgress;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private AppRepository repository;
    private LiveData<List<SubjectWithProgress>> allSubjects;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        allSubjects = repository.getAllSubjects();
    }

    public LiveData<List<SubjectWithProgress>> getAllSubjects() {
        return allSubjects;
    }

    public void insertSubject(Subject subject) {
        repository.insertSubject(subject);
    }

    public void deleteSubject(Subject subject) {
        repository.deleteSubject(subject);
    }
}
