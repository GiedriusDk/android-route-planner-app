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

public class MonumentsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "monuments";
    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};

    public static class SharedVariables {
        public static boolean[] monumentSelected = new boolean[4];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monuments);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(MonumentsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MonumentsActivity.this, ListActivity.class);
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
            "Siekiant atminti žydų gydytoją Cemachą Šabadą, kuris dažnai pavadinamas daktaru Aiskauda, 2007-ais metais buvo pastatytas jo paminklas. Galima drąsiai teigti, kad jis – legendinė asmenybė, kuri nusipelnė ne tik vilniečių, tačiau ir visos Lietuvos gyventojų pagarbos.",
            "Minint poeto 250-ąsias gimimo metines, Vilniaus universiteto kriptoje buvo pastatytas skulptoriaus Konstantino Bogdano sukurtas Kristijono Donelaičio paminklas, pagamintas iš stiklo plastiko, nors Donelaitis niekada nebuvo Vilniuje.",
            "Paminklą vieninteliam Lietuvos karaliui Mindaugui galima pamatyti priešais Lietuvos nacionalinį muziejų. Ten jis pastatytas 2003 m. minint Mindaugo karūnavimo 750 metų jubiliejų. Jis buvo atidengtas 2003-ųjų metų liepos 6 dieną, kuomet ir minima Mindaugo karūnavimo diena.",
            "1984 metais pastatytas ir atidengtas paminklas – skulptoriaus Gedimino Jokūbonio ir architekto Vytauto Čeknausko bendras darbas. 1987 metais rugpjūčio 23 dieną apie 3 000 žmonių susirinko prie Adomo Mickevičiaus paminklo tyliai pasmerkti Ribentropo-Molotovo paktą. Nuo šio mitingo prasidėjo Lietuvos atgimimas."

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

        for (int i = 0; i < MonumentsActivity.SharedVariables.monumentSelected.length; i++) {
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


                    MonumentsActivity.SharedVariables.monumentSelected[finalI] = isChecked;
                }
            });
        }
    }




    public static void clearMonumentCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < MonumentsActivity.SharedVariables.monumentSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < MonumentsActivity.SharedVariables.monumentSelected.length) {
                MonumentsActivity.SharedVariables.monumentSelected[i] = false;

            }
        }
    }

    private static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }
}
