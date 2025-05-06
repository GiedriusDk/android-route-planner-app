package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;

public class StonesActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "stones";

    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};
    public static class SharedVariables {
        public static boolean[] stoneSelected = new boolean[4]; // Assuming you have 4 churches
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stones);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(StonesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StonesActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });


        ImageView infoIcon1 = findViewById(R.id.info_icon_1);
        ImageView infoIcon2 = findViewById(R.id.info_icon_2);
        ImageView infoIcon3 = findViewById(R.id.info_icon_3);
        ImageView infoIcon4 = findViewById(R.id.info_icon_4);

        infoIcon1.setOnClickListener(v -> showDescription(0));
        infoIcon2.setOnClickListener(v -> showDescription(1));
        infoIcon3.setOnClickListener(v -> showDescription(2));
        infoIcon4.setOnClickListener(v -> showDescription(3));
    }


    String[] descriptions = {
            "Akmuo Vilniaus 650-mečiui paminėti: 1973 m., minint 650-ąsias Vilniaus paminėjimo metines, Katedros (tada Gedimino) aikštėje buvo atidengtas paminklinis akmuo su užrašu „Vilnius 1323–1973“. Šalia akmens 60 metų augusį beržą 2020 m. lapkritį pakeitė raudonasis ąžuolas.",
            "Tautos namų akmuo: 1907 m. žymūs šalies veikėjai (Mikalojus Konstantinas Čiurlionis, Jonas Basanavičius ir kt.) ketino ant kalno pastatyti Tautos namus – visos Lietuvos kultūros centrą. Po 100 metų, 2007 m., ant kalno buvo atidengtas paminklinis akmuo su užrašu „100 metų Tautos namų idėjai“.",
            "Vilniaus Jono Basanavičiaus progimnazijos akmuo: 2001 m. lapkričio 23 d., švenčiant Jono Basanavičiaus gimimo 150-ąsias metines, prie mokyklos atidengtas paminklinis akmuo, skirtas gydytojui, mokslininkui, Vasario 16-osios Akto signatarui Jonui Basanavičiui. Pilkas akmuo savo forma kiek primena Lietuvos valstybės kontūrus.",
            "Žvėryno akmuo su Gediminaičių stulpais: Šis akmuo – didžiojo kunigaikščio valdų riboženklis. Akmuo datuojamas XV a. II p. – XVI a. pr. Taigi, Žvėryno akmenyje iškalti vieni ankstyviausių Gediminaičių stulpų. Žinoma, kad XIX a. vid. už keleto kilometrų dešiniajame Neries aukštupio krante gulėjo dar vienas akmuo su tokiu ženklu."
    };


    private void showDescription(int index) {
        String description = descriptions[index];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aprašymas")
                .setMessage(description)
                .setPositiveButton("OK", null)
                .show();
    }
    private void restoreCheckboxStates() {

        for (int i = 0; i < StonesActivity.SharedVariables.stoneSelected.length; i++) {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isChecked = preferences.getBoolean(PREF_CHECKBOXES[i], false);
            CheckBox checkBox = findViewById(getResources().getIdentifier("checkbox" + (i + 1), "id", getPackageName()));
            checkBox.setChecked(isChecked);
            final int finalI = i;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(PREF_CHECKBOXES[finalI], isChecked);
                    editor.apply();


                    StonesActivity.SharedVariables.stoneSelected[finalI] = isChecked;
                }
            });
        }
    }

    public static void clearStonesCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < StonesActivity.SharedVariables.stoneSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < StonesActivity.SharedVariables.stoneSelected.length) {
                StonesActivity.SharedVariables.stoneSelected[i] = false;

            }
        }
    }

    public static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }

}