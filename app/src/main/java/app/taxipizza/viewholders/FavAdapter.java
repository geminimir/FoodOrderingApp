package app.taxipizza.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
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


public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder>{

    private List<String> listKeys = new ArrayList<>();
    private Context context;

    public FavAdapter(List<String> listKeys, Context context) {
        this.listKeys = listKeys;
        this.context = context;
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.favorite_item_layout, parent, false);
        return new FavViewHolder(itemView);
    }

    DatabaseReference reference;
    String imageUrl;
    @Override
    public void onBindViewHolder(final FavViewHolder holder, final int position) {

        reference = FirebaseDatabase.getInstance().getReference("Meals");
        reference.child(listKeys.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final Food food = dataSnapshot.getValue(Food.class);
                imageUrl = food.getImageOne();
                Picasso.with(context).load(imageUrl).into(holder.imgMeal);
                holder.txtMealName.setText(food.getName());
                holder.txtPrice.setText(String.format("%.2f DT", Float.parseFloat(food.getPrice()) * ((100 - Float.parseFloat(food.getDiscount())) / 100)));

                holder.imgAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToCart(v, food, listKeys.get(position));
                    }
                });
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent favs = new Intent(context, MealDetailsActivity.class);
                        favs.putExtra("mealId", listKeys.get(position));
                        favs.putExtra("meal", food);
                        favs.putExtra("favs", true);
                        context.startActivity(favs);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addToCart(View v, final Food meal, final String mealId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Snackbar.make(v, String.format("%s est ajoutée au Panier", meal.getName()),Snackbar.LENGTH_SHORT).show();
        final List<Order> cart;
        User currentUser = Utils.getCurrentUser(context);
        if(currentUser.getCart() != null)
            cart = currentUser.getCart();
        else
            cart = new ArrayList<Order>();

        database.getReference("Users").child(Utils.getCurrentUser(context).getPhone()).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean found = false;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Order order = postSnapshot.getValue(Order.class);
                    if(mealId.equals(order.getProductId())){
                        order.setQuantity(String.valueOf(Integer.parseInt(order.getQuantity()) + 1));

                        database.getReference("Users")
                                .child(Utils.getCurrentUser(context).getPhone())
                                .child("cart")
                                .child(postSnapshot.getKey())
                                .setValue(order);

                        found = true;
                    }
                }
                if(!found) {
                    cart.add(new Order(
                            mealId,
                            meal.getName(),
                            "1",
                            meal.getPrice(),
                            meal.getDiscount()
                    ));
                    database.getReference("Users")
                            .child(Utils.getCurrentUser(context).getPhone())
                            .child("cart")
                            .setValue(cart);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listKeys.size();
    }

   public class FavViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtMealName, txtPrice;
        public CircleImageView imgMeal;
        public ImageButton imgAddToCart;
       public LinearLayout viewForeground;
       public RelativeLayout viewBackground;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public FavViewHolder(View itemView) {
            super(itemView);

            txtMealName = itemView.findViewById(R.id.txtMealName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgMeal = itemView.findViewById(R.id.imgMeal);
            imgAddToCart = itemView.findViewById(R.id.imgAddToCart);

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
