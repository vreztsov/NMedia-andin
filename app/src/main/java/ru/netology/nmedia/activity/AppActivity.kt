package ru.netology.nmedia.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import androidx.activity.viewModels
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.getCurrentFragment
import ru.netology.nmedia.util.getRootFragment
import ru.netology.nmedia.util.goToLogin

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationsPermission()

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        checkGoogleApiAvailability()

        requestNotificationsPermission()

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                    it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                val currentFragment = supportFragmentManager.getCurrentFragment()
                val rootFragment = supportFragmentManager.getRootFragment()

                return when (menuItem.itemId) {
                    R.id.signin, R.id.signup -> {
                        if (currentFragment != null) {
                            goToLogin(currentFragment)
                        }
                        true
                    }

                    R.id.signout -> {
                        if (currentFragment != null) {
                            AndroidUtils.hideKeyboard(currentFragment.requireView())
                            val builder: AlertDialog.Builder =
                                AlertDialog.Builder(this@AppActivity)
                            builder
                                .setMessage(getString(R.string.logout_confirm_request))
                                .setTitle(getString(R.string.action_confirm_title))
                                .setPositiveButton(getString(R.string.action_continue)) { dialog, which ->
                                    AppAuth.getInstance().removeAuth()
                                    if (currentFragment is NewPostFragment) {
                                        rootFragment.navController.navigateUp()
                                    }
                                }
                                .setNegativeButton(getString(R.string.action_cancel)) { dialog, which -> }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
                        true
                    }

                    else -> false
                }
            }
        })

    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
        }
    }
}