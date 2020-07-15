package edu.skku.map.inskkagram_2017311608;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity{

    private DatabaseReference mPostReference;
    private StorageReference mStorageRef;

    private DrawerLayout mDrawerLayout;
    private TabLayout tabLayout;
    private ViewPager2 mViewPager2;
    private TabAdapter mTabAdapter;

    private static final int PICK_IMAGE = 777;
    Uri currentImageUri;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        final User user = (User)getIntent().getSerializableExtra("user");

        mPostReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.username);
        mStorageRef = FirebaseStorage.getInstance().getReference("Profile Images");

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

        StorageReference ref = mStorageRef.child(user.username+".jpg");
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

        ViewPager2 viewPager2 = findViewById(R.id.pager);
        viewPager2.setAdapter(new TabAdapter(this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Personal");
                        break;
                    case 1:
                        tab.setText("Public");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

        //Intent intent = new Intent(MainPageActivity.this, TabAdapter.class);
        //intent.putExtra("user", user);

        Intent intent1 = new Intent(MainPageActivity.this, PersonalFragment.class);
        intent1.putExtra("user", user);

        Intent intent2 = new Intent(MainPageActivity.this, PublicFragment.class);
        intent2.putExtra("user", user);

        ImageButton button = findViewById(R.id.button_addpost);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainPageActivity.this, NewPostActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

            StorageReference ref = mStorageRef.child(name+".jpg");
            UploadTask uploadTask = ref.putFile(currentImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainPageActivity.this, "Upload success!", Toast.LENGTH_LONG);
                }
            });
        }
    }

}
