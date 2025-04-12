package com.example.natksport;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentPagerAdapter extends FragmentStateAdapter {

    public FragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NewsFragment();
            case 1:
                return new PlayersFragment();
            //case 2:
            //    return new CoachesFragment();
            case 3:
                return new UsersFragment();
            default:
                return new NewsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
