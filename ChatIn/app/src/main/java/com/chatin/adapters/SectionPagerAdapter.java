package com.chatin.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.chatin.fragments.ChatsFragment;
import com.chatin.fragments.RequestsFragment;
import com.chatin.fragments.FriendsFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(@NonNull  FragmentManager fm) {
        super(fm);
    }

    @NonNull

    @Override
    public Fragment getItem(int position) {
        switch(position){

            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

//            case 2:
//                RequestsFragment requestsFragment = new RequestsFragment();
//                return requestsFragment;
//


            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0: return "Chats";
            case 1: return "Friends";
//            case 2: return "Requests";

            default: return null;
        }
    }
}