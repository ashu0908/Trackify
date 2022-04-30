package com.android.arijit.firebase.walker.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.arijit.firebase.walker.R;
import com.android.arijit.firebase.walker.applications.App;
import com.android.arijit.firebase.walker.interfaces.OnDataFetchedListener;
import com.android.arijit.firebase.walker.interfaces.OnFirebaseResultListener;
import com.android.arijit.firebase.walker.models.PictureData;
import com.android.arijit.firebase.walker.models.ResultData;
import com.android.arijit.firebase.walker.viewmodel.GalleryViewModel;
import com.android.arijit.firebase.walker.viewmodel.HistoryListViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FirebaseUtil {
    private static final String TAG = FirebaseUtil.class.getSimpleName();
    final static String DATE = "date",
            TIME = "time",
            DISTANCE = "distance",
            TRAVEL_COORDINATES = "travel_coordinates",
            SERVER_TIME = "server_time",
            HISTORY_COLLECTION = "travel_history",
            USER = "user",
            PICTURE_COLLECTION = "picture_collection",
            PICTURES = "pictures",
            COVID_STATUS = "covid_status",
            STATUS = "status";

    final static String COLLECTION_SEPARATOR = "/";
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static String getUserEmail() {
        String email;
        try {
            email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        } catch (Exception e) {
            email = "email";
        }
        return email;
    }

    public static void storeData(ResultData data, HistoryListViewModel historyListViewModel, OnFirebaseResultListener listener) {

        String email = getUserEmail();

        HashMap<String, Object> toPut = new HashMap<>();
        toPut.put(USER, email);
        toPut.put(DATE, data.getDate());
        toPut.put(TIME, data.getTime());
        toPut.put(DISTANCE, data.getDistanceTravelled());
        toPut.put(TRAVEL_COORDINATES, data.getTravelCoordinates());
        toPut.put(SERVER_TIME, FieldValue.serverTimestamp());

        mFirestore.collection(HISTORY_COLLECTION)
                .add(toPut)
                .addOnCompleteListener(task -> {
                    data.setId(task.getResult().getId());
                    historyListViewModel.addResultData(data);
                })
                .addOnFailureListener(error -> {
                    listener.onFirebaseResult(App.getContext().getString(R.string.error_save));
                    Log.i(TAG, "storeData: failure " + error.getMessage());
                });
    }

    @SuppressWarnings("unchecked")
    public static void fetchData(final HistoryListViewModel historyViewModel, @Nullable final OnDataFetchedListener listener) {

        String email = getUserEmail();
        ArrayList<ResultData> fetchedData = new ArrayList<>();

        mFirestore.collection(HISTORY_COLLECTION)
                .orderBy(SERVER_TIME)
                .whereEqualTo(USER, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        Log.i(TAG, "onComplete: " + task.getResult().size());
                        for (QueryDocumentSnapshot docSnap : task.getResult()) {
                            ResultData res = new ResultData();
                            res.setId(docSnap.getId());
                            res.setDate(docSnap.getString(DATE));
                            res.setTime(docSnap.getString(TIME));
                            Log.i(TAG, "fetchData: " + res.getId() + " " + res.getDate() + " " + res.getTime());
                            res.setDistanceTravelled(Float.parseFloat(Objects.requireNonNull(docSnap.get(DISTANCE)).toString()));
                            ArrayList<Object> tmp = (ArrayList<Object>) docSnap.get(TRAVEL_COORDINATES);
                            ArrayList<LatLng> toPutInRes = new ArrayList<>();
                            assert tmp != null;
                            for (Object ob : tmp) {
                                HashMap<String, Double> hash = (HashMap<String, Double>) ob;
                                LatLng each = new LatLng(
                                        Objects.requireNonNull(hash.get("latitude")),
                                        Objects.requireNonNull(hash.get("longitude"))
                                );
                                toPutInRes.add(each);
                            }
                            res.setTravelCoordinates(toPutInRes);

                            fetchedData.add(res);
                        }
                        if (listener != null) listener.onDataFetched(null);
                        historyViewModel.setHistoryLiveList(fetchedData);
                    } else {
                        if (listener != null)
                            listener.onDataFetched(App.getContext().getString(R.string.error_fetch));
                        Log.i(TAG, "storeData: failure " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }
    //https://console.firebase.google.com/v1/r/project/distance-tracker-app-63ebc/firestore/indexes?create_composite=CmFwcm9qZWN0cy9kaXN0YW5jZS10cmFja2VyLWFwcC02M2ViYy9kYXRhYmFzZXMvKGRlZmF1bHQpL2NvbGxlY3Rpb25Hcm91cHMvdHJhdmVsX2hpc3RvcnkvaW5kZXhlcy9fEAEaCAoEdXNlchABGg8KC3NlcnZlcl90aW1lEAEaDAoIX19uYW1lX18QAQ

    public static void deleteData(Context context, String id, OnFirebaseResultListener listener) {
        mFirestore.collection(HISTORY_COLLECTION)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> listener.onFirebaseResult("Deleted"))
                .addOnFailureListener(e -> {
                    listener.onFirebaseResult(context.getString(R.string.error_fetch));
                    Log.i(TAG, "deleteData: " + e.getMessage());
                });
    }

    public static boolean isVerifiedUser() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            return true;
        } else if (mAuth.getCurrentUser() != null)
            mAuth.signOut();
        return false;
    }

    /**
     * static method to upload the image from
     * the provided path to Firebase Storage
     * /gs/email/photo.jpeg
     *
     * @param filePath uri of the file to be uploaded
     */
    public static void uploadImage(Uri filePath) {
        if (filePath == null) return;
        String email = getUserEmail();

        // Defining the child of storageReference
        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child(email + "/" + UUID.randomUUID().toString());

        // adding listeners on upload
        // or failure of image
        String finalEmail = email;
        ref.putFile(filePath)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String urlToImage = uri.toString();
                            saveImageData(finalEmail, urlToImage);
                            // TODO: Delete the local copy of the cache
                        })
                )
                .addOnFailureListener(e -> Log.i(TAG, "uploadImage: " + e.getMessage()));
    }

    public static void saveImageData(String email, String url) {
        PictureData picData = new PictureData(url);
        mFirestore.collection(
                PICTURE_COLLECTION + COLLECTION_SEPARATOR + email + COLLECTION_SEPARATOR + PICTURES)
                .add(picData)
                .addOnFailureListener(error -> Log.i(TAG, "storeData: failure " + error.getMessage()));
    }

    /**
     * fetch all the data related
     * to images from the Firebase
     */
    public static void fetchPictureData(GalleryViewModel.OnDataFetchListener onDoneCallback) {

        String email = getUserEmail();
        ArrayList<PictureData> pictureList = new ArrayList<>();
        mFirestore.collection(
                PICTURE_COLLECTION +
                        COLLECTION_SEPARATOR + email +
                        COLLECTION_SEPARATOR + PICTURES
        )
                .orderBy("date")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                            pictureList.addAll(
                                    queryDocumentSnapshots.getDocuments()
                                            .stream()
                                            .map(documentSnapshot ->
                                                    documentSnapshot.toObject(PictureData.class))
                                            .collect(Collectors.toList())
                            );
                            onDoneCallback.onDataFetch(pictureList);
                        }
                );

    }

    /**
     * Update the covid status
     * of user in the firebase
     *
     * @param status whether user is effected or not
     */
    public static void updateCovidStatus(boolean status, Date date, Consumer<Void> listener) {

        String email = getUserEmail();

        HashMap<String, Object> data = new HashMap<>();
        data.put(STATUS, status);
        data.put(DATE, new Timestamp(date));

        mFirestore.document(COVID_STATUS + COLLECTION_SEPARATOR + email)
                .set(data)
                .addOnSuccessListener(unused -> listener.accept(null))
                .addOnFailureListener(e -> Log.i(TAG, "updateCovidStatus: " + e.getMessage()));

    }

    public static void fetchCovidStatus(BiConsumer<Boolean, Date> onUpdateStatusListener) {
        String email = getUserEmail();

        mFirestore.document(COVID_STATUS + COLLECTION_SEPARATOR + email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Boolean status = (Boolean) documentSnapshot.getBoolean(STATUS);
                    Date date = null;
                    Timestamp ts = (Timestamp) documentSnapshot.get(DATE);
                    if (ts != null) date = ts.toDate();
                    onUpdateStatusListener.accept(status, date);
                })
                .addOnFailureListener(e -> Log.i(TAG, "fetchCovidStatus: " + e.getMessage()));

    }

    public static void deleteCovidStatus(Consumer<Void> listener) {
        String email = getUserEmail();
        mFirestore.document(COVID_STATUS + COLLECTION_SEPARATOR + email)
                .delete()
                .addOnSuccessListener(unused -> listener.accept(null))
                .addOnFailureListener(e -> Log.i(TAG, "deleteCovidStatus: " + e.getMessage()));
    }


}
