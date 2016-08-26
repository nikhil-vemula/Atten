package vsnick.jasmine.cbit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Bundle bundle;
    private TextView hallTicNO,name;
    SharedPreferences sharedPreferences;
    ImageView imageViewRound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bundle=savedInstanceState;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences =getSharedPreferences("myfile", Context.MODE_PRIVATE);
        Boolean firstTime = sharedPreferences.getBoolean("first",true);
        if(firstTime)
        {
            startActivity(new Intent(this,LoginActivity.class));
            if(firstTime)
                finish();
        }
        View hView =  navigationView.getHeaderView(0);
        imageViewRound=(ImageView)hView.findViewById(R.id.imageView_round);
        name = (TextView) hView.findViewById(R.id.name);
        hallTicNO = (TextView) hView.findViewById(R.id.hallticno);
    }

    @Override
    protected void onResume() {
        super.onResume();
        name.setText(sharedPreferences.getString("name_of_user",""));
        hallTicNO.setText(sharedPreferences.getString("username",""));
        File file = new File(getFilesDir(),"usrPic.jpg");
        Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
        Log.d("vsn", "onCreate: "+b);
        imageViewRound.setImageBitmap(b);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_user) {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            onResume();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.attendance) {
            Intent intent = new Intent(getApplicationContext(),Attendance.class);
            startActivity(intent);
        }else if(id == R.id.syllabus){
            getExternalFilesDir("pdf");
            File path = new File(getFilesDir(),"internal/");
            File pdffile = new File(path,"Syllabus.pdf");
            Intent shareIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(pdffile.toString()));
            shareIntent.setDataAndType(Uri.parse(pdffile.toString()),"application/pdf");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(shareIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
