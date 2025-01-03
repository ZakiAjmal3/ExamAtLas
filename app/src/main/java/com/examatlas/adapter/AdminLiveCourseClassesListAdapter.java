package com.examatlas.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.AdminJoinLiveClassActivity;
import com.examatlas.activities.AdminLiveCoursesClassesActivity;
import com.examatlas.models.AdminLiveCoursesClassesListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminLiveCourseClassesListAdapter extends RecyclerView.Adapter<AdminLiveCourseClassesListAdapter.ViewHolder> {
    Context context;
    ArrayList<AdminLiveCoursesClassesListModel> liveCoursesClassesListModelArrayList;
    String joinClassURL = Constant.BASE_URL + "liveclass/joinNow";
    SessionManager sessionManager;
    String authToken;

    public AdminLiveCourseClassesListAdapter(Context context, ArrayList<AdminLiveCoursesClassesListModel> liveCoursesClassesListModelArrayList) {
        this.context = context;
        this.liveCoursesClassesListModelArrayList = liveCoursesClassesListModelArrayList;
        sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public AdminLiveCourseClassesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_live_courses_classes_list_item_list, parent, false);
        return new AdminLiveCourseClassesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminLiveCourseClassesListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.itemView.setTag(position);

        holder.classTitleTxt.setText(liveCoursesClassesListModelArrayList.get(position).getTitle());
        holder.liveTxt.setText(liveCoursesClassesListModelArrayList.get(position).getStatus());

        String formatedDate = formatDate(liveCoursesClassesListModelArrayList.get(position).getDate());
        String formatedTimeAndDate = formatedDate + " (" + liveCoursesClassesListModelArrayList.get(position).getTime() + ")";

        holder.timeAndDateTxt.setText(formatedTimeAndDate);
        holder.scheduleTxtDisplay.setText(liveCoursesClassesListModelArrayList.get(position).getScheduledTime());

        holder.goLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinMeetingNow(liveCoursesClassesListModelArrayList.get(position));
            }
        });

    }

    private void joinMeetingNow(AdminLiveCoursesClassesListModel adminLiveCoursesClassesListModel) {
        String scheduleLiveClassesURL = Constant.BASE_URL + "liveclass/joinNow";

        // Prepare JSON Object for the request body
        JSONObject params = new JSONObject();
        try {
            params.put("role", sessionManager.getUserData().get("role"));
            params.put("scheduledClassId", adminLiveCoursesClassesListModel.getClassID());
            params.put("meetingId", adminLiveCoursesClassesListModel.getMeetingID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the JSONObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, scheduleLiveClassesURL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        try {
                            Log.e("createScheduleClasses", responseObject.toString());
                            boolean status = responseObject.getBoolean("status");
                            if (status) {
                                String token = responseObject.getString("token");
//                                2024-12-20 15:47:55.625  3078-3078  createScheduleClasses   com.examatlas                        E  {"status":true,"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI2N2IwZjkxYS1lODNhLTQ2MDEtYWJjMy01ZjU4MGQ4Yjg2MDIiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19jcmVhdGVfcm9vbSIsImFsbG93X2NyZWF0ZV9tZWV0aW5nIiwiYWxsb3dfam9pbiJdLCJ2ZXJzaW9uIjoyLCJtZWV0aW5nSWQiOiI2NzY1MWE2MmQzNGNmNGY2YzI3Y2Q0YTMiLCJpYXQiOjE3MzQ2ODk4NzQsImV4cCI6MTczNDY5MzQ3NH0.zaA3BUBOYc-ZGvYFchUud2f1RqixP9B7XMcg3nXENoc","getScheduledCourse":{"_id":"67651a62d34cf4f6c27cd4a3","courseId":"6749725443e055f3c09cad05","title":"testing","description":"description","meetingId":"946w-oyni-5h04","time":"09:30","date":"2024-12-31T00:00:00.000Z","addedBy":"67629d52b0cf0af603f7e3ff","deletedBy":null,"deletedAt":null,"scheduleTime":"2024-12-31T00:00:00.000Z","duration":2,"students":[],"status":"scheduled","is_active":true,"createdAt":"2024-12-20T07:18:58.147Z","updatedAt":"2024-12-20T07:18:58.147Z","__v":0}}
                                Intent intent = new Intent(context, AdminJoinLiveClassActivity.class);
                                intent.putExtra("meetingID", adminLiveCoursesClassesListModel.getMeetingID());
                                intent.putExtra("token", token);
                                intent.putExtra("course_id", adminLiveCoursesClassesListModel.getCourseID());
                                context.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(context, "An error occurred while processing the response.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error response
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                String responseData = new String(error.networkResponse.data, "UTF-8");
                                errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                                errorMessage += "\nResponse Data: " + responseData;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("CategoryFetchError", errorMessage);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add Authorization header if needed
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        // Add the request to the queue
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return liveCoursesClassesListModelArrayList.size();
    }
    private String formatDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Date date;
        try {
            date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // Return original if parsing fails
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeAndDateTxt,classTitleTxt,liveTxt,scheduleTxtDisplay,goLiveBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeAndDateTxt = itemView.findViewById(R.id.timeAndDateTxt);
            classTitleTxt = itemView.findViewById(R.id.classTitleTxt);
            liveTxt = itemView.findViewById(R.id.liveTxt);
            scheduleTxtDisplay = itemView.findViewById(R.id.scheduleTxtDisplay);
            goLiveBtn = itemView.findViewById(R.id.goLiveTxt);

        }
    }
}
