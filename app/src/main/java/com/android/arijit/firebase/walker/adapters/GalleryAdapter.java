package com.android.arijit.firebase.walker.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.arijit.firebase.walker.R;
import com.android.arijit.firebase.walker.databinding.GalleryHeaderItemBinding;
import com.android.arijit.firebase.walker.databinding.GalleryListItemBinding;
import com.android.arijit.firebase.walker.models.PictureData;
import com.android.arijit.firebase.walker.models.PictureHeaderData;
import com.android.arijit.firebase.walker.models.SuperPictureData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class GalleryAdapter extends
        ListAdapter<SuperPictureData, RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0,
            ITEM_DATA = 1;

    public GalleryAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder vh;
        if(viewType == ITEM_HEADER) {
            vh = new HeaderViewHolder(GalleryHeaderItemBinding.inflate(inflater));
            StaggeredGridLayoutManager.LayoutParams lp = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setFullSpan(true);
            vh.itemView.setLayoutParams(lp);
        } else {
            vh = new GalleryViewHolder(GalleryListItemBinding.inflate(inflater));
        }

        return vh;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case ITEM_DATA: {
                PictureData pictureData = (PictureData) getItem(position);
                ((GalleryViewHolder) holder).bind(pictureData);
                break;
            }
            case ITEM_HEADER:{
                PictureHeaderData headerData = (PictureHeaderData) getItem(position);
                ((HeaderViewHolder) holder).bind(headerData);
                break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        SuperPictureData data = getItem(position);
        if(data instanceof PictureData)
            return ITEM_DATA;
        return ITEM_HEADER;
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        private final GalleryListItemBinding binding;
        public GalleryViewHolder(GalleryListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(PictureData data) {
            String url = data.imgUrl;
            Glide.with(binding.getRoot())
                    .load(url)
                    .fitCenter()
                    .placeholder(R.drawable.ic_baseline_insert_photo_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .apply(new RequestOptions().transform(new RoundedCorners(8)))
                    .into(binding.img);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private final GalleryHeaderItemBinding binding;
        public HeaderViewHolder(GalleryHeaderItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(PictureHeaderData data){
            binding.date.setText(data.date);
        }
    }

    private final static DiffUtil.ItemCallback<SuperPictureData> DIFF_CALLBACK = new DiffUtil.ItemCallback<SuperPictureData>() {
        @Override
        public boolean areItemsTheSame(@NonNull SuperPictureData oldItem, @NonNull SuperPictureData newItem) {
            if(oldItem instanceof PictureData && newItem instanceof PictureData)
                return ((PictureData) oldItem).imgUrl.equals(((PictureData) newItem).imgUrl);
            if(oldItem instanceof PictureHeaderData && newItem instanceof PictureHeaderData)
                return ((PictureHeaderData) oldItem).date.equals(((PictureHeaderData) newItem).date);
            return false;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull SuperPictureData oldItem, @NonNull SuperPictureData newItem) {
            return newItem == oldItem;
        }
    };
}
