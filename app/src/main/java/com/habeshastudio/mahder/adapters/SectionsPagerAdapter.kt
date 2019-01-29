package com.habeshastudio.mahder.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.habeshastudio.mahder.activities.ChatsFragment
import com.habeshastudio.mahder.activities.FriendsFragment

/**
 * Created by kibrom on 3/28/2018.
 */

internal class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                val chatsFragment = ChatsFragment()
                return ChatsFragment()
            }
            1 -> {
                val friendsFragment = FriendsFragment()
                return FriendsFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {

            0 -> return "Chats"
            1 -> return "Friends"
            else -> return null
        }
    }
}
