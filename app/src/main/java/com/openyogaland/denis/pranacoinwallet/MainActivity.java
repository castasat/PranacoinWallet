package com.openyogaland.denis.pranacoinwallet;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.jetbrains.annotations.Contract;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener,
                                                               OnClickListener
{
  // fields
  private HomeFragment    homeFragment;
  // TODO private HistoryFragment historyFragment;
  private SendFragment    sendFragment;
  // TODO private RestoreFragment restoreFragment;
  private BackupFragment  backupFragment;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // during the first program start
    if(savedInstanceState == null)
    {
      homeFragment = (homeFragment == null) ? new HomeFragment() : homeFragment;
      loadFragment(homeFragment);
    }
    
    BottomNavigationView navigationView = findViewById(R.id.navigation);
    navigationView.setOnNavigationItemSelectedListener(this);
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
      // TODO case R.id.navigation_history:
        // historyFragment = (historyFragment == null) ? new HistoryFragment() : historyFragment;
        // fragment = historyFragment;
        // break;
      case R.id.navigation_send:
        sendFragment = (sendFragment == null) ? new SendFragment() : sendFragment;
        fragment = sendFragment;
        break;
      // TODO case R.id.navigation_restore:
        // restoreFragment = (restoreFragment == null) ? new RestoreFragment() : restoreFragment;
        // fragment = restoreFragment;
        // break;
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
      transaction.replace(R.id.fragment_container, fragment);
      transaction.commit();
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
  
  private void openQuitDialog()
  {
    Builder quitDialog = new Builder(this);
    quitDialog.setTitle(R.string.quit_dialog_message);
    quitDialog.setPositiveButton(R.string.exit_yes, this);
    quitDialog.setNegativeButton(R.string.exit_no, this);
    quitDialog.show();
  }
  
  @Override
  public void onClick(DialogInterface dialogInstance, int which)
  {
    if(which == DialogInterface.BUTTON_POSITIVE)
    {
      finish();
    }
    else if(which == DialogInterface.BUTTON_NEGATIVE)
    {
      dialogInstance.dismiss();
    }
  }
}