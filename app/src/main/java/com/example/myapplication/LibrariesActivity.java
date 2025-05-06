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

public class LibrariesActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "libraries";
    private static final String[] PREF_CHECKBOXES = {"checkbox1", "checkbox2", "checkbox3", "checkbox4"};
    public static class SharedVariables {
        public static boolean[] librarySelected = new boolean[4];
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libraries);

        restoreCheckboxStates();

        Button btnMap = findViewById(R.id.btn_map);
        Button btnList = findViewById(R.id.btn_list);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(LibrariesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ListActivity
                Intent intent = new Intent(LibrariesActivity.this, ListActivity.class);
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
            "1950 m. įkurta biblioteka yra labai mėgstama vilniečių ir miesto svečių. Bibliotekoje sukaupta beveik 500 tūkst. įvairių mokslo sričių knygų, periodinių leidinių, muzikos, filmų, žemėlapių, natų bei grožinės literatūros leidinių lietuvių, anglų, rusų, vokiečių, prancūzų ir kitomis pasaulio kalbomis. Kiekvienais metais bibliotekoje apsilanko apie 200 tūkst. lankytojų.",
            "Didžiausia Lietuvos biomedicinos ir jai artimų mokslų biblioteka Lietuvoje. Įkurta 1941 m. Lietuvos medicinos biblioteka kaupia ir teikia medicinos informaciją vartotojams, dalyvauja įgyvendinant valstybės sveikatos programas, saugo rašytinį Lietuvos medicinos mokslo paveldą. Saugomi senosios medicinos literatūros paminklai – 5000 egz.",
            "Nacionalinė kultūros įstaiga, veikianti informacijos sklaidos, kultūros, mokslo ir švietimo srityse, vykdanti bibliotekų veiklą ir pagal kompetenciją užtikrinanti valstybės informacijos politikos įgyvendinimą. 2019 m. Nacionalinė biblioteka minėjo veiklos šimtmetį. Veiklos tikslas - kaupti, saugoti ir aktualinti nacionalinį dokumentinį kultūros paveldą.",
            "Biblioteką įkūrė žymus Vilniaus advokatas, visuomenininkas, bibliofilas Tadas Stanislovas Vrublevskis. Teisę naudotis Biblioteka turi visi Lietuvos ir užsienio šalių piliečiai. Bibliotekos pagrindinis tikslas – tenkinti mokslo visuomenės informacinius poreikius, išsaugoti ir ateities kartoms perduoti sukauptus ir toliau kaupiamus dokumentinio paveldo rinkinius bei vykdyti mokslinę ir edukacinę misiją."
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

        for (int i = 0; i < LibrariesActivity.SharedVariables.librarySelected.length; i++) {
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


                    LibrariesActivity.SharedVariables.librarySelected[finalI] = isChecked;
                }
            });
        }
    }




    public static void clearLibraryCheckboxes(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        for (int i = 0; i < LibrariesActivity.SharedVariables.librarySelected.length; i++) {
            CheckBox checkBox = ((Activity)context).findViewById(context.getResources().getIdentifier("checkbox" + (i + 1), "id", context.getPackageName()));
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
            if (i >= 0 && i < LibrariesActivity.SharedVariables.librarySelected.length) {
                LibrariesActivity.SharedVariables.librarySelected[i] = false;

            }
        }
    }

    public static void removeMarkersFromMap(GoogleMap googleMap) {
        googleMap.clear();
    }
}