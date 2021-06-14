package projekti.ampuappi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class feedback_Activity extends AppCompatActivity {
    private JSON json;
    private ArrayList<String> arrayListFeedbackTeksti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_);

        json = new JSON("feedback");

        json.setKey("feedback", this);

        arrayListFeedbackTeksti = json.get_json("feedback", "body");

        TextView tv = findViewById(R.id.feedback_textview);
        tv.setText(arrayListFeedbackTeksti.toString());


    }


    public void onClick_exit_FB(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickListener_to_email(View view)
    {

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "feedback@JokuAppi.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Palautetta");
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }

}