package app.taxipizza.fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import app.taxipizza.activities.ConfirmationActivity;
import app.taxipizza.models.Order;
import app.taxipizza.models.User;
import app.taxipizza.viewholders.CartAdapter;

/**
 * Created by user on 16/02/2018.
 */

public class MyCartFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference cart;

    CartAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    TextView txtTotal;
    RelativeLayout btnPlaceOrder;

    List<Order> cartList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mycart_fragment, container, false);
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
        txtTitle.setText("Mon Panier");

        recyclerView = getActivity().findViewById(R.id.recycler_my_cart);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        database = FirebaseDatabase.getInstance();

        loadCartItems();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getContext(), "Moving", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                Toast.makeText(getContext(), "Moving", Toast.LENGTH_SHORT).show();
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteItemFromCart(viewHolder);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if(viewHolder != null) {
                    View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).viewForeground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        btnPlaceOrder = getActivity().findViewById(R.id.btnPlaceOrder);
        txtTotal = getActivity().findViewById(R.id.txtTotal);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartList.size() > 0) {
                    Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(v, "Votre panier est vide!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteItemFromCart(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        cartList.remove(cartList.get(position));
        User updateUser = Utils.getCurrentUser(getContext());
        updateUser.setCart(cartList);
        Utils.setCurrentUser(getContext(), updateUser);
        database.getReference("Users").child(Utils.getCurrentUser(getContext()).getPhone()).setValue(updateUser);
        float totalPrice = 0.0f;
        for(Order order : cartList)
            totalPrice += Float.parseFloat(order.getPrice()) * Float.parseFloat(order.getQuantity());

        txtTotal.setText(String.format("%.2f DT", totalPrice));
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, cartList.size());
    }

    private void loadCartItems() {
        cart = database.getReference("Users").child(Utils.getCurrentUser(getContext()).getPhone()).child("cart");
        cart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float totalPrice = 0;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Order order = postSnapshot.getValue(Order.class);
                    cartList.add(order);
                    totalPrice += Float.parseFloat(order.getPrice()) * Float.parseFloat(order.getQuantity());
                    txtTotal.setText(String.format("%.2f DT", totalPrice));
                }
                adapter = new CartAdapter(cartList, getActivity());
                recyclerView.setAdapter(adapter);
                User updateUser = Utils.getCurrentUser(getContext());
                updateUser.setCart(cartList);

                Utils.setCurrentUser(getContext(), updateUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setTotalPrice();
    }

    private void setTotalPrice() {
        database = FirebaseDatabase.getInstance();
        cart = database.getReference("Users").child(Utils.getCurrentUser(getContext()).getPhone()).child("cart");

        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float totalPrice = 0;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Order order = postSnapshot.getValue(Order.class);
                    totalPrice += Float.parseFloat(order.getPrice()) * Float.parseFloat(order.getQuantity()) * ((100 - Float.parseFloat(order.getDiscount())) / 100);
                    txtTotal.setText(String.format("%.2f DT", totalPrice));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
