package com.example.bailam.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bailam.database.AppRepository;
import com.example.bailam.database.User;

public class LoginViewModel extends AndroidViewModel {
    private AppRepository repository;
    private MutableLiveData<User> loginResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> registerResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public LiveData<User> getLoginResult() { return loginResult; }
    public LiveData<Boolean> getRegisterResult() { return registerResult; }

    public void login(String username, String password) {
        repository.login(username, password, u -> loginResult.setValue(u));
    }
    
    public void registerUserWithCheck(String username, String password) {
        repository.checkUserExists(username, exists -> {
            if (exists) {
                registerResult.setValue(false); // Exists, fail
            } else {
                repository.registerUser(new User(username, password));
                registerResult.setValue(true); // Success
            }
        });
    }
}
