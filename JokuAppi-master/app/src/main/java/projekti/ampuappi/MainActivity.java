package projekti.ampuappi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private JSON json;
    private ArrayList<String> arrayListTokaNappiTitle, arrayListTokaNappiTeksti, arrayListKolmasNappiTitle, arrayListKolmasNappiTeksti, arrayListNeljasNappiTitle, arrayListNeljasNappiTeksti;

    private DownloadResources downloadResources;
    private ImagesReturnus imagesReturnus;
    private ImageView imageViewTesti;
    private ArrayList<Bitmap> bitmaps;
    private ArrayList<Uri> uris;
    private Button btnF5;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bitmaps = new ArrayList<>();
        downloadResources = new DownloadResources();
        btnF5 = findViewById(R.id.button_f5);
        imagesReturnus = new ImagesReturnus();

        final Context context = this;
        Thread downLoadImages = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    downloadResources.downloadFiles("diat1", "diat1", ".jpg", context);
                    downloadResources.downloadFiles("diat2", "diat2", ".jpg", context);
                    downloadResources.downloadFiles("resources", "db", ".json", context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btnF5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadImages.start();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart){
            showStartDialog();
        }

        json = new JSON("dialogs");

        json.setKey("dialogs", this);

        arrayListTokaNappiTitle = json.get_json("first", "title");
        arrayListTokaNappiTeksti = json.get_json("first", "body");

        arrayListKolmasNappiTitle = json.get_json("second", "title");
        arrayListKolmasNappiTeksti = json.get_json("second", "body");

        arrayListNeljasNappiTitle = json.get_json("third", "title");
        arrayListNeljasNappiTeksti = json.get_json("third", "body");




        int pituus = arrayListTokaNappiTeksti.size();
        Log.d("pituus", String.valueOf(pituus));
    }

    private void showStartDialog()
    {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        new AlertDialog.Builder(this)
                .setTitle("Vastuuvapauslauseke")
                .setMessage("Tämä on vain ohjeeksi, emme ota vastuuta kaikista ohjeista")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("firstStart", false);
                        editor.apply();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("EN Hyväksy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("firstStart", true);
                        editor.apply();
                        finish();
                        System.exit(0);


                    }
                })
                .create().show();


    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_MenuItem:
                Intent intent_info = new Intent(MainActivity.this,info_Activity.class);
                startActivity(intent_info);

                return super.onOptionsItemSelected(item);

            case R.id.feedback_MenuItem:
                Intent intent_feedback = new Intent(MainActivity.this,feedback_Activity.class);
                startActivity(intent_feedback);

                return super.onOptionsItemSelected(item);

            case R.id.exit_MenuItem:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return super.onOptionsItemSelected(item);

            default:
                    return super.onOptionsItemSelected(item); }
    }

    public void onClickListener_StraightToLabor(View view)
    {
        Intent intent = new Intent(this, Straight_to_labor_activity.class);
        startActivity(intent);
    }

    public void onClickListener_Second_button(View view)
    {
        final AlertDialog.Builder tl = new AlertDialog.Builder(MainActivity.this);

        final View view1;



        LayoutInflater inflater = getLayoutInflater();


        view1 = inflater.inflate(R.layout.alertbox_to_location_layout, null);
        TextView textView = (TextView) view1.findViewById(R.id.id_textViewToLocation);
        textView.setText(arrayListTokaNappiTeksti.toString());

        view = inflater.inflate(R.layout.toolbar_layout, null);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(arrayListTokaNappiTitle.toString());

        tl.setCustomTitle(view);

        tl.setMessage(arrayListTokaNappiTitle.toString());
        Log.d("Apua", arrayListTokaNappiTitle.toString());
        Log.d("Apua", arrayListTokaNappiTeksti.toString());
        //tl.setMessage(arrayListSynnytysTehtavaanTullessaTeksti);
        tl.setView(view1);



        tl.setNegativeButton("Poistu", (dialog, which) -> dialog.cancel());
        tl.show();
    }

    public void onClickListener_Third_Button(View view)
    {
        final AlertDialog.Builder tl = new AlertDialog.Builder(MainActivity.this);

        final View view1;



        LayoutInflater inflater = getLayoutInflater();


        view1 = inflater.inflate(R.layout.alertbox_to_location_layout, null);
        TextView textView = (TextView) view1.findViewById(R.id.id_textViewToLocation);
        textView.setText(arrayListKolmasNappiTeksti.toString());

        view = inflater.inflate(R.layout.toolbar_layout, null);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(arrayListKolmasNappiTitle.toString());

        tl.setCustomTitle(view);

        tl.setMessage(arrayListKolmasNappiTitle.toString());
        Log.d("Apua", arrayListKolmasNappiTitle.toString());
        Log.d("Apua", arrayListKolmasNappiTeksti.toString());
        //tl.setMessage(arrayListSynnytysTehtavaanTullessaTeksti);
        tl.setView(view1);



        tl.setNegativeButton("Poistu", (dialog, which) -> dialog.cancel());
        tl.show();
    };

    public void onClickListener_Fourth_Button(View view) {

        final AlertDialog.Builder tl = new AlertDialog.Builder(MainActivity.this);

        final View view1;



        LayoutInflater inflater = getLayoutInflater();


        view1 = inflater.inflate(R.layout.alertbox_to_location_layout, null);
        TextView textView = (TextView) view1.findViewById(R.id.id_textViewToLocation);
        textView.setText(arrayListNeljasNappiTeksti.toString());

        view = inflater.inflate(R.layout.toolbar_layout, null);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(arrayListKolmasNappiTitle.toString());

        tl.setCustomTitle(view);

        tl.setMessage(arrayListNeljasNappiTitle.toString());
        Log.d("Apua", arrayListNeljasNappiTitle.toString());
        Log.d("Apua", arrayListNeljasNappiTeksti.toString());
        //tl.setMessage(arrayListSynnytysTehtavaanTullessaTeksti);
        tl.setView(view1);



        tl.setNegativeButton("Poistu", (dialog, which) -> dialog.cancel());
        tl.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            // Tehdään intentti jolla hypätään puhelimen valikkoon, System.exit(0) sulkee vain nykyisen activityn ja hyppää restartin kautta edelliseen activityyn.

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            View view;
            final AlertDialog.Builder otw = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(R.layout.toolbar_layout, null);
            otw.setTitle("Lopetus");
            otw.setMessage("Haluatko varmasti lopettaa");

            otw.setNegativeButton("Poistu", (dialog, which) -> startActivity(intent));
            otw.setPositiveButton("Jatka", ((dialog, which) -> dialog.dismiss()));
            otw.show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}


   /* final ImageView kuva = new ImageView(context);                                                   // Jätetty esimerkiksi tuleville hommille
            kuva.setImageResource(R.drawable.ohje);
            layout.addView(kuva);*/
// Toast.makeText(getApplicationContext(), "Suoraan synnytykseen", Toast.LENGTH_SHORT).show();         // jätetty esimerkiksi

 /*final AlertDialog.Builder renameDialog = new AlertDialog.Builder(MainActivity.this);                 // Jätetty esimerkiksi talteen
        renameDialog.setView(R.layout.straight_to_labor_layout);
        renameDialog.setNegativeButton("Poistu", (dialog, which) -> dialog.cancel());
        renameDialog.show();*/

 /*Intent intent_feedback_actions = new Intent(Intent.ACTION_SEND);                                        // jätetty malliksi
        intent_feedback_actions.setType("text/parse");
        intent_feedback_actions.putExtra(Intent.EXTRA_EMAIL, "feedback@ampuappi.com");
        intent_feedback_actions.putExtra(Intent.EXTRA_SUBJECT, "Palautetta sovellukseta");

        startActivity(Intent.createChooser(intent_feedback_actions, "Lähtä sähköposti"));*/