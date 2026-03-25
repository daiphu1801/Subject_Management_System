package com.example.bailam.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AppRepository {
    private AppDao appDao;
    private AppExecutors executors;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        appDao = db.appDao();
        executors = AppExecutors.getInstance();
    }

    public LiveData<List<SubjectWithProgress>> getAllSubjects() {
        return appDao.getAllSubjectsWithProgress();
    }

    public LiveData<List<Task>> getTasksBySubject(int subjectId) {
        return appDao.getTasksBySubject(subjectId);
    }

    public void insertSubject(Subject subject) {
        executors.diskIO().execute(() -> appDao.insertSubject(subject));
    }

    public void deleteSubject(Subject subject) {
        executors.diskIO().execute(() -> appDao.deleteSubject(subject));
    }

    public void insertTask(Task task) {
        executors.diskIO().execute(() -> appDao.insertTask(task));
    }

    public void updateTask(Task task) {
        executors.diskIO().execute(() -> appDao.updateTask(task));
    }

    public void deleteTask(Task task) {
        executors.diskIO().execute(() -> appDao.deleteTask(task));
    }

    public void registerUser(User user) {
        executors.diskIO().execute(() -> appDao.registerUser(user));
    }

    public interface LoginCallback {
        void onResult(User user);
    }

    public void login(String username, String pass, LoginCallback callback) {
        executors.diskIO().execute(() -> {
            User u = appDao.login(username, pass);
            executors.mainThread().execute(() -> callback.onResult(u));
        });
    }

    public interface CheckUserCallback {
        void onResult(boolean exists);
    }

    public void checkUserExists(String username, CheckUserCallback callback) {
        executors.diskIO().execute(() -> {
            User u = appDao.checkUserExists(username);
            executors.mainThread().execute(() -> callback.onResult(u != null));
        });
    }
}
