package app.taxipizza.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.Request;
import app.taxipizza.models.StaffUser;
import retrofit2.Call;
import retrofit2.Callback;

public class TrackOrderActivity extends AppCompatActivity implements OnMapReadyCallback{

    FirebaseDatabase database;
    DatabaseReference requests, staff;

    String orderId = "";

    TextView txtOrderId, txtTime;
    GoogleMap mMap;

    Marker deliveryGuyMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        TextView txtTitle = findViewById(R.id.txtTitle);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/KittenSlantTrial.ttf");

        txtTitle.setTypeface(face);
        txtTitle.setText("Suivez la Commande");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtOrderId = findViewById(R.id.txtOrderId);
        txtTime = findViewById(R.id.txtTime);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        staff = database.getReference("Staff");

        if(!Utils.isConnectedToInternet(getApplicationContext()))
            Utils.NetworkAlert(this);

        if (getIntent() != null) {
            orderId = getIntent().getStringExtra("orderId");
            txtOrderId.setText(String.format("Commande ID: #%s", orderId));

            staff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        StaffUser user = post.getValue(StaffUser.class);
                        if (user.getCurrentOrder().equals(orderId)) {
                            final double lat = Double.parseDouble(user.getLatitude());
                            final double lng = Double.parseDouble(user.getLongitude());
                            final LatLng position = new LatLng(lat, lng);

                            if (deliveryGuyMarker != null)
                                deliveryGuyMarker.remove();

                            deliveryGuyMarker = mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(Utils.getGuyLocationMarker(getApplicationContext()))));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17.0f));

                            requests.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Request request = dataSnapshot.getValue(Request.class);

                                    String[] location = request.getDeliveryLocation().split(",");
                                    double latitude = Double.parseDouble(location[0]);
                                    double longitude = Double.parseDouble(location[1]);
                                    LatLng deliveryLocation = new LatLng(latitude, longitude);

                                    mMap.addMarker(new MarkerOptions().position(deliveryLocation).icon(BitmapDescriptorFactory.fromBitmap(Utils.getDeliveryLocationMarker(getApplicationContext()))));

                                    setDistance(position, deliveryLocation);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            startActivity(new Intent(TrackOrderActivity.this, DrawerActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void setDistance(final LatLng currentPosition, final LatLng deliveryLocation) {
        String requestAPI = null;
        try {
            requestAPI = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + deliveryLocation.latitude + "," + deliveryLocation.longitude + "&" +
                    "keys=" + getResources().getString(R.string.google_direction_api);

            Utils.getGoogleAPI().getPath(requestAPI).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legObject = legs.getJSONObject(0);

                        JSONObject durationObject = legObject.getJSONObject("duration");
                        String duration = durationObject.getString("text").replace(" mins", "").replace(" min", "");
                        txtTime.setText(duration);
                    } catch (Exception e) {
                        setDistance(currentPosition, deliveryLocation);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
