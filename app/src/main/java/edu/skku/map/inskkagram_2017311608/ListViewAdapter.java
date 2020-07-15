package edu.skku.map.inskkagram_2017311608;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private StorageReference mStorageRef;

    private LayoutInflater inflater;
    private Activity activity;
    private ArrayList<Post> posts;
    String tag_splits[];
    String tag_sentence = "";

    public ListViewAdapter(FragmentActivity activity, ArrayList<Post> posts) {
        this.activity = activity;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        if(inflater==null){
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if ( convertView == null ) {
            convertView = inflater.inflate(R.layout.item_post, parent, false);
        }

        Post post = posts.get(position);

        final ImageView UserImg = (ImageView)convertView.findViewById(R.id.postUserimg);
        TextView UserName = (TextView)convertView.findViewById(R.id.postUsername);
        TextView Contents = (TextView)convertView.findViewById(R.id.postContents);
        TextView Tags = (TextView)convertView.findViewById(R.id.postTags);
        final ImageView Img = (ImageView)convertView.findViewById(R.id.postImg);

        final long ONE_MEGABYTE = 1024*1024;

        if(post.imguri==""){
            Img.setVisibility(View.GONE);
        } else{
            Img.getLayoutParams().height = 800;
            Img.setScaleType(ImageView.ScaleType.CENTER_CROP);

            StorageReference ref2 = mStorageRef.child("Post Images").child(post.imguri+".jpg");
            ref2.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Img.setImageBitmap(bitmap);
                }
            });
        }

        UserImg.getLayoutParams().height = 150;
        UserImg.getLayoutParams().width = 150;
        UserImg.setScaleType(ImageView.ScaleType.FIT_XY);

        UserName.setText(post.user.username);
        Contents.setText(post.contents);

        tag_splits = post.tags.split("\\s+");

        for(String i : tag_splits){
            tag_sentence += "#"+i+" ";
        }
        Tags.setText(tag_sentence);
        tag_sentence="";

        StorageReference ref = mStorageRef.child("Profile Images").child(post.user.username+".jpg");

        ref.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                UserImg.setImageBitmap(bitmap);
            }
        });

        return convertView;
    }

}
