package com.example.memorableplaces;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    ArrayList<String> latitudes = new ArrayList<>();
    ArrayList<String> longitudes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);


        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();
        try{
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs",ObjectSerializer.serialize(new ArrayList<String>())));

        }catch (IOException e){
            e.printStackTrace();
        }
        if(places.size()>0 && latitudes.size()>0&& longitudes.size()>0){
            if(places.size()==latitudes.size() && places.size()==longitudes.size()){
                for(int i=0;i<places.size();i++){
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                }
            }
        }else{
            places.add("Add a new Place...");
            locations.add(new LatLng(0,0));
        }


        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeIndex", position);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setMessage("Do you want it to remove from your favourite list?")
                        .setTitle("Are You Sure!?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                places.remove(position);
                                locations.remove(position);
                                latitudes.remove(position);
                                longitudes.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("No",null).show();

                return false;
            }
        });

    }
}
