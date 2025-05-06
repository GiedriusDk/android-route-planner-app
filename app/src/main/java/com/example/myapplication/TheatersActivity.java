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

public class TheatersActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "theaters";
    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};

    public static class SharedVariables {
        public static boolean[] theaterSelected = new boolean[4];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theaters);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(TheatersActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TheatersActivity.this, ListActivity.class);
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
            "Dabartinė teatro buveinė pastatyta 1981 m. Lietuvos nacionalinis dramos teatras bendradarbiauja su kitų miestų teatrais, vaidina ne tik sostinės, bet ir kitų Lietuvos miestų žiūrovams, dalyvauja teatro festivaliuose Lietuvoje ir užsienyje. Pastaruoju metu spektakliai rodyti buvo Avinjone, Lione, Paryžiuje.",
            "Viena patraukliausių ne tik teatro, bet apskritai miesto kultūros erdvių. Išskirtinė ir pastato architektūra: žiūrovų salė turi unikalias stiklo blokelių lubas; tokios visoje Europoje išlikusios tik dvejos – Vilniuje ir Prahoje. Teatre yra 226 vietų žiūrovų salė.",
            "Dramos teatras įsteigtas 1965 m. Jaunimo teatras gastroliavo daugelyje Europos šalių, Jungtinėse Amerikos Valstijose, Izraelyje, Australijoje, dalyvavo teatrų festivaliuose. 1976 m. pastatytas pirmasis lietuviškas miuziklas „Ugnies medžioklė su varovais“.",
            "Didžiausias (biudžetu, kolektyvu, plotu, scena) Lietuvos teatras. Specializuojasi muzikiniame teatre, t. y. baleto ir operos (ne dramos) spektakliuose. Šis pastatas buvo pastatytas 1974 m. Teatre yra 1 000 vietų didžioji salė ir 250 vietų kamerinė salė."
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

        for (int i = 0; i < TheatersActivity.SharedVariables.theaterSelected.length; i++) {
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


                    TheatersActivity.SharedVariables.theaterSelected[finalI] = isChecked;
                }
            });
        }
    }




    public static void clearTheaterCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < TheatersActivity.SharedVariables.theaterSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < TheatersActivity.SharedVariables.theaterSelected.length) {
                TheatersActivity.SharedVariables.theaterSelected[i] = false;

            }
        }
    }

    private static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }
}
