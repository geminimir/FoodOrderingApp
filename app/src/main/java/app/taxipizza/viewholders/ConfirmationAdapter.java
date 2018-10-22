package app.taxipizza.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.taxipizza.R;
import app.taxipizza.models.Food;
import app.taxipizza.models.Order;
import de.hdodenhof.circleimageview.CircleImageView;

class ConfirmationViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_cart_name, txt_price;
    public ImageView imgCount;
    public CircleImageView food_cart_image;

    public ConfirmationViewHolder(View itemView) {
        super(itemView);

        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_price);
        imgCount = itemView.findViewById(R.id.imgItemCount);
        food_cart_image = itemView.findViewById(R.id.cart_item_image);
    }
}

public class ConfirmationAdapter extends RecyclerView.Adapter<ConfirmationViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public ConfirmationAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public ConfirmationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.confirmation_item_layout, parent, false);
        return new ConfirmationViewHolder(itemView);
    }

    DatabaseReference reference;
    String imageUrl;
    @Override
    public void onBindViewHolder(final ConfirmationViewHolder holder, final int position) {

        TextDrawable drawable = TextDrawable.builder()
                .buildRound("" + listData.get(position).getQuantity(), ContextCompat.getColor(context, R.color.btnSignUp));
        holder.imgCount.setImageDrawable(drawable);

        reference = FirebaseDatabase.getInstance().getReference("Meals");
        reference.child(listData.get(position).getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                imageUrl = food.getImageOne();
                Picasso.with(context).load(imageUrl).into(holder.food_cart_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        float price = (Float.parseFloat(listData.get(position).getPrice()) * Float.parseFloat(listData.get(position).getQuantity())) * ((100 - Float.parseFloat(listData.get(position).getDiscount())) / 100);
        holder.txt_price.setText(String.format("%.2f DT", price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

}

