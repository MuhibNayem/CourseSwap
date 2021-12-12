package com.example.addrop;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class itemViewModel extends ViewModel {
    private  final MutableLiveData<ArrayList> selectedItem = new MutableLiveData<>();
    private  final MutableLiveData<ArrayList> selectedCourses = new MutableLiveData<>();

    public void setData(ArrayList arrayList){
        selectedItem.setValue(arrayList);


    }

    public LiveData<ArrayList> getData(){
        return selectedItem;
    }

    public void setCoureData(ArrayList arrayList){
        selectedCourses.setValue(arrayList);

    }

    public LiveData<ArrayList> getCourseData(){
        return selectedCourses;
    }

}
