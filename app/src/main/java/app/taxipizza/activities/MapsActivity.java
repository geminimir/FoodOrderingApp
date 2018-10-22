package app.taxipizza.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.LatLngLocation;
import app.taxipizza.models.User;
import static app.taxipizza.Utils.Utils.TaxiPizzaLocation;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    RelativeLayout btnConfirm;
    private GoogleMap mMap;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        TextView txtTitle = findViewById(R.id.txtTitle);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/KittenSlantTrial.ttf");

        txtTitle.setTypeface(face);
        txtTitle.setText("Lieu de Livraison");

        btnConfirm = findViewById(R.id.btnConfirmation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deliveryLocation != null) {
                    User currentUser = Utils.getCurrentUser(getApplicationContext());
                    LatLngLocation delivery = new LatLngLocation(deliveryLocation.latitude, deliveryLocation.longitude);
                    currentUser.setDeliveryAddress(delivery);
                    Utils.setCurrentUser(getApplicationContext(), currentUser);
                    FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getPhone()).setValue(currentUser);
                }
                Intent cart = new Intent(MapsActivity.this, ConfirmationActivity.class);
                startActivity(cart);
                finish();
            }
        });
    }

    LatLng deliveryLocation = null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map));
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        User currentUser = Utils.getCurrentUser(getApplicationContext());
        if(currentUser.getDeliveryAddress() != null) {
            LatLng position = new LatLng(currentUser.getDeliveryAddress().getLatitude(), currentUser.getDeliveryAddress().getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(Utils.getDeliveryLocationMarker(getApplicationContext()))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TaxiPizzaLocation, 17.0f));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                float[] distance = new float[3];
                Location.distanceBetween(TaxiPizzaLocation.latitude, TaxiPizzaLocation.longitude, latLng.latitude, latLng.longitude, distance);
                int dis = (int) distance[0];
                if( dis > 3000) {
                    Snackbar.make(btnConfirm, "Désolé, ce lieu est hors de notre portée.", Snackbar.LENGTH_LONG).show();
                } else {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(Utils.getDeliveryLocationMarker(getApplicationContext()))));
                    deliveryLocation = latLng;
                }
            }
        });
    }
}
