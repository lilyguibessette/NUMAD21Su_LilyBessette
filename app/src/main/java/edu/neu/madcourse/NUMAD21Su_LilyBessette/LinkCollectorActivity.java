package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LinkCollectorActivity extends AppCompatActivity {
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkcollector);
        back = findViewById(R.id.backbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LinkCollectorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }



}

