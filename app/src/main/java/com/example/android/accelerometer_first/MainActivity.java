package com.example.android.accelerometer_first;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.android.accelerometer_first";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.level);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void start_game(View view){
        Spinner spinner = (Spinner) findViewById(R.id.level);
        String nivel = (String) spinner.getSelectedItem();

        Intent intent = new Intent(this, gaming.class);
        intent.putExtra(EXTRA_MESSAGE,nivel);
        startActivity(intent);
    }
}
