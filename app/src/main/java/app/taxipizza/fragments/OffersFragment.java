package app.taxipizza.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import app.taxipizza.models.Food;
import app.taxipizza.viewholders.OffersAdapter;

/**
 * Created by user on 16/02/2018.
 */

public class OffersFragment extends Fragment {

    RecyclerView recyclerview;
    LinearLayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference offers;

    OffersAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.offers_fragment, container, false);
    }

    List<Food> offersList = new ArrayList<>();
    List<String> keysList = new ArrayList<>();
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("");

        TextView txtTitle = getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Offres");

        recyclerview = getActivity().findViewById(R.id.recycler_offers);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        offers = database.getReference("Meals");

        offers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food food = postSnapshot.getValue(Food.class);
                    if(food.getDiscount() != null && !food.getDiscount().equals("0"))
                    offersList.add(food);
                    keysList.add(postSnapshot.getKey());
                }
                adapter = new OffersAdapter(getActivity(), keysList, offersList);
                recyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.food, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
