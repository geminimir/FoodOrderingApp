package app.taxipizza.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import app.taxipizza.R;
import app.taxipizza.Remote.IGoogleAPI;
import app.taxipizza.Remote.RetrofitClient;
import app.taxipizza.models.Food;
import app.taxipizza.models.User;


public class Utils {

    public static final String FACEBOOK_LOGIN = "fbLogin" + new Random().nextInt(900) + 100;

    public static final LatLng TaxiPizzaLocation = new LatLng(35.8484589, 10.605323);

    public static String getRandomHexString(){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < 5){
            sb.append(Integer.toHexString(r.nextInt()).toUpperCase());
        }

        return sb.toString().substring(0, 5);
    }

    public static User getCurrentUser(Context context) {
        try {
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
            String json = sharedPreferences.getString("currentUser", "");
            return gson.fromJson(json, User.class);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static void setCurrentUser(Context context,  User currentUser) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentUser", gson.toJson(currentUser));
        editor.apply();
    }

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "placée";
        else if(status.equals("2"))
            return "en route";
        else if(status.equals("3"))
            return "presque là";
        else if(status.equals("4"))
            return "livrée";
        else return "";

    }

    public static final String baseUrl = "https://maps.googleapis.com";

    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseUrl).create(IGoogleAPI.class);
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null) {
                for(int i=0; i< info.length; i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static void NetworkAlert(final Context context) {
        new AwesomeWarningDialog(context)
                .setTitle("Pas de Connection Internet")
                .setMessage("Assurez-vous que votre appareil est connecté à Internet.")
                .setColoredCircle(R.color.colorPrimary)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.black)
                .setCancelable(false)
                .setButtonText("Réessayer")
                .setButtonBackgroundColor(R.color.colorPrimary)
                .setButtonTextColor(android.R.color.black)
                .setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        Activity current = (Activity)context;
                        context.startActivity(current.getIntent());
                        ((Activity) context).finish();
                    }
                })
                .show();
    }

    public static void showFab(Context context, final FloatingActionButton fab) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.fab_in);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.startAnimation(animation);
                fab.setVisibility(View.VISIBLE);
            }
        }, 500);

    }

    public static void hideFab(Context context, FloatingActionButton fab) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fab_out);
        fab.startAnimation(animation);
        fab.setVisibility(View.INVISIBLE);
    }

    public static String EpochToDate(long time, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.FRANCE);
        return format.format(new Date(time));

    }

    public static void NumberOfOffers(final TextView txtOffersCount) {

        final List<Food> offersList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference offers = database.getReference("Meals");

        offers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food food = postSnapshot.getValue(Food.class);
                    if(food.getDiscount() != null && !food.getDiscount().equals("0"))
                        offersList.add(food);
                }
                txtOffersCount.setText(String.valueOf(offersList.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void NumberOfItemsInCart(Context context, final TextView txtCartCount) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cart = database.getReference("Users").child(getCurrentUser(context).getPhone());

        cart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getCart() != null) {
                    int cartSize = user.getCart().size();
                    txtCartCount.setText(String.valueOf(cartSize));
                } else {
                    txtCartCount.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static Bitmap getDeliveryLocationMarker(Context context) {
        Resources resources = context.getResources();
        int height = (int) resources.getDimension(R.dimen.height_76);
        int width = (int) resources.getDimension(R.dimen.width_76);
        BitmapDrawable bitmapdraw=(BitmapDrawable)context.getResources().getDrawable(R.drawable.delivery_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap marker =  Bitmap.createScaledBitmap(b, width, height, false);
        return marker;
    }

    public static Bitmap getGuyLocationMarker(Context applicationContext) {
        Resources resources = applicationContext.getResources();
        int height = (int) resources.getDimension(R.dimen.height_76);
        int width = (int) resources.getDimension(R.dimen.width_63);
        BitmapDrawable bitmapdraw=(BitmapDrawable)applicationContext.getResources().getDrawable(R.drawable.meal_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap marker =  Bitmap.createScaledBitmap(b, width, height, false);
        return marker;
    }

}
