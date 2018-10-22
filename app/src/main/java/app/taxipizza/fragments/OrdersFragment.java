package app.taxipizza.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import app.taxipizza.models.Request;
import app.taxipizza.viewholders.OrderAdapter;

/**
 * Created by user on 16/02/2018.
 */

public class OrdersFragment extends Fragment {

    RecyclerView recyclerOrders;
    LinearLayoutManager layoutManager;

    List<Request> requests = new ArrayList<>();
    List<String> keys = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference orders;

    OrderAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.orders_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("");
        TextView txtTitle = getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Dernières Commandes");

        database = FirebaseDatabase.getInstance();
        orders = database.getReference("Requests");

        recyclerOrders = getActivity().findViewById(R.id.recycler_order);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerOrders.setHasFixedSize(true);
        recyclerOrders.setLayoutManager(layoutManager);

        orders.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Request request = postSnapshot.getValue(Request.class);
                            if(request.getPhone().equals(Utils.getCurrentUser(getContext()).getPhone())) {
                                requests.add(request);
                                keys.add(postSnapshot.getKey());
                            }
                        }
                        adapter = new OrderAdapter(keys, requests, getActivity());
                        recyclerOrders.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.food, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
