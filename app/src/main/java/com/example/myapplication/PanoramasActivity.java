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

public class PanoramasActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "panoramas";
    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};

    public static class SharedVariables {
        public static boolean[] panoramaSelected = new boolean[4];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panoramas);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(PanoramasActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PanoramasActivity.this, ListActivity.class);
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
            "Vienos aukščiausių Vilniaus kalvų pavadinimas kildinamas iš italų kalbos žodžio altano, reiškiančio terasą ant stogo ar pavėsinę. Tokia pavėsinė išliko iki šiol – ją 1933 m. pastatė vilnietė Melanija Dluska su vyru, čia auginę egzotinius augalus. Nuo kalno atsiveria įspūdingas vaizdas.",
            "12 metrų aukščio paminklas tapo tautinės tapatybės simboliu, tačiau šiandien jis dažnai apšviečiamas skirtingu apšvietimu, norint paminėti ypatingus įvykius Lietuvai ir pasauliui. Dabartinį memorialą sukūrė architektas ir skulptorius A. Vivulskis 1916 m., 1950 m. sovietų valdžia paminklą nugriovė, o 1988 m. jis buvo atstatytas.",
            "Subačiaus gatvė minima jau XV amžiaus dokumentuose. Iš gatvių sankirtoje esančios apžvalgos aikštelės atsiveria gražiausia Užupio panorama. Nuostabiai atrodo ant Vilnios šlaito stovinti XVIII amžiaus nedidelė grakšti šventojo Baltramiejaus bažnytėlė.",
            "48 metrų aukščio kalnas yra viena aukščiausių vietų Vilniaus senamiestyje, tad iš čia puiku grožėtis 360 ° panoramomis. 1989 m. Gedimino kalnas tapo pradžios tašku Baltijos keliui. 2003 m. Vilniuje, ant Gedimino kalno, pradėjo veikti vienintelis funikulierius. 2019 m. kalno šlaite buvo rasti 1863–1864 m. sukilimo vadų ir dalyvių palaikai."
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

        for (int i = 0; i < PanoramasActivity.SharedVariables.panoramaSelected.length; i++) {
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


                    PanoramasActivity.SharedVariables.panoramaSelected[finalI] = isChecked;
                }
            });
        }
    }




    public static void clearPanoramaCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < PanoramasActivity.SharedVariables.panoramaSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < PanoramasActivity.SharedVariables.panoramaSelected.length) {
                PanoramasActivity.SharedVariables.panoramaSelected[i] = false;

            }
        }
    }

    private static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }
}
