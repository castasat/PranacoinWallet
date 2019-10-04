package com.openyogaland.denis.pranacoin_wallet_2_0.view

import android.app.AlertDialog.Builder
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.DialogInterface.OnClickListener
import android.content.SharedPreferences.Editor
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPrivacyPolicyAcceptedListener
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import android.view.KeyEvent
import android.view.MenuItem
import androidx.fragment.app.DialogFragment.STYLE_NO_TITLE
import androidx.lifecycle.ViewModelProvider
import com.openyogaland.denis.pranacoin_wallet_2_0.application.PranacoinWallet2.PranacoinWallet2.log
import com.openyogaland.denis.pranacoin_wallet_2_0.view.GoogleSignInActivity.Companion.GOOGLE_ACCOUNT_ID
import com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel.MainViewModel

class
MainActivity
  : AppCompatActivity(),
    OnNavigationItemSelectedListener,
    OnClickListener,
    OnPrivacyPolicyAcceptedListener
{
  // fields
  private var privacyPolicyAcceptedByUser = false
  
  // fragments
  private var policyFragment : PolicyFragment? = null
  private var homeFragment : HomeFragment? = null
  private var sendFragment : SendFragment? = null
  private var backupFragment : BackupFragment? = null
  
  // architecture fields
  private lateinit var mainViewModel : MainViewModel
  
  override fun
  onCreate(savedInstanceState : Bundle?)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    mainViewModel = ViewModelProvider(this)
    .get(MainViewModel::class.java)
    
    updateViewModelGoogleAccountId()
    
    privacyPolicyAcceptedByUser = loadPrivacyPolicyAcceptedState()
    
    // during the first program start or until user has not accepted Privacy Policy
    if(!privacyPolicyAcceptedByUser)
    {
      policyFragment =
        policyFragment
        ?: PolicyFragment()
      
      policyFragment?.let {policyFragment : PolicyFragment ->
        policyFragment.setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        policyFragment.setOnPrivacyPolicyAcceptedListener(this)
        loadFragment(policyFragment)
      }
    }
    
    homeFragment =
      homeFragment
      ?: HomeFragment()
    
    homeFragment?.let {homeFragment : HomeFragment ->
      loadFragment(homeFragment)
    }
    
    val navigationView = findViewById<BottomNavigationView>(R.id.navigation)
    navigationView.setOnNavigationItemSelectedListener(this)
    
    // TODO handle app links and application indexing
    val appLinkIntent = intent
    appLinkIntent.action
    appLinkIntent.data
  }
  
  private fun
  updateViewModelGoogleAccountId()
  {
    intent.extras?.getString(GOOGLE_ACCOUNT_ID)
    ?.let {googleAccountId : String ->
      mainViewModel.googleAccountId = googleAccountId
      log("MainActivity.updateViewModelGoogleAccountId(): id = $googleAccountId")
    }
  }
  
  override fun
  onNavigationItemSelected(item : MenuItem) : Boolean
  {
    val fragment : Fragment
    
    when(item.itemId)
    {
      R.id.navigation_home   ->
      {
        fragment =
          homeFragment
          ?: HomeFragment()
      }
      R.id.navigation_send   ->
      {
        fragment =
          sendFragment
          ?: SendFragment()
      }
      R.id.navigation_backup ->
      {
        fragment =
          backupFragment
          ?: BackupFragment()
      }
      else                   ->
      {
        fragment =
          homeFragment
          ?: HomeFragment()
      }
    }
    return loadFragment(fragment)
  }
  
  private fun loadFragment(fragment : Fragment) : Boolean
  {
    val transaction = supportFragmentManager.beginTransaction()
    
    // committing transaction
    if(fragment is AppCompatDialogFragment && !fragment.isAdded)
    {
      fragment.show(transaction, "")
    }
    else
    {
      transaction.replace(R.id.fragment_container, fragment)
      transaction.commit()
    }
    return true
  }
  
  override fun onBackPressed()
  {
    openQuitDialog()
  }
  
  override fun onKeyDown(keyCode : Int, event : KeyEvent) : Boolean
  {
    // on BACK key down
    if(keyCode == KeyEvent.KEYCODE_BACK)
    {
      openQuitDialog()
      return true
    }
    return super.onKeyDown(keyCode, event)
  }
  
  private fun openQuitDialog()
  {
    val quitDialog = Builder(this)
    quitDialog.setTitle(R.string.quit_dialog_message)
    quitDialog.setPositiveButton(R.string.exit_yes, this)
    quitDialog.setNegativeButton(R.string.exit_no, this)
    quitDialog.show()
  }
  
  override fun onClick(dialog : DialogInterface, which : Int)
  {
    when(which)
    {
      BUTTON_POSITIVE -> finish()
      BUTTON_NEGATIVE -> dialog.dismiss()
      else            -> dialog.dismiss()
    }
  }
  
  override fun onPrivacyPolicyAccepted(privacyPolicyAcceptedByUser : Boolean)
  {
    this.privacyPolicyAcceptedByUser = privacyPolicyAcceptedByUser
    savePrivacyPolicyAcceptedState(privacyPolicyAcceptedByUser)
  }
  
  private fun
  savePrivacyPolicyAcceptedState(privacyPolicyAcceptedByUser : Boolean)
  {
    getPreferences(MODE_PRIVATE).edit()
    ?.let {editor : Editor ->
      editor.putBoolean(PRIVACY_POLICY_ACCEPTED, privacyPolicyAcceptedByUser)
      editor.apply()
    }
  }
  
  private fun
  loadPrivacyPolicyAcceptedState() : Boolean
  {
    return getPreferences(MODE_PRIVATE).getBoolean(PRIVACY_POLICY_ACCEPTED, false)
  }
  
  companion object
  {
    const val PRIVACY_POLICY_ACCEPTED = "privacy policy accepted"
  }
}