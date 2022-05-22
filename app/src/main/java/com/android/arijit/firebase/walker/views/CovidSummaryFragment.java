package com.android.arijit.firebase.walker.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.arijit.firebase.walker.R;
import com.android.arijit.firebase.walker.databinding.FragmentCovidSummaryBinding;
import com.android.arijit.firebase.walker.services.AlarmReceiver;
import com.android.arijit.firebase.walker.utils.OnBackPressImpl;
import com.android.arijit.firebase.walker.viewmodels.FormViewModel;

public class CovidSummaryFragment extends Fragment {

    private FragmentCovidSummaryBinding binding;
    private FormViewModel viewModel;

    private CovidSummaryFragment() {
        // Required empty public constructor
    }


    public static CovidSummaryFragment newInstance() {
        return new CovidSummaryFragment();
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
        binding = FragmentCovidSummaryBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnConfirm.setOnClickListener(this::showConfirmationDialog);
    }

    private void showConfirmationDialog(View v) {
        new AlertDialog.Builder(binding.getRoot().getContext())
                .setMessage(getString(R.string.mark_as_safe_qn))
                .setCancelable(false)
                .setTitle(getString(R.string.confirm))
                .setPositiveButton(R.string.ok, (d, i) -> {
                    viewModel.onConfirmNegative();
                    AlarmReceiver.cancelAlarm(requireContext());
                })
                .setNegativeButton(R.string.cancel, (d, i) -> d.dismiss())
                .show();
    }
}