package app.taxipizza.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.taxipizza.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 26/02/2018.
 */

public class DetailOrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtPrice;
    public Button btnOrder;
    public CircleImageView imgMeal;

    public DetailOrderViewHolder(View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.txtName);
        txtPrice = itemView.findViewById(R.id.txtPrice);
        btnOrder = itemView.findViewById(R.id.btnOrder);
        imgMeal = itemView.findViewById(R.id.imgMeal);
    }
}
