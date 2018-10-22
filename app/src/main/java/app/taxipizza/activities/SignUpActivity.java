package app.taxipizza.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.User;
import dmax.dialog.SpotsDialog;

public class SignUpActivity extends AppCompatActivity {

    EditText edtPhone, edtName, edtPassword;//, edtAddress;
    Button btnRegister;

    FirebaseDatabase database;
    DatabaseReference users;

    ImageButton imgClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sign_up2);

        edtPhone = findViewById(R.id.edtPhone);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        //edtAddress = findViewById(R.id.edtAddress);
        btnRegister = findViewById(R.id.btnRegister);
        imgClose = findViewById(R.id.imgClose);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isConnectedToInternet(getApplicationContext())) {
                    final AlertDialog waitingDialog = new SpotsDialog(SignUpActivity.this, R.style.CustomRegister);
                    waitingDialog.show();

                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //check if already user_phone
                            if(!dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                waitingDialog.dismiss();
                                final User user = new User(edtName.getText().toString(),
                                        edtPassword.getText().toString(),
                                        edtPhone.getText().toString(),
                                        "default",
                                        null,
                                        null,
                                        true);

                                users.child(edtPhone.getText().toString())
                                        .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Utils.setCurrentUser(getBaseContext(), user);
                                        final AlertDialog waitingDialog = new SpotsDialog(SignUpActivity.this, R.style.SignUpDialog);
                                        waitingDialog.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                waitingDialog.dismiss();
                                                startActivity(new Intent(SignUpActivity.this, DrawerActivity.class));
                                                finish();
                                            }
                                        }, 2100);
                                    }
                                });
                            }
                            else {
                                waitingDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Phone Number already registred", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    Utils.NetworkAlert(SignUpActivity.this);
                }
            }
        });
    }
}
