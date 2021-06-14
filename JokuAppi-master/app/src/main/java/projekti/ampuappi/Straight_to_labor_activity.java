package projekti.ampuappi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import java.io.InputStream;
import java.util.ArrayList;

public class Straight_to_labor_activity extends AppCompatActivity {

    private ViewGroup containerView;
    private int synnytysTapahtuma;
    private int diojenMaara;

    private int firstDiaLength, secondDiaLength, thirdDiaLength, fourthDiaLenght, fifthDiaLenght, sixthDiaLenght;

    private JSON json;
    Context context;

    private ArrayList<String> arrayListEkavaiheTitle, arrayListPonnistusVaiheTitle,  arrayListVikaVaiheTitle,
            arrayListPeratilaTitle, arrayListNapanuoraTitle, arrayListHartiaTitle;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_straight_to_labor_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.button_straight_to_labor);
        toolbar.inflateMenu(R.menu.menu);
        setActionBar(toolbar);



    }

    public int getJSONLength(int synnytysTapahtuma) {
        json = new JSON("diat1");
        json.setKey("diat1", getApplicationContext());

        arrayListEkavaiheTitle = json.get_json("diat1", "title");
        json.setKey("diat2", getApplicationContext());
        arrayListPonnistusVaiheTitle = json.get_json("diat2", "title");
        arrayListVikaVaiheTitle = json.get_json("diat2", "title");
        arrayListPeratilaTitle = json.get_json("diat2", "title");
        arrayListNapanuoraTitle = json.get_json("diat2", "title");
        arrayListHartiaTitle = json.get_json("diat2", "title");

        firstDiaLength = arrayListEkavaiheTitle.size();
        Log.d("pituus", String.valueOf(firstDiaLength));

        secondDiaLength = arrayListPonnistusVaiheTitle.size();
        Log.d("pituus", String.valueOf(secondDiaLength));

        thirdDiaLength = arrayListVikaVaiheTitle.size();
        Log.d("pituus", String.valueOf(thirdDiaLength));

        fourthDiaLenght = arrayListPeratilaTitle.size();

        fifthDiaLenght = arrayListHartiaTitle.size();

        sixthDiaLenght = arrayListNapanuoraTitle.size();

        if (synnytysTapahtuma == 1){ return firstDiaLength;}
        else if (synnytysTapahtuma == 2){return secondDiaLength;}
        else if (synnytysTapahtuma == 3){return thirdDiaLength;}
        else if (synnytysTapahtuma == 4){return fourthDiaLenght;}
        else if (synnytysTapahtuma == 5){return fifthDiaLenght;}
        else if (synnytysTapahtuma == 6){return sixthDiaLenght;}
        return 0;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_MenuItem:
                Intent intent_info = new Intent(Straight_to_labor_activity.this,info_Activity.class);
                startActivity(intent_info);

                return super.onOptionsItemSelected(item);

            case R.id.feedback_MenuItem:
                Intent intent_feedback = new Intent(Straight_to_labor_activity.this,feedback_Activity.class);
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

    // viedään putextrana tieto mikä synnytystapahtuma valittu karttanäkymässä, lisäksi viedään tieto montako "diaa" tapahtumassa on


    public void onClickListener_ekaVaihe(View view) {

        synnytysTapahtuma = 1;
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("key", synnytysTapahtuma);
        intent.putExtra("sivujenmaara", getJSONLength(1));
        startActivity(intent);
    }

    public void onClickListener_NormalLabor(View view)
    {
        synnytysTapahtuma = 2;
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("key", synnytysTapahtuma);
        intent.putExtra("sivujenmaara", getJSONLength(2));      // haetaan JSONISTA montako slidea halutaan totetuttaa ja viedään lukumäärä SliderActivity luokalle
        startActivity(intent);
    }

    public void onClickListener_kolmasVaihe(View view) {
        synnytysTapahtuma = 3;
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("key", synnytysTapahtuma);
        intent.putExtra("sivujenmaara", getJSONLength(3));
        startActivity(intent);
    }

    public void onClickListener_peratilan_synnytys(View view)
    {
        synnytysTapahtuma = 4;   // tilaa käytetään valitsemaan
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("key", synnytysTapahtuma);
        intent.putExtra("sivujenmaara", getJSONLength(4));
        startActivity(intent);
    }

    public void onClickListener_hartiaDystokia(View view) {
        synnytysTapahtuma = 5;
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("key", synnytysTapahtuma);
        intent.putExtra("sivujenmaara", getJSONLength(5));
        startActivity(intent);
    }

    public void onClickListener_napanuoran_esiinluiskahdus(View view) {
        synnytysTapahtuma =6;
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("key", synnytysTapahtuma);
        intent.putExtra("sivujenmaara", getJSONLength(6));
        startActivity(intent);
    }





    public void onClick_exit (View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            // finish sulkee current activityn ja palaa edelliseen activityyn stackissä. Käytä mieluummin kuin tekemällä aina uusi activity koska nopeampi ja jos ei tarvetta palata vanhaan activityyn.

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}