package edu.skku.map.inskkagram_2017311608;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    String username = "", password = "";
    EditText usernameET, passwordET;
    Button button_login;

    private ValueEventListener checkLogin = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    User usersBean = user.getValue(User.class);

                    if (usersBean.password.equals(password)) {
                        Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
                        intent.putExtra("user", usersBean);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");

        usernameET = findViewById(R.id.editText_login_username);
        usernameET.setText(name);

        passwordET = findViewById(R.id.editText_login_password);

        button_login = findViewById(R.id.button_login_login);
        TextView textView_signup = findViewById(R.id.textView_login_signup);

        button_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                username = usernameET.getText().toString();
                password = passwordET.getText().toString();

                if((username.length())==0){
                    Toast.makeText(LoginActivity.this, "Wrong Username", Toast.LENGTH_SHORT).show();
                }
                else{
                    mPostReference.child("users").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(checkLogin);
                }
            }
        });

        textView_signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
