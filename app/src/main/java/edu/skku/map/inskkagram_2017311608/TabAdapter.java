package edu.skku.map.inskkagram_2017311608;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabAdapter extends FragmentStateAdapter{

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                PersonalFragment personalFragment = new PersonalFragment();
                return personalFragment;
            case 1:
                PublicFragment publicFragment = new PublicFragment();
                return publicFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount(){
        return 2;
    }

}
