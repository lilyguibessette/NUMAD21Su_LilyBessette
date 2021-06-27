package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class AtYourServiceActivity extends AppCompatActivity {
    Button back;
    Button search_openfda;
    FloatingActionButton searchDrugButton;
    private static String[] queryResult;
    private String drugName;
    private String dosageForm;
    private boolean searchGeneric;
    Spinner spinnerDrugForm;
    Spinner spinnerBrandGeneric;
    String searchBrandGeneric;
    String product_ndc;
    String product_type;
    String routes;
    String manufacturer_name;
    String pharm_class_epc;
    String active_ingredients;
    TextView product_ndc_tv;
    TextView product_type_tv;
    TextView routes_tv;
    TextView manufacturer_name_tv;
    TextView pharm_class_epc_tv;
    TextView active_ingredients_tv;
    ProgressBar progressCircle;
    private final Handler textHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atyourservice);
        product_ndc_tv = findViewById(R.id.product_ndc);
        product_type_tv = findViewById(R.id.product_type);
        routes_tv = findViewById(R.id.routes);
        manufacturer_name_tv = findViewById(R.id.manufacturer_name);
        pharm_class_epc_tv = findViewById(R.id.pharm_class_epc);
        active_ingredients_tv = findViewById(R.id.active_ingredients);
        back = findViewById(R.id.backbutton);
        search_openfda = findViewById(R.id.search_openfda_button);
        spinnerDrugForm = findViewById(R.id.spinnerdrugform);
        spinnerBrandGeneric = findViewById(R.id.spinnerbrandgeneric);
        drugName = ((EditText) findViewById(R.id.drug_name)).getText().toString();
        progressCircle =(ProgressBar)findViewById(R.id.progressBar); // initiate the progress bar

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AtYourServiceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        search_openfda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drugName = ((EditText) findViewById(R.id.drug_name)).getText().toString();
                progressCircle.setVisibility(View.VISIBLE);
                runCallThread(view);
            }
        });

        spinnerDrugForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] drugForms = getResources().getStringArray(R.array.drugformspinner);
                dosageForm = drugForms[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerBrandGeneric.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] brandGeneric = getResources().getStringArray(R.array.brandgenericspinner);
                searchBrandGeneric = brandGeneric[position];
                searchGeneric = !searchBrandGeneric.equals("BRAND NAME");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchGeneric = true;
            }
        });
    }



    public static boolean isValidAPICall() {
        String error = queryResult[0];
        return error != null && !error.equals("error");
    }

    public void runCallThread(View view){
        runnableThread callThread = new runnableThread();
        new Thread(callThread).start();
    }

    class runnableThread implements Runnable{
        @Override
        public void run() {
            if (drugName != null) {
                queryResult = ndcLookUp();
                boolean valid = isValidAPICall();
                if(valid) {
                    Log.e("openFDA", "valid api call");
                    textHandler.post(() -> {
                        product_ndc_tv.setText(product_ndc);
                        product_type_tv.setText(product_type);
                        routes_tv.setText(routes);
                        manufacturer_name_tv.setText(manufacturer_name);
                        pharm_class_epc_tv.setText(pharm_class_epc);
                        active_ingredients_tv.setText(active_ingredients);
                    });
                } else{
                    Log.e("openFDA", "invalid api call");
                    textHandler.post(() -> {
                        product_ndc_tv.setText("Invalid Search");
                        product_type_tv.setText("Invalid Search");
                        routes_tv.setText("Invalid Search");
                        manufacturer_name_tv.setText("Invalid Search");
                        pharm_class_epc_tv.setText("Invalid Search");
                        active_ingredients_tv.setText("Invalid Search");
                        Toast.makeText(AtYourServiceActivity.this, R.string.search_error, Toast.LENGTH_SHORT).show();
                 });
                }}
                else {
                textHandler.post(() -> {
                            product_ndc_tv.setText("Invalid Search");
                            product_type_tv.setText("Invalid Search");
                            routes_tv.setText("Invalid Search");
                            manufacturer_name_tv.setText("Invalid Search");
                            pharm_class_epc_tv.setText("Invalid Search");
                            active_ingredients_tv.setText("Invalid Search");
                            Toast.makeText(AtYourServiceActivity.this, R.string.search_error, Toast.LENGTH_SHORT).show();
                });
                }
            progressCircle.setVisibility(View.INVISIBLE);
        }


        // networking code - https://api.fda.gov/drug/ndc.json?search=generic_name:"empagliflozin"+AND+dosage_form:"TABLET"&limit=1

        protected String[] ndcLookUp(){
            String[] results = new String[6];
            URL url;
            String ndcURL;
            try{
                if (searchGeneric) {
                    Log.e("searchGeneric ", "true");
                    ndcURL = "https://api.fda.gov/drug/ndc.json?search=generic_name:\"" + drugName + "\"+AND+dosage_form:\"" + dosageForm + "\"&limit=1";
                } else{
                    Log.e("searchGeneric ", "false");
                    ndcURL = "https://api.fda.gov/drug/ndc.json?search=brand_name:\"" + drugName + "\"+AND+dosage_form:\"" + dosageForm + "\"&limit=1";
                }
                Log.e("ndcURL",ndcURL);
                url = new URL(ndcURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                final String response = convertStreamToString(inputStream);
                JSONObject jObject = new JSONObject(response);
                JSONObject drugResults = jObject.getJSONArray("results").getJSONObject(0);
                product_ndc = drugResults.getString("product_ndc");
                product_type = drugResults.getString("product_type").replace("[","").replace("]","").replace("\"","");
                routes = drugResults.getString("route").replace("[","").replace("]","").replace("\"","");
                JSONObject openfda = drugResults.getJSONObject("openfda");
                manufacturer_name = openfda.getString("manufacturer_name").replace("[","").replace("]","").replace("\"","");
                pharm_class_epc = openfda.getJSONArray("pharm_class_epc").toString().replace("[","").replace("]","").replace("\"","");
                JSONArray active_ingredients_array = drugResults.getJSONArray("active_ingredients");
                ArrayList<String> active_ingredients_list = new ArrayList<>();
                for (int i = 0; i < active_ingredients_array.length(); i++) {
                    active_ingredients_list.add(active_ingredients_array.getJSONObject(i).getString("name").replace("\"",""));
                }
                active_ingredients = active_ingredients_list.toString().replace("[","").replace("]","").replace("\"","");
                if (active_ingredients_array.length() > 3){
                    active_ingredients_list = new ArrayList<>();
                    active_ingredients_list.add(active_ingredients_array.getJSONObject(0).getString("name").replace("\"",""));
                    active_ingredients_list.add(active_ingredients_array.getJSONObject(1).getString("name").replace("\"",""));
                    active_ingredients_list.add(active_ingredients_array.getJSONObject(2).getString("name").replace("\"",""));
                    active_ingredients = active_ingredients_list.toString().replace("[","").replace("]","").replace("\"","");
                    active_ingredients = active_ingredients + " - (And more)";
                }

                results[0] = product_ndc;
                results[1] = product_type;
                results[2] = routes;
                results[3] = manufacturer_name;
                results[4] = pharm_class_epc;
                results[5] = active_ingredients;
                Log.e("results NDC: ", "product_ndc"+ product_ndc);
                return results;

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            results[0] = "error";
            return results;
        }

        private String convertStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next().replace(",", ",\n") : "";
        }
    }
}

