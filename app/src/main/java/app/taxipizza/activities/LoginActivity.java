package app.taxipizza.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.User;
import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    TextView txtCreateAccount;
    Button btnSignIn;
    LinearLayout btnFacebook;

    FirebaseDatabase database;
    DatabaseReference users;

    EditText edtPhone, edtPassword;

    CallbackManager FbcallbackManager;
    LoginManager fbLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        btnSignIn = findViewById(R.id.btnSingIn);
        btnFacebook = findViewById(R.id.btnFacebook);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Utils.isConnectedToInternet(getApplicationContext())) {
                    users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get User Information
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());
                                if (user.getPassword() != null) {
                                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                                        Utils.setCurrentUser(getBaseContext(), user);

                                        final AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
                                        waitingDialog.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                waitingDialog.dismiss();
                                                startActivity(new Intent(LoginActivity.this, DrawerActivity.class));
                                                finish();
                                            }
                                        }, 2100);
                                    } else
                                        Snackbar.make(v, "Le Téléphone ou le mot de passe est incorrect", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Snackbar.make(v, "Une erreur s'est produite, Veuillez réessayer", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Utils.NetworkAlert(LoginActivity.this);
                }
            }
        });


        printKeyHash();
        FacebookLoginSetup();

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isConnectedToInternet(getApplicationContext()))
                    fbLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                else
                    Utils.NetworkAlert(LoginActivity.this);
            }
        });

        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

    }

    private void FacebookLoginSetup() {
        FbcallbackManager = CallbackManager.Factory.create();
        fbLoginManager = LoginManager.getInstance();
        fbLoginManager.registerCallback(FbcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("fblogin", accessToken);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name = response.getJSONObject().getString("name");
                            String password = Utils.FACEBOOK_LOGIN;
                            String thumb_image = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                            Log.d("fblogin", thumb_image);
                            SocialMediaLogin(name, password, thumb_image);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FbcallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    boolean userExists = false;
    private void SocialMediaLogin(final String name, final String password, final String thumb_image) {

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if(user.getName().equals(name) && user.getPassword().contains("fbLogin")) {
                        userExists = true;
                        Utils.setCurrentUser(getApplicationContext(), user);
                    }
                }
                if(!userExists) {
                    showPhoneNumberDialog(name, password, thumb_image);
                } else {
                    startActivity(new Intent(LoginActivity.this, DrawerActivity.class));
                    finish();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("app.foodapp", PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void showPhoneNumberDialog(final String name, final String password, final String thumb_image) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Encore une étape !");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView  = inflater.inflate(R.layout.dialog_phone_number, null);
        final EditText edtPhoneNumber = dialogView.findViewById(R.id.edtPhone);

        alertDialog.setView(dialogView);

        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.child(edtPhoneNumber.getText().toString()).exists()) {
                            final User user = new User(
                                    name,
                                    password,
                                    edtPhoneNumber.getText().toString(),
                                    thumb_image,
                                    null, null,
                                    true);
                            users.child(edtPhoneNumber.getText().toString()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Utils.setCurrentUser(getBaseContext(), user);
                                    final AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.SignUpDialog);
                                    waitingDialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            waitingDialog.dismiss();
                                            startActivity(new Intent(LoginActivity.this, DrawerActivity.class));
                                            finish();
                                        }
                                    }, 2100);
                                }
                            });
                        } else {
                            users.child(edtPhoneNumber.getText().toString()).removeValue();
                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.child(edtPhoneNumber.getText().toString()).getValue(User.class);
                                    Utils.setCurrentUser(getApplicationContext(), user);
                                    final AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
                                    waitingDialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            waitingDialog.dismiss();
                                            startActivity(new Intent(LoginActivity.this, DrawerActivity.class));
                                            finish();
                                        }
                                    }, 2100);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        alertDialog.show();
    }

}
