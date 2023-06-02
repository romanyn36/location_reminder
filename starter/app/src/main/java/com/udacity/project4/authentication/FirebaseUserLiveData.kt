package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData :LiveData<FirebaseUser?>(){
    private val firebaseAuth=FirebaseAuth.getInstance()
    // set the value of this FireUserLiveData object by hooking it up to equal the value of the current FirebaseUser

    private val authStateListener=FirebaseAuth.AuthStateListener {
        // Use the FirebaseAuth instance instantiated at the beginning of the class to get an
        //  entry point into the Firebase Authentication SDK the app is using.
        //  With an instance of the FirebaseAuth class, you can now query for the current user.
    firebaseAuth ->
                value=firebaseAuth.currentUser
    }
    // When this object has an active observer, start observing the FirebaseAuth state to see if
    // there is currently a logged in user.
    override fun onActive() {
        firebaseAuth.addAuthStateListener (authStateListener)
    }


    // When this object no longer has an active observer, stop observing the FirebaseAuth state to
    // prevent memory leaks.
    override fun onInactive() {
        firebaseAuth.removeAuthStateListener (authStateListener)
    }
}
