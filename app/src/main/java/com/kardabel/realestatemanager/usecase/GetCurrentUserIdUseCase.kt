package com.kardabel.realestatemanager.usecase

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {

    operator fun invoke(): String {
        return firebaseAuth.currentUser!!.uid
    }
}