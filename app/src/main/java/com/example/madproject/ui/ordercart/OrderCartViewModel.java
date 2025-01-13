package com.example.madproject.ui.ordercart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderCartViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public OrderCartViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is order cart fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}