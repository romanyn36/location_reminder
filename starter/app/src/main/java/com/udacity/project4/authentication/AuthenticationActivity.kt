package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */

class AuthenticationActivity : AppCompatActivity() {
    // Get a reference to the ViewModel scoped to this Fragment
    private val viewModel by viewModels<AuthenticationViewModel>()
    companion object {
        const val TAG = "MainFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }
    private lateinit var bind:ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=DataBindingUtil.setContentView(this,R.layout.activity_authentication)

//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
        bind.loginBtn.setOnClickListener {
            launchSignInFlow()
        }
//          TODO: If the user was authenticated, send him to RemindersActivity
                observeAuthenticationState()
//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }
    private fun observeAuthenticationState()
    {
        viewModel.authenticationState.observe(this, Observer {state->
            when(state)
            {
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED->
                {

                        Toast.makeText(this,"logged in",Toast.LENGTH_SHORT).show()
                        // go to RemindersActivity
                            val intent=Intent(this,RemindersActivity::class.java)
                            startActivity(intent)
                            finish()
                }


                AuthenticationViewModel.AuthenticationState.UNAUTHENTICATED->
                    Toast.makeText(this,"please login",Toast.LENGTH_SHORT).show()
            }
        })

    }
    private fun launchSignInFlow()
    {
    // Give users the option to sign in / register with their email or Google account.
    val provider= arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(),AuthUI.IdpConfig.GoogleBuilder().build()) //login ways
    startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(provider)
            .build()
            , SIGN_IN_RESULT_CODE
    )

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // TODO Listen to the result of the sign in process by filter for when
        //  SIGN_IN_REQUEST_CODE is passed back. Start by having log statements to know
        //  whether the user has signed in successfully
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(TAG, "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!")
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }
}
