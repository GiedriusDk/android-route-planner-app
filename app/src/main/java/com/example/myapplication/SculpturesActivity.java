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

public class SculpturesActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "sculptures";
    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};

    public static class SharedVariables {
        public static boolean[] sculptureSelected = new boolean[4];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sculptures);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(SculpturesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SculpturesActivity.this, ListActivity.class);
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
            "Lietuvių skalikai buvo medžiotojų pagalbininkai jau XVI amžiuje, taigi, ne vieną amžių džiugino žmones savo draugija ir pagalba. Gedimino pilies kalno papėdėje stovinti skulptūra skirta pagerbti vienintelę Lietuvoje išveista šunų veislę – lietuvių skaliką.",
            "2002 m. skulptoriaus Romo Vilčiausko sukurta bronzinė skulptūra „Užupio undinėlė“, dar kitaip vadinama Užupio mergele, sėdi Vilnelės upės krantinėje įrengtoje nišoje, priešais Užupio kavinės terasą. Ši undinė laikoma vienu iš Užupio simbolių.",
            "Užupio angelas buvo sukurtas Užupio rajono globėjui Zenonui Šteiniui atminti. Ant 8,5 m pjedestalo stovintis Užupio angelas, pučia savo trimitą ir skelbia pasauliui žinią apie Užupio rajono atgimimą bei kūrybos laisvę.",
            "Pylimo gatvėje pastatytu Margučiu siekta, kad idėja „užsikrėstu“ ir kiti miesto mikrorajonai. Jis turėjo paskatinti kiekvieną miesto mikrorajoną pasistatyti po savo kiaušinį, kuris ne tik visus suvienytų, bet ir primintų apie laukiantį atgimimą."
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

        for (int i = 0; i < SculpturesActivity.SharedVariables.sculptureSelected.length; i++) {
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


                    SculpturesActivity.SharedVariables.sculptureSelected[finalI] = isChecked;
                }
            });
        }
    }




    public static void clearSculptureCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < SculpturesActivity.SharedVariables.sculptureSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < SculpturesActivity.SharedVariables.sculptureSelected.length) {
                SculpturesActivity.SharedVariables.sculptureSelected[i] = false;

            }
        }
    }

    private static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }
}
