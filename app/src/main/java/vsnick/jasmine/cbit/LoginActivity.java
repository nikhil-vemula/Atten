package vsnick.jasmine.cbit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button signIn = (Button) findViewById(R.id.sign_in);

        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    public void signIn()
    {
        final ProgressDialog progress;
        progress=new ProgressDialog(this);
        progress.setMessage("Authenticating..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
        AsyncTask task = new AsyncTask() {
            Boolean status=false;
            String username="";
            String password="";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                EditText usrname,pass;
                usrname = (EditText) findViewById(R.id.email);
                username = usrname.getText().toString();
                pass = (EditText) findViewById(R.id.password);
                password = pass.getText().toString();
            }

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
                //Log.d("vsn", "onPostExecute: "+document2);
                Document loginStat = null;
                try {
                    loginStat = document2.parse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Element title= loginStat.getElementsByTag("title").first();
                if(title.text().equals("Student Login"))
                {
                    status =true;
                    FileOutputStream outputStream;
                    try {
                        outputStream = openFileOutput("Attendance", Context.MODE_PRIVATE);
                        outputStream.write(loginStat.toString().getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String imgUrl = loginStat.getElementById("ctl00_cpHeader_ucStud_ImgStudPic").attr("src");
                    Log.d("vsn", "doInBackground:"+imgUrl);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                progress.dismiss();
                if(status)
                {
                    SharedPreferences sharedPref = getSharedPreferences("myfile",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("first",false);
                    editor.putString("username",username);
                    editor.putString("password",password);
                    editor.commit();
                    finish();
                }
                else
                {
                    TextView error = (TextView) findViewById(R.id.error);
                    error.setVisibility(View.VISIBLE);
                }
            }
        };
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            task.execute();
        } else {
            Toast.makeText(getApplicationContext(),"Internet required",Toast.LENGTH_SHORT).show();
        }
    }
}

