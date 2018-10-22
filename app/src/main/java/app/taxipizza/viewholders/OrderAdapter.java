package app.taxipizza.viewholders;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import app.taxipizza.Interface.ItemClickListener;
import app.taxipizza.R;
import app.taxipizza.models.Request;

class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtDate;
    public RecyclerView recyclerView;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderViewHolder(View itemView) {
        super(itemView);

        //recyclerView = itemView.findViewById(R.id.recycler_orders);
        //txtDate = itemView.findViewById(R.id.txtDate);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    FirebaseDatabase database;
    DatabaseReference orderReference;

    private List<Request> listData = new ArrayList<>();
    private Context context;
    private List<String> keys;

    public OrderAdapter(List<String> keys, List<Request> listData, Context context) {
        this.context = context;
        this.listData = listData;
        this.keys = keys;

        database = FirebaseDatabase.getInstance();
        orderReference = database.getReference("Requests");
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(itemView);
    }


    LinearLayoutManager layoutManager;
    //OrderDetailsAdapter adapter;
    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {
        Request request = listData.get(position);
        //holder.txtDate.setText(Utils.EpochToDate(Long.parseLong(request.getTimeStamp()), "dd MMM"));

        layoutManager = new LinearLayoutManager(context);
        //holder.recyclerView.setLayoutManager(layoutManager);

      //  OrderDetailsAdapter adapter = new OrderDetailsAdapter(context, request.getOrders());
      //  holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
