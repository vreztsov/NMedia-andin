package ru.netology.nmedia.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment
import ru.netology.nmedia.activity.ImageFragment
import ru.netology.nmedia.activity.NewPostFragment

fun goToLogin(startFragment: Fragment) {
      val actionFromTo =
        when {
            (startFragment is FeedFragment) -> R.id.action_feedFragment_to_loginFragment
            (startFragment is NewPostFragment) -> R.id.action_newPostFragment_to_loginFragment
            (startFragment is ImageFragment) -> R.id.action_imageFragment_to_loginFragment
            else -> null
        }

    if (actionFromTo != null)
        startFragment.findNavController().navigate(
            actionFromTo
        )
}

fun FragmentManager.getRootFragment(): NavHostFragment =
    this.findFragmentById(
        R.id.nav_host_fragment
    ) as NavHostFragment

fun FragmentManager.getCurrentFragment(): Fragment? {
    return this
        .getRootFragment()
        .childFragmentManager
        .primaryNavigationFragment
}

