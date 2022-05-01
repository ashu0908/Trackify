package com.android.arijit.firebase.walker.viewmodel;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.arijit.firebase.walker.models.PictureData;
import com.android.arijit.firebase.walker.models.PictureHeaderData;
import com.android.arijit.firebase.walker.models.SuperPictureData;
import com.android.arijit.firebase.walker.utils.FirebaseUtil;
import com.android.arijit.firebase.walker.views.GalleryFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GalleryViewModel extends ViewModel {

    private static final String TAG = GalleryFragment.class.getSimpleName();
    public MutableLiveData<List<SuperPictureData>> pictureList = new MutableLiveData<>();

    public GalleryViewModel() {
//        refreshPictureList();
    }

    private List<SuperPictureData> modifyPictureList(List<PictureData> list) {
        List<SuperPictureData> result = new ArrayList<>();
        PictureHeaderData lastHeader = null;

        for (PictureData picture : list) {
            Date date = picture.getDate().toDate();

            @SuppressLint("SimpleDateFormat")
            String sdf = new SimpleDateFormat("d MMM yyyy").format(date);

            if (lastHeader == null || !lastHeader.date.equals(sdf)) {
                lastHeader = new PictureHeaderData(sdf);
                result.add(lastHeader);
            }
            result.add(picture);
        }

        return result;
    }

    public void refreshPictureList() {
        FirebaseUtil.fetchPictureData(list ->
                Observable.fromCallable(() -> modifyPictureList(list))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<SuperPictureData>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull List<SuperPictureData> superPictureData) {
                                pictureList.setValue(superPictureData);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                            }
                        })
        );
    }

}
