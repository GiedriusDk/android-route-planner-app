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

public class FountainsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "fountains";

    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};
    public static class SharedVariables {
        public static boolean[] fountainSelected = new boolean[4];
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fountains);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(FountainsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ListActivity
                Intent intent = new Intent(FountainsActivity.this, ListActivity.class);
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
            "Ko gero, kiekvienas praeinantis pro Tilto gatvėje įsikūrusį Lietuvos istorijos institutą atkreipė dėmesį į instituto kiemelyje esantį Vilniui neįprastą ir išskirtinį fontaną. Nors iš pažiūros trapus ažūrinis fontanas oficialaus pavadinimo neturi, miestiečiai jį meiliai vadina Žiedu arba Žydėjimu.",
            "Per naujausią parko rekonstrukciją 2013 m. mėginta iš dalies atkurti XIX a. dailininko Aleksandero Władysławo Strausso sukurtą aplinką. Pagrindinis takas dabar veda prie centrinio fontano. Dabartinis grojantis ir šokantis fontanas, kitaip Rožyno aikštės fontanas, linksmina susirinkusiuosius 14 melodijų, kurios suderintos su fontano vandens kaskadų darbu.",
            "Visų nuostabai, 1974-ųjų cirkuliacinis vandens siurblys, pagamintas pagal to meto technologijas, veikia iki šiol. Operos fontanas, kurio autorius architektas Aleksandras Statys Lukšas, išsiskyrė savo modernumu, jo vandens čiurkšlės, kaip tuomet buvo įprasta kituose projektuose, nebuvo aukštos.",
            "Lankytina Vilniaus vieta – akcentuoja harmoniją ir džiaugsmą – tai tarsi antipodas čia stovėjusiam stabui, kuris kėlė baimę ir nepasitikėjimą. Srovių formos ir aukščiai keičiasi pagal sudarytą veikimo scenarijų, vandens srovės forma gali kisti iki 10 kartų per sekundę."

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

        for (int i = 0; i < FountainsActivity.SharedVariables.fountainSelected.length; i++) {
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


                    FountainsActivity.SharedVariables.fountainSelected[finalI] = isChecked;
                }
            });
        }
    }




    public static void clearFountainsCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < FountainsActivity.SharedVariables.fountainSelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < FountainsActivity.SharedVariables.fountainSelected.length) {
                FountainsActivity.SharedVariables.fountainSelected[i] = false;

            }
        }
    }

    public static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }


}