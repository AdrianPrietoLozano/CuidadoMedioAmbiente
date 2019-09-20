package com.example.cuidadodelambiente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            RunDatabaseHelper helper = new RunDatabaseHelper(this);
            helper.close();
            Toast.makeText(getApplicationContext(), "BIEN", Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
