package app.taxipizza.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.taxipizza.Interface.ItemClickListener;
import app.taxipizza.R;


public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;

    public ItemClickListener itemClickListener;
    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName = itemView.findViewById(R.id.menu_name);
        imageView = itemView.findViewById(R.id.menu_image);

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
