package com.iss.androidca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Activity2 extends AppCompatActivity {
    String path;
    String pathblank;
    List list;
    ImageView imageView;
    GridView gridview;
    ImageView view2;
    String pre ;
    int count = 1;
    int prenum;
    int match=0;
    Chronometer simpleChronometer;
    Intent in;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridview = findViewById(R.id.gridview);
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

        list = Arrays.asList(0,1,2,3,4,5,0,1,2,3,4,5);
        Collections.shuffle(list);//randomly

        in = new Intent(this,Activity1.class);

        for(int i=0;i<12;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.blank);
            map.put("ItemText", list.get(i).toString());
            lstImageItem.add(map);
        }

        SimpleAdapter saImageItems = new SimpleAdapter(
                this, lstImageItem, R.layout.gridview_item,
                new String[] {"ItemImage","ItemText"},
                new int[] {R.id.ItemImage,R.id.ItemText}
        );
        gridview.setAdapter(saImageItems);
        gridview.setOnItemClickListener(new ItemClickListener());

        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
        simpleChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                int ms= (int)(time - h*3600000- m*60000 - s*1000)/10;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                cArg.setText(hh+":"+mm+":"+ss);
            }
        });
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.start();
    }

    Handler handler = new Handler();

    public class  ItemClickListener implements AdapterView.OnItemClickListener
    {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            imageView= arg1.findViewById(R.id.ItemImage);
            path = getFilesDir() + "/" + item.get("ItemText") + ".jpg";
            pathblank = getFilesDir() + "/blank.jpg";
            if(count ==1){
                displayImage(path);
                count++;
                prenum = arg2;
                view2 = imageView;
                pre = item.get("ItemText").toString();
            }
            else if(pre == item.get("ItemText").toString() && prenum != arg2){
                displayImage(path);
                count = 1;
                match++;
                if(match == 6){
                    simpleChronometer.stop();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(in);
                        }
                    },500);
                }
                TextView x = (TextView)findViewById(R.id.match);
                x.setText("Match :"+ String.valueOf(match)+"/ 6");
                view2 =null;
            }
            else if(pre == item.get("ItemText").toString() && prenum == arg2){
                count = 2;
            }
            else {
                displayImage(path);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayBlank(imageView);
                        displayBlank(view2);
                    }
                },1500);
                count = 1;
            }
        }
    }

    protected void displayImage(String fpath) {
        try {
            File file = new File(fpath);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void displayBlank(ImageView img) {
        try {
            img.setImageResource(R.drawable.blank);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}