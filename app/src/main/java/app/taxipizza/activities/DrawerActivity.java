package app.taxipizza.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import app.taxipizza.R;
import app.taxipizza.Utils.NetworkAlert;
import app.taxipizza.Utils.Utils;
import app.taxipizza.fragments.FavoritesFragment;
import app.taxipizza.fragments.MenuFragment;
import app.taxipizza.fragments.MyCartFragment;
import app.taxipizza.fragments.OffersFragment;
import app.taxipizza.fragments.SettingsFragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static app.taxipizza.Utils.Utils.getCurrentUser;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName, txtCartCount, txtOffersCount;
    CircleImageView imgProfilePic;
    LinearLayout profileLayout;

    NavigationView navigationView;

    int CALL_PERMISSION_CODE = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.home_frame, new MenuFragment());
        tx.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        if(!Utils.isConnectedToInternet(getApplicationContext()))
            Utils.NetworkAlert(this);

        if (getIntent() != null && getIntent().getStringExtra("fragment") != null) {
            if (getIntent().getStringExtra("fragment").equals("cart")) {
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.home_frame, new MyCartFragment());
                t.commit();
                navigationView.getMenu().getItem(2).setChecked(true);
            }
        }

        View headerView = navigationView.getHeaderView(0);
        profileLayout = headerView.findViewById(R.id.profileLayout);
        imgProfilePic = headerView.findViewById(R.id.imgProfilePic);
        Picasso.with(getApplicationContext())
                .load(getCurrentUser(getApplicationContext()).getThumb_image())
                .error(getResources().getDrawable(R.drawable.profile_icon))
                .into(imgProfilePic);
        txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(getCurrentUser(getApplicationContext()).getName());

        /*profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(DrawerActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });*/

        initializeCountDrawer();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                initializeCountDrawer();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initializeCountDrawer() {
        //Gravity property aligns the text
        MenuItem mycart = navigationView.getMenu().findItem(R.id.nav_cart);
        txtCartCount = (TextView) mycart.getActionView();
        MenuItem offers = navigationView.getMenu().findItem(R.id.nav_offers);
        txtOffersCount = (TextView) offers.getActionView();

        txtCartCount.setGravity(Gravity.CENTER_VERTICAL);
        txtCartCount.setTypeface(null, Typeface.BOLD);
        txtCartCount.setTextColor(getResources().getColor(R.color.btnSignActive));
        txtOffersCount.setGravity(Gravity.CENTER_VERTICAL);
        txtOffersCount.setTypeface(null, Typeface.BOLD);
        txtOffersCount.setTextColor(getResources().getColor(R.color.btnSignActive));

        Utils.NumberOfItemsInCart(getApplicationContext(), txtCartCount);
        Utils.NumberOfOffers(txtOffersCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeCountDrawer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_mycart) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.home_frame, new MyCartFragment());
            tx.commit();
            navigationView.getMenu().getItem(2).setChecked(true);
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_menu) {
            tx.replace(R.id.home_frame, new MenuFragment());
            tx.commit();
        } else if (id == R.id.nav_fav) {
            tx.replace(R.id.home_frame, new FavoritesFragment());
            tx.commit();
        } else if (id == R.id.nav_offers) {
            tx.replace(R.id.home_frame, new OffersFragment());
            tx.commit();
        } /*else if (id == R.id.nav_orders) {
            tx.replace(R.id.home_frame, new OrdersFragment());
            tx.commit();
        } */else if (id == R.id.nav_log_out) {
            Utils.setCurrentUser(getApplicationContext(), null);
            Intent signInScreen = new Intent(DrawerActivity.this, LoginActivity.class);
            startActivity(signInScreen);
            finish();
        } else if (id == R.id.nav_cart) {
            tx.replace(R.id.home_frame, new MyCartFragment());
            tx.commit();
            getFragmentManager().beginTransaction().remove(new SettingsFragment.Settings()).commit();
        } else if (id == R.id.nav_settings) {
            tx.replace(R.id.home_frame, new SettingsFragment());
            tx.commit();
        } else if (id == R.id.nav_menu) {
            tx.replace(R.id.home_frame, new MenuFragment());
            tx.commit();
        } else if (id == R.id.nav_call) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DrawerActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "20110022"));
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "20110022"));
                startActivity(intent);
            }
        }
    }


}
