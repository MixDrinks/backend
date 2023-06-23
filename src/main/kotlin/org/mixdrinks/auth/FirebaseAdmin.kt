package org.mixdrinks.auth

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

fun initFirebase(firebaseAdminSdkJson: String) {
    FirebaseApp.initializeApp(
        FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(firebaseAdminSdkJson.byteInputStream()))
            .build()
    )
}
