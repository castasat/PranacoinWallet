@file:Suppress("NAME_SHADOWING")

package com.openyogaland.denis.pranacoin_wallet_2_0.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.SignInButton.SIZE_WIDE
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.crashlytics
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log

class GoogleSignInActivity : AppCompatActivity() {

    // Google Auth
    private lateinit var googleSignInButton: SignInButton
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInAccount: GoogleSignInAccount

    // Google Analytics
    private lateinit var analytics: FirebaseAnalytics

    // TODO 0013-1 add email sign-in possibility
    // TODO 0013-3 add phone sign-in possibility
    // TODO 0013-4 add facebook sign-in possibility
    // TODO 0013-7 add Apple sign-in possibility
    // TODO 0013-8 add Anonymous sign-in possibility
    // TODO 0013-6 add github sign-in possibility
    // TODO 0013-5 add twitter sign-in possibility
    // TODO 0013-2 add email link sign-in possibility (no password)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.google_sign_in)

        log("GoogleSignInActivity.onCreate()")

        // Google Analytics initialization
        analytics = Firebase.analytics

        // Google Auth initialization
        googleSignInOptions = Builder(DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.server_client_id))
            .requestId()
            .build()

        googleSignInClient = getClient(this, googleSignInOptions)
        googleSignInButton = findViewById(R.id.googleSignInButton)
        googleSignInButton.setSize(SIZE_WIDE)
        googleSignInButton.setOnClickListener { signIn() }
    }

    override fun onStart() {
        super.onStart()
        log("GoogleSignInActivity.onStart()")
        getLastSignedInAccount(this)
            ?.let { googleSignInAccount: GoogleSignInAccount ->
                log("GoogleSignInActivity.onStart(): googleSignInAccount = $googleSignInAccount")
                FirebaseCrashlytics.getInstance().setUserId("$googleSignInAccount")
                this.googleSignInAccount = googleSignInAccount
                updateUI(googleSignInAccount)
            }
    }

    private fun updateUI(googleSignInAccount: GoogleSignInAccount) {
        log("GoogleSignInActivity.updateUI")
        val mainActivityIntent = Intent(this, MainActivity::class.java)

        val googleAccountId = googleSignInAccount.id
        val googleEmail = googleSignInAccount.email
        // TODO 0034 possibility to switch accounts
        /*googleSignInAccount.id
        googleSignInAccount.displayName
        googleSignInAccount.email
        googleSignInAccount.givenName
        googleSignInAccount.familyName
        googleSignInAccount.photoUrl*/

        googleAccountId?.let { googleAccountId ->
            googleEmail?.let { googleEmail ->
                FirebaseCrashlytics.getInstance().apply {
                    setCustomKey(GOOGLE_ACCOUNT_ID, googleAccountId)
                    setCustomKey(GOOGLE_EMAIL, googleEmail)
                }
            }
        }
        mainActivityIntent.putExtra(GOOGLE_ACCOUNT_ID, googleAccountId)
        mainActivityIntent.putExtra(GOOGLE_EMAIL, googleEmail)
        startActivity(mainActivityIntent)
    }

    private fun signIn() {
        log("GoogleSignInActivity.signIn()")
        val signInIntent = googleSignInClient.signInIntent
        log("GoogleSignInActivity.signIn(): signInIntent = $signInIntent")
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        log("GoogleSignInActivity.onActivityResult()")
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        log("GoogleSignInActivity.handleSignInResult()")
        try {
            completedTask.getResult(ApiException::class.java)
                ?.let { googleSignInAccount: GoogleSignInAccount ->
                    this.googleSignInAccount = googleSignInAccount
                    log(
                        "GoogleSignInActivity.handleSignInResult(): " +
                                "success id = ${googleSignInAccount.id}"
                    )
                    updateUI(googleSignInAccount)
                }
        } catch (e: ApiException) {
            log("GoogleSignInActivity.handleSignInResult(): $e")
            crashlytics(e)
            e.printStackTrace()
            Toast.makeText(this, "$e", LENGTH_LONG).show()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 108
        const val GOOGLE_EMAIL = "google email"
        const val GOOGLE_ACCOUNT_ID = "google account id"
    }
}