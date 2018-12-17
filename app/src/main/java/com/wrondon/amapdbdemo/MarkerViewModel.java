package com.wrondon.amapdbdemo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MarkerViewModel extends ViewModel {
    private static final DatabaseReference MARKER_REF =
            FirebaseDatabase.getInstance().getReference("marker");

    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(MARKER_REF);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
