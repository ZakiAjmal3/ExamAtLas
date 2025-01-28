package com.examatlas.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity2DetailsInput extends AppCompatActivity {
    Button nextBtn;
    EditText edtFirstName,edtLastName, edtEmail, edtPhone;

    SessionManager sessionManager;
    String authToken;
    Spinner stateSpinner,citySpinner;
    String[] states = {"Select State","Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal" };
    String selectedState = null,selectedCity = null;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2_details_input);

        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtNumber);
        stateSpinner = findViewById(R.id.spinnerState);
        citySpinner = findViewById(R.id.spinnerCity);

        nextBtn = findViewById(R.id.userNextBtn);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        Map<String, String[]> citiesMap = new HashMap<>();
        citiesMap.put("Andhra Pradesh", new String[] {
                "Select City", "Anantapur", "Chittoor", "East Godavari", "Guntur", "YSR Kadapa", "Krishna", "Kurnool", "Nellore",
                "Prakasam", "Srikakulam", "Visakhapatnam", "Vizianagaram", "West Godavari"
        });

        citiesMap.put("Arunachal Pradesh", new String[] {
                "Select City", "Anjaw", "Changlang", "East Kameng", "East Siang", "Kurung Kumey", "Lohit", "Longding",
                "Lower Dibang Valley", "Lower Subansiri", "Namsai", "Papum Pare", "Tawang", "Tirap", "Upper Dibang Valley",
                "Upper Siang", "West Kameng", "West Siang", "Siang", "Dibang Valley", "Kamle", "Kra Daadi", "Lepa-Rada",
                "Pakke-Kessang", "Keyi Panyor"
        });

        citiesMap.put("Assam", new String[]{
                "Select City", "Baksa", "Barpeta", "Bongaigaon", "Cachar", "Charaideo", "Chirang", "Darrang", "Dhemaji", "Dhubri",
                "Dibrugarh", "Golaghat", "Hailakandi", "Hojai", "Jorhat", "Kamrup", "Kamrup Metropolitan", "Karbi Anglong", "Karimganj",
                "Kokrajhar", "Lakhimpur", "Majuli", "Morigaon", "Nagaon", "Nalbari", "Sivasagar", "Sonitpur", "South Salmara-Mankachar",
                "Tinsukia", "Udalguri", "West Karbi Anglong", "Bajali", "Biswanath", "Dima Hasao", "Goalpara"
        });

        citiesMap.put("Bihar", new String[]{
                "Select City", "Araria", "Arwal", "Aurangabad", "Banka", "Begusarai", "Bhagalpur", "Bhojpur", "Buxar", "Darbhanga",
                "East Champaran", "Gaya", "Gopalganj", "Jamui", "Jehanabad", "Kaimur", "Katihar", "Khagaria", "Kishanganj",
                "Lakhisarai", "Madhepura", "Madhubani", "Munger", "Muzaffarpur", "Nalanda", "Nawada", "Purnia", "Rohtas", "Sasaram",
                "Sitamarhi", "Supaul", "Siwan", "Vaishali", "West Champaran"
        });

        citiesMap.put("Chhattisgarh", new String[]{
                "Select City", "Balod", "Baloda Bazar", "Balrampur", "Bastar", "Bemetara", "Bijapur", "Bilaspur", "Dantewada", "Dhamtari",
                "Durg", "Gariaband", "Gaurella-Pendra-Marwahi", "Janjgir-Champa", "Jashpur", "Kabirdham", "Kanker", "Kondagaon",
                "Khairagarh-Chhuikhadan-Gandai", "Korba", "Koriya", "Mahasamund", "Manendragarh-Chirmiri-Bharatpur",
                "Mohla-Manpur-Ambagarh Chowki", "Mungeli", "Narayanpur", "Raigarh", "Raipur", "Rajnandgaon", "Sarangarh-Bilaigarh",
                "Sakti", "Sukma", "Surajpur", "Surguja"
        });

        citiesMap.put("Goa", new String[]{
                "Select City", "North Goa", "South Goa"
        });
        citiesMap.put("Gujarat", new String[]{
                "Select City", "Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha", "Bharuch", "Bhavnagar", "Botad", "Chhota Udaipur",
                "Dahod", "Dang", "Devbhoomi Dwarka", "Gandhinagar", "Gir Somnath", "Jamnagar", "Junagadh", "Kutch", "Kheda", "Mahisagar",
                "Mehsana", "Morbi", "Narmada", "Navsari", "Panchmahal", "Patan", "Porbandar", "Rajkot", "Sabarkantha", "Surat",
                "Surendranagar", "Tapi", "Vadodara", "Valsad"
        });
        citiesMap.put("Haryana", new String[]{
                "Select City", "Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurugram", "Hisar", "Jhajjar", "Jind",
                "Kaithal", "Karnal", "Kurukshetra", "Mahendragarh", "Narnaul", "Palwal", "Panchkula", "Panipat", "Rewari", "Rohtak",
                "Sirsa", "Sonipat", "Yamunanagar"
        });

        citiesMap.put("Himachal Pradesh", new String[]{
                "Select City", "Bilaspur", "Chamba", "Hamirpur", "Kangra", "Kullu", "Mandi", "Solan", "Sirmaur", "Una", "Kinnaur", "Lahaul and Spiti", "Shimla"
        });

        citiesMap.put("Jharkhand", new String[]{
                "Select City", "Bokaro", "Chaibasa", "Dhanbad", "Deoghar", "Dumka", "Garhwa", "Giridih", "Hazaribagh", "Jamtara", "Khunti",
                "Koderma", "Latehar", "Lohardaga", "Pakur", "Palamu", "Ranchi", "Sahebganj", "Seraikela-Kharsawan", "Simdega", "West Singhbhum",
                "Chatra", "East Singhbhum", "Godda", "Gumla", "Ramgarh"
        });

        citiesMap.put("Karnataka", new String[]{
                "Select City", "Bagalkot", "Bengaluru", "Belagavi", "Ballari", "Bidar", "Chamarajanagar", "Chikkamagaluru", "Chitradurga",
                "Dakshina Kannada", "Davangere", "Dharwad", "Gadag", "Hassan", "Haveri", "Kalaburagi", "Kodagu", "Kolar", "Koppal", "Mysuru",
                "Raichur", "Ramanagara", "Shivamogga", "Tumakuru", "Udupi", "Uttara Kannada", "Yadgir", "Vijayanagara"
        });

        citiesMap.put("Kerala", new String[]{
                "Select City", "Alappuzha", "Ernakulam", "Idukki", "Kottayam", "Kozhikode", "Malappuram", "Palakkad", "Pathanamthitta",
                "Thiruvananthapuram", "Thrissur", "Wayanad"
        });

        citiesMap.put("Madhya Pradesh", new String[]{
                "Select City", "Alirajpur", "Anuppur", "Ashoknagar", "Balaghat", "Barwani", "Betul", "Bhind", "Bhopal", "Burhanpur", "Chhindwara",
                "Damoh", "Datia", "Dewas", "Dhar", "Dindori", "Guna", "Gwalior", "Hoshangabad", "Indore", "Jabalpur", "Jhabua", "Katni",
                "Khandwa", "Khargone", "Mandsaur", "Morena", "Narmada", "Neemuch", "Panna", "Raisen", "Rajgarh", "Ratlam", "Rewa", "Sagar",
                "Satna", "Sehore", "Seoni", "Shahdol", "Shivpuri", "Sidhi", "Singrauli", "Tikamgarh", "Ujjain", "Vidisha"
        });
        citiesMap.put("Maharashtra", new String[]{
                "Select City", "Ahmednagar", "Akola", "Amravati", "Aurangabad", "Bhandara", "Beed", "Bhandara", "Buldhana", "Chandrapur",
                "Dhule", "Gadchiroli", "Gondia", "Hingoli", "Jalgaon", "Jalna", "Kolhapur", "Latur", "Mumbai", "Nagpur", "Nanded", "Nandurbar",
                "Navi Mumbai", "Osmanabad", "Pune", "Raigad", "Ratnagiri", "Sangli", "Satara", "Solapur", "Thane", "Wardha", "Washim", "Yavatmal"
        });
        citiesMap.put("Manipur", new String[]{
                "Select City", "Bishnupur", "Churachandpur", "Imphal East", "Imphal West", "Senapati", "Tamenglong", "Thoubal", "Ukhrul"
        });
        citiesMap.put("Meghalaya", new String[]{
                "Select City", "East Garo Hills", "East Khasi Hills", "Jaintia Hills", "Ri-Bhoi", "South Garo Hills", "South Khasi Hills",
                "West Garo Hills", "West Khasi Hills"
        });
        citiesMap.put("Mizoram", new String[]{
                "Select City", "Aizawl", "Champhai", "Kolasib", "Lunglei", "Mamit", "Serchhip", "Siaha"
        });

        citiesMap.put("Nagaland", new String[] {
                "Select City", "Dimapur", "Kohima", "Mokokchung", "Mon", "Phek", "Tuensang", "Wokha", "Zunheboto"
        });

        citiesMap.put("Odisha", new String[] {
                "Select City", "Angul", "Balangir", "Balasore", "Bargarh", "Bhadrak", "Boudh", "Cuttack", "Deogarh", "Dhenkanal",
                "Gajapati", "Ganjam", "Jagatsinghpur", "Jajpur", "Jharsuguda", "Kalahandi", "Kandhamal", "Kendrapara", "Kendujhar", "Khurda",
                "Koraput", "Malkangiri", "Mayurbhanj", "Nayagarh", "Nuapada", "Puri", "Rayagada", "Sambalpur", "Sonepur", "Subarnapur",
                "Sundargarh"
        });

        citiesMap.put("Punjab", new String[] {
                "Select City", "Amritsar", "Barnala", "Bathinda", "Fatehgarh Sahib", "Firozpur", "Gurdaspur", "Hoshiarpur", "Jalandhar",
                "Kapurthala", "Ludhiana", "Mansa", "Moga", "Muktsar", "Pathankot", "Patiala", "Rupnagar", "S.A.S. Nagar", "Sangrur", "Tarn Taran"
        });

        citiesMap.put("Rajasthan", new String[] {
                "Select City", "Ajmer", "Alwar", "Banswara", "Baran", "Barmer", "Bharatpur", "Bhilwara", "Bikaner", "Bundi", "Chittorgarh",
                "Churu", "Dausa", "Dholpur", "Dungarpur", "Hanumangarh", "Jaipur", "Jaisalmer", "Jhalawar", "Jhunjhunu", "Jodhpur", "Karauli",
                "Kota", "Nagaur", "Pali", "Pratapgarh", "Rajasamand", "Sikar", "Sirohi", "Sri Ganganagar", "Tonk", "Udaipur"
        });

        citiesMap.put("Sikkim", new String[]{
                "Select City", "Gangtok", "Mangan", "Pakyong", "Soreng", "Namchi", "Gyalshing"
        });

        citiesMap.put("Tamil Nadu", new String[]{
                "Select City", "Ariyalur", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kanchipuram", "Kanyakumari",
                "Karur", "Krishnagiri", "Madurai", "Nagapattinam", "Namakkal", "Perambalur", "Pudukkottai", "Ramanathapuram", "Salem", "Sivaganga",
                "Tenkasi", "Thanjavur", "Theni", "Tiruvallur", "Tirunelveli", "Tirupur", "Vellore", "Villupuram", "Virudhunagar"
        });

        citiesMap.put("Telangana", new String[]{
                "Select City", "Adilabad", "Hyderabad", "Jagtial", "Jangaon", "Jayashankar", "Jogulamba", "Kamareddy", "Karimnagar",
                "Khammam", "Mahabubnagar", "Medak", "Nalgonda", "Nirmal", "Nizamabad", "Peddapalli", "Sangareddy", "Siddipet", "Suryapet",
                "Vikarabad", "Warangal", "Kumuram Bheem Asifabad", "Mancherial", "Rajanna Sircilla", "Jayashankar Bhupalpally",
                "Medak", "Sangareddy", "Hanumakonda", "Mulugu", "Bhadradri Kothagudem", "Mahabubabad", "Nalgonda", "Yadadri Bhuvanagiri",
                "Medchal–Malkajgiri", "Ranga Reddy", "Narayanpet", "Mahabubnagar", "Nagarkurnool", "Wanaparthy", "Jogulamba Gadwal"
        });

        citiesMap.put("Tripura", new String[]{
                "Select City", "Dhalai", "Khowai", "North Tripura", "Sepahijala", "South Tripura", "Unakoti", "West Tripura"
        });
        citiesMap.put("Uttarakhand", new String[]{
                "Select City", "Almora", "Bageshwar", "Chamoli", "Champawat", "Dehradun", "Haridwar", "Nainital", "Pauri Garhwal", "Pithoragarh",
                "Rudraprayag", "Tehri Garhwal", "Udham Singh Nagar", "Uttarkashi"
        });

        citiesMap.put("Uttar Pradesh", new String[]{
                "Select City", "Agra", "Aligarh", "Ambedkar Nagar", "Amethi", "Amroha", "Auraiya", "Ayodhya", "Azamgarh", "Baghpat", "Ballia",
                "Balrampur", "Banda", "Barabanki", "Bareilly", "Basti", "Bijnor", "Budaun", "Bulandshahr", "Chandauli", "Chitrakoot", "Deoria",
                "Etah", "Etawah", "Faizabad", "Farrukhabad", "Fatehpur", "Firozabad", "Gautam Buddh Nagar", "Ghaziabad", "Gonda", "Gorakhpur",
                "Hamirpur", "Hapur", "Hardoi", "Hathras", "Jaunpur", "Jhansi", "Jalaun", "Jansath", "Jeevan Nagar", "Kanpur", "Kannauj", "Kanshiram Nagar",
                "Kaushambi", "Kushinagar", "Lakhimpur Kheri", "Lalitpur", "Lucknow", "Maharajganj", "Mahoba", "Mainpuri", "Mathura", "Mau", "Meerut",
                "Mirzapur", "Moradabad", "Muzaffarnagar", "Pilibhit", "Pratapgarh", "Prayagraj", "Rae Bareli", "Rampur", "Saharanpur", "Sant Kabir Nagar",
                "Sant Ravidas Nagar", "Sambhal", "Shahjahanpur", "Shamli", "Shravasti", "Siddharthnagar", "Sitapur", "Sonbhadra", "Sultanpur", "Unnao", "Varanasi"
        });
        // ArrayAdapter for States Spinner
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

// On Item Selected Listener for the States Spinner
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedState = (String) parentView.getItemAtPosition(position);

                // Get cities for the selected state (handle null case)
                String[] cities = citiesMap.get(selectedState);
                if (cities == null) {
                    cities = new String[]{};  // Set to empty array if no cities are found
                }

                // ArrayAdapter for Cities Spinner
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(SignUpActivity2DetailsInput.this, android.R.layout.simple_spinner_item, cities);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle no selection if necessary
            }
        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCity = (String) parentView.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle no selection if necessary
            }
        });
        nextBtn.setOnClickListener(v -> {
            boolean isAllFieldsValid = checkAllFields();
            if (isAllFieldsValid){
                sendingOTP();
            }
        });
    }
    public boolean checkAllFields(){
        if (edtFirstName.getText().toString().isEmpty()){
            edtFirstName.setError("First Name is required");
            return false;
        }
        if (edtLastName.getText().toString().isEmpty()){
            edtLastName.setError("Last Name is required");
            return false;
        }
        if (edtPhone.getText().toString().isEmpty()) {
            edtPhone.setError("Phone is required");
            return false;
        }
        if (edtEmail.getText().toString().isEmpty()){
            edtEmail.setError("Email is required");
            return false;
        }
        if (selectedState == null || selectedState.equals("Select State")) {
            Toast.makeText(this, "Please select a state", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedCity == null || selectedCity.equals("Select City")) {
            Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendingOTP() {
        String verifyEmailUrl = Constant.BASE_URL + "v1/otp/email";

        Log.e("sendingOTP method", verifyEmailUrl);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",edtEmail.getText().toString().trim());
            jsonObject.put("action","registration");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        progressDialog = new Dialog(SignUpActivity2DetailsInput.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, verifyEmailUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("success");
                            String message = response.getString("message");
                            Toast.makeText(SignUpActivity2DetailsInput.this, message, Toast.LENGTH_SHORT).show();

                            if (status.equals("true")) {
                                Log.e("Success log",response.toString());
                                Intent intent = new Intent(SignUpActivity2DetailsInput.this, OtpActivity.class);
                                intent.putExtra("firstName",edtFirstName.getText().toString().trim());
                                intent.putExtra("lastName",edtLastName.getText().toString().trim());
                                intent.putExtra("phone",edtPhone.getText().toString().trim());
                                intent.putExtra("email",edtEmail.getText().toString().trim());
                                intent.putExtra("state",selectedState);
                                intent.putExtra("city",selectedCity);
                                intent.putExtra("task","signUp");
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SignUpActivity2DetailsInput.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String errorMessage = "Error: " + error.toString();
                Toast.makeText(SignUpActivity2DetailsInput.this, error.toString(), Toast.LENGTH_SHORT).show();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(SignUpActivity2DetailsInput.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e("BlogFetchError", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        MySingleton.getInstance(SignUpActivity2DetailsInput.this).addToRequestQueue(jsonObjectRequest);
    }
}