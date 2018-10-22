package app.taxipizza.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hedgehog.ratingbar.RatingBar;

import app.taxipizza.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 06/01/2018.
 */

public class ReviewViewHolder extends RecyclerView.ViewHolder {

    public TextView txtUserName, txtRatingTime, txtComment;
    public RatingBar ratingBar;
    public CircleImageView imgUserProfilePic;
    public ReviewViewHolder(View itemView) {
        super(itemView);

        txtUserName = itemView.findViewById(R.id.txtUserName);
        txtRatingTime = itemView.findViewById(R.id.txtRatingTime);
        txtComment = itemView.findViewById(R.id.txtComment);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        imgUserProfilePic = itemView.findViewById(R.id.imgProfilePic);
    }



}
