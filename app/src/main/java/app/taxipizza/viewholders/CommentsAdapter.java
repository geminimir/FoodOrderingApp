package app.taxipizza.viewholders;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hedgehog.ratingbar.RatingBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.taxipizza.Interface.ItemClickListener;
import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.activities.MealDetailsActivity;
import app.taxipizza.models.Food;
import app.taxipizza.models.Order;
import app.taxipizza.models.Rating;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by user on 02/03/2018.
 */

class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMealPrice, txtMealName;
    public EditText edtComment;
    public RatingBar ratingBar;
    public Button btnSend;
    public CircleImageView imgMeal;


    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CommentsViewHolder(View itemView) {
        super(itemView);
        imgMeal = itemView.findViewById(R.id.imgMeal);
        txtMealName = itemView.findViewById(R.id.txtMealName);
        txtMealPrice = itemView.findViewById(R.id.txtMealPrice);
        edtComment = itemView.findViewById(R.id.edtComment);
        ratingBar = itemView.findViewById(R.id.rating);
        btnSend = itemView.findViewById(R.id.btnSend);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}

public class CommentsAdapter extends RecyclerView.Adapter<CommentsViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;
    private String key;

    public CommentsAdapter(String key, List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
        this.key = key;
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.comment_item_layout, parent, false);
        return new CommentsViewHolder(itemView);
    }

    DatabaseReference reference;
    String imageUrl;
    int rate = 0;
    @Override
    public void onBindViewHolder(final CommentsViewHolder holder, final int position) {

        holder.ratingBar.setStar(0);
        holder.edtComment.setText("");
        holder.txtMealPrice.setText(String.format("%.2f DT", Float.parseFloat(listData.get(position).getPrice()) * ((100 - Float.parseFloat(listData.get(position).getDiscount())) / 100)));
        holder.txtMealName.setText(listData.get(position).getProductName());
        holder.ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float RatingCount) {
                rate = (int) RatingCount;
            }
        });
        holder.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rate > 0) {
                    Rating rating = new Rating(
                            Utils.getCurrentUser(getApplicationContext()).getPhone(),
                            listData.get(position).getProductId(),
                            String.valueOf(rate),
                            holder.edtComment.getText().toString(),
                            String.valueOf(System.currentTimeMillis())
                    );
                    FirebaseDatabase.getInstance().getReference("Ratings").push().setValue(rating);
                    listData.remove(position);
                    notifyDataSetChanged();
                } else {
                    Snackbar.make(v, "Veuillez attribuer une Note", Snackbar.LENGTH_LONG).show();
                }
                
                if(position  == listData.size()) {
                    String ns = Context.NOTIFICATION_SERVICE;
                    NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                    nMgr.cancel(1);
                    ((Activity)context).finish();
                    Toast.makeText(context, "Merci pour votre Avis", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Food[] Meal = new Food[1];
        reference = FirebaseDatabase.getInstance().getReference("Meals");
        reference.child(listData.get(position).getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Meal[0] = dataSnapshot.getValue(Food.class);
                imageUrl = Meal[0].getImageOne();
                Picasso.with(context).load(imageUrl).into(holder.imgMeal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

}
