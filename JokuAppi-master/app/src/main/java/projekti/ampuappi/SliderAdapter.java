package projekti.ampuappi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    public int mikaSynnytys;
    public int sivumaara;
    JSON json;
    private int pituusNormaali;
    private int pituusPeratila;
    private int pituusNapanuora;
    private int pituusHartia;
    private int pituusEkaVaihe;
    private int pituusKolmasVaihe;
    public int[] ensimmainenvaihe_slides;
    public int[] normaalitila_slides;
    public int[] peratila_slides;
    public int[] napanuora_slides;
    public int[] hartiadystokia_slides;
    public int[] kolmasvaihe_slides;

    //Pictures
    private ArrayList<Uri> picUris1;
    private ArrayList<Uri> picUris2;
    ImagesReturnus imagesReturnus;


    //public
    // Tuodaan parametrinä MainActivitystä saatu tieto mikä synnytystapahtuma ja montako sivua esityksessä on


    private ArrayList<String> arrayListEkavaiheTitle, arrayListEkavaiheTeksti, arrayListPonnistusVaiheTitle, arrayListPonnistusVaiheTeksti, arrayListVikaVaiheTitle, arrayListVikaVaiheTeksti,
            arrayListPeratilaTitle, arrayListPeratilaTeksti, arrayListNapanuoraTitle, arrayListNapanuoraTeksti, arrayListHartiaTitle, arrayListHartiaTeksti;


    public SliderAdapter(Context context, int pArvo, int pSivumaara)
    {

        //Getting picture uris from ImagesReturnus
        imagesReturnus = new ImagesReturnus();
        picUris1 = imagesReturnus.getUris(context, "diat1");
        picUris2 = imagesReturnus.getUris(context, "diat2");


        this.context = context;
        mikaSynnytys = pArvo;
        sivumaara = pSivumaara;

        json = new JSON("diat1");
        json.setKey("diat1", this.context);

        arrayListEkavaiheTitle = json.get_json("diat1", "title");
        arrayListEkavaiheTeksti = json.get_json("diat1", "body");

        json.setKey("diat2", this.context);

        arrayListPonnistusVaiheTitle = json.get_json("diat2", "title");
        arrayListPonnistusVaiheTeksti = json.get_json("diat2", "body");

        arrayListVikaVaiheTitle = json.get_json("diat2", "title");
        arrayListVikaVaiheTeksti = json.get_json("diat2", "body");

        arrayListPeratilaTitle = json.get_json("diat2", "title");
        arrayListPeratilaTeksti = json.get_json("diat2", "body");

        arrayListNapanuoraTitle = json.get_json("diat2", "title");
        arrayListNapanuoraTeksti = json.get_json("diat2", "body");

        arrayListHartiaTitle = json.get_json("diat2", "title");
        arrayListHartiaTeksti = json.get_json("diat2", "body");


        pituusNormaali = arrayListPonnistusVaiheTitle.size();
        pituusPeratila = arrayListPeratilaTitle.size();
        pituusNapanuora = arrayListNapanuoraTitle.size();
        pituusHartia = arrayListHartiaTitle.size();
        pituusEkaVaihe = arrayListEkavaiheTitle.size();
        pituusKolmasVaihe = arrayListVikaVaiheTitle.size();

        normaalitila_slides= new int[pituusNormaali];
        peratila_slides= new int[pituusPeratila];
        napanuora_slides = new int[pituusNapanuora];
        hartiadystokia_slides = new int[pituusHartia];
        ensimmainenvaihe_slides= new int[pituusEkaVaihe];
        kolmasvaihe_slides = new int[pituusKolmasVaihe];

    }

    // luo automaattisesti tehtäessä kun extendaa PageAdapterin
    @Override
    public int getCount() {
        return sivumaara;
    }   //TODO tee tästä skaalautuva

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    // vaaditaan inflantoimaan kaikki tässä adapterissa
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        for(int i=0; i<pituusPeratila; i++) {peratila_slides[i] = R.drawable.loremipsum;}
        for(int i=0; i<pituusNormaali; i++) {normaalitila_slides[i]=R.drawable.loremipsum;}
        for(int i=0; i<pituusEkaVaihe; i++) {ensimmainenvaihe_slides[i]=R.drawable.loremipsum;}
        for(int i=0; i<pituusKolmasVaihe; i++) {kolmasvaihe_slides[i]=R.drawable.loremipsum;}
        for(int i=0; i<pituusHartia; i++) {hartiadystokia_slides[i]=R.drawable.loremipsum;}
        for(int i=0; i<pituusNapanuora; i++) {napanuora_slides[i]=R.drawable.loremipsum;}


        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        // Asetetaan oikeat tiedot arrayista
        ImageView slideImageView = (ImageView) view.findViewById(R.id.id_ImageView);
        TextView slideHeader = (TextView) view.findViewById(R.id.id_TextLabel);
        TextView slideText = (TextView) view.findViewById(R.id.id_textViewToLocation);

        // normaalitila 2 vaihe
        if (mikaSynnytys == 1)
        {
            Log.d("apua", String.valueOf(position));
            //slideImageView.setImageResource(ensimmainenvaihe_slides[position]);

            if (picUris1.size() < pituusEkaVaihe) slideImageView.setImageResource(ensimmainenvaihe_slides[position]);
            else slideImageView.setImageURI(picUris1.get(position));

            slideHeader.setText(arrayListEkavaiheTitle.get(position));
            slideText.setText(arrayListEkavaiheTeksti.get(position));
       //     Log.d("apua",arrayListPonnistusVaiheTitle.toString());
        }

        // perätila
        if (mikaSynnytys == 2)
        {
            Log.d("apua", String.valueOf(position));
            if (picUris2.size() < pituusNormaali) slideImageView.setImageResource(normaalitila_slides[position]);
            else slideImageView.setImageURI(picUris2.get(position));

            slideHeader.setText(arrayListPonnistusVaiheTitle.get(position));
            slideText.setText(arrayListPonnistusVaiheTeksti.get(position));
        }
        if (mikaSynnytys == 3)
        {
            Log.d("apua", String.valueOf(position));
            slideImageView.setImageResource(kolmasvaihe_slides[position]);
            slideHeader.setText(arrayListVikaVaiheTitle.get(position));
            slideText.setText(arrayListVikaVaiheTeksti.get(position));
        }

        if (mikaSynnytys == 4)
        {
            slideImageView.setImageResource(peratila_slides[position]);
            slideHeader.setText(arrayListPeratilaTitle.get(position));
            slideText.setText(arrayListPeratilaTeksti.get(position));
        }

        // EKAVAIHE
        if (mikaSynnytys == 5)
        {
            slideImageView.setImageResource(hartiadystokia_slides[position]);
            slideHeader.setText(arrayListHartiaTitle.get(position));
            slideText.setText(arrayListHartiaTeksti.get(position));


        }
        // 3 VAIHE
        if (mikaSynnytys == 6)
        {
            slideImageView.setImageResource(napanuora_slides[position]);
            slideHeader.setText(arrayListNapanuoraTitle.get(position));
            slideText.setText(arrayListNapanuoraTeksti.get(position));
        }

        container.addView(view);

        return view;
    }

    // kun tullaan viimeiselle sivulle niin pysähtyy, estää päällekkäiset slidet

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);
    }


}