package com.openyogaland.denis.pranacoinwallet;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.jetbrains.annotations.Contract;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener,
                                                               OnClickListener,
                                                               OnPrivacyPolicyAcceptedListener
{
  // constants
  private final static String PRIVACY_POLICY_ACCEPTED = "privacy policy accepted";
  // fields
  private boolean           privacyPolicyAcceptedByUser = false;
  private SharedPreferences sharedPreferences;
  private PolicyFragment    policyFragment;
  private HomeFragment      homeFragment;
  private SendFragment      sendFragment;
  private BackupFragment    backupFragment;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  
    privacyPolicyAcceptedByUser = loadPrivacyPolicyAcceptedState();
  
    // during the first program start or until user has not accepted Privacy Policy
    if(!privacyPolicyAcceptedByUser)
    {
      policyFragment = (policyFragment == null) ? new PolicyFragment() : policyFragment;
      policyFragment.setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
      policyFragment.setOnPrivacyPolicyAcceptedListener(this);
      loadFragment(policyFragment);
    }
    homeFragment = (homeFragment == null) ? new HomeFragment() : homeFragment;
    loadFragment(homeFragment);
    BottomNavigationView navigationView = findViewById(R.id.navigation);
    navigationView.setOnNavigationItemSelectedListener(this);
    
    // TODO handle app links and application indexing
    Intent appLinkIntent = getIntent();
    appLinkIntent.getAction();
    appLinkIntent.getData();
  }
  
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item)
  {
    Fragment fragment;
    switch(item.getItemId())
    {
      default:
      case R.id.navigation_home:
        homeFragment = (homeFragment == null) ? new HomeFragment() : homeFragment;
        fragment = homeFragment;
        break;
      case R.id.navigation_send:
        sendFragment = (sendFragment == null) ? new SendFragment() : sendFragment;
        fragment = sendFragment;
        break;
      case R.id.navigation_backup:
        backupFragment = (backupFragment == null) ? new BackupFragment() : backupFragment;
        fragment = backupFragment;
        break;
    }
    return loadFragment(fragment);
  }
  
  @Contract("null -> false")
  private boolean loadFragment(Fragment fragment)
  {
    if(fragment != null)
    {
      // local variables
      FragmentTransaction transaction;
      
      // commiting transaction
      transaction = getSupportFragmentManager().beginTransaction();
      if (fragment instanceof AppCompatDialogFragment && (!fragment.isAdded()))
      {
        ((AppCompatDialogFragment) fragment).show(transaction, "");
      }
      else
      {
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
      }
      return true;
    }
    return false;
  }
  
  @Override
  public void onBackPressed()
  {
    openQuitDialog();
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    // on BACK key down
    if(keyCode == KeyEvent.KEYCODE_BACK)
    {
      openQuitDialog();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
  
  void openQuitDialog()
  {
    Builder quitDialog = new Builder(this);
    quitDialog.setTitle(R.string.quit_dialog_message);
    quitDialog.setPositiveButton(R.string.exit_yes, this);
    quitDialog.setNegativeButton(R.string.exit_no, this);
    quitDialog.show();
  }
  
  @Override
  public void onClick(DialogInterface dialog, int which)
  {
    if(which == DialogInterface.BUTTON_POSITIVE)
    {
      finish();
    }
    else if(which == DialogInterface.BUTTON_NEGATIVE)
    {
      dialog.dismiss();
    }
  }
  
  @Override
  public void onPrivacyPolicyAccepted(boolean privacyPolicyAcceptedByUser)
  {
    this.privacyPolicyAcceptedByUser = privacyPolicyAcceptedByUser;
    savePrivacyPolicyAcceptedState(privacyPolicyAcceptedByUser);
  }
  
  private void savePrivacyPolicyAcceptedState(boolean privacyPolicyAcceptedByUser)
  {
    sharedPreferences = getPreferences(Context.MODE_PRIVATE);
    Editor editor = sharedPreferences.edit();
    editor.putBoolean(PRIVACY_POLICY_ACCEPTED, privacyPolicyAcceptedByUser);
    editor.apply();
  }
  
  private boolean loadPrivacyPolicyAcceptedState()
  {
    sharedPreferences = getPreferences(Context.MODE_PRIVATE);
    return sharedPreferences.getBoolean(PRIVACY_POLICY_ACCEPTED, false);
  }
}