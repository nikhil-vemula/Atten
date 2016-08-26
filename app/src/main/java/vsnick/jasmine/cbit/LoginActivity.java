package vsnick.jasmine.cbit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
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
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            final ProgressDialog progress;
            progress=new ProgressDialog(this);
            progress.setMessage("Authenticating..");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
            AsyncTask task = new AsyncTask() {
                Boolean status=false,error=false;
                String username="";
                String password="";
                String name_of_user="";
                String year="";

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
                protected void onProgressUpdate(Object[] values) {
                    super.onProgressUpdate(values);
                    progress.setMessage(values[0].toString());
                }

                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Connection.Response conn = null;
                        conn = Jsoup.connect("http://erp.cbit.org.in/Login.aspx")
                                .ignoreContentType(true)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .followRedirects(true)
                                .method(Connection.Method.GET)
                                .execute();
                        Log.d("vsn", "doInBackground: " + conn);
                        Document login = null;
                        login = conn.parse();
                        Element ele = login.getElementById("form1");
                        Elements ini = ele.getElementsByTag("input");
                        HashMap<String, String> h = new HashMap<String, String>();
                        for (Element i : ini) {
                            h.put(i.attr("name"), i.attr("value"));
                        }
                        Connection.Response document = null;
                        document = Jsoup.connect("http://erp.cbit.org.in/Login.aspx")
                                .data("__LASTFOCUS", h.get("__LASTFOCUS"))
                                .data("__EVENTTARGET", h.get("__EVENTTARGET"))
                                .data("__EVENTARGUMENT", "__EVENTARGUMENT")
                                .data("__VIEWSTATE", h.get("__VIEWSTATE"))
                                .data("__EVENTVALIDATION", h.get("__EVENTVALIDATION"))
                                .data("txtUserName", username)
                                .data("btnNext", "Next")
                                .cookies(conn.cookies())
                                .method(Connection.Method.POST)
                                .execute();
                        String imgUrl = null;
                        imgUrl = document.parse().getElementById("ImgUserPhoto").attr("src");
                        Document login2 = null;
                        login2 = document.parse();
                        Element ele2 = login2.getElementById("form1");
                        Elements ini2 = ele2.getElementsByTag("input");
                        HashMap<String, String> g = new HashMap<String, String>();
                        for (Element i : ini2) {
                            g.put(i.attr("name"), i.attr("value"));
                        }
                        Connection.Response document2 = null;
                        document2 = Jsoup.connect("http://erp.cbit.org.in/Login.aspx")
                                .data("__LASTFOCUS", "")
                                .data("__EVENTTARGET", "")
                                .data("__EVENTARGUMENT", "")
                                .data("__VIEWSTATE", g.get("__VIEWSTATE"))
                                .data("__EVENTVALIDATION", g.get("__EVENTVALIDATION"))
                                .data("txtPassword", password)
                                .data("btnSubmit", "Submit")
                                .cookies(document.cookies())
                                .method(Connection.Method.POST)
                                .execute();
                        //Log.d("vsn", "onPostExecute: "+document2);
                        Document loginStat = null;
                        loginStat = document2.parse();
                        Element title = loginStat.getElementsByTag("title").first();
                        if (title.text().equals("Student Login")) {
                            publishProgress("Grabbing Attendance");
                            status = true;
                            FileOutputStream outputStream;
                            outputStream = openFileOutput("Attendance", Context.MODE_PRIVATE);
                            outputStream.write(loginStat.toString().getBytes());
                            outputStream.close();
                            Bitmap bitmap;
                            URL url = new URL("http://erp.cbit.org.in/" + imgUrl);
                            URLConnection connec = url.openConnection();
                            bitmap = BitmapFactory.decodeStream(connec.getInputStream());
                            File file = new File(getFilesDir(), "usrPic.jpg");
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                            String s = loginStat.getElementById("ctl00_cpHeader_ucStud_lblStudentStatus").text();
                            name_of_user = s.substring(8, s.indexOf("(") - 1);
                            year = s.substring(s.lastIndexOf(',') + 2);
                            publishProgress("Downloading Syllabus..");
                            //Downloading Syllabus
                            //progress.setMessage("Downloading Syllabus..");
                            String pdfUrl = "http://cbit.ac.in/files/BE%20-%20CSE%20-%20III%20Year%20-%20Scheme%20and%20Syllabus.pdf";
                            url = new URL(pdfUrl);
                            URLConnection conection = url.openConnection();
                            conection.connect();
                            // download the file
                            InputStream input = new BufferedInputStream(url.openStream(), 8192);

                            // Output stream
                            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/internal/Sysllabus.pdf");
                            byte data[] = new byte[1024];

                            int count = 0;

                            while ((count = input.read(data)) != -1) {
                                // writing data to file
                                output.write(data, 0, count);
                            }
                            output.flush();
                            output.close();
                            input.close();
                        }

                    }catch(SocketTimeoutException e){
                        e.printStackTrace();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        error = true;
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    progress.dismiss();
                    if(!error)
                    {
                        Toast.makeText(getApplicationContext(),"Server error",Toast.LENGTH_SHORT).show();
                    } else if(status)
                    {
                        SharedPreferences sharedPref = getSharedPreferences("myfile",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("first",false);
                        editor.putString("username",username);
                        editor.putString("password",password);
                        editor.putString("name_of_user",name_of_user);
                        editor.putString("year",year);
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
            task.execute();
        } else {
            Toast.makeText(getApplicationContext(),"Internet required",Toast.LENGTH_SHORT).show();
        }
    }
}

