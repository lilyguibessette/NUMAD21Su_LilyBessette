package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewAbout(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View aboutView = getLayoutInflater().inflate(R.layout.contact_info, null);
        Button new_contact_info_backButton = (Button) aboutView.findViewById(R.id.backbutton);
        dialogBuilder.setView(aboutView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        new_contact_info_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.clickybutton:
                Intent intent = new Intent(MainActivity.this, ClickyActivity.class);
                startActivity(intent);
                break;
        }

    }

}