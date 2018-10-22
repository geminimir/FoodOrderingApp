package app.taxipizza.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.taxipizza.Interface.ItemClickListener;
import app.taxipizza.R;
import app.taxipizza.activities.MealDetailsActivity;
import app.taxipizza.models.Food;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by user on 27/02/2018.
 */

class OffersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtName, txtIngredients, txtDiscount;
    public ImageView imgMeal;

    public ItemClickListener itemClickListener;

    public OffersViewHolder(View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtMealName);
        txtIngredients = itemView.findViewById(R.id.txtMealIngredients);
        txtDiscount = itemView.findViewById(R.id.txtDiscount);
        imgMeal = itemView.findViewById(R.id.imgMeal);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
public class OffersAdapter  extends  RecyclerView.Adapter<OffersViewHolder>{

        private List<Food> listOffers = new ArrayList<>();
        private List<String> listKeys = new ArrayList<>();
        private Context context;

    public OffersAdapter(Context context, List<String> listKeys, List<Food> listOffers) {
        this.listOffers = listOffers;
        this.context = context;
        this.listKeys = listKeys;
    }

    @Override
    public OffersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.offer_item_layout, parent, false);
        return new OffersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OffersViewHolder viewHolder, int position) {
        final Food model = listOffers.get(position);
        viewHolder.txtName.setText(model.getName());
        viewHolder.txtIngredients.setText(model.getIngredients());

        Picasso.with(context)
                .load(model.getImageOne())
                .into(viewHolder.imgMeal);
        viewHolder.txtDiscount.setText("-" + model.getDiscount() + "%");
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent offers = new Intent(context, MealDetailsActivity.class);
                offers.putExtra("meal", model);
                offers.putExtra("mealId", listKeys.get(position));
                offers.putExtra("offers", true);
                context.startActivity(offers);
            }
        });
    }

    private static Bitmap textAsBitmap(String text, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(40);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    @Override
    public int getItemCount() {
        return listOffers.size();
    }
}
