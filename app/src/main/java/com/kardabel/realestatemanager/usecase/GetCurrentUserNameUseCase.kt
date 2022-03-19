package com.kardabel.realestatemanager.usecase

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class GetCurrentUserNameUseCase@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {

    operator fun invoke(): String {
        return firebaseAuth.currentUser!!.displayName.toString()
    }

}