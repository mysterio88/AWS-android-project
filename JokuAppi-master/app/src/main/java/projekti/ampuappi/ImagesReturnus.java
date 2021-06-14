package projekti.ampuappi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class ImagesReturnus {





    public ArrayList<Uri> getUris(Context context, String pName){
        ArrayList<Uri> uris = new ArrayList<>();
        Uri uri = Uri.parse(context.getFilesDir().toString() + "/diat10.jpg");

        String[] files = context.fileList();

        for(int i = 0; i < files.length; i++){

            if(files[i].contains(pName)){
                uri = Uri.parse(context.getFilesDir().toString() + "/" + files[i]);
                uris.add(uri);

            }
        }
        for (int i = 0; i < uris.size(); i++){
            Log.d("apua", uris.get(i).toString());
        }

        return uris;


    }



}
