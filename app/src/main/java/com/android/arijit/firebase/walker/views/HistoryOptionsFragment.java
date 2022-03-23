package com.android.arijit.firebase.walker.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.arijit.firebase.walker.R;
import com.android.arijit.firebase.walker.databinding.FragmentHistoryOptionsBinding;
import com.android.arijit.firebase.walker.interfaces.OnFirebaseResultListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryOptionsFragment extends Fragment {

    FragmentHistoryOptionsBinding binding;
    private OnFirebaseResultListener firebaseResultListener;

    private HistoryOptionsFragment(){}

    public static HistoryOptionsFragment newInstance(
        OnFirebaseResultListener firebaseResultListener
    ){
        HistoryOptionsFragment hOF = new HistoryOptionsFragment();
        hOF.firebaseResultListener = firebaseResultListener;
        return hOF;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((BottomNavigationView)requireActivity().findViewById(R.id.navigation))
                        .setSelectedItemId(R.id.navigation_home);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryOptionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void loadFragment(Fragment fragment) {
        assert getFragmentManager() != null;
        getFragmentManager()
                .beginTransaction()
                .addToBackStack("stack")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.historyOpenBtn.setOnClickListener( v -> loadFragment(new HistoryFragment(firebaseResultListener)));

        binding.galleryOpenBtn.setOnClickListener(v -> loadFragment(GalleryFragment.newInstance()));

    }
}
