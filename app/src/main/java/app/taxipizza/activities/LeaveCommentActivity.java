package app.taxipizza.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.Order;
import app.taxipizza.viewholders.CommentsAdapter;

public class LeaveCommentActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference requests, ratings;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    CommentsAdapter adapter;

    String orderId = "";
    List<Order> listOrders = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_comment);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        TextView txtTitle = findViewById(R.id.txtTitle);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/KittenSlantTrial.ttf");

        txtTitle.setTypeface(face);
        txtTitle.setText("Commentaires");

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        ratings = database.getReference("Ratings");

        recyclerView = findViewById(R.id.recycler_leave_comment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (Utils.isConnectedToInternet(getApplicationContext())) {
            if (getIntent() != null) {
                orderId = getIntent().getStringExtra("orderId");
                requests.child(orderId).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            listOrders.add(postSnapshot.getValue(Order.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                adapter = new CommentsAdapter(orderId, listOrders, this);
                recyclerView.setAdapter(adapter);
            }
        } else {
            Utils.NetworkAlert(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            startActivity(new Intent(LeaveCommentActivity.this, DrawerActivity.class));
        return super.onOptionsItemSelected(item);
    }
}
