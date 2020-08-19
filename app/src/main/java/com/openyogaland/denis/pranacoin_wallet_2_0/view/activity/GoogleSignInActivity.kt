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
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.Companion.log

class GoogleSignInActivity : AppCompatActivity() {
    private lateinit var googleSignInButton: SignInButton
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInAccount: GoogleSignInAccount

    // TODO 0013 add sign-in possibilities
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.google_sign_in)
        log("GoogleSignInActivity.onCreate()")
        googleSignInOptions = Builder(DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .requestId()
                .build()
        log("GoogleSignInActivity.onCreate(): googleSignInOptions = $googleSignInOptions")
        googleSignInClient = getClient(this, googleSignInOptions)
        log("GoogleSignInActivity.onCreate(): googleSignInClient = $googleSignInClient")
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
                this.googleSignInAccount = googleSignInAccount
                updateUI(googleSignInAccount)
            }
    }

    private fun updateUI(googleSignInAccount: GoogleSignInAccount) {
        log("GoogleSignInActivity.updateUI")
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        val googleAccountId = googleSignInAccount.id
        log("GoogleSignInActivity.updateUI(): googleAccountId = $googleAccountId")
        mainActivityIntent.putExtra(GOOGLE_ACCOUNT_ID, googleAccountId)
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
            e.printStackTrace()
            Toast.makeText(this, "$e", LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        val RC_SIGN_IN = 108

        @JvmStatic
        val GOOGLE_ACCOUNT_ID = "google_account_id"
    }
}