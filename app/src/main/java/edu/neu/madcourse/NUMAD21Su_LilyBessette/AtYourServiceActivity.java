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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class AtYourServiceActivity extends AppCompatActivity implements AtYourServiceDialogFragment.AtYourServiceDialogListener {
    Button back;
    Button search_openfda;
    FloatingActionButton searchDrugButton;
    private static String[] queryResult;
    private String drugName;
    private String dosageForm;
    private boolean searchGeneric;
    String searchBrandGeneric;
    Spinner spinnerDrugForm;
    Spinner spinnerBrandGeneric;
    String[] drugForms;
    String[] brandGeneric;
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
    TextView name_tv;
    TextView form_tv;
    TextView genericBrand_tv;
    //private final Handler textHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atyourservice);
        name_tv = findViewById(R.id.drug_name_tv);
        form_tv = findViewById(R.id.drug_form_tv);
        genericBrand_tv = findViewById(R.id.drug_generic_brand_tv);
        product_ndc_tv = findViewById(R.id.product_ndc);
        product_type_tv = findViewById(R.id.product_type);
        routes_tv = findViewById(R.id.routes);
        manufacturer_name_tv = findViewById(R.id.manufacturer_name);
        pharm_class_epc_tv = findViewById(R.id.pharm_class_epc);
        active_ingredients_tv = findViewById(R.id.active_ingredients);
        back = findViewById(R.id.backbutton);
        search_openfda = findViewById(R.id.search_openfda_button);
        searchDrugButton = findViewById(R.id.searchDrugButton);
        init(savedInstanceState);

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
                runCallThread(view);
            }
        });

        searchDrugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for prompt of search criteria
                startServiceCollectorDialog();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("drugName",drugName);
        outState.putString("dosageForm",dosageForm);
        outState.putString("searchBrandGeneric",searchBrandGeneric);
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState != null ) {
            if (savedInstanceState.containsKey("drugName")) {
                drugName = savedInstanceState.getString("drugName");
                name_tv.setText(drugName);
            }
            if (savedInstanceState.containsKey("dosageForm")) {
                dosageForm = savedInstanceState.getString("dosageForm");
                form_tv.setText(dosageForm);

            }
            if (savedInstanceState.containsKey("searchBrandGeneric")) {
                searchBrandGeneric = savedInstanceState.getString("searchBrandGeneric");
                genericBrand_tv.setText(searchBrandGeneric);
            }
        }
    }

    public void startServiceCollectorDialog() {
        DialogFragment serviceDialog = new AtYourServiceDialogFragment();
        serviceDialog.show(getSupportFragmentManager(), "AtYourServiceDialogFragment");
    }

    public void onDialogPositiveClick(DialogFragment ndcInputDialog) {
        Dialog searchInputDialog = ndcInputDialog.getDialog();
        spinnerDrugForm = findViewById(R.id.spinnerdrugform);
        spinnerBrandGeneric = findViewById(R.id.sprinnerbrandgeneric);

        drugForms = getResources().getStringArray(R.array.drugformspinner);
        brandGeneric = getResources().getStringArray(R.array.brandgenericspinner);

        drugName = ((EditText) searchInputDialog.findViewById(R.id.drug_name)).getText().toString();

        spinnerDrugForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dosageForm = drugForms[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerBrandGeneric.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchBrandGeneric = brandGeneric[position];
                searchGeneric = !searchBrandGeneric.equals("BRAND NAME");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if ((drugName != null) && (dosageForm != null)){
            //   setSearchCriteria();
            ndcInputDialog.dismiss();
        } else {
            Toast.makeText(AtYourServiceActivity.this, R.string.drug_input_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment ndcInputDialog) {
        ndcInputDialog.dismiss();
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
            if ((drugName != null) && (dosageForm != null)) {
                // loading sign --
                queryResult = ndcLookUp();
                boolean valid = isValidAPICall();
                if(valid) {
                    Log.e("openFDA: ", "valid api call");
/*                    textHandler.post(() -> {
                        product_ndc_tv.setText(product_ndc);
                        product_type_tv.setText(product_type);
                        routes_tv.setText(routes);
                        manufacturer_name_tv.setText(manufacturer_name);
                        active_ingredients_tv.setText(active_ingredients);
                    });
                }*/
                } else {
                    Toast.makeText(AtYourServiceActivity.this, R.string.search_error, Toast.LENGTH_SHORT).show();
                }
            }

        }


        // networking code - https://api.fda.gov/drug/ndc.json?search=generic_name:"empagliflozin"+AND+dosage_form:"TABLET"&limit=1

        protected String[] ndcLookUp(){
            String[] results = new String[6];
            URL url;
            String ndcURL;
            try{
                if (searchGeneric) {
                    ndcURL = "https://api.fda.gov/drug/ndc.json?search=generic_name:\"" + drugName + "\"+AND+dosage_form:\"" + dosageForm + "\"&limit=1";
                } else{
                    ndcURL = "https://api.fda.gov/drug/ndc.json?search=brand_name:\"" + drugName + "\"+AND+dosage_form:\"" + dosageForm + "\"&limit=1";
                }
                url = new URL(ndcURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                final String response = convertStreamToString(inputStream);
                JSONObject jObject = new JSONObject(response);
                JSONObject drugResults = jObject.getJSONObject("results");
                product_ndc = drugResults.getString("product_ndc");
                product_type = drugResults.getString("product_type");
                routes = drugResults.getString("route");
                JSONObject openfda = drugResults.getJSONObject("openfda");
                manufacturer_name = openfda.getString("manufacturer_name");
                pharm_class_epc = openfda.getString("pharm_class_epc");
                active_ingredients = drugResults.getString("active_ingredients");
                results[0] = product_ndc;
                results[1] = product_type;
                results[2] = routes;
                results[3] = manufacturer_name;
                results[4] = pharm_class_epc;
                results[5] = active_ingredients;
                return results;

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }//Log.e(TAG, "MalformedURLException";
            results[0] = "error";
            return results;
        }

        private String convertStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next().replace(",", ",\n") : "";
        }
    }
}

