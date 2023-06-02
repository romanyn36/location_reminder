package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthenticationViewModel :ViewModel(){
    //  creating this variable, other classes will be able to query for whether the user is logged
    //  in or not
    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
    // authentication state
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }
}