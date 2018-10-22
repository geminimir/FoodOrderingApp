package app.taxipizza.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.Order;
import app.taxipizza.models.Request;
import app.taxipizza.models.User;
import app.taxipizza.services.ListenOrderUpdateService;
import app.taxipizza.viewholders.ConfirmationAdapter;

public class ConfirmationActivity extends AppCompatActivity implements OnMapReadyCallback {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    ConfirmationAdapter adapter;

    TextView txtTotal, txtEditOrder, txtEditDeliveryLocation;
    RelativeLayout btnConfirmation;

    List<Order> cart;

    FirebaseDatabase database;
    DatabaseReference requests;
    static int LOCATION_PERMISSION_CODE = 1010;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        TextView txtTitle = findViewById(R.id.txtTitle);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/KittenSlantTrial.ttf");

        txtTitle.setTypeface(face);
        txtTitle.setText("Confirmation");

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        txtTotal = findViewById(R.id.txtTotal);
        txtEditOrder = findViewById(R.id.txtOrderEdit);
        txtEditDeliveryLocation = findViewById(R.id.txtLocationEdit);
        btnConfirmation = findViewById(R.id.btnConfirmation);
        recyclerView = findViewById(R.id.recycler_confirmation);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        cart = Utils.getCurrentUser(getApplicationContext()).getCart();

        adapter = new ConfirmationAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        setTotalPrice(cart);

        txtEditOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart = new Intent(ConfirmationActivity.this, DrawerActivity.class);
                cart.putExtra("fragment", "cart");
                startActivity(cart);
                finish();
            }
        });

        txtEditDeliveryLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ConfirmationActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                } else {
                    Intent i = new Intent(ConfirmationActivity.this, MapsActivity.class);
                    startActivity(i);
                }
            }
        });

        btnConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //TODO Order tracking
                final User user = Utils.getCurrentUser(getApplicationContext());
                if(user.getDeliveryAddress() != null && cart.size() > 0) {
                    if(Utils.isConnectedToInternet(getApplicationContext())) {
                        database = FirebaseDatabase.getInstance();
                        requests = database.getReference("Requests");
                        requests.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean exists = false;
                                final String requestId = Utils.getRandomHexString();
                                do {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (postSnapshot.getKey().equals(requestId))
                                            exists = true;
                                    }
                                    if (!exists) {
                                        Request request = new Request(
                                                user.getPhone(),
                                                user.getName(),
                                                txtTotal.getText().toString(),
                                                String.valueOf(System.currentTimeMillis()),
                                                cart,
                                                user.getDeliveryAddress().latitude + "," + user.getDeliveryAddress().longitude);

                                        database.getReference("Users")
                                                .child(user.getPhone())
                                                .child("cart")
                                                .removeValue();

                                        requests.child(requestId)
                                                .setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                user.setCart(null);
                                                Utils.setCurrentUser(getApplicationContext(), user);
                                                startService(new Intent(ConfirmationActivity.this, ListenOrderUpdateService.class).putExtra("orderId", requestId));
                                            }
                                        });
                                        startActivity(new Intent(ConfirmationActivity.this, DrawerActivity.class).putExtra("cart", true));
                                        finish();
                                    } else{
                                        Toast.makeText(ConfirmationActivity.this, "Exists", Toast.LENGTH_SHORT).show();
                                    }
                                } while (exists);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else {
                        Utils.NetworkAlert(ConfirmationActivity.this);
                    }
                } else {
                    Snackbar.make(v, "Veuillez choisir le lieu de livraison", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void setTotalPrice(List<Order> cart) {
        float totalPrice = 0;
        if(cart != null) {
            for (Order order : cart)
                totalPrice += Float.parseFloat(order.getPrice()) * Float.parseFloat(order.getQuantity()) * ((100 - Float.parseFloat(order.getDiscount())) / 100);
        }
        txtTotal.setText(String.format("%.2f DT", totalPrice));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map));
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        User currentUser = Utils.getCurrentUser(getApplicationContext());
        if(currentUser.getDeliveryAddress() != null) {
            LatLng position = new LatLng(currentUser.getDeliveryAddress().getLatitude(), currentUser.getDeliveryAddress().getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(Utils.getDeliveryLocationMarker(getApplicationContext()))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));
        } else {
            LatLng LocalTaxiPizza = new LatLng(35.8484589, 10.605323);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LocalTaxiPizza, 16.0f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(ConfirmationActivity.this, MapsActivity.class);
                startActivity(i);
            }
        }
    }
}
