package com.iss.androidca;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.commons.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Activity1 extends AppCompatActivity {


    TextView statusTv;
    ProgressBar statusPb;
    EditText et;
    GridView gv;
    ArrayList<String> imageurls = new ArrayList<String>();
    ArrayList<Bitmap> imageBtps = new ArrayList<Bitmap>();
    ArrayList<Integer> selectedPos = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

        // W/System.err: android.os.NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        statusTv = (TextView) findViewById(R.id.textView);
        statusPb = (ProgressBar) findViewById(R.id.progressBar);
        et = (EditText) findViewById(R.id.url);
        gv = (GridView) findViewById(R.id.gridview);

        Button fetctbtn = (Button) findViewById(R.id.fetch);
        fetctbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = et.getText().toString();
                try {
                    URLConnection connection = new URL( url ).openConnection();
                    InputStream inStream = connection.getInputStream();
                    String htmlText = IOUtils.toString(inStream, connection.getContentEncoding());
                    System.out.println("Binny you are here..");
                    System.out.println(htmlText);

                    ArrayList<Integer> indexes ;
                    String s = "<img src=\"";

                    indexes=findIndexes(htmlText);
                    for (Integer i=1; i < 21;i++){
                        Integer temp = indexes.get(i);
                        System.out.println(temp);
                        temp = temp + s.length ();
                        System.out.println (htmlText.substring (temp, htmlText.indexOf ("\"", temp + 1)));
                        imageurls.add(htmlText.substring (temp, htmlText.indexOf ("\"", temp + 1)));
                    }
                    statusPb.setMax( imageurls.size() );
                    gv.invalidateViews();


                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Something went wrong");
                    System.out.println(e.getMessage());
                }

            }
        });

        gv.setAdapter(new ImageAdapterGridView(this));
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            View viewPrev;
//            int nPrevSelGridItem = -1;

            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                Toast.makeText(getBaseContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show();

                if (selectedPos.indexOf(position) == -1) {
                    if (selectedPos.size() < 6) {

                        selectedPos.add(position);
                        View viewPrev = (View) gv.getChildAt(position);
                        viewPrev.setBackgroundColor(Color.GRAY );
                        Toast.makeText(getBaseContext(), selectedPos.toString(), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getBaseContext(), "-  Maximum of 6 items chosen already.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getBaseContext(), Activity2.class);

                        ArrayList<String> ids = new ArrayList<String>();

                        for (int i=0; i < selectedPos.size(); i++) {
                            ids.add( selectedPos.get(i).toString() );
                        }
                        for(int i =0;i<ids.size();i++) {
                            long imageLen = 0;
                            long totalSoFar = 0;
                            int readLen = 0;
                            Bitmap bitmap = null;
                            String id1 = ids.get(i);
                            int first = Integer.parseInt(id1);
                            String url = imageurls.get(first);
                            String fpath = getFilesDir() + "/" + i + ".jpg";
                            try {
                                URL url1 = new URL(url);

                                byte[] data = new byte[1024];
                                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                                conn.connect();
                                imageLen = conn.getContentLength();
                                InputStream in = url1.openStream();
                                BufferedInputStream bufIn = new BufferedInputStream(in, 2048);
                                OutputStream out = new FileOutputStream(fpath);
                                while ((readLen = bufIn.read(data)) != -1) {
                                    totalSoFar += readLen;
                                    out.write(data, 0, readLen);
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("ids", ids);
                        bundle.putStringArrayList("imageurls", imageurls);
//                        bundle.putParcelableArrayList("imageBtps", imageBtps);

                        intent.putExtras(bundle);
                        startActivity(intent);

                    }

                } else {
                    // ignore or do nothing, if user selected the same icon again.
                    selectedPos.remove( selectedPos.indexOf(position) );
                    View viewPrev = (View) gv.getChildAt(position);
                    viewPrev.setBackgroundColor(Color.WHITE);
                    Toast.makeText(getBaseContext(), selectedPos.toString(), Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdapterGridView(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageurls.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView;

            if (convertView == null) {
                mImageView = new ImageView(mContext);
                mImageView.setLayoutParams(new GridView.LayoutParams(248, 248));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setPadding(8, 8, 8, 8);
                new DownloadImageTask(mImageView, position).execute(
                        imageurls.get(position) );
            } else {
                mImageView = (ImageView) convertView;
            }
            mImageView.setImageResource(mThumbIds[position]);
//            mImageView.setImageResource(imageIDs[position]);
            return mImageView;
        }

        public Integer[] mThumbIds = {
                R.drawable.imageplaceholder, R.drawable.imageplaceholder, R.drawable.imageplaceholder,
                R.drawable.imageplaceholder, R.drawable.imageplaceholder, R.drawable.imageplaceholder,
                R.drawable.imageplaceholder, R.drawable.imageplaceholder, R.drawable.imageplaceholder,
                R.drawable.imageplaceholder, R.drawable.imageplaceholder, R.drawable.imageplaceholder,
                R.drawable.imageplaceholder, R.drawable.imageplaceholder, R.drawable.imageplaceholder,
                R.drawable.imageplaceholder, R.drawable.imageplaceholder, R.drawable.imageplaceholder,
                R.drawable.imageplaceholder, R.drawable.imageplaceholder
        };
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int pos;

        public DownloadImageTask(ImageView bmImage, int pos) {
            this.bmImage = bmImage;
            this.pos = pos;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
                System.out.println("Storing bitmap =>");
                System.out.println(this.pos);
                if (this.pos == 0 & imageBtps.size() > 0){
                    // Dont add this..
                } else {
                    imageBtps.add(bmp);
                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            System.out.println("Position = ");
            System.out.println(this.pos);
            if (this.pos == 0 & imageBtps.size() > 0) {
                String message = String.format("Downloading %d of %d images...", imageurls.size(), imageurls.size());
                statusPb.setProgress(imageurls.size());
                statusTv.setText(message);
            } else {
                String message = String.format("Downloading %d of %d images...", this.pos, imageurls.size());
                statusPb.setProgress(this.pos);
                statusTv.setText(message);
            }
        }
    }

    public static ArrayList<Integer> findIndexes(String str){
        String searchableString = str;
        String keyword = "<img src=\"";
        ArrayList<Integer> indexes = new ArrayList<Integer>();

        int index = searchableString.indexOf(keyword);
        while (index >=0){
            indexes.add(index);
            System.out.println("Index : "+index);
            index = searchableString.indexOf(keyword, index+keyword.length())   ;
        }
        return indexes;
    }
}
