package com.example.natksport;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentPagerAdapter extends FragmentStateAdapter {

    private final String userRole;

    public FragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity, String userRole) {
        super(fragmentActivity);
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = new NewsFragment();
                break;
            case 1:
                PlayersFragment playersFragment = new PlayersFragment();
                playersFragment.setUserRole(userRole);
                fragment = playersFragment;

                break;
            //case 2:
                //fragment = new CoachesFragment();
              //  break;
            case 3:
                if ("Администратор".equals(userRole)) {
                    fragment = new UsersFragment();
                } else {

                    fragment = new NewsFragment();
                }
                break;
            default:
                fragment = new NewsFragment();
                break;
        }

        if (fragment instanceof RoleAwareFragment) {
            ((RoleAwareFragment) fragment).setUserRole(userRole);
        }

        return fragment;
    }

    @Override
    public int getItemCount() {

        return "Администратор".equals(userRole) ? 4 : 3;
    }
}
