package com.examatlas.activities.Books;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.examatlas.R;

public class CustomRatingBar extends LinearLayout {

    private int numStars = 5;
    private float rating = 0f;
    private ImageView[] starImages;

    public CustomRatingBar(Context context) {
        super(context);
        init(context);
    }

    public CustomRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Set the orientation of the layout to horizontal
        setOrientation(HORIZONTAL);

        // Initialize the stars (you can change the number of stars or images here)
        starImages = new ImageView[numStars];

        for (int i = 0; i < numStars; i++) {
            ImageView starImage = new ImageView(context);
            starImage.setImageResource(R.drawable.star_empty_rating_bar);  // Initial empty star

            // Set image layout parameters (wrap content so each star will take up space accordingly)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            starImage.setLayoutParams(params);
            addView(starImage);
            starImages[i] = starImage;
        }
        updateRating(rating);
    }

    public void setRating(float rating) {
        if (rating < 0) {
            rating = 0;
        } else if (rating > numStars) {
            rating = numStars;
        }
        this.rating = rating;
        updateRating(rating);
    }

    private void updateRating(float rating) {
        int fullStars = (int) rating;  // Full stars (integer part)
        float fractional = rating - fullStars;  // Fractional part (for half-star logic)

        for (int i = 0; i < numStars; i++) {
            if (i < fullStars) {
                starImages[i].setImageResource(R.drawable.star_filled_rating_bar);  // Full star
            } else if (i == fullStars && fractional >= 0.5) {
                starImages[i].setImageResource(R.drawable.star_half_filled_rating_bar);  // Half star
            } else {
                starImages[i].setImageResource(R.drawable.star_empty_rating_bar);  // Empty star
            }
        }
    }
}
