package projekti.ampuappi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class DownloadResources {

    private Bitmap bitmap;
    private String url = "";//diat1/0.jpg
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();



    public void downloadFiles(String folder, String pfileName, String extension, Context context){

        //this.url = url;
        int testInt = 0;
        int picIndex = -1;
        String fileName;
        while (testInt == 0){

            picIndex++;
            String changingUrl;
            try  {
                //Your code goes here
                if(folder == "resources"){
                    fileName = pfileName + extension;
                    changingUrl = this.url + folder + "/" + fileName;
                    testInt = 1;
                }else {
                    fileName = pfileName + picIndex + extension;
                    changingUrl = this.url + folder + "/" + fileName;
                }
                Log.d("apua", fileName);
                java.net.URL url1 = new java.net.URL(changingUrl);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                }

                fos.close();
                inputStream.close();

                //bitmap = BitmapFactory.decodeStream(inputStream);
                //bitmaps.add(bitmap);
                Log.d("bitmap", String.valueOf(bitmaps.size()));


            } catch (Exception e) {
                e.printStackTrace();
                testInt = 1;

            }
        }


    }

    public void setBitmap(String url){
        this.url = url;
        //downLoadImages.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




}
