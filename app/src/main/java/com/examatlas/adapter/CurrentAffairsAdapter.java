package com.examatlas.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.CurrentAffairsActivity;
import com.examatlas.activities.CurrentAffairsSingleViewActivity;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.fragment.HomeFragment;
import com.examatlas.models.CurrentAffairsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CurrentAffairsAdapter extends RecyclerView.Adapter<CurrentAffairsAdapter.ViewHolder> {
    private ArrayList<CurrentAffairsModel> currentAffairsModelArrayList;
    Fragment context;
    Context context1;

    public CurrentAffairsAdapter(ArrayList<CurrentAffairsModel> currentAffairsModelArrayList, Fragment context, Context context1) {
        this.currentAffairsModelArrayList = currentAffairsModelArrayList;
        this.context = context;
        this.context1 = context1;
    }

    @NonNull
    @Override
    public CurrentAffairsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_current_affair_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentAffairsAdapter.ViewHolder holder, int position) {

        if (context1 != null && context1 instanceof CurrentAffairsActivity){
            // Increase the size of the ImageView when the context is CurrentAffairsActivity
            ViewGroup.LayoutParams layoutParams = holder.cfImage.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;  // Set the width to match parent
            layoutParams.height = 600;// Set a fixed height (adjust this value as needed)
            holder.cfImage.setLayoutParams(layoutParams);

            // Increase the text size of the title when the context is CurrentAffairsActivity
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);  // Increase text size to 18sp (adjust this value as needed)
        }
        holder.itemView.setTag(currentAffairsModelArrayList.get(position));

        if (context != null){
            Glide.with(context)
                    .load(currentAffairsModelArrayList.get(position).getCfImageURL())
                    .error(R.drawable.image1)
                    .into(holder.cfImage);
        }else {
            Glide.with(context1)
                    .load(currentAffairsModelArrayList.get(position).getCfImageURL())
                    .error(R.drawable.image1)
                    .into(holder.cfImage);
        }

        String titleStr = currentAffairsModelArrayList.get(position).getCfTitle();
// Set the title text first to measure the number of lines
        holder.title.setText(titleStr);

        holder.title.post(new Runnable() {
            @Override
            public void run() {
                // Get the Layout object from the TextView to measure lines
                Layout layout = holder.title.getLayout();

                // Check the number of lines
                int lineCount = layout.getLineCount();

                // Ensure there are at least two lines
                if (lineCount >= 2) {
                    String firstTwoLinesText = "";

                    // Get the start and end positions for the first two lines
                    int startLine = 0; // The first line
                    int endLine = 1;   // The second line

                    // Extract the first line
                    int startPos1 = layout.getLineStart(startLine);
                    int endPos1 = layout.getLineEnd(startLine);
                    firstTwoLinesText += holder.title.getText().subSequence(startPos1, endPos1);

                    // Extract the second line
                    int startPos2 = layout.getLineStart(endLine);
                    int endPos2 = layout.getLineEnd(endLine);
                    firstTwoLinesText += " " + holder.title.getText().subSequence(startPos2, endPos2);

                    // Truncate the last three characters and add "..."
                    if (firstTwoLinesText.length() > 3) {
                        firstTwoLinesText = firstTwoLinesText.substring(0, firstTwoLinesText.length() - 3) + "...";
                    }

                    // Set the text to the title with the first two lines and ellipsis
                    holder.title.setText(firstTwoLinesText);
                } else {
                    // If there are fewer than 2 lines, just set the text normally
                    holder.title.setText(titleStr);
                }
            }
        });

        holder.tags.setText(currentAffairsModelArrayList.get(position).getCfTags());

//        if (currentAffairsModelArrayList.get(position).getCfImage() != null) {
        if (context != null) {
            Glide.with(context)
//                    .load(currentAffairsModelArrayList.get(position).getCfImage())
                    .load(currentAffairsModelArrayList.get(position).getCfImageURL())
                    .placeholder(R.drawable.image1)
                    .error(R.drawable.image1)
                    .into(holder.cfImage);
        }
//        } else {
//            Glide.with(context)
//                    .load(R.drawable.noimage)
//                    .placeholder(R.drawable.noimage)
//                    .error(R.drawable.noimage)
//                    .into(holder.cfImage);
//        }
        // Enable JavaScript (optional, depending on your content)

        holder.content.setVisibility(View.GONE);

        WebSettings webSettings = holder.content.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String htmlContentTxt = currentAffairsModelArrayList.get(position).getCfContent();

        String injectedCss = "<style type=\"text/css\">"
                + "p { font-size: 10px !important; line-height: 1.4; }"    // Set <p> font size to 10px and line-height for readability
                + "h1 { font-size: 14px !important; }"    // Set <h1> font size to 14px (smaller)
                + "h2 { font-size: 12px !important; }"    // Set <h2> font size to 12px (smaller)
                + "h3 { font-size: 11px !important; }"    // Set <h3> font size to 11px (smaller)
                + "h4 { font-size: 10px !important; }"    // Set <h4> font size to 10px (smaller)
                + "h5 { font-size: 9px !important; }"     // Set <h5> font size to 9px (smaller)
                + "h6 { font-size: 8px !important; }"     // Set <h6> font size to 8px (smaller)
                + "ul, ol { font-size: 10px !important; }" // Set list items font size to 10px
                + "li { font-size: 10px !important; }"    // Set list item font size to 10px
                + "img { width: 100% !important; height: auto !important; }"  // Adjust image size to fit container
                + "</style>";
        String fullHtmlContent = injectedCss + htmlContentTxt;

        // Disable scrolling and over-scrolling
        holder.content.setVerticalScrollBarEnabled(false);  // Disable vertical scroll bar
        holder.content.setOverScrollMode(WebView.OVER_SCROLL_NEVER); // Disable over-scrolling effect

        // Load the modified HTML content
        holder.content.loadData(fullHtmlContent, "text/html", "UTF-8");

        if (context1 != null){
            // If context is not HomeFragment, set the CardView width to match parent
            ViewGroup.LayoutParams layoutParams = holder.mainCardView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.mainCardView.setLayoutParams(layoutParams);
            holder.content.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return currentAffairsModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, tags;
        ImageView cfImage;
        WebView content;
        CardView mainCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cfImage = itemView.findViewById(R.id.imgAffair);
            title = itemView.findViewById(R.id.txtTitle);
            content = itemView.findViewById(R.id.txtContent);
            tags = itemView.findViewById(R.id.tagTxt);
            mainCardView = itemView.findViewById(R.id.main);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof HomeFragment) {
                        Intent intent = new Intent(context.getContext(), CurrentAffairsSingleViewActivity.class);
                        intent.putExtra("CAID",currentAffairsModelArrayList.get(getAdapterPosition()).getCfID());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}