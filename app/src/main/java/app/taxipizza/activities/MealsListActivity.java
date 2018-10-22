package app.taxipizza.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import app.taxipizza.Interface.ItemClickListener;
import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.Food;
import app.taxipizza.viewholders.FoodViewHolder;

public class MealsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference mealsList;

    String menuId, menuName;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        TextView txtTitle = findViewById(R.id.txtTitle);

        database = FirebaseDatabase.getInstance();

        if(!Utils.isConnectedToInternet(getApplicationContext()))
            Utils.NetworkAlert(this);

        mealsList = database.getReference("Meals");

        recyclerView = findViewById(R.id.recycler_food);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            menuId = getIntent().getStringExtra("MenuId");
            menuName = getIntent().getStringExtra("MenuName");
            if (!menuId.isEmpty() && menuId != null && !menuName.isEmpty() && menuName != null) {

                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/KittenSlantTrial.ttf");

                txtTitle.setTypeface(face);
                txtTitle.setText(menuName);

                mSwipeRefreshLayout = findViewById(R.id.swipe_container);
                mSwipeRefreshLayout.setOnRefreshListener(this);
                mSwipeRefreshLayout.setColorSchemeResources(
                        R.color.black,
                        R.color.colorPrimary,
                        android.R.color.holo_red_light,
                        android.R.color.holo_blue_dark);

               // if (Utils.isConnectedToInternet(getApplicationContext()))
                    loadListFood(menuId);
               // else
                 //   Utils.NetworkAlert(MealsListActivity.this);
            }
        }
    }

    private void loadListFood(String catergoryId) {
        Query query = mealsList.orderByChild("menuId").equalTo(catergoryId);
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImageOne())
                        .into(viewHolder.food_image);
                viewHolder.food_price.setText(String.format("%s DT", model.getPrice()));
                viewHolder.food_ingredients.setText(model.getIngredients());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(MealsListActivity.this, MealDetailsActivity.class);
                        foodDetail.putExtra("meal", model);
                        foodDetail.putExtra("MenuId", menuId);
                        foodDetail.putExtra("MenuName", menuName);
                        foodDetail.putExtra("mealId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                        finish();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.food, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        } else if(id == R.id.action_mycart){
            Intent intent = new Intent(MealsListActivity.this, DrawerActivity.class);
            intent.putExtra("fragment", "cart");
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadListFood(menuId);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }
}
