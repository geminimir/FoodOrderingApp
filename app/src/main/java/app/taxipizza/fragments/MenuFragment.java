package app.taxipizza.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import app.taxipizza.Interface.ItemClickListener;
import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.activities.MealsListActivity;
import app.taxipizza.models.Menus;
import app.taxipizza.viewholders.MenuViewHolder;


public class MenuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerAdapter<Menus, MenuViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("");
        setHasOptionsMenu(true);
        TextView txtTitle = getActivity().findViewById(R.id.txtTitle);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/KittenSlantTrial.ttf");

        txtTitle.setTypeface(face);
        txtTitle.setText("Menu");

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Menus");

        recycler_menu = getActivity().findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_menu.setLayoutManager(layoutManager);

        mSwipeRefreshLayout = getActivity().findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_dark);

        //if(Utils.isConnectedToInternet(getActivity()))
            loadMenu();
    }


    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Menus, MenuViewHolder>(
                Menus.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, final Menus model, int position) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getContext())
                        .load(model.getImage())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(getActivity(), MealsListActivity.class);
                        foodList.putExtra("MenuId", adapter.getRef(position).getKey());
                        foodList.putExtra("MenuName", model.getName());
                        startActivity(foodList);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
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

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMenu();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);

    }

    private void setupBadge(Menu menu) {

        int mCartItemCount = 5;
        final MenuItem menuItem = menu.findItem(R.id.action_mycart);
        RelativeLayout badgeLayout = (RelativeLayout) menuItem.getActionView();
        TextView mCounter =  badgeLayout.findViewById(R.id.cart_badge);
        mCounter.setText("2");
    }
}
