package app.taxipizza.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import app.taxipizza.Interface.ItemClickListener;
import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.activities.MealDetailsActivity;
import app.taxipizza.models.Food;
import app.taxipizza.models.Order;
import app.taxipizza.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    DatabaseReference reference;
    String imageUrl;
    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {

        holder.nbrQuantity.setValue(Integer.parseInt(listData.get(position).getQuantity()));
        holder.nbrQuantity.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(Utils.getCurrentUser(context).getPhone())
                        .child("cart")
                        .child(String.valueOf(position))
                        .child("quantity")
                        .setValue(String.valueOf(value));

                User updateUser = Utils.getCurrentUser(context);
                List<Order> cartList = updateUser.getCart();
                cartList.get(position).setQuantity(String.valueOf(value));
                updateUser.setCart(cartList);
                Utils.setCurrentUser(context, updateUser);

                float price = (Float.parseFloat(listData.get(position).getPrice()) * value) * ((100 - Float.parseFloat(listData.get(position).getDiscount())) / 100);
                holder.txt_price.setText(String.format("%.2f DT", price));
            }
        });

        final Food[] Meal = new Food[1];
        reference = FirebaseDatabase.getInstance().getReference("Meals");
        reference.child(listData.get(position).getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Meal[0] = dataSnapshot.getValue(Food.class);
                imageUrl = Meal[0].getImageOne();
                Picasso.with(context).load(imageUrl).into(holder.food_cart_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        float price = (Float.parseFloat(listData.get(position).getPrice()) * Float.parseFloat(listData.get(position).getQuantity())) * ((100 - Float.parseFloat(listData.get(position).getDiscount())) / 100);
        holder.txt_price.setText(String.format("%.2f DT", price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetail = new Intent(context, MealDetailsActivity.class);
                foodDetail.putExtra("mealId", listData.get(position).getProductId());
                foodDetail.putExtra("meal", Meal[0]);
                foodDetail.putExtra("cart", true);
                context.startActivity(foodDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_cart_name, txt_price;
        public NumberPicker nbrQuantity;
        public CircleImageView food_cart_image;
        public LinearLayout viewForeground;
        public RelativeLayout viewBackground;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public CartViewHolder(View itemView) {
            super(itemView);

            txt_cart_name = itemView.findViewById(R.id.cart_item_name);
            txt_price = itemView.findViewById(R.id.cart_item_price);
            nbrQuantity = itemView.findViewById(R.id.cart_item_quantity);
            food_cart_image = itemView.findViewById(R.id.cart_item_image);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            viewBackground = itemView.findViewById(R.id.viewBackground);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

}

