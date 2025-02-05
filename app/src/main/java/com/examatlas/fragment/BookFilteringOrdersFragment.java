package com.examatlas.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.examatlas.R;
import com.examatlas.activities.Books.MyBookOrderHistory;

public class BookFilteringOrdersFragment extends Fragment {
    TextView applyTxtBtn;
    RadioButton radioOrdersButton, radioNotShippedButton, radioCancelledButton;
    String orderType = "Placed", orderDate = "30d";
    RadioButton radioLast30Days, radioLast3Months, radio6Months, radio2025, radio2024, radio2023, radio2022;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.filtering_my_orders_dailog_layout, container, false);

        applyTxtBtn = view.findViewById(R.id.applyTxtBtn);

        applyTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MyBookOrderHistory) {
                    MyBookOrderHistory obj = (MyBookOrderHistory) getActivity();
                    obj.filteringMethod(orderType, orderDate);  // Call the method on the activity
                }
            }
        });
        radioOrdersButton = view.findViewById(R.id.radioOrdersButton);
        radioOrdersButton.setChecked(true);
        radioNotShippedButton = view.findViewById(R.id.radioNotShippedButton);
        radioCancelledButton = view.findViewById(R.id.radioCancelledButton);

        radioLast30Days = view.findViewById(R.id.radioLast30DaysButton);
        radioLast30Days.setChecked(true);
        radioLast3Months = view.findViewById(R.id.radioLast3MonthsButton);
        radio6Months = view.findViewById(R.id.radio6MonthsButton);
        radio2025 = view.findViewById(R.id.radio2025Button);
        radio2024 = view.findViewById(R.id.radio2024Button);
        radio2023 = view.findViewById(R.id.radio2023Button);
        radio2022 = view.findViewById(R.id.radio2022Button);

        radioOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderType = "Placed";
                radioOrdersButton.setChecked(true);
                radioCancelledButton.setChecked(false);
                radioNotShippedButton.setChecked(false);
            }
        });
        radioNotShippedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderType = "Not Shipped";
                radioNotShippedButton.setChecked(true);
                radioCancelledButton.setChecked(false);
                radioOrdersButton.setChecked(false);
            }
        });
        radioCancelledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderType = "Cancelled";
                radioCancelledButton.setChecked(true);
                radioOrdersButton.setChecked(false);
                radioNotShippedButton.setChecked(false);
            }
        });

        radioLast30Days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDate = "30d";
                radioLast30Days.setChecked(true);
                radioLast3Months.setChecked(false);
                radio6Months.setChecked(false);
                radio2025.setChecked(false);
                radio2024.setChecked(false);
                radio2023.setChecked(false);
                radio2022.setChecked(false);
            }
        });
        radioLast3Months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDate = "90d";
                radioLast3Months.setChecked(true);
                radioLast30Days.setChecked(false);
                radio6Months.setChecked(false);
                radio2025.setChecked(false);
                radio2024.setChecked(false);
                radio2023.setChecked(false);
                radio2022.setChecked(false);
            }
        });
        radio6Months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDate = "180d";
                radio6Months.setChecked(true);
                radioLast30Days.setChecked(false);
                radioLast3Months.setChecked(false);
                radio2025.setChecked(false);
                radio2024.setChecked(false);
                radio2023.setChecked(false);
                radio2022.setChecked(false);
            }
        });
        radio2025.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDate = "2025";
                radio2025.setChecked(true);
                radioLast30Days.setChecked(false);
                radioLast3Months.setChecked(false);
                radioLast30Days.setChecked(false);
                radio2024.setChecked(false);
                radio2023.setChecked(false);
                radio2022.setChecked(false);
            }
        });
        radio2024.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDate = "2024";
                radio2024.setChecked(true);
                radioLast30Days.setChecked(false);
                radioLast3Months.setChecked(false);
                radioLast30Days.setChecked(false);
                radio2025.setChecked(false);
                radio2023.setChecked(false);
                radio2022.setChecked(false);
            }
        });
        radio2023.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDate = "2023";
                radio2023.setChecked(true);
                radioLast30Days.setChecked(false);
                radioLast3Months.setChecked(false);
                radioLast30Days.setChecked(false);
                radio2024.setChecked(false);
                radio2025.setChecked(false);
                radio2022.setChecked(false);
            }
        });
        radio2022.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDate = "2022";
                radio2022.setChecked(true);
                radioLast30Days.setChecked(false);
                radioLast3Months.setChecked(false);
                radioLast30Days.setChecked(false);
                radio2024.setChecked(false);
                radio2023.setChecked(false);
                radio2025.setChecked(false);
            }
        });

        return view;
    }
}
