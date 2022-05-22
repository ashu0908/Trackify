package com.android.arijit.firebase.walker.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.arijit.firebase.walker.R;
import com.android.arijit.firebase.walker.viewmodels.FormViewModel;

public class CovidFragment extends Fragment {

    private FormViewModel viewModel;

    private CovidFragment() {
        // Required empty public constructor
    }

    public static CovidFragment newInstance() {
        return new CovidFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_covid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getStatus().observe(getViewLifecycleOwner(), status -> {
            if (status == -1)
                return;
            view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
            /*
             * status code
             * 0 = negative
             * 1 = positive
             */
            if (status == 0) {
                loadFragment(FormFragment.newInstance());
            }
            else {
                loadFragment(CovidSummaryFragment.newInstance());
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        assert getFragmentManager() != null;
        getFragmentManager()
                .beginTransaction()
                .addToBackStack("stack")
                .replace(R.id.form_selector_fragment, fragment)
                .commit();
    }
}