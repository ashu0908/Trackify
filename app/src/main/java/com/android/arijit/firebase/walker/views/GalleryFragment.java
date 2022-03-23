package com.android.arijit.firebase.walker.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.arijit.firebase.walker.adapters.GalleryAdapter;
import com.android.arijit.firebase.walker.databinding.FragmentGalleryBinding;
import com.android.arijit.firebase.walker.viewmodel.GalleryViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {

    private static final int GALLERY_SPAN_COUNT = 3;
    //    private static final String TAG = GalleryViewModel.class.getSimpleName();
    private FragmentGalleryBinding binding;
    private GalleryViewModel viewModel;

    private GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress();
        viewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
    }

    private void handleBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(GALLERY_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        binding.photosList.setLayoutManager(manager);

        GalleryAdapter adapter = new GalleryAdapter();
        binding.photosList.setAdapter(adapter);

        viewModel.pictureList.observe( getViewLifecycleOwner(), adapter::submitList);
    }
}
