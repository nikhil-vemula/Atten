package vsnick.jasmine.cbit;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class Attendance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
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
                            .data("txtUserName","160114733173")
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
                            .data("txtPassword","14cbit")
                            .data("btnSubmit","Submit")
                            .cookies(document.cookies())
                            .method(Connection.Method.POST)
                            .execute();
                } catch (IOException e) {
                    Log.d("vsn", "doInBackground: ");
                    e.printStackTrace();
                }
                try {
                    attendance = document2.parse().getElementById("ctl00_cpStud_lblTotalPercentage").text();
                } catch (IOException e) {
                    Log.d("vsn", "doInBackground: ");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                progress.dismiss();
                TextView textView = (TextView) findViewById(R.id.atten);
                textView.setText(attendance);
            }
        };
        task.execute();
    }
}
