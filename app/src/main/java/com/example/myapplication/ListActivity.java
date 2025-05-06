// ListActivity.java
package com.example.myapplication;

import static com.example.myapplication.MainActivity.clearCheckboxes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private Button btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Button uncheck = findViewById(R.id.uncheck);
        uncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StonesActivity.clearStonesCheckboxes(ListActivity.this);
                ChurchesActivity.clearChurchCheckboxes(ListActivity.this);
                LibrariesActivity.clearLibraryCheckboxes(ListActivity.this);
                FountainsActivity.clearFountainsCheckboxes(ListActivity.this);
                MuseumsActivity.clearMuseumsCheckboxes(ListActivity.this);
                MonumentsActivity.clearMonumentCheckboxes(ListActivity.this);
                PanoramasActivity.clearPanoramaCheckboxes(ListActivity.this);
                SculpturesActivity.clearSculptureCheckboxes(ListActivity.this);
                TheatersActivity.clearTheaterCheckboxes(ListActivity.this);
                BridgesActivity.clearBridgeCheckboxes(ListActivity.this);
                clearCheckboxes();
            }
        });

        btnMap = findViewById(R.id.btn_map);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the MainActivity when the "Map" button is clicked
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });




        List<ObjectItem> objectList = new ArrayList<>();
        objectList.add(new ObjectItem("Akmenys"));
        objectList.add(new ObjectItem("Bažnyčios"));
        objectList.add(new ObjectItem("Bibliotekos"));
        objectList.add(new ObjectItem("Fontanai"));
        objectList.add(new ObjectItem("Muziejai"));
        objectList.add(new ObjectItem("Paminklai asmenybėms"));
        objectList.add(new ObjectItem("Panoramų vietos"));
        objectList.add(new ObjectItem("Skulptūros"));
        objectList.add(new ObjectItem("Teatrai"));
        objectList.add(new ObjectItem("Tiltai"));



        ArrayAdapter<ObjectItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, objectList);


        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (objectList.get(position).getName().equals("Akmenys")) {

                    Intent intent = new Intent(ListActivity.this, StonesActivity.class);
                    startActivity(intent);
                }

                if (objectList.get(position).getName().equals("Bažnyčios")) {
                    // Open the ChurchesActivity
                    Intent intent = new Intent(ListActivity.this, ChurchesActivity.class);
                    startActivity(intent);
                }
                if (objectList.get(position).getName().equals("Bibliotekos")) {

                    Intent intent = new Intent(ListActivity.this, LibrariesActivity.class);
                    startActivity(intent);
                }
                if (objectList.get(position).getName().equals("Fontanai")) {

                    Intent intent = new Intent(ListActivity.this, FountainsActivity.class);
                    startActivity(intent);
                }
                if (objectList.get(position).getName().equals("Muziejai")) {

                    Intent intent = new Intent(ListActivity.this, MuseumsActivity.class);
                    startActivity(intent);
                }
                if (objectList.get(position).getName().equals("Paminklai asmenybėms")) {

                    Intent intent = new Intent(ListActivity.this, MonumentsActivity.class);
                    startActivity(intent);
                }
                if (objectList.get(position).getName().equals("Panoramų vietos")) {

                    Intent intent = new Intent(ListActivity.this, PanoramasActivity.class);
                    startActivity(intent);
                }
                if (objectList.get(position).getName().equals("Skulptūros")) {

                    Intent intent = new Intent(ListActivity.this, SculpturesActivity.class);
                    startActivity(intent);
                }
                if (objectList.get(position).getName().equals("Teatrai")) {

                    Intent intent = new Intent(ListActivity.this, TheatersActivity.class);
                    startActivity(intent);
                }
                if (objectList.get(position).getName().equals("Tiltai")) {

                    Intent intent = new Intent(ListActivity.this, BridgesActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


}
