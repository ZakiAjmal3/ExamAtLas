package com.examatlas.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;

public class TestFragment extends Fragment {

    RecyclerView liveClassesRecycler;
    ProgressBar liveProgress;
//    List<Course> courseList = new ArrayList<>();

    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_classes, container, false);

        liveProgress = view.findViewById(R.id.liveProgress);
        liveClassesRecycler = view.findViewById(R.id.liveClassesRecycler);
        liveClassesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

//        getExamsList();

        return view;
    }

//    private void getExamsList() {
//        testProgress.setVisibility(View.VISIBLE);
//        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
//        Call<CourseRequest> call = api.getCourseList();
//        call.enqueue(new Callback<CourseRequest>() {
//            @Override
//            public void onResponse(Call<CourseRequest> call, Response<CourseRequest> response) {
//                testProgress.setVisibility(View.GONE);
//                if (response.body() != null) {
//                    if (response.body().getStatus()) {
//                        courseList.addAll(response.body().getCourses());
//                        TestCourseAdapter testCourseAdapter = new TestCourseAdapter(TestFragment.this, courseList);
//                        testCourseRecycler.setAdapter(testCourseAdapter);
//                    }
//                } else {
//                    Log.e("BODY", "Body is null");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CourseRequest> call, Throwable t) {
//                testProgress.setVisibility(View.GONE);
//                Log.e("EXCEPTION", t.getLocalizedMessage());
//            }
//        });
//    }

}