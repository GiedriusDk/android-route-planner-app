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

public class MuseumsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "museums";
    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};

    public static class SharedVariables {
        public static boolean[] museumSelected = new boolean[4];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museums);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(MuseumsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MuseumsActivity.this, ListActivity.class);
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
            "MO muziejus, įkurtas asmenine mokslininkų Danguolės ir Viktoro Butkų iniciatyva, beveik dešimtmetį veikė kaip muziejus be sienų. Sukauptą apie 6 000 modernaus ir šiuolaikinio meno kūrinių kolekciją sudaro Lietuvos dailės aukso fondas nuo 1960-ųjų iki šių dienų. MO muziejaus misija – menas arčiau žmonių, daugiau žmonių prie meno.",
            "Istorinio ir šiuolaikinio meno bei edukacijos erdvė, įsikūrusi Vilniaus senamiestyje, Jonušo Radvilos rūmuose. Rūmuose veikia Lietuvos nacionalinio dailės muziejaus filialas. šiuo metu lankytojams atviruose muziejaus korpusuose veikia ilgalaikės ekspozicijos. Muziejus pritaikytas žmonėms su judėjimo negalia ir tėvams su vežimėliais.",
            "Daugiafunkcė meno ir kultūros erdvė Vilniuje, pristatanti Lietuvos bei užsienio XX–XXI a. meną. Savo veiklą galerija pradėjo 1993 metais. Tai aktyvios komunikacijos erdvė, kurioje žiūrovai susipažįsta su nuolatine ekspozicija, parodomis, dalyvauja kultūros renginiuose, paskaitose ir edukacinėse programose.",
            "Buvusi Vilniaus žemutinės pilies komplekso dalis. Pastatas priklauso Vilniaus pilių kultūriniam rezervatui, jame įsikūręs Nacionalinis Lietuvos Didžiosios Kunigaikštystės valdovų rūmų muziejus. Šiame muziejuje galima apžiūrėti keturis pagrindinius maršrutus, pristatančius šios istorinės rezidencijos funkcijas."
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

        for (int i = 0; i < MuseumsActivity.SharedVariables.museumSelected.length; i++) {
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


                    MuseumsActivity.SharedVariables.museumSelected[finalI] = isChecked;
                }
            });
        }
    }




    public static void clearMuseumsCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < MuseumsActivity.SharedVariables.museumSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < MuseumsActivity.SharedVariables.museumSelected.length) {
                MuseumsActivity.SharedVariables.museumSelected[i] = false;

            }
        }
    }

    private static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }
}
