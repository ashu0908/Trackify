package com.android.arijit.firebase.walker.viewmodel;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.arijit.firebase.walker.utils.FirebaseUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FormViewModel extends ViewModel {
    // private static final String TAG = FormViewModel.class.getSimpleName();
    private final MutableLiveData<String> _buttonValue = new MutableLiveData<>("Choose Date");

    public LiveData<String> getButtonValue() {
        return _buttonValue;
    }

    /*
     * status code
     * 0 = negative
     * 1 = positive
     */
    private final MutableLiveData<Integer> _record_status = new MutableLiveData<>(-1);

    public LiveData<Integer> getStatus() {
        return _record_status;
    }

    private final MutableLiveData<String> _record_date = new MutableLiveData<>();

    public LiveData<String> getRecordDate() {
        return _record_date;
    }

    private Calendar currDate;

    public Calendar getCurrDate() {
        if (currDate == null) {
            currDate = Calendar.getInstance();
        }
        return currDate;
    }

    private void setCurrDate(int year, int month, int day) {
        if (currDate == null)
            currDate = Calendar.getInstance();
        currDate.set(year, month, day);
    }

    public FormViewModel() {
        FirebaseUtil.fetchCovidStatus((status, date) -> {
            if (status == null || date == null) {
                _record_status.postValue(0);
                return;
            }
            _record_status.postValue(status ? 1 : 0);
            String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
            _record_date.postValue(dateStr);
        });
    }

    /**
     * Update the selected date
     *
     * @param year  Year of the date selected
     * @param month Month of the date selected in 0-indexing
     * @param day   Day of the date selected
     */
    public void updateChosenDate(int year, int month, int day) {
        setCurrDate(year, month, day);
        _buttonValue.setValue(getDateStr());
    }

    @SuppressLint("DefaultLocale")
    public String getDateStr() {

        return String.format("%d/%d/%d", getCurrDate().get(Calendar.DAY_OF_MONTH),
                getCurrDate().get(Calendar.MONTH) + 1,
                getCurrDate().get(Calendar.YEAR));
    }

    public void onConfirmPositive() {
        LocalDate date = LocalDate.of(
                getCurrDate().get(Calendar.YEAR),
                getCurrDate().get(Calendar.MONTH) + 1,
                getCurrDate().get(Calendar.DAY_OF_MONTH)
        );
        Date dd = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        FirebaseUtil.updateCovidStatus(true, dd, (v) -> {
            _record_status.postValue(1);
            String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dd);
            _record_date.postValue(dateStr);
        });
    }

    public void onConfirmNegative() {
        FirebaseUtil.deleteCovidStatus((v) -> _record_status.postValue(0));
    }

}
