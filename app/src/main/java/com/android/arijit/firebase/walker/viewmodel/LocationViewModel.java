package com.android.arijit.firebase.walker.viewmodel;

import android.net.Uri;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.arijit.firebase.walker.interfaces.OnFirebaseResultListener;
import com.android.arijit.firebase.walker.models.ResultData;
import com.android.arijit.firebase.walker.utils.FirebaseUtil;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocationViewModel extends ViewModel {

    public enum Mode {
        DEFAULT, COVID;
    }

    private ResultData resultData;
    public final ObservableFloat distInMetre = new ObservableFloat();
    private final MutableLiveData<ArrayList<LatLng>> curGotPosition = new MutableLiveData<>();
    private final MutableLiveData<Boolean> trackState = new MutableLiveData<>(Boolean.FALSE);
    public final ObservableField<String> resDate = new ObservableField<>();
    public final ObservableField<String> resTime = new ObservableField<>();
    private final MutableLiveData<Mode> _currMode = new MutableLiveData<>();

    public LiveData<Mode> getCurrentMode() {
        return _currMode;
    }
    public void setMode(Mode aDefault) {
        _currMode.setValue(aDefault);
    }

    private final MutableLiveData<List<LatLng>> _hotspotList = new MutableLiveData<>();
    public LiveData<List<LatLng>> getHotspotList() {
        return _hotspotList;
    }

    private Uri currPhotoUri;

    public LocationViewModel() {
        this.distInMetre.set(0f);
        this.curGotPosition.setValue(new ArrayList<>());
        this.resultData = new ResultData();
        setDateTime();
        _currMode.setValue(Mode.DEFAULT);
    }

    public Uri getCurrPhotoUri() {
        return currPhotoUri;
    }

    public void setCurrPhotoUri(Uri currPhotoUri) {
        this.currPhotoUri = currPhotoUri;
    }

    public ObservableFloat getDistInMetre() {
        return distInMetre;
    }

    public LiveData<ArrayList<LatLng>> getCurGotPosition() {
        return curGotPosition;
    }

    public LatLng getLastCoordinates() {
        int size = Objects.requireNonNull(curGotPosition.getValue()).size();
        return curGotPosition.getValue().get(size - 1);
    }

    public boolean isCoorListEmpty() {
        return Objects.requireNonNull(curGotPosition.getValue()).isEmpty();
    }

    public void addLatLng(LatLng latLng) {
        Objects.requireNonNull(this.curGotPosition.getValue()).add(latLng);
        this.curGotPosition.setValue(this.curGotPosition.getValue());
    }

    public void setDistInMetre(float distInMetre) {
        this.distInMetre.set(distInMetre);
    }

    public void setCurGotPosition(ArrayList<LatLng> curGotPosition) {
        this.curGotPosition.setValue(curGotPosition);
    }

    public boolean getTrackState() {
        return Objects.requireNonNull(this.trackState.getValue());
    }

    public void setTrackState(boolean state){
        this.trackState.setValue(state);
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
        setDateTime();
    }

    public ResultData getResultData() {
        return resultData;
    }

    public void saveResult(HistoryListViewModel historyListViewModel, OnFirebaseResultListener listener) {
        resultData.setDistanceTravelled(this.distInMetre.get());
        resultData.setTravelCoordinates(this.curGotPosition.getValue());
        setDateTime();
        FirebaseUtil.storeData(this.getResultData(), historyListViewModel, listener);
    }

    private void setDateTime() {
        this.resDate.set(resultData.getDate());
        this.resTime.set(resultData.getTime());
    }

    public void uploadCurrentPhoto() {
        File image = new File(getCurrPhotoUri().getPath());
            FirebaseUtil.uploadImage(getCurrPhotoUri());
    }

    public void refreshHotspotList() {
        FirebaseUtil.getCovidHotspot(_hotspotList::postValue);
    }

}
