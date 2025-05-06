package com.example.myapplication;

import static android.content.ContentValues.TAG;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.databinding.BottomsheetlayoutBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TransitDetails;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button btnList;
    private Button btnTSP;
    private ArrayList<LatLng> selectedCoordinates = new ArrayList<>();
    private static GoogleMap googleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private GeoApiContext mGeoApiContext = null;
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1001;
    private static final long REFRESH_INTERVAL = 5000;

    private Handler mHandler = new Handler();
    private long mRefreshTime = REFRESH_INTERVAL;
    private LatLng currentUserLocation;
    private boolean isFirstLocationFetch = true;


    private int checkedButtonId;
    LatLng[] churchCoordinates;
    LatLng[] stoneCoordinates;
    LatLng[] libraryCoordinates;
    LatLng[] fountainCoordinates;
    LatLng[] museumCoordinates;
    LatLng[] monumentCoordinates;
    LatLng[] panoramaCoordinates;
    LatLng[] sculptureCoordinates;
    LatLng[] therterCoordinates;
    LatLng[] bridgeCoordinates;
    private LinearLayout sidePanel;
    private ImageButton btnOpenPanel;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (isFirstLocationFetch) {

            mHandler.postDelayed(locationRunnable, 500);
            isFirstLocationFetch = false;
        } else {
            mHandler.postDelayed(locationRunnable, mRefreshTime);
        }


        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.outdoor_toggle_buttonGroup);
        Button walkingButton = findViewById(R.id.btn_walking);
        Button cyclingButton = findViewById(R.id.btn_bus);
        toggleGroup.check(R.id.btn_walking);
        checkedButtonId = R.id.btn_walking;
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    checkedButtonId = checkedId;
                    if (checkedId == R.id.btn_walking) {
                        walkingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        cyclingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    } else if (checkedId == R.id.btn_bus) {
                        walkingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        cyclingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    }
                }
            }
        });

        sidePanel = findViewById(R.id.side_panel);
        btnOpenPanel = findViewById(R.id.btn_open_panel);
        btnOpenPanel.setVisibility(View.GONE);
        btnOpenPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSidePanel();
            }
        });

        btnList = findViewById(R.id.btn_list);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the ListActivity when the button is clicked
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        btnTSP = findViewById(R.id.btn_algorithm);
        btnTSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RECEIVED", "SELECTED COORDINATES " + selectedCoordinates);
                getLastKnownLocation();
                reorderCoordinates(selectedCoordinates, currentUserLocation);
            }
        });

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.my_map_api_key))
                    .build();
        }













    }

    private Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                getLastKnownLocation();
            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            }


            mHandler.postDelayed(this, mRefreshTime);
        }
    };

    private void toggleSidePanel() {
        if (sidePanel.getVisibility() == View.VISIBLE) {
            sidePanel.setVisibility(View.GONE);
        } else {
            sidePanel.setVisibility(View.VISIBLE);
        }
    }



    public static void clearCheckboxes() {

        googleMap.clear();
    }






    private void getLastKnownLocation() {
        Log.d("Location", "getLastKnownLocation: called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        currentUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d("XOXO", String.valueOf(currentUserLocation));

                    } else {

                        Log.e("Location", "getLastLocation: Failed to get location");
                    }
                }
            });
        } else {

            Log.e("Location", "getLastKnownLocation: Location permission not granted");
        }
    }
    private void addCustomMarker(LatLng position, String text, String title) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .icon(createCustomMarker(text));

        googleMap.addMarker(markerOptions);
    }


    private BitmapDescriptor createCustomMarker(String text) {
        View markerLayout = LayoutInflater.from(this).inflate(R.layout.custom_marker_layout, null);
        TextView markerText = markerLayout.findViewById(R.id.markerText);
        markerText.setText(text);

        markerLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }





    private void showDialog(String objectType, String objectName) {
        BottomsheetlayoutBinding binding = BottomsheetlayoutBinding.inflate(LayoutInflater.from(this));
        View dialogView = binding.getRoot();

        binding.choosetxt.setText(objectName);
        String description = getDescriptionFor(objectName);
        binding.editTextView.setText(description);

        binding.choosetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (objectType.equals("stone")) {
                    Intent intent = new Intent(MainActivity.this, StonesActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("church")) {
                    Intent intent = new Intent(MainActivity.this, ChurchesActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("library")) {
                    Intent intent = new Intent(MainActivity.this, LibrariesActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("fountain")) {
                    Intent intent = new Intent(MainActivity.this, FountainsActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("museum")) {
                    Intent intent = new Intent(MainActivity.this, MuseumsActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("monument")) {
                    Intent intent = new Intent(MainActivity.this, MonumentsActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("panorama")) {
                    Intent intent = new Intent(MainActivity.this, PanoramasActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("sculpture")) {
                    Intent intent = new Intent(MainActivity.this, SculpturesActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("theater")) {
                    Intent intent = new Intent(MainActivity.this, TheatersActivity.class);
                    startActivity(intent);
                } else if (objectType.equals("bridge")) {
                    Intent intent = new Intent(MainActivity.this, BridgesActivity.class);
                    startActivity(intent);
                }
            }
        });


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        dialog.show();


        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private String getDescriptionFor(String objectName) {
        String description;
        Map<String, String> descriptions = new HashMap<>();

        descriptions.put("Akmuo Vilniaus 650-mečiui paminėti", "1973 m., minint 650-ąsias Vilniaus paminėjimo metines, Katedros (tada Gedimino) aikštėje buvo atidengtas paminklinis akmuo su užrašu „Vilnius 1323–1973“. Šalia akmens 60 metų augusį beržą 2020 m. lapkritį pakeitė raudonasis ąžuolas.");
        descriptions.put("Tautos namų akmuo", "1907 m. žymūs šalies veikėjai (Mikalojus Konstantinas Čiurlionis, Jonas Basanavičius ir kt.) ketino ant kalno pastatyti Tautos namus – visos Lietuvos kultūros centrą. Po 100 metų, 2007 m., ant kalno buvo atidengtas paminklinis akmuo su užrašu „100 metų Tautos namų idėjai“.");
        descriptions.put("Vilniaus Jono Basanavičiaus progimnazijos akmuo", "2001 m. lapkričio 23 d., švenčiant Jono Basanavičiaus gimimo 150-ąsias metines, prie mokyklos atidengtas paminklinis akmuo, skirtas gydytojui, mokslininkui, Vasario 16-osios Akto signatarui Jonui Basanavičiui. Pilkas akmuo savo forma kiek primena Lietuvos valstybės kontūrus.");
        descriptions.put("Žvėryno akmuo su Gediminaičių stulpais", "Šis akmuo – didžiojo kunigaikščio valdų riboženklis. Akmuo datuojamas XV a. II p. – XVI a. pr. Taigi, Žvėryno akmenyje iškalti vieni ankstyviausių Gediminaičių stulpų. Žinoma, kad XIX a. vid. už keleto kilometrų dešiniajame Neries aukštupio krante gulėjo dar vienas akmuo su tokiu ženklu.");

        descriptions.put("Šv. Kotrynos bažnyčia", "Statyta 1625–1743 m. vėlyvojo baroko bažnyčia, pasižymi gausiu dekoru, plastiškais fasadais. Sovietmečiu bažnyčia priklausė Vilniaus dailės muziejui. Joje buvo įrengtas sandėlis, vienuolyno patalpose – įstaigos ir butai. Pagrindinė nava pritaikyta koncertams, todėl šiuo metu bažnyčioje vyksta įvairūs kultūriniai renginiai, vyksta klasikinės muzikos festivaliai.");
        descriptions.put("Švč. Mergelės Marijos Ramintojos bažnyčia", "Vienintelis Lietuvoje vienabokštis vėlyvojo baroko kulto pastatas ir paskutinis Vilniaus baroko paminklas. Bažnyčią statė vienuoliai augustinai 1746–1768 m. Bažnyčia buvo žinoma dėl stebuklingojo Marijos Paguodos paveikslo, kabėjusio pagrindiniame altoriuje. Bažnyčios bokšto aukštis siekia net 41,5 metrų aukštį.");
        descriptions.put("Šv. Jonų bažnyčia", "Pilnas šios bažnyčios pavadinimas Vilniaus šv. Jono Krikštytojo ir šv. Jono apaštalo ir evangelisto bažnyčia. Ji stovi Vilniaus universiteto teritorijoje ir turi gotikos, baroko ir klasicistinių bruožų. Įrengta ji buvo 1426 m. Iki XIX a. bažnyčioje, be pamaldų, vykdavo įvairūs susirinkimai, disputai, vaidinimai, būdavo iškilmingai sutinkami LDK kunigaikščiai. Bažnyčios varpinė yra aukščiausias Vilniaus senamiesčio statinys (69 m).");
        descriptions.put("Vilniaus Šv. Onos bažnyčia", "Pasakojama, kad traukdamasis iš Rusijos, Napoleonas aplankė Vilnių ir susižavėjęs šia bažnyčia pasakė, kad, jei galėtų, tai pasidėtų šv. Onos bažnyčią ant delno ir pasiimtų su savimi į Paryžių. Šv. Onos bažnyčia – žymiausias gotikos paminklas Lietuvoje, statyta 1495–1501 m. ir paskutinį kartą restauruota 1970 m. 1992 m. bažnyčia įtraukta į LR Kultūros vertybių registrą.");

        descriptions.put("Adomo Mickevičiaus biblioteka", "1950 m. įkurta biblioteka yra labai mėgstama vilniečių ir miesto svečių. Bibliotekoje sukaupta beveik 500 tūkst. įvairių mokslo sričių knygų, periodinių leidinių, muzikos, filmų, žemėlapių, natų bei grožinės literatūros leidinių lietuvių, anglų, rusų, vokiečių, prancūzų ir kitomis pasaulio kalbomis. Kiekvienais metais bibliotekoje apsilanko apie 200 tūkst. lankytojų.");
        descriptions.put("Medicinos biblioteka", "Didžiausia Lietuvos biomedicinos ir jai artimų mokslų biblioteka Lietuvoje. Įkurta 1941 m. Lietuvos medicinos biblioteka kaupia ir teikia medicinos informaciją vartotojams, dalyvauja įgyvendinant valstybės sveikatos programas, saugo rašytinį Lietuvos medicinos mokslo paveldą. Saugomi senosios medicinos literatūros paminklai – 5000 egz. ");
        descriptions.put("Lietuvos nacionalinė Martyno Mažvydo biblioteka", "Nacionalinė kultūros įstaiga, veikianti informacijos sklaidos, kultūros, mokslo ir švietimo srityse, vykdanti bibliotekų veiklą ir pagal kompetenciją užtikrinanti valstybės informacijos politikos įgyvendinimą. 2019 m. Nacionalinė biblioteka minėjo veiklos šimtmetį. Veiklos tikslas - kaupti, saugoti ir aktualinti nacionalinį dokumentinį kultūros paveldą.");
        descriptions.put("Lietuvos mokslų akademijos Vrublevskių biblioteka", "Biblioteką įkūrė žymus Vilniaus advokatas, visuomenininkas, bibliofilas Tadas Stanislovas Vrublevskis. Teisę naudotis Biblioteka turi visi Lietuvos ir užsienio šalių piliečiai. Bibliotekos pagrindinis tikslas – tenkinti mokslo visuomenės informacinius poreikius, išsaugoti ir ateities kartoms perduoti sukauptus ir toliau kaupiamus dokumentinio paveldo rinkinius bei vykdyti mokslinę ir edukacinę misiją.");

        descriptions.put("Bernardinų sodo fontanas", "Per naujausią parko rekonstrukciją 2013 m. mėginta iš dalies atkurti XIX a. dailininko Aleksandero Władysławo Strausso sukurtą aplinką. Pagrindinis takas dabar veda prie centrinio fontano. Dabartinis grojantis ir šokantis fontanas, kitaip Rožyno aikštės fontanas, linksmina susirinkusiuosius 14 melodijų, kurios suderintos su fontano vandens kaskadų darbu.");
        descriptions.put("Fontanas prie Lietuvos istorijos instituto", "Ko gero, kiekvienas praeinantis pro Tilto gatvėje įsikūrusį Lietuvos istorijos institutą atkreipė dėmesį į instituto kiemelyje esantį Vilniui neįprastą ir išskirtinį fontaną. Nors iš pažiūros trapus ažūrinis fontanas oficialaus pavadinimo neturi, miestiečiai jį meiliai vadina Žiedu arba Žydėjimu.");
        descriptions.put("Lukiškių fontanas", "Lankytina Vilniaus vieta – akcentuoja harmoniją ir džiaugsmą – tai tarsi antipodas čia stovėjusiam stabui, kuris kėlė baimę ir nepasitikėjimą. Srovių formos ir aukščiai keičiasi pagal sudarytą veikimo scenarijų, vandens srovės forma gali kisti iki 10 kartų per sekundę.");
        descriptions.put("Operos ir baleto teatro fontanas", "Visų nuostabai, 1974-ųjų cirkuliacinis vandens siurblys, pagamintas pagal to meto technologijas, veikia iki šiol. Operos fontanas, kurio autorius architektas Aleksandras Statys Lukšas, išsiskyrė savo modernumu, jo vandens čiurkšlės, kaip tuomet buvo įprasta kituose projektuose, nebuvo aukštos.");

        descriptions.put("MO muziejus", "MO muziejus, įkurtas asmenine mokslininkų Danguolės ir Viktoro Butkų iniciatyva, beveik dešimtmetį veikė kaip muziejus be sienų. Sukauptą apie 6 000 modernaus ir šiuolaikinio meno kūrinių kolekciją sudaro Lietuvos dailės aukso fondas nuo 1960-ųjų iki šių dienų. MO muziejaus misija – menas arčiau žmonių, daugiau žmonių prie meno.");
        descriptions.put("Nacionaline dailės galerija", "Daugiafunkcė meno ir kultūros erdvė Vilniuje, pristatanti Lietuvos bei užsienio XX–XXI a. meną. Savo veiklą galerija pradėjo 1993 metais. Tai aktyvios komunikacijos erdvė, kurioje žiūrovai susipažįsta su nuolatine ekspozicija, parodomis, dalyvauja kultūros renginiuose, paskaitose ir edukacinėse programose.");
        descriptions.put("Radvilų rūmų dailės muziejus", "Istorinio ir šiuolaikinio meno bei edukacijos erdvė, įsikūrusi Vilniaus senamiestyje, Jonušo Radvilos rūmuose. Rūmuose veikia Lietuvos nacionalinio dailės muziejaus filialas. šiuo metu lankytojams atviruose muziejaus korpusuose veikia ilgalaikės ekspozicijos. Muziejus pritaikytas žmonėms su judėjimo negalia ir tėvams su vežimėliais.");
        descriptions.put("Valdovų rūmai", "Buvusi Vilniaus žemutinės pilies komplekso dalis. Pastatas priklauso Vilniaus pilių kultūriniam rezervatui, jame įsikūręs Nacionalinis Lietuvos Didžiosios Kunigaikštystės valdovų rūmų muziejus. Šiame muziejuje galima apžiūrėti keturis pagrindinius maršrutus, pristatančius šios istorinės rezidencijos funkcijas.");

        descriptions.put("Adomo Mickevičiaus paminklas", "1984 metais pastatytas ir atidengtas paminklas – skulptoriaus Gedimino Jokūbonio ir architekto Vytauto Čeknausko bendras darbas. 1987 metais rugpjūčio 23 dieną apie 3 000 žmonių susirinko prie Adomo Mickevičiaus paminklo tyliai pasmerkti Ribentropo-Molotovo paktą. Nuo šio mitingo prasidėjo Lietuvos atgimimas.");
        descriptions.put("Karaliaus Mindaugo paminklas", "Paminklą vieninteliam Lietuvos karaliui Mindaugui galima pamatyti priešais Lietuvos nacionalinį muziejų. Ten jis pastatytas 2003 m. minint Mindaugo karūnavimo 750 metų jubiliejų. Jis buvo atidengtas 2003-ųjų metų liepos 6 dieną, kuomet  ir minima Mindaugo karūnavimo diena.");
        descriptions.put("Kristijono Donelaičio paminklas", "Minint poeto 250-ąsias gimimo metines, Vilniaus universiteto kriptoje buvo pastatytas skulptoriaus Konstantino Bogdano sukurtas Kristijono Donelaičio paminklas, pagamintas iš stiklo plastiko, nors Donelaitis niekada nebuvo Vilniuje.");
        descriptions.put("Paminklas Cemachui Šabadui", "Siekiant atminti žydų gydytoją Cemachą Šabadą, kuris dažnai pavadinamas daktaru Aiskauda, 2007-ais metais buvo pastatytas  jo paminklas. Galima drąsiai teigti, kad jis – legendinė asmenybė, kuri nusipelnė ne tik vilniečių, tačiau ir visos Lietuvos gyventojų pagarbos.");

        descriptions.put("Altanos kalnas", "Vienos aukščiausių Vilniaus kalvų pavadinimas kildinamas iš italų kalbos žodžio altano, reiškiančio terasą ant stogo ar pavėsinę. Tokia pavėsinė išliko iki šiol – ją 1933 m. pastatė vilnietė Melanija Dluska su vyru, čia auginę egzotinius augalus. Nuo kalno atsiveria įspūdingas vaizdas.");
        descriptions.put("Gedimino kalnas", "48 metrų aukščio kalnas yra viena aukščiausių vietų Vilniaus senamiestyje, tad iš čia puiku grožėtis 360 ° panoramomis. 1989 m. Gedimino kalnas tapo pradžios tašku Baltijos keliui. 2003 m. Vilniuje, ant Gedimino kalno, pradėjo veikti vienintelis funikulierius. 2019 m. kalno šlaite buvo rasti 1863–1864 m. sukilimo vadų ir dalyvių palaikai.");
        descriptions.put("Subačiaus gatvės apžvalgos aikštelė", "Subačiaus gatvė minima jau XV amžiaus dokumentuose. Iš gatvių sankirtoje esančios apžvalgos aikštelės atsiveria gražiausia Užupio panorama. Nuostabiai atrodo ant Vilnios šlaito stovinti XVIII amžiaus nedidelė grakšti šventojo Baltramiejaus bažnytėlė.");
        descriptions.put("Trijų Kryžių kalnas", "12 metrų aukščio paminklas tapo tautinės tapatybės simboliu, tačiau šiandien jis dažnai apšviečiamas skirtingu apšvietimu, norint paminėti ypatingus įvykius Lietuvai ir pasauliui. Dabartinį memorialą sukūrė architektas ir skulptorius A. Vivulskis 1916 m., 1950 m. sovietų valdžia paminklą nugriovė, o 1988 m. jis buvo atstatytas.");

        descriptions.put("Lietuvių skalikai", "Lietuvių skalikai buvo medžiotojų pagalbininkai jau XVI amžiuje, taigi, ne vieną amžių džiugino žmones savo draugija ir pagalba. Gedimino pilies kalno papėdėje stovinti skulptūra skirta pagerbti vienintelę Lietuvoje išveista šunų veislę – lietuvių skaliką.");
        descriptions.put("Skulptūra „Margutis“", "Pylimo gatvėje pastatytu Margučiu siekta, kad idėja „užsikrėstu“ ir kiti miesto mikrorajonai. Jis turėjo paskatinti kiekvieną miesto mikrorajoną pasistatyti po savo kiaušinį, kuris ne tik visus suvienytų, bet ir primintų apie laukiantį atgimimą.");
        descriptions.put("Užupio angelo skulptūra", "Užupio angelas buvo sukurtas Užupio rajono globėjui Zenonui Šteiniui atminti. Ant 8,5 m pjedestalo stovintis Užupio angelas, pučia savo trimitą ir skelbia pasauliui žinią apie Užupio rajono atgimimą bei kūrybos laisvę.");
        descriptions.put("Užupio undinėlė", "2002 m. skulptoriaus Romo Vilčiausko sukurta bronzinė skulptūra „Užupio undinėlė“, dar kitaip vadinama Užupio mergele, sėdi Vilnelės upės krantinėje įrengtoje nišoje, priešais Užupio kavinės terasą. Ši undinė laikoma vienu iš Užupio simbolių.");

        descriptions.put("Lietuvos nacionalinis dramos teatras", "Dabartinė teatro buveinė pastatyta 1981 m. Lietuvos nacionalinis dramos teatras bendradarbiauja su kitų miestų teatrais, vaidina ne tik sostinės, bet ir kitų Lietuvos miestų žiūrovams, dalyvauja teatro festivaliuose Lietuvoje ir užsienyje. Pastaruoju metu spektakliai rodyti buvo Avinjone, Lione, Paryžiuje.");
        descriptions.put("Lietuvos nacionalinis operos ir baleto teatras", "Didžiausias (biudžetu, kolektyvu, plotu, scena) Lietuvos teatras. Specializuojasi muzikiniame teatre, t. y. baleto ir operos (ne dramos) spektakliuose. Šis pastatas buvo pastatytas 1974 m. Teatre yra 1 000 vietų didžioji salė ir 250 vietų kamerinė salė.");
        descriptions.put("Valstybinis jaunimo teatras", "Dramos teatras įsteigtas 1965 m. Jaunimo teatras gastroliavo daugelyje Europos šalių, Jungtinėse Amerikos Valstijose, Izraelyje, Australijoje, dalyvavo teatrų festivaliuose. 1976 m. pastatytas pirmasis lietuviškas miuziklas „Ugnies medžioklė su varovais“.");
        descriptions.put("Valstybinis Vilniaus mažasis teatras", "Viena patraukliausių ne tik teatro, bet apskritai miesto kultūros erdvių. Išskirtinė ir pastato architektūra: žiūrovų salė turi unikalias stiklo blokelių lubas; tokios visoje Europoje išlikusios tik dvejos – Vilniuje ir Prahoje. Teatre yra 226 vietų žiūrovų salė.");

        descriptions.put("Baltasis tiltas", "240 m. ilgio tiltas, jungiantis Naujamiesčio ir Šnipiškių rajonus, buvo pastatytas 1995 m. Ant tilto sumontuota beveik 20 m aukščio nerūdijančio plieno skulptūra „Spindulys–Ietis“, simbolizuojanti „išsiveržimą, tautos atgimimą, narsą siekiant Nepriklausomybės ir nuolatinę kovą dėl jos“.");
        descriptions.put("Karaliaus Mindaugo tiltas", "Tiltas jungia Žirmūnų seniūniją su Vilniaus senamiesčiu. Tiltas pastatytas 2003 m., karaliaus Mindaugo karūnavimo 750 metų jubiliejaus proga. Tilto ilgis – 101 m. Tarp Žaliojo ir Karaliaus Mindaugo tiltų, Neries krantinėje, 2004 m. buvo įgyvendintas originalus menininko Gitenio Umbraso projektas „Meilės krantai“.");
        descriptions.put("Užupio tiltas", "Užupio tiltas pirmą kartą minimas LDK 1605 m. privilegijoje. Nuo tada šioje vietoje stovėjo mediniai tiltai – vieni sudegė, kitus nunešė potvyniai, treti buvo perstatyti. Dabartinis tiltas pastatytas 1901 m. Jo ilgis yra tik 19 m.");
        descriptions.put("Žvėryno tiltas", "Pastatytas per dvejus metus 1905–1907 m. tiltas yra vienintelis (neskaitant uždaryto Bukčių pėsčiųjų tilto) su atramomis vandenyje. 1991 m. ant tilto stovėjo Seimo gynėjų barikados. 2006 m. tiltas rekonstruotas. 2010 m. po tiltu pakabinta skulptūra „Laivas-pusmėnulis“.");


        description = descriptions.get(objectName);
        return description != null ? description : "No description available";
    }


    private String determineObjectType(LatLng position) {

        if (Arrays.asList(stoneCoordinates).contains(position)) {
            return "stone";
        } else if (Arrays.asList(churchCoordinates).contains(position)) {
            return "church";
        }else if (Arrays.asList(libraryCoordinates).contains(position)) {
            return "library";
        }else if (Arrays.asList(fountainCoordinates).contains(position)) {
            return "fountain";
        }else if (Arrays.asList(museumCoordinates).contains(position)) {
            return "museum";
        }else if (Arrays.asList(monumentCoordinates).contains(position)) {
            return "monument";
        }else if (Arrays.asList(panoramaCoordinates).contains(position)) {
            return "panorama";
        }else if (Arrays.asList(sculptureCoordinates).contains(position)) {
            return "sculpture";
        }else if (Arrays.asList(therterCoordinates).contains(position)) {
            return "theater";
        }else if (Arrays.asList(bridgeCoordinates).contains(position)) {
            return "bridge";
        }

        return "";
    }

    private HashMap<LatLng, String> markerTitles = new HashMap<>();
    String objectName;
    String objectType;
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                objectName = marker.getTitle();
                objectType = determineObjectType(marker.getPosition());

                showDialog(objectType, objectName);
                return true;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                sidePanel.setVisibility(View.GONE);
            }
        });
        LatLng vilniusLocation = new LatLng(54.687157, 25.279652);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vilniusLocation, 13));


        churchCoordinates = new LatLng[]{
                new LatLng(54.681834106214765, 25.28076634335416),
                new LatLng(54.67937447886862, 25.290306128925376),
                new LatLng(54.68234020258136, 25.288472391137446),
                new LatLng(54.683210904079765, 25.29291585090478),
        };
        stoneCoordinates = new LatLng[]{
                new LatLng(54.68504930132466, 25.2884419532564),
                new LatLng(54.68558946101458, 25.269208252369705),
                new LatLng(54.67970116481425, 25.256511413532564),
                new LatLng(54.68673422133072, 25.257605359470254),
        };
        libraryCoordinates = new LatLng[]{
                new LatLng(54.68008565594965, 25.27975788967012),
                new LatLng(54.68922218376033, 25.274522152465643),
                new LatLng(54.690513625323156, 25.26383653327599),
                new LatLng(54.68831408429547, 25.2877261735245),
        };
        fountainCoordinates = new LatLng[]{
                new LatLng(54.68825878407844, 25.283680316854234),
                new LatLng(54.68379798669392, 25.297015583969568),
                new LatLng(54.68979112811694, 25.27964388312454),
                new LatLng(54.68912542326186, 25.26998395625619),
        };
        museumCoordinates = new LatLng[]{
                new LatLng(54.67956977728381, 25.278036328762234),
                new LatLng(54.683681259196746, 25.279338494714125),
                new LatLng(54.69637707089872, 25.270966502037936),
                new LatLng(54.68568279099727, 25.289128820785297),
        };
        monumentCoordinates = new LatLng[]{
                new LatLng(54.6773540729649, 25.284323946780656),
                new LatLng(54.682882866509935, 25.286528536635483),
                new LatLng(54.68757484998406, 25.288449749461893),
                new LatLng(54.682829415076995, 25.293294708405984),
        };
        panoramaCoordinates = new LatLng[]{
                new LatLng(54.68384223561891, 25.30058269327004),
                new LatLng(54.68679170669435, 25.297573354317226),
                new LatLng(54.677512606903946, 25.30055473938081),
                new LatLng(54.68697467692463, 25.291013491018827),
        };
        sculptureCoordinates = new LatLng[]{
                new LatLng(54.68613074329288, 25.29368889711371),
                new LatLng(54.68056468005465, 25.292429364139412),
                new LatLng(54.68065057710977, 25.295398494426),
                new LatLng(54.67547746244294, 25.282851289370864),
        };
        therterCoordinates = new LatLng[]{
                new LatLng(54.686318320820625, 25.284038986419436),
                new LatLng(54.68747926866282, 25.277470498287716),
                new LatLng(54.67664264082896, 25.286483459580747),
                new LatLng(54.68883543922313, 25.277812500484035),
        };
        bridgeCoordinates = new LatLng[]{
                new LatLng(54.69277303769166, 25.27252104739659),
                new LatLng(54.69049037601486, 25.258855800232702),
                new LatLng(54.68050797037856, 25.292465447041533),
                new LatLng(54.689036573118365, 25.2882370313524),
        };
        String[] stoneTitles = {
                "Akmuo Vilniaus 650-mečiui paminėti",
                "Tautos namų akmuo",
                "Vilniaus Jono Basanavičiaus progimnazijos akmuo",
                "Žvėryno akmuo su Gediminaičių stulpais"
        };
        String[] churcheTitles = {
                "Šv. Kotrynos bažnyčia",
                "Švč. Mergelės Marijos Ramintojos bažnyčia",
                "Šv. Jonų bažnyčia",
                "Vilniaus Šv. Onos bažnyčia"
        };
        String[] libraryTitles = {
                "Adomo Mickevičiaus biblioteka",
                "Medicinos biblioteka",
                "Lietuvos nacionalinė Martyno Mažvydo biblioteka",
                "Lietuvos mokslų akademijos Vrublevskių biblioteka"
        };
        String[] fountainTitles = {
                "Fontanas prie Lietuvos istorijos instituto",
                "Bernardinų sodo fontanas",
                "Operos ir baleto teatro fontanas",
                "Lukiškių fontanas"
        };
        String[] museumTitles = {
                "MO muziejus",
                "Radvilų rūmų dailės muziejus",
                "Nacionaline dailės galerija",
                "Valdovų rūmai"
        };
        String[] monumentTitles = {
                "Paminklas Cemachui Šabadui",
                "Kristijono Donelaičio paminklas",
                "Karaliaus Mindaugo paminklas",
                "Adomo Mickevičiaus paminklas"
        };
        String[] panoramaTitles = {
                "Altanos kalnas",
                "Trijų Kryžių kalnas",
                "Subačiaus gatvės apžvalgos aikštelė",
                "Gedimino kalnas"
        };
        String[] sculptureTitles = {
                "Lietuvių skalikai",
                "Užupio undinėlė",
                "Užupio angelo skulptūra",
                "Skulptūra „Margutis“"
        };
        String[] theaterTitles = {
                "Lietuvos nacionalinis dramos teatras",
                "Valstybinis Vilniaus mažasis teatras",
                "Valstybinis jaunimo teatras",
                "Lietuvos nacionalinis operos ir baleto teatras"
        };
        String[] bridgeTitles = {
                "Baltasis tiltas",
                "Žvėryno tiltas",
                "Užupio tiltas",
                "Karaliaus Mindaugo tiltas"
        };


        for (int i = 0; i < stoneCoordinates.length; i++) {
            if (StonesActivity.SharedVariables.stoneSelected[i]) {
                LatLng position = stoneCoordinates[i];
                String title = stoneTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }

        for (int i = 0; i < churchCoordinates.length; i++) {
            if (ChurchesActivity.SharedVariables.churchSelected[i]) {
                LatLng position = churchCoordinates[i];
                String title = churcheTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }
        for (int i = 0; i < libraryCoordinates.length; i++) {
            if (LibrariesActivity.SharedVariables.librarySelected[i]) {
                LatLng position = libraryCoordinates[i];
                String title = libraryTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }
        for (int i = 0; i < fountainCoordinates.length; i++) {
            if (FountainsActivity.SharedVariables.fountainSelected[i]) {
                LatLng position = fountainCoordinates[i];
                String title = fountainTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }
        for (int i = 0; i < museumCoordinates.length; i++) {
            if (MuseumsActivity.SharedVariables.museumSelected[i]) {
                LatLng position = museumCoordinates[i];
                String title = museumTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }

        for (int i = 0; i < monumentCoordinates.length; i++) {
            if (MonumentsActivity.SharedVariables.monumentSelected[i]) {
                LatLng position = monumentCoordinates[i];
                String title = monumentTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }
        for (int i = 0; i < panoramaCoordinates.length; i++) {
            if (PanoramasActivity.SharedVariables.panoramaSelected[i]) {
                LatLng position = panoramaCoordinates[i];
                String title = panoramaTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }

        for (int i = 0; i < sculptureCoordinates.length; i++) {
            if (SculpturesActivity.SharedVariables.sculptureSelected[i]) {
                LatLng position = sculptureCoordinates[i];
                String title = sculptureTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }

        for (int i = 0; i < therterCoordinates.length; i++) {
            if (TheatersActivity.SharedVariables.theaterSelected[i]) {
                LatLng position = therterCoordinates[i];
                String title = theaterTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }

        for (int i = 0; i < bridgeCoordinates.length; i++) {
            if (BridgesActivity.SharedVariables.bridgeSelected[i]) {
                LatLng position = bridgeCoordinates[i];
                String title = bridgeTitles[i];
                googleMap.addMarker(new MarkerOptions().position(position).title(title));
                selectedCoordinates.add(position);
                markerTitles.put(position, title);
            }
        }



    }

    private LatLng lastCoordinate;

    private void reorderCoordinates(List<LatLng> coordinates, LatLng currentUserLocation) {

        if (coordinates.size() < 1) {
            return;
        }

        List<LatLng> reorderedCoordinates = new ArrayList<>();
        LatLng currentCoordinate = currentUserLocation;


        reorderedCoordinates.add(currentCoordinate);

        while (!coordinates.isEmpty()) {
            LatLng closestCoordinate = null;
            double closestDistance = Double.MAX_VALUE;

            for (LatLng coordinate : coordinates) {
                double distance = calculateDistance(currentCoordinate, coordinate);
                if (distance < closestDistance) {
                    closestCoordinate = coordinate;
                    closestDistance = distance;
                }
            }

            currentCoordinate = closestCoordinate;
            coordinates.remove(currentCoordinate);
            reorderedCoordinates.add(currentCoordinate);
        }

        coordinates.clear();
        coordinates.addAll(reorderedCoordinates);
        calculateDirections(reorderedCoordinates);
        if (!reorderedCoordinates.isEmpty()) {

            lastCoordinate = reorderedCoordinates.get(reorderedCoordinates.size() - 1);
            Log.d("LastRouteCoordinate", "Last coordinate: " + lastCoordinate.latitude + ", " + lastCoordinate.longitude);
            startDistanceCheckingTask(currentUserLocation, lastCoordinate);
        } else {
            Log.d("LastRouteCoordinate", "No coordinates in reordered route.");
        }
    }

    public double calculateDistance(LatLng coordinate1, LatLng coordinate2) {
        try {
            DirectionsApiRequest request = DirectionsApi.newRequest(mGeoApiContext)
                    .origin(new com.google.maps.model.LatLng(coordinate1.latitude, coordinate1.longitude))
                    .destination(new com.google.maps.model.LatLng(coordinate2.latitude, coordinate2.longitude));
            if (checkedButtonId == R.id.btn_walking) {
                request.mode(TravelMode.WALKING);
            } else {
                request.mode(TravelMode.TRANSIT);

            }

            DirectionsResult result = request.await();

            if (result.routes != null && result.routes.length > 0 &&
                    result.routes[0].legs != null && result.routes[0].legs.length > 0) {

                return result.routes[0].legs[0].distance.inMeters / 1000.0;
            } else {
                System.out.println("No route found.");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    private double totalDistance;
    private long totalDuration;
    private int totalRoutes;
    private int routesProcessed;

    private void calculateDirections(List<LatLng> selectedCoordinates) {
        Log.d("TAGdirections", "calculateDirections: calculating directions.");

        if (selectedCoordinates.size() < 1) {
            Log.e(TAG, "calculateDirections: Insufficient coordinates for directions.");
            return;
        }
        googleMap.clear();
        for (int i = 1; i < selectedCoordinates.size(); i++) {
            LatLng position = selectedCoordinates.get(i);
            String text = String.valueOf(i);
            String title = markerTitles.get(position);
            addCustomMarker(position, text, title);
        }
        totalRoutes = selectedCoordinates.size() - 1;
        routesProcessed = 0;
        totalDistance = 0;
        totalDuration = 0;


        for (int i = 0; i < selectedCoordinates.size() - 1; i++) {
            LatLng origin = selectedCoordinates.get(i);
            LatLng destination = selectedCoordinates.get(i + 1);

            com.google.maps.model.LatLng originLatLng = new com.google.maps.model.LatLng(
                    origin.latitude,
                    origin.longitude
            );
            com.google.maps.model.LatLng destinationLatLng = new com.google.maps.model.LatLng(
                    destination.latitude,
                    destination.longitude
            );

            DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

            directions.alternatives(false);
            if (checkedButtonId == R.id.btn_walking) {
                directions.mode(TravelMode.WALKING);
            } else {
                directions.mode(TravelMode.TRANSIT);

            }
            directions.origin(originLatLng);
            directions.destination(destinationLatLng);


            directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    addPolylinesToMap(result);

                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
                }
            });
        }
    }
    String routeLengthStr;
    String routeDurationStr;
    private void addPolylinesToMap(final DirectionsResult result) {
        Log.d("TAGpolylines", "calculateDirections: calculating directions.");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("TAGrun", "run: result routes: " + result.routes.length);

                for (DirectionsRoute route : result.routes) {
                    Log.d("TAGsufor", "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    PolylineOptions polylineOptions = new PolylineOptions().addAll(newDecodedPath);
                    polylineOptions.color(Color.RED);
                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                    polyline.setClickable(true);

                    totalDistance += route.legs[0].distance.inMeters / 1000.0;
                    totalDuration += route.legs[0].duration.inSeconds;
                }
                routesProcessed++;
                long hours = totalDuration / 3600;
                long minutes = (totalDuration % 3600) / 60;

                routeLengthStr = String.format("%.2f km", totalDistance);

                routeDurationStr = String.format("%d hours %d minutes", hours, minutes);
                if (checkedButtonId != R.id.btn_walking) {
                    displayTransitInfo(result);
                }
                if (routesProcessed == totalRoutes) {
                    showRouteDialog(routeLengthStr, routeDurationStr);
                }
            }
        });
    }


    private void showRouteDialog(String length, String duration) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Maršruto informacija");
        builder.setMessage("Maršruto ilgis: " + length + "\nMaršruto trukmė: " + duration);
        builder.setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StonesActivity.clearStonesCheckboxes(MainActivity.this);
                ChurchesActivity.clearChurchCheckboxes(MainActivity.this);
                LibrariesActivity.clearLibraryCheckboxes(MainActivity.this);
                FountainsActivity.clearFountainsCheckboxes(MainActivity.this);
                MuseumsActivity.clearMuseumsCheckboxes(MainActivity.this);
                MonumentsActivity.clearMonumentCheckboxes(MainActivity.this);
                PanoramasActivity.clearPanoramaCheckboxes(MainActivity.this);
                SculpturesActivity.clearSculptureCheckboxes(MainActivity.this);
                TheatersActivity.clearTheaterCheckboxes(MainActivity.this);
                BridgesActivity.clearBridgeCheckboxes(MainActivity.this);
                clearCheckboxes();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    LatLng departureStopLocation;
    LatLng arrivalStopLocation;
    List<LatLng> deparuteStopLocations = new ArrayList<>();
    List<LatLng> arrivalStopLocations = new ArrayList<>();

    private void displayTransitInfo(DirectionsResult result) {
        if (result != null && result.routes != null && result.routes.length > 0) {

            DirectionsRoute route = result.routes[0];
            for (DirectionsLeg leg : route.legs) {
                for (DirectionsStep step : leg.steps) {
                    if (step.transitDetails != null) {
                        TransitDetails transitDetails = step.transitDetails;
                        String busLine = transitDetails.line.shortName;
                        String departureStop = transitDetails.departureStop.name;
                        departureStopLocation = new LatLng(transitDetails.departureStop.location.lat, transitDetails.departureStop.location.lng);
                        String arrivalStop = transitDetails.arrivalStop.name;
                        arrivalStopLocation = new LatLng(transitDetails.arrivalStop.location.lat, transitDetails.arrivalStop.location.lng);
                        deparuteStopLocations.add(arrivalStopLocation);
                        arrivalStopLocations.add(arrivalStopLocation);
                        btnOpenPanel.setVisibility(View.VISIBLE);

                        String transitInfo =  busLine + ": " + departureStop + " ->\n" + arrivalStop;
                        addTransitInfoToPanel(transitInfo);
                        addPolylinesToMapBus(departureStopLocation, arrivalStopLocation, result);

                        addDepartureStopMarkers(new LatLng(transitDetails.departureStop.location.lat, transitDetails.departureStop.location.lng), departureStop, busLine);
                        addArrivalStopMarkers(new LatLng(transitDetails.arrivalStop.location.lat, transitDetails.arrivalStop.location.lng), arrivalStop, busLine);
                    }
                }
            }
        }
    }



    private void addDepartureStopMarkers(LatLng location, String busStopName, String busLine) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Įlipimas: " + busStopName + "Auotobusas: " + busLine);
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setVisible(true);
    }



    private void addArrivalStopMarkers(LatLng location, String busStopName, String busLine) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Išlipimas: " + busStopName + " Auotobusas: " + busLine);
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setVisible(true);
    }
    private void addPolylinesToMapBus(LatLng departureStopLocation, LatLng arrivalStopLocation, DirectionsResult result) {
        if (result != null && result.routes != null && result.routes.length > 0) {
            DirectionsRoute route = result.routes[0];
            for (DirectionsLeg leg : route.legs) {
                for (DirectionsStep step : leg.steps) {
                    if (step.transitDetails != null) {
                        TransitDetails transitDetails = step.transitDetails;
                        LatLng departureLatLng = new LatLng(transitDetails.departureStop.location.lat, transitDetails.departureStop.location.lng);
                        LatLng arrivalLatLng = new LatLng(transitDetails.arrivalStop.location.lat, transitDetails.arrivalStop.location.lng);

                        if (departureLatLng.equals(departureStopLocation) && arrivalLatLng.equals(arrivalStopLocation)) {

                            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(step.polyline.getEncodedPath());
                            List<LatLng> polylinePoints = new ArrayList<>();

                            for (com.google.maps.model.LatLng latLng : decodedPath) {
                                polylinePoints.add(new LatLng(latLng.lat, latLng.lng));
                            }

                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .addAll(polylinePoints)
                                    .color(Color.GREEN)
                                    .width(8)
                                    .zIndex(3);
                            googleMap.addPolyline(polylineOptions);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void addTransitInfoToPanel(String transitInfo) {

        LinearLayout sidePanelLayout = findViewById(R.id.side_panel);

        TextView headerTextView = findViewById(R.id.header_text);

        if (headerTextView.getVisibility() != View.VISIBLE) {
            headerTextView.setVisibility(View.VISIBLE);
        }

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8);
        textView.setLayoutParams(params);
        textView.setText(transitInfo);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        sidePanelLayout.addView(textView);
    }
    private boolean isDialogShown = false;

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        stopDistanceCheckingTask();
    }

    private Handler handler = new Handler();
    private Runnable distanceCheckRunnable;

    private void startDistanceCheckingTask(final LatLng initialUserLocation, LatLng lastCoordinate) {
        distanceCheckRunnable = new Runnable() {
            @Override
            public void run() {
                getLastKnownLocation();

                checkDistanceAndShowDialog(currentUserLocation, lastCoordinate);
                handler.postDelayed(this, 10000);
            }
        };

        handler.post(distanceCheckRunnable);
    }

    private void stopDistanceCheckingTask() {
        if (handler != null && distanceCheckRunnable != null) {
            handler.removeCallbacks(distanceCheckRunnable);
        }
    }

    private void checkDistanceAndShowDialog(LatLng currentUserLocation, LatLng lastCoordinate) {
        Log.d("currentlocation", String.valueOf(currentUserLocation));

        if (lastCoordinate == null) {
            return;
        }

        double distance = calculateDistance(currentUserLocation, lastCoordinate) * 1000;
        if (distance >= 0 && distance < 10) {
            if (!isDialogShown) {
                showCompletionDialog();
            }
        }
    }

    private void showCompletionDialog() {
        isDialogShown = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sveikinu!!!");
        builder.setMessage("Jūs įveikėte maršrutą!");
        builder.setPositiveButton("YAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                stopDistanceCheckingTask();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }






}
