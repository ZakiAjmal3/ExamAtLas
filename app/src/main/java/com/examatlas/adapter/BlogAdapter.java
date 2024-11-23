package com.examatlas.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.fragment.HomeFragment;
import com.examatlas.models.BlogModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    private ArrayList<BlogModel> blogModelArrayList;
    Fragment context;

    public BlogAdapter(ArrayList<BlogModel> blogModelArrayList, Fragment context) {
        this.blogModelArrayList = blogModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BlogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_blog_layout_with_wide_width,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull BlogAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(blogModelArrayList.get(position));
        holder.title.setText(blogModelArrayList.get(position).getTitle());
        holder.tags.setText(blogModelArrayList.get(position).getTags());

        holder.webViewContent.setVisibility(View.GONE);

        // Enable JavaScript (optional, depending on your content)
        WebSettings webSettings = holder.webViewContent.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String htmlContentTxt = blogModelArrayList.get(position).getContent();

        // Inject CSS to control the image size
        String injectedCss = "<style>"
                + "p { font-size: 20px; }" // Increase text size only for <p> tags (paragraphs)
                + "img { width: 100%; height: auto; }" // Adjust image size as needed
                + "</style>";
        String fullHtmlContent = injectedCss + htmlContentTxt;

        // Disable scrolling and over-scrolling
        holder.webViewContent.setVerticalScrollBarEnabled(false);  // Disable vertical scroll bar
        holder.webViewContent.setOverScrollMode(WebView.OVER_SCROLL_NEVER); // Disable over-scrolling effect

        // Load the modified HTML content
        holder.webViewContent.loadData(fullHtmlContent, "text/html", "UTF-8");

        // If context is not HomeFragment, set the CardView width to match parent
        if (!(context instanceof HomeFragment)) {
            // Modify the width of the CardView in the RecyclerView
            ViewGroup.LayoutParams layoutParams = holder.mainCardView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.mainCardView.setLayoutParams(layoutParams);
            holder.webViewContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return blogModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,tags;
        WebView webViewContent;
        CardView mainCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtBlogTitle);
            webViewContent = itemView.findViewById(R.id.content);
            tags = itemView.findViewById(R.id.tagTxt);

            mainCardView = itemView.findViewById(R.id.blogLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the context is an instance of HomeFragment
                    if (context instanceof HomeFragment) {
                        // Cast context to DashboardActivity and load the BlogFragment
                        DashboardActivity dashboardActivity = (DashboardActivity) context.getActivity();
                        if (dashboardActivity != null) {
                            dashboardActivity.loadFragment(new BlogFragment());
                            dashboardActivity.bottom_navigation.setSelectedItemId(R.id.blogs);
                            dashboardActivity.bottom_navigation.setSelected(true);
                        }
                    }
                }
            });
        }
    }
}
