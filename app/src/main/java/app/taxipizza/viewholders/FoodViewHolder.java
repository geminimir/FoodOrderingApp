package app.taxipizza.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.taxipizza.Interface.ItemClickListener;
import app.taxipizza.R;


public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView  food_name, food_price, food_ingredients;
    public ImageView food_image;

    public ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        food_image = itemView.findViewById(R.id.food_image);
        food_name = itemView.findViewById(R.id.food_name);
        food_price = itemView.findViewById(R.id.food_price);
        food_ingredients = itemView.findViewById(R.id.food_ingredients);

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
