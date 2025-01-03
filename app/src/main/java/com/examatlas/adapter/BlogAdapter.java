package com.examatlas.adapter;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.BlogSingleViewActivity;
import com.examatlas.activities.CurrentAffairsActivity;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.models.BlogModel;

import java.util.ArrayList;


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
        holder.tags.setText(blogModelArrayList.get(position).getTags());
        String titleStr = blogModelArrayList.get(position).getTitle();

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


        Glide.with(context)
                .load(blogModelArrayList.get(position).getImageURL())
                .error(R.drawable.image1)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return blogModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,tags;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtBlogTitle);
            imageView = itemView.findViewById(R.id.imgBlog);
            tags = itemView.findViewById(R.id.tagTxt);

            if (context instanceof BlogFragment){

                // Set the WebView's width to 150dp
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                itemView.setLayoutParams(params);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    }
                    Intent intent = new Intent(context.getActivity(), BlogSingleViewActivity.class);
                    intent.putExtra("blogID",blogModelArrayList.get(getAdapterPosition()).getBlogID());
                    context.startActivity(intent);
                }
            });
        }
    }
}
