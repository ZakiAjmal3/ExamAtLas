package com.examatlas.adapter.books;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;

public class StickyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View types
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_STICKY = 2;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            // Inflating Header layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_homepage_sticky_header_layout, parent, false);
            return new VHHeader(view);
        } else if (viewType == TYPE_STICKY) {
            // Inflating Sticky layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_homepage_sticky_stick_layout, parent, false);
            return new VHSticky(view);
        } else if (viewType == TYPE_FOOTER) {
            // Inflating Footer layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_homepage_sticky_footer_layout, parent, false);
            return new VHFooter(view);
        }
        throw new RuntimeException("There is no view type that matches: " + viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionStickyHeader(position)) {
            return TYPE_STICKY;
        }
        return TYPE_FOOTER;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionStickyHeader(int position) {
        return position == 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHHeader) {
            // Bind Header layout data
            VHHeader vhHeader = (VHHeader) holder;
            // Optionally set any data in the header, like the logo or toolbar buttons
        } else if (holder instanceof VHSticky) {
            // Bind Sticky layout data (Search bar)
            VHSticky vhSticky = (VHSticky) holder;
            // Optionally set data for sticky header (like search functionality)
        } else if (holder instanceof VHFooter) {
            // Bind Footer layout data (Slider)
            VHFooter vhFooter = (VHFooter) holder;
            // Optionally set data for footer if required (e.g., slider data)
        }
    }

    @Override
    public int getItemCount() {
        return 3;  // 1 header, 1 sticky, 1 footer; Adjust this as needed for more items in the list
    }

    // Header ViewHolder
    private class VHHeader extends RecyclerView.ViewHolder {
        ImageView logo, cartBtn;

        public VHHeader(View view) {
            super(view);
            logo = view.findViewById(R.id.logo);
            cartBtn = view.findViewById(R.id.cartBtn);
        }
    }

    // Sticky ViewHolder (Search Bar)
    private class VHSticky extends RecyclerView.ViewHolder {
        EditText searchIcon;
        ImageView search;

        public VHSticky(View view) {
            super(view);
            searchIcon = view.findViewById(R.id.search_icon);
            search = view.findViewById(R.id.search);
        }
    }

    // Footer ViewHolder (Slider)
    private class VHFooter extends RecyclerView.ViewHolder {
        com.denzcoskun.imageslider.ImageSlider slider;

        public VHFooter(View view) {
            super(view);
            slider = view.findViewById(R.id.slider);
        }
    }
}
