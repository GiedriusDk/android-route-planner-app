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

public class BridgesActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "bridges";

    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};
    public static class SharedVariables {
        public static boolean[] bridgeSelected = new boolean[4];
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridges);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(BridgesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ListActivity
                Intent intent = new Intent(BridgesActivity.this, ListActivity.class);
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
            "240 m. ilgio tiltas, jungiantis Naujamiesčio ir Šnipiškių rajonus, buvo pastatytas 1995 m. Ant tilto sumontuota beveik 20 m aukščio nerūdijančio plieno skulptūra „Spindulys–Ietis“, simbolizuojanti „išsiveržimą, tautos atgimimą, narsą siekiant Nepriklausomybės ir nuolatinę kovą dėl jos“.",
            "Pastatytas per dvejus metus 1905–1907 m. tiltas yra vienintelis (neskaitant uždaryto Bukčių pėsčiųjų tilto) su atramomis vandenyje. 1991 m. ant tilto stovėjo Seimo gynėjų barikados. 2006 m. tiltas rekonstruotas. 2010 m. po tiltu pakabinta skulptūra „Laivas-pusmėnulis“.",
            "Užupio tiltas pirmą kartą minimas LDK 1605 m. privilegijoje. Nuo tada šioje vietoje stovėjo mediniai tiltai – vieni sudegė, kitus nunešė potvyniai, treti buvo perstatyti. Dabartinis tiltas pastatytas 1901 m. Jo ilgis yra tik 19 m.",
            "Tiltas jungia Žirmūnų seniūniją su Vilniaus senamiesčiu. Tiltas pastatytas 2003 m., karaliaus Mindaugo karūnavimo 750 metų jubiliejaus proga. Tilto ilgis – 101 m. Tarp Žaliojo ir Karaliaus Mindaugo tiltų, Neries krantinėje, 2004 m. buvo įgyvendintas originalus menininko Gitenio Umbraso projektas „Meilės krantai“."
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

        for (int i = 0; i < BridgesActivity.SharedVariables.bridgeSelected.length; i++) {
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


                    BridgesActivity.SharedVariables.bridgeSelected[finalI] = isChecked;
                }
            });
        }
    }




    public static void clearBridgeCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < BridgesActivity.SharedVariables.bridgeSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < BridgesActivity.SharedVariables.bridgeSelected.length) {
                BridgesActivity.SharedVariables.bridgeSelected[i] = false;

            }
        }
    }

    public static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }



}