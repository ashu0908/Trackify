package com.android.arijit.firebase.walker.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.arijit.firebase.walker.R;
import com.android.arijit.firebase.walker.databinding.FragmentFormBinding;
import com.android.arijit.firebase.walker.utils.OnBackPressImpl;
import com.android.arijit.firebase.walker.viewmodel.FormViewModel;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    FragmentFormBinding binding;
    FormViewModel viewModel;

    public FormFragment() {
        // Required empty public constructor
    }

    public static FormFragment newInstance() {
        return new FormFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, OnBackPressImpl.getInstance(requireActivity()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFormBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.datePicker.setOnClickListener(this::showDatePickerDialog);
        binding.btnConfirm.setOnClickListener(this::showConfirmationDialog);
    }

    private void showDatePickerDialog(View v) {
        DialogFragment dialog = new DatePickerFragment(viewModel.getCurrDate(), this);
        dialog.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showConfirmationDialog(View v) {
        new AlertDialog.Builder(binding.getRoot().getContext())
                .setMessage(getString(R.string.confirm_message, viewModel.getDateStr()))
                .setCancelable(false)
                .setTitle(getString(R.string.confirm))
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> viewModel.onConfirmPositive())
                .setNegativeButton(R.string.cancel, (d, i) -> d.dismiss())
                .show();
    }

    /**
     *
     * @param datePicker DatePicker object
     * @param year Year of the date selected
     * @param month Month of the date selected in 0-indexing
     * @param day Day of the date selected
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        viewModel.updateChosenDate(year, month, day);
    }

    public static class DatePickerFragment extends DialogFragment  {

        private final DatePickerDialog.OnDateSetListener dateSetListener;
        private final Calendar initDate;

        DatePickerFragment(Calendar initDate, DatePickerDialog.OnDateSetListener listener){
            this.initDate = initDate;
            this.dateSetListener = listener;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstance){
            return new DatePickerDialog(getActivity(), dateSetListener, initDate.get(Calendar.YEAR), initDate.get(Calendar.MONTH), initDate.get(Calendar.DAY_OF_MONTH));
        }

    }

}