package edu.skku.map.inskkagram_2017311608;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    private StorageReference mStorageRef;

    private DrawerLayout mDrawerLayout;

    private static final int PICK_IMAGE = 777;
    private static final int PICK_IMAGE2 = 888;
    Uri currentImageUri;

    Uri postImageUri;
    ImageButton postImageBtn;
    String contents = "", tags = "";
    EditText contentsET, tagsET;
    CheckBox isPub;
    Boolean pub = false;
    Button button_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        final User user = (User)getIntent().getSerializableExtra("user");

        mPostReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.drawer);

        View headerView = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.drawer_item1:{
                        break;
                    }
                    case R.id.drawer_item2:{
                        break;
                    }
                    case R.id.drawer_item3:{
                        break;
                    }
                }
                return false;
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();
        mDrawerLayout.closeDrawer(GravityCompat.START);

        TextView tv = (TextView)headerView.findViewById(R.id.drawer_textView);
        ImageView img = (ImageView)headerView.findViewById(R.id.drawer_imageView);
        MenuItem item1 = menu.findItem(R.id.drawer_item1);
        MenuItem item2 = menu.findItem(R.id.drawer_item2);
        MenuItem item3 = menu.findItem(R.id.drawer_item3);

        img.getLayoutParams().height = 300;
        img.getLayoutParams().width = 300;
        img.setScaleType(ImageView.ScaleType.FIT_XY);

        tv.setText(user.username);

        StorageReference ref = mStorageRef.child("Profile Images").child(user.username+".jpg");
        final long ONE_MEGABYTE = 1024*1024;
        ref.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                NavigationView navigationView = (NavigationView)findViewById(R.id.drawer);
                View headerView = navigationView.getHeaderView(0);
                ImageView img = (ImageView)headerView.findViewById(R.id.drawer_imageView);
                img.setImageBitmap(bitmap);
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        item1.setTitle(user.fullname);
        item2.setTitle(user.birthday);
        item3.setTitle(user.email);

        //Post Main
        contentsET = (EditText) findViewById(R.id.editText_contents);
        tagsET = (EditText) findViewById(R.id.editText_tags);
        isPub = (CheckBox) findViewById(R.id.checkBox);
        postImageBtn = (ImageButton) findViewById(R.id.imageButton);
        postImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE2);
            }
        });

        button_submit = findViewById(R.id.button_createpost);

        button_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                contents = contentsET.getText().toString();
                tags = tagsET.getText().toString();
                pub = isPub.isChecked();

                String key = mPostReference.child("posts").push().getKey();

                if(contents.length()==0){
                    Toast.makeText(NewPostActivity.this, "Please input contents", Toast.LENGTH_SHORT).show();
                } else{
                    if(postImageUri==null){
                        Post post = new Post(user, contents, tags, "", pub);
                        Map<String, Object> postValues = post.toMap();
                        Map<String, Object> childUpdates =new HashMap<>();
                        if(pub){
                            childUpdates.put("/posts/" + key, postValues);
                            childUpdates.put("/user-posts/" + user.username +"/" + key, postValues);
                        } else{
                            childUpdates.put("/user-posts/" + user.username +"/" + key, postValues);
                        }

                        mPostReference.updateChildren(childUpdates);
                    }else{
                        StorageReference ref = mStorageRef.child("Post Images").child(postImageUri.getLastPathSegment()+".jpg");
                        UploadTask uploadTask = ref.putFile(postImageUri);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(NewPostActivity.this, "Upload success!", Toast.LENGTH_LONG);
                            }
                        });
                        Post post = new Post(user, contents, tags, postImageUri.getLastPathSegment(), pub);
                        Map<String, Object> postValues = post.toMap();
                        Map<String, Object> childUpdates =new HashMap<>();
                        if(pub){
                            childUpdates.put("/posts/" + key, postValues);
                            childUpdates.put("/user-posts/" + user.username +"/" + key, postValues);
                        } else{
                            childUpdates.put("/user-posts/" + user.username +"/" + key, postValues);
                        }

                        mPostReference.updateChildren(childUpdates);
                    }

                    Intent intent = new Intent(NewPostActivity.this, MainPageActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){
            NavigationView navigationView = (NavigationView)findViewById(R.id.drawer);
            View headerView = navigationView.getHeaderView(0);
            ImageView img = (ImageView)headerView.findViewById(R.id.drawer_imageView);
            TextView tv = (TextView)headerView.findViewById(R.id.drawer_textView);
            String name = tv.getText().toString();
            //check = true;
            currentImageUri = data.getData();
            img.setImageURI(data.getData());

            StorageReference ref = mStorageRef.child("Profile Images").child(name+".jpg");
            UploadTask uploadTask = ref.putFile(currentImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(NewPostActivity.this, "Upload success!", Toast.LENGTH_LONG);
                }
            });
        }

        if(requestCode == PICK_IMAGE2){
            postImageBtn = (ImageButton) findViewById(R.id.imageButton);
            postImageUri = data.getData();
            postImageBtn.setImageURI(data.getData());
        }
    }

}
