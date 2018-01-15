package com.frandog.a20180115_02;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView tv,tv2,tv3;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.imageView);
        tv = findViewById(R.id.textView);
        tv2 = findViewById(R.id.textView2);
        tv3 = findViewById(R.id.textView3);
        pb = findViewById(R.id.progressBar);
    }
    public void click1(View v) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String str_url = "https://5.imimg.com/data5/UH/ND/MY-4431270/red-rose-flower-500x500.jpg";
                URL url;

                try {
                    url = new URL(str_url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1];    //起一個位元陣列(圖是用陣列一批一批傳的)(原本是1024，但由於快到看不出來，改成1)
                    final int totalLength = conn.getContentLength();      //get總長度
                    int sum = 0;
                    int length;     //由於圖片並不一定是1024的倍數，所以要去抓他最後一次傳陣列時的長度
                    while ((length = inputStream.read(buf)) != -1)       //只要不是沒傳完圖(-1)就繼續傳
                    {
                        sum += length;
                        final int tmp = sum;
                        bos.write(buf, 0, length);        //第二個參數0表示從哪裏開始傳，由於圖一定是從頭傳到尾，所以填0。第三個參數是因為圖不一定是1024的倍數，所以要去抓最後一次傳陣列時的長度
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(String.valueOf(tmp) + "/" + totalLength);
//                                pb.setProgress(pb.getProgress() + tmp);       //我的寫法
                                pb.setProgress(100 * tmp / totalLength);
                            }
                        });
                    }
                    byte[] results = bos.toByteArray();
                    final Bitmap bmp = BitmapFactory.decodeByteArray(results, 0, results.length);       //要把位元陣列轉成圖
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageBitmap(bmp);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void click2(View v){     //可與click1同時執行
            MyTask task = new MyTask();
            task.execute(10);
    }

    class MyTask extends AsyncTask<Integer,Integer,String>      ////參數為"背後執行","進度更新","完成執行"(可上網查綠豆湯的AsyncTask)
    {
        @Override
        protected void onPostExecute(String s) {    //執行完接收s
            super.onPostExecute(s);
            tv3.setText(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tv2.setText(String.valueOf(values[0]));
        }
        @Override
        protected String doInBackground(Integer... integers) {
            int i;
            for(i=0;i<=integers[0];i++)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("TASK","doInBackground,i:" + i);
                publishProgress(i);     //公布進度
            }
            return "okay";      //執行完回傳"okay"
        }
    }


    public void click3(View v)
    {
        MyImageTask task = new MyImageTask();
        task.execute("https://5.imimg.com/data5/UH/ND/MY-4431270/red-rose-flower-500x500.jpg");
    }
    class MyImageTask extends AsyncTask<String,Integer,Bitmap>      //參數為"背後執行","進度更新","完成執行"
    {
        @Override
        protected void onPostExecute(Bitmap bitmap) {       //執行完會收到bitmap
            super.onPostExecute(bitmap);
            img.setImageBitmap(bitmap);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {        //為了塞網址，要String
            String str_url = strings[0];
            URL url;
            try {
                url = new URL(str_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                final int totalLength = conn.getContentLength();
                int sum = 0;
                int length;
                while ((length = inputStream.read(buf)) != -1) {
                    sum += length;
                    final int tmp = sum;
                    bos.write(buf, 0, length);
                }
                byte[] results = bos.toByteArray();
                final Bitmap bmp = BitmapFactory.decodeByteArray(results, 0, results.length);
                return bmp;
            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }
    }
}

