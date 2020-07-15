package edu.skku.map.inskkagram_2017311608;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.skku.map.inskkagram_2017311608.R;

public class PublicFragment extends Fragment {
    private ListView listView;
    private ArrayList<Post> posts;
    private ListViewAdapter adapter;

    private DatabaseReference mPostReference;

    public PublicFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_public, container, false);
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts");

        User user = (User)getActivity().getIntent().getSerializableExtra("user");

        listView = (ListView)rootView.findViewById(R.id.list_public);
        posts = new ArrayList<Post>();

        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Post get = postSnapshot.getValue(Post.class);
                    posts.add(new Post(get.user, get.contents, get.tags, get.imguri, get.pub));
                }
                adapter = new ListViewAdapter(getActivity(), posts);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rootView;
    }
}
