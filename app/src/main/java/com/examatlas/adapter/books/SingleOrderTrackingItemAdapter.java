package com.examatlas.adapter.books;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.activities.Books.MyBookOrderHistory;
import com.examatlas.activities.Books.SingleOrderViewActivity;
import com.examatlas.activities.Books.TrackingSingleActivity;
import com.examatlas.models.Books.MyOrdersItemModel;
import com.examatlas.models.Books.SingleBookTrackingItemModel;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;

public class SingleOrderTrackingItemAdapter extends RecyclerView.Adapter<SingleOrderTrackingItemAdapter.ViewHolder> {
    Context context;
    ArrayList<SingleBookTrackingItemModel> singleBookTrackingItemModelArrayList;
    public SingleOrderTrackingItemAdapter(Context context, ArrayList<SingleBookTrackingItemModel> singleBookTrackingItemModelArrayList) {
        this.context = context;
        this.singleBookTrackingItemModelArrayList = singleBookTrackingItemModelArrayList;
    }

    @NonNull
    @Override
    public SingleOrderTrackingItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_book_order_tracking_item_timeline, parent, false);
        return new SingleOrderTrackingItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleOrderTrackingItemAdapter.ViewHolder holder, int position) {

        holder.detail.setText(singleBookTrackingItemModelArrayList.get(position).getStatus());
        holder.title.setText(singleBookTrackingItemModelArrayList.get(position).getTitle());

        if (position == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.timelineBGColor));
            holder.timelineView.setMarkerColor(ContextCompat.getColor(context,R.color.green));
            holder.timelineView.setStartLineColor(ContextCompat.getColor(context, R.color.timelineBGColor), ContextCompat.getColor(context, R.color.timelineBGColor));
            holder.timelineView.setEndLineColor(ContextCompat.getColor(context, R.color.green), ContextCompat.getColor(context, R.color.green));
        } else if (position == singleBookTrackingItemModelArrayList.size() - 1) {
            holder.timelineView.setEndLineColor(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.white));
            holder.timelineView.setMarkerColor(ContextCompat.getColor(context,R.color.dark_grey));
            holder.timelineView.setStartLineColor(ContextCompat.getColor(context, R.color.dark_grey), ContextCompat.getColor(context, R.color.dark_grey));
        }else {
            holder.timelineView.setStartLineColor(ContextCompat.getColor(context, R.color.dark_grey), ContextCompat.getColor(context, R.color.dark_grey));
            holder.timelineView.setMarkerColor(ContextCompat.getColor(context,R.color.dark_grey));
            holder.timelineView.setEndLineColor(ContextCompat.getColor(context, R.color.dark_grey), ContextCompat.getColor(context, R.color.dark_grey));
        }
    }

    @Override
    public int getItemCount() {
        return singleBookTrackingItemModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView detail, title;
        TimelineView timelineView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.detail);
            title = itemView.findViewById(R.id.title);
            timelineView = itemView.findViewById(R.id.time_marker);
//            bookImg = itemView.findViewById(R.id.imgBook);

            if (context instanceof SingleOrderViewActivity) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String orderPlacedDate = ((SingleOrderViewActivity) context).getOrderPlacedDate();
                        Intent intent = new Intent(context, TrackingSingleActivity.class);
                        intent.putExtra("shipment_id,", singleBookTrackingItemModelArrayList.get(getAdapterPosition()).getShipmentId());
                        intent.putExtra("orderPlacedDate,", orderPlacedDate);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }
}
