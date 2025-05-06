package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class ChurchesActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "churches";

    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};
    public static class SharedVariables {
        public static boolean[] churchSelected = new boolean[4];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_churches);


        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(ChurchesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ListActivity
                Intent intent = new Intent(ChurchesActivity.this, ListActivity.class);
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
            "Statyta 1625–1743 m. vėlyvojo baroko bažnyčia, pasižymi gausiu dekoru, plastiškais fasadais. Sovietmečiu bažnyčia priklausė Vilniaus dailės muziejui. Joje buvo įrengtas sandėlis, vienuolyno patalpose – įstaigos ir butai. Pagrindinė nava pritaikyta koncertams, todėl šiuo metu bažnyčioje vyksta įvairūs kultūriniai renginiai, vyksta klasikinės muzikos festivaliai.",
            "Vienintelis Lietuvoje vienabokštis vėlyvojo baroko kulto pastatas ir paskutinis Vilniaus baroko paminklas. Bažnyčią statė vienuoliai augustinai 1746–1768 m. Bažnyčia buvo žinoma dėl stebuklingojo Marijos Paguodos paveikslo, kabėjusio pagrindiniame altoriuje. Bažnyčios bokšto aukštis siekia net 41,5 metrų aukštį.",
            "Pilnas šios bažnyčios pavadinimas Vilniaus šv. Jono Krikštytojo ir šv. Jono apaštalo ir evangelisto bažnyčia. Ji stovi Vilniaus universiteto teritorijoje ir turi gotikos, baroko ir klasicistinių bruožų. Įrengta ji buvo 1426 m. Iki XIX a. bažnyčioje, be pamaldų, vykdavo įvairūs susirinkimai, disputai, vaidinimai, būdavo iškilmingai sutinkami LDK kunigaikščiai. Bažnyčios varpinė yra aukščiausias Vilniaus senamiesčio statinys (69 m).",
            "Pasakojama, kad traukdamasis iš Rusijos, Napoleonas aplankė Vilnių ir susižavėjęs šia bažnyčia pasakė, kad, jei galėtų, tai pasidėtų šv. Onos bažnyčią ant delno ir pasiimtų su savimi į Paryžių. Šv. Onos bažnyčia – žymiausias gotikos paminklas Lietuvoje, statyta 1495–1501 m. ir paskutinį kartą restauruota 1970 m. 1992 m. bažnyčia įtraukta į LR Kultūros vertybių registrą."
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

        for (int i = 0; i < SharedVariables.churchSelected.length; i++) {
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

                    SharedVariables.churchSelected[finalI] = isChecked;
                }
            });
        }

    }



    public static void clearChurchCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < ChurchesActivity.SharedVariables.churchSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < SharedVariables.churchSelected.length) {
                SharedVariables.churchSelected[i] = false;

            }
        }
    }
    public static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }

}









