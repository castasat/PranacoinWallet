package com.openyogaland.denis.pranacoin_wallet_2_0.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.PranacoinWallet2.log

class
GoogleSignInActivity
  : AppCompatActivity()
{
  // fields
  private lateinit var googleSignInButton : SignInButton
  private lateinit var googleSignInOptions : GoogleSignInOptions
  private lateinit var googleSignInClient : GoogleSignInClient
  private lateinit var googleSignInAccount : GoogleSignInAccount
  
  override fun
  onCreate(savedInstanceState : Bundle?)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.google_sign_in)
    
    googleSignInButton = findViewById(R.id.googleSignInButton)
    googleSignInButton.setSize(SignInButton.SIZE_STANDARD)
    
    googleSignInButton.setOnClickListener {
      signIn()
    }
    
    googleSignInOptions =
      GoogleSignInOptions
      .Builder(DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.server_client_id))
      .requestEmail()
      .requestId()
      .build()
    
    googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
  }
  
  override fun
  onStart()
  {
    super.onStart()
    
    
    if(GoogleSignIn
      .getLastSignedInAccount(this) == null)
    {
      // TODO sign in
    }
    
    GoogleSignIn
    .getLastSignedInAccount(this)
    ?.let {googleSignInAccount : GoogleSignInAccount ->
      this.googleSignInAccount = googleSignInAccount
      updateUI(googleSignInAccount)
    }
  }
  
  private fun
  updateUI(googleSignInAccount : GoogleSignInAccount)
  {
    log("GoogleSignInActivity.updateUI")
    val mainActivityIntent = Intent(this, MainActivity::class.java)
    startActivity(mainActivityIntent)
  }
  
  private fun
  signIn()
  {
    log("GoogleSignInActivity.signIn()")
    val signInIntent = googleSignInClient.signInIntent
    startActivityForResult(signInIntent, RC_SIGN_IN)
  }
  
  override fun
  onActivityResult(requestCode : Int,
                   resultCode : Int,
                   data : Intent?)
  {
    super.onActivityResult(requestCode, resultCode, data)
    
    log("GoogleSignInActivity.onActivityResult()")
    
    if(requestCode == RC_SIGN_IN)
    {
      val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
      handleSignInResult(task)
    }
  }
  
  private fun
  handleSignInResult(completedTask : Task<GoogleSignInAccount>)
  {
  
    log("GoogleSignInActivity.handleSignInResult()")
    
    try
    {
      completedTask
      .getResult(ApiException::class.java)
      ?.let {googleSignInAccount : GoogleSignInAccount ->
        this.googleSignInAccount = googleSignInAccount
        
        log("GoogleSignInActivity.handleSignInResult(): success id = ${googleSignInAccount.id}")
        updateUI(googleSignInAccount)
      }
    }
    catch(e : ApiException)
    {
      log("GoogleSignInActivity.handleSignInResult(): $e")
      e.printStackTrace()
      // TODO sign in
    }
  }
  
  companion object
  {
    @JvmStatic
    val RC_SIGN_IN = 108
  }
}