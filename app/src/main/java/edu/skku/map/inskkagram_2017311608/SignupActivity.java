package edu.skku.map.inskkagram_2017311608;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    String username = "", password = "", fullname = "", birthday = "", email = "";
    EditText usernameET, passwordET, fullnameET, birthdayET, emailET;
    Button button_signup;

    private ValueEventListener checkRegister = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

            while (child.hasNext()) {
                if (username.equals(child.next().getKey())) {
                    Toast.makeText(getApplicationContext(), "Please use another username", Toast.LENGTH_LONG).show();
                    mPostReference.removeEventListener(this);
                    return;
                }
            }
            postFirebaseDatabase(true);

            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameET = (EditText) findViewById(R.id.editText_signup_username);
        passwordET = (EditText) findViewById(R.id.editText_signup_password);
        fullnameET = (EditText) findViewById(R.id.editText_signup_fullname);
        birthdayET = (EditText) findViewById(R.id.editText_signup_birthday);
        emailET = (EditText) findViewById(R.id.editText_signup_email);
        button_signup = (Button) findViewById(R.id.button_signup_signup);

        mPostReference = FirebaseDatabase.getInstance().getReference("users");
        
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameET.getText().toString();
                password = passwordET.getText().toString();
                fullname = fullnameET.getText().toString();
                /*TextWatcher tw = new TextWatcher() {
                    private String current = "";
                    private String yyyymmdd = "YYYYMMDD";
                    private Calendar cal = Calendar.getInstance();
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!s.toString().equals(current)) {
                            String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                            String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                            int cl = clean.length();
                            int sel = cl;
                            for (int i = 2; i <= cl && i < 6; i += 2) {
                                sel++;
                            }
                            //Fix for pressing delete next to a forward slash
                            if (clean.equals(cleanC)) sel--;

                            if (clean.length() < 8){
                                clean = clean + yyyymmdd.substring(clean.length());
                            }else{
                                //This part makes sure that when we finish entering numbers
                                //the date is correct, fixing it otherwise
                                int day  = Integer.parseInt(clean.substring(0,2));
                                int mon  = Integer.parseInt(clean.substring(2,4));
                                int year = Integer.parseInt(clean.substring(4,8));

                                mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                                cal.set(Calendar.MONTH, mon-1);
                                year = (year<1900)?1900:(year>2100)?2100:year;
                                cal.set(Calendar.YEAR, year);
                                // ^ first set year for the line below to work correctly
                                //with leap years - otherwise, date e.g. 29/02/2012
                                //would be automatically corrected to 28/02/2012

                                day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                                clean = String.format("%02d%02d%02d",day, mon, year);
                            }

                            clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                    clean.substring(2, 4),
                                    clean.substring(4, 8));

                            sel = sel < 0 ? 0 : sel;
                            current = clean;
                            birthdayET.setText(current);
                            birthdayET.setSelection(sel < current.length() ? sel : current.length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                birthdayET.addTextChangedListener(tw);*/
                birthday = birthdayET.getText().toString();
                email = emailET.getText().toString();

                if((username.length()*password.length()*fullname.length()*birthday.length()*email.length())==0){
                    Toast.makeText(SignupActivity.this, "Please fill all blanks", Toast.LENGTH_SHORT).show();
                } else{
                    mPostReference.addListenerForSingleValueEvent(checkRegister);

                }
            }
        });

    }

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            User post = new User(username, password, fullname, birthday, email);
            postValues = post.toMap();
        }
        childUpdates.put(username, postValues);
        mPostReference.updateChildren(childUpdates);
        clearET();
    }
    public void clearET(){
        usernameET.setText("");
        passwordET.setText("");
        fullnameET.setText("");
        birthdayET.setText("");
        emailET.setText("");
        username="";
        password="";
        fullname="";
        birthday="";
        email="";
    }
}
