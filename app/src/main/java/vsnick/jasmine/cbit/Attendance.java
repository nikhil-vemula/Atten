package vsnick.jasmine.cbit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class Attendance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        setAttendance();
        Button button = (Button) findViewById(R.id.update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAttendance();
            }
        });
    }
    void setAttendance()
    {
        File file = new File(getFilesDir(),"Attendance");
        Document doc=null;
        try {
            doc = Jsoup.parse(file, "UTF-8", "http://erp.cbit.org.in/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView textView = (TextView) findViewById(R.id.atten);
        textView.setText(doc.getElementById("ctl00_cpStud_lblTotalPercentage").text());
        StringBuilder stringBuilder = new StringBuilder();
        Element table = doc.getElementById("ctl00_cpStud_PanelSubjectwise");
        Elements rows = table.getElementsByTag("tr");
        Subject[] subjects = new Subject[rows.size()];
        for(int i=1;i<rows.size();i++)
        {
            Elements cols = rows.get(i).getElementsByTag("td");
            if(cols.size()!=0)
                subjects[i] = new Subject(cols.get(1).text(),cols.get(2).text(),cols.get(3).text(),cols.get(4).text(),cols.get(5).text());
        }
        ListView listView = (ListView) findViewById(R.id.atten_detailed);
        listView.setAdapter(new CustomAdapter(this,subjects));
    }
    void updateAttendance()
    {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            SharedPreferences sharedPreferences = getSharedPreferences("myfile",Context.MODE_PRIVATE);
            final String username = sharedPreferences.getString("username",null);
            final String password = sharedPreferences.getString("password",null);
            final ProgressDialog progress;
            progress=new ProgressDialog(this);
            progress.setMessage("Grabbing Attendance..");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
            AsyncTask task = new AsyncTask() {
                String attendance="";
                @Override
                protected Object doInBackground(Object[] params) {
                    Connection.Response conn= null;
                    try {
                        conn = Jsoup.connect("http://erp.cbit.org.in/Login.aspx")
                                .ignoreContentType(true)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .followRedirects(true)
                                .method(Connection.Method.GET)
                                .execute();
                    } catch (IOException e) {
                        Log.d("vsn", "doInBackground: ");
                        e.printStackTrace();
                    }
                    Document login = null;
                    try {
                        login = conn.parse();
                    } catch (IOException e) {
                        Log.d("vsn", "doInBackground: ");
                        e.printStackTrace();
                    }
                    Element ele = login.getElementById("form1");
                    Elements ini = ele.getElementsByTag("input");
                    HashMap<String,String> h = new HashMap<String,String>();
                    for(Element i : ini)
                    {
                        h.put(i.attr("name"),i.attr("value"));
                    }
                    Connection.Response document= null;
                    try {
                        document = Jsoup.connect("http://erp.cbit.org.in/Login.aspx")
                                .data("__LASTFOCUS",h.get("__LASTFOCUS"))
                                .data("__EVENTTARGET",h.get("__EVENTTARGET"))
                                .data("__EVENTARGUMENT","__EVENTARGUMENT")
                                .data("__VIEWSTATE",h.get("__VIEWSTATE"))
                                .data("__EVENTVALIDATION",h.get("__EVENTVALIDATION"))
                                .data("txtUserName",username)
                                .data("btnNext","Next")
                                .cookies(conn.cookies())
                                .method(Connection.Method.POST)
                                .execute();
                    } catch (IOException e) {
                        Log.d("vsn", "doInBackground: ");
                        e.printStackTrace();
                    }
                    Document login2 = null;
                    try {
                        login2 = document.parse();
                    } catch (IOException e) {
                        Log.d("vsn", "doInBackground: ");
                        e.printStackTrace();
                    }
                    Element ele2 = login2.getElementById("form1");
                    Elements ini2 = ele2.getElementsByTag("input");
                    HashMap<String,String> g = new HashMap<String,String>();
                    for(Element i : ini2)
                    {
                        g.put(i.attr("name"),i.attr("value"));
                    }
                    Connection.Response document2= null;
                    try {
                        document2 = Jsoup.connect("http://erp.cbit.org.in/Login.aspx")
                                .data("__LASTFOCUS","")
                                .data("__EVENTTARGET","")
                                .data("__EVENTARGUMENT","")
                                .data("__VIEWSTATE",g.get("__VIEWSTATE"))
                                .data("__EVENTVALIDATION",g.get("__EVENTVALIDATION"))
                                .data("txtPassword",password)
                                .data("btnSubmit","Submit")
                                .cookies(document.cookies())
                                .method(Connection.Method.POST)
                                .execute();
                    } catch (IOException e) {
                        Log.d("vsn", "doInBackground: ");
                        e.printStackTrace();
                    }
                        FileOutputStream outputStream;
                        try {
                            outputStream = openFileOutput("Attendance", Context.MODE_PRIVATE);
                            outputStream.write(document2.parse().toString().getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    progress.dismiss();
                    setAttendance();
                }
            };
            task.execute();
        } else {
            Toast.makeText(getApplicationContext(),"Internet required",Toast.LENGTH_SHORT).show();
        }

    }
}
