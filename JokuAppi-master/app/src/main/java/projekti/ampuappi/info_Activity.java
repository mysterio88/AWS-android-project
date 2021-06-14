package projekti.ampuappi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class info_Activity extends AppCompatActivity {
    private JSON json;
    private ArrayList<String> arrayListINFOTeksti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_);

        json = new JSON("infopage");

        json.setKey("infopage", this);


        arrayListINFOTeksti = json.get_json("info", "body");

        TextView tv = findViewById(R.id.info_textview);
        tv.setText(arrayListINFOTeksti.toString());


    }

    public void onClick_exit_IB(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}