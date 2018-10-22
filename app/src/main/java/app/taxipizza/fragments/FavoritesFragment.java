package app.taxipizza.fragments;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.viewholders.CartAdapter;
import app.taxipizza.viewholders.FavAdapter;

/**
 * Created by user on 16/02/2018.
 */

public class FavoritesFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference favorites;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FavAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites_fragment, container, false);
    }

    List<String> listkeys = new ArrayList<>();
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("");
        setHasOptionsMenu(true);
        TextView txtTitle = getActivity().findViewById(R.id.txtTitle);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/KittenSlantTrial.ttf");

        txtTitle.setTypeface(face);
        txtTitle.setText("Favoris");

        recyclerView = getActivity().findViewById(R.id.recycler_fav);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        favorites = database.getReference("Favorites").child(Utils.getCurrentUser(getContext()).getPhone());
        favorites.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    listkeys.add(postSnapshot.getKey());
                }
                adapter = new FavAdapter(listkeys, getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteItemFromFavs(viewHolder);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                View foregroundView = ((FavAdapter.FavViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if(viewHolder != null) {
                    View foregroundView = ((FavAdapter.FavViewHolder)viewHolder).viewForeground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View foregroundView = ((FavAdapter.FavViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View foregroundView = ((FavAdapter.FavViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    private void deleteItemFromFavs(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();

        if(Utils.isConnectedToInternet(getContext())) {
            favorites.child(listkeys.get(position))
                    .removeValue();
            listkeys.remove(listkeys.get(position));
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, listkeys.size());
        } else
            Utils.NetworkAlert(getContext());
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
