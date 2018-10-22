package app.taxipizza.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hedgehog.ratingbar.RatingBar;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import app.taxipizza.R;
import app.taxipizza.Utils.SliderPagerAdapter;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.Food;
import app.taxipizza.models.Order;
import app.taxipizza.models.Rating;
import app.taxipizza.models.User;
import app.taxipizza.viewholders.ReviewViewHolder;
import me.relex.circleindicator.CircleIndicator;

public class MealDetailsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference meals;

    RelativeLayout btnAddToCart;
    FloatingActionButton fbFav;

    Food meal;
    String mealId, menuId, menuName;
    boolean FromCart, FromOffers, FromFavs;

    TextView txtPrice, txtEstimated, txtDescription, txtNoReviews, txtCommentsCount, txtPlus, txtDiscount;
    RatingBar ratingBar;

    FirebaseRecyclerAdapter<Rating, ReviewViewHolder> adapter;

    RecyclerView recycler_review;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        TextView txtTitle = findViewById(R.id.txtTitle);

        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        txtEstimated = findViewById(R.id.txtEstimation);
        txtNoReviews = findViewById(R.id.txtNoReviews);
        txtPlus = findViewById(R.id.txtPlus);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtCommentsCount = findViewById(R.id.txtCommentsCount);
        ratingBar = findViewById(R.id.ratingBar);
        //ImageView imgMeal = findViewById(R.id.imgMeal);

        recycler_review = findViewById(R.id.recycler_reviews);
        layoutManager = new LinearLayoutManager(this);
        recycler_review.setLayoutManager(layoutManager);

        btnAddToCart = findViewById(R.id.btnAddCart);
        fbFav = findViewById(R.id.fbFav);


        fbFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbFav.setImageResource(R.drawable.ic_favorite);
            }
        });
        database = FirebaseDatabase.getInstance();

        meals = database.getReference("Food");

        if(getIntent() != null) {
            try {
                meal = getIntent().getParcelableExtra("meal");
                mealId = getIntent().getStringExtra("mealId");
                menuId = getIntent().getStringExtra("MenuId");
                menuName = getIntent().getStringExtra("MenuName");
                FromCart = getIntent().getBooleanExtra("cart", false);
                FromOffers = getIntent().getBooleanExtra("offers", false);
                FromFavs = getIntent().getBooleanExtra("favs", false);
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/KittenSlantTrial.ttf");

                txtTitle.setTypeface(face);

                txtTitle.setText(meal.getName());
                txtEstimated.setText(meal.getEstimated());
                txtDescription.setText(meal.getDescription());
                txtPrice.setText(String.format("%s DT", meal.getPrice()));
                txtDiscount.setText(meal.getDiscount() + "%");

                loadMealRating(mealId);
                loadReviews(mealId);
                isMealFav(mealId);

                ViewPager vp_slider = findViewById(R.id.vp_slider);
                ArrayList<String> slider_image_list = new ArrayList<>();
                CircleIndicator indicator = findViewById(R.id.indicator);
                slider_image_list.add(meal.getImageOne());
                slider_image_list.add(meal.getImageTwo());
                slider_image_list.add(meal.getImageThree());

            SliderPagerAdapter sliderPagerAdapter = new SliderPagerAdapter(MealDetailsActivity.this, slider_image_list);
            vp_slider.setAdapter(sliderPagerAdapter);
            indicator.setViewPager(vp_slider);
            sliderPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());
            } catch (NullPointerException e) { }
        }

        txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlusDescriptionDialog(meal.getDescription(), meal.getIngredients());
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showFab(getApplicationContext(), fbFav);
            }
        }, 500);

        if(!Utils.isConnectedToInternet(getApplicationContext()))
            Utils.NetworkAlert(this);

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, String.format("%s est ajoutée au panier", meal.getName()),Snackbar.LENGTH_SHORT).show();
                final List<Order> cart;
                final User currentUser = Utils.getCurrentUser(getApplicationContext());
                if(currentUser.getCart() != null)
                    cart = currentUser.getCart();
                else
                    cart = new ArrayList<Order>();

                database.getReference("Users").child(Utils.getCurrentUser(getApplicationContext()).getPhone()).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean found = false;
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Order order = postSnapshot.getValue(Order.class);
                            if(mealId.equals(order.getProductId())){
                                order.setQuantity(String.valueOf(Integer.parseInt(order.getQuantity()) + 1));
                                //TODO Check Error here.
                                //TODO Then add Swipe to delete in cart and favs;
                                database.getReference("Users")
                                        .child(Utils.getCurrentUser(getApplicationContext()).getPhone())
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
                                    .child(Utils.getCurrentUser(getApplicationContext()).getPhone())
                                    .child("cart")
                                    .setValue(cart);
                        }
                        currentUser.setCart(cart);
                        Utils.setCurrentUser(getApplicationContext(), currentUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void showPlusDescriptionDialog(String description, String ingredients) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MealDetailsActivity.this);
        alertDialog.setTitle("Déscription");
        alertDialog.setMessage(String.format("%s \n\nIngrédients : \n\n%s", description, ingredients));

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void loadReviews(String mealId) {
        DatabaseReference ratingTable = database.getReference("Ratings");
        final DatabaseReference Users = database.getReference("Users");
        adapter = new FirebaseRecyclerAdapter<Rating, ReviewViewHolder>(
                Rating.class,
                R.layout.review_layout,
                ReviewViewHolder.class,
                ratingTable.orderByChild("foodId").equalTo(mealId).limitToLast(5)) {
            @Override
            protected void populateViewHolder(final ReviewViewHolder viewHolder, final Rating model, int position) {
                viewHolder.ratingBar.setStar(Integer.parseInt(model.getRateValue()));
                if(model.getComment().isEmpty())
                    viewHolder.txtComment.setVisibility(View.GONE);
                else
                    viewHolder.txtComment.setText(model.getComment());
                viewHolder.txtRatingTime.setText(Utils.EpochToDate(Long.parseLong(model.getTimeStamp()), "dd MMMM yyyy"));
                Users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            if(postSnapshot.getKey().equals(model.getUserPhone())) {
                                User user = postSnapshot.getValue(User.class);
                                viewHolder.txtUserName.setText(user.getName());
                                Picasso.with(getApplicationContext())
                                        .load(user.getThumb_image())
                                        .error(getResources().getDrawable(R.drawable.profile_icon))
                                        .into(viewHolder.imgUserProfilePic);
                            }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        recycler_review.setAdapter(adapter);
    }

    private void isMealFav(final String mealId) {
        User currentUser = Utils.getCurrentUser(getApplicationContext());
        database.getReference("Favorites").child(currentUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> favorites = new ArrayList<>();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    favorites.add(postSnapshot.getValue().toString());

                if(favorites.contains(mealId))
                    fbFav.setImageResource(R.drawable.ic_favorite);
                else
                    fbFav.setImageResource(R.drawable.ic_favorite_unchecked);
                fbFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if(favorites.contains(mealId)) {
                            database.getReference("Favorites")
                                    .child(Utils.getCurrentUser(getApplicationContext()).getPhone())
                                    .child(mealId)
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    fbFav.setImageResource(R.drawable.ic_favorite_unchecked);
                                    favorites.remove(mealId);
                                    Snackbar.make(v, meal.getName() + " est retirée des Favoris", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            favorites.add(mealId);
                            database.getReference("Favorites")
                                    .child(Utils.getCurrentUser(getApplicationContext()).getPhone())
                                    .child(mealId)
                                    .setValue(mealId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    fbFav.setImageResource(R.drawable.ic_favorite);
                                    Snackbar.make(v, meal.getName() + " est ajoutée aux Favoris", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMealRating(String mealId) {
        DatabaseReference ratingTable = database.getReference("Ratings");
        Query foodRating = ratingTable.orderByChild("foodId").equalTo(mealId);
        foodRating.addListenerForSingleValueEvent(new ValueEventListener() {
            int count =0, sum;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+= Integer.parseInt(item.getRateValue());
                    count++;
                }
                if(count != 0.0) {
                    int average = sum / count;
                    ratingBar.setStar(average);
                    txtNoReviews.setVisibility(View.GONE);
                    txtCommentsCount.setText(String.format("Commentaires (%d)", count));
                } else {
                    txtNoReviews.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meal_details, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_mycart) {
            Intent cart = new Intent(MealDetailsActivity.this, DrawerActivity.class);
            cart.putExtra("fragment", "cart");
            startActivity(cart);
            finish();
        } else if(id == android.R.id.home) {
            if(FromCart == false && FromOffers == false && FromFavs == false) {
                Utils.hideFab(getApplicationContext(), fbFav);
                Intent intent = new Intent(MealDetailsActivity.this, MealsListActivity.class);
                intent.putExtra("MenuId", menuId);
                intent.putExtra("MenuName", menuName);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void animateHeart(final FloatingActionButton view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(700);
        animation.setFillAfter(true);

        view.startAnimation(animation);

    }

    private Animation prepareAnimation(Animation animation){
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
