package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> places;
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        places = new ArrayList<String>();
        places.add("Add a new Place...");

        LatLng temp = new LatLng(0,0);
        locations.add(temp);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placeIndex",position);
                Toast.makeText(MainActivity.this,Integer.toString(position),Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

    }
}
