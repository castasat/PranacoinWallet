package com.openyogaland.denis.pranacoin_wallet_2_0.view.activity

import android.app.AlertDialog.Builder
import android.content.DialogInterface
import android.content.DialogInterface.*
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment.STYLE_NO_TITLE
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPrivacyPolicyAcceptedListener
import com.openyogaland.denis.pranacoin_wallet_2_0.view.activity.GoogleSignInActivity.Companion.GOOGLE_ACCOUNT_ID
import com.openyogaland.denis.pranacoin_wallet_2_0.view.activity.GoogleSignInActivity.Companion.GOOGLE_EMAIL
import com.openyogaland.denis.pranacoin_wallet_2_0.view.dialog.AlertDialogUtil.showAlertDialog
import com.openyogaland.denis.pranacoin_wallet_2_0.view.dialog.PolicyDialog
import com.openyogaland.denis.pranacoin_wallet_2_0.view.fragment.BackupFragment
import com.openyogaland.denis.pranacoin_wallet_2_0.view.fragment.HomeFragment
import com.openyogaland.denis.pranacoin_wallet_2_0.view.fragment.SendFragment
import com.openyogaland.denis.pranacoin_wallet_2_0.viewmodel.MainViewModel

// TODO 0007 navigate across fragments with swipe to the left and to the right
class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener,
    OnClickListener, OnPrivacyPolicyAcceptedListener {
    private var privacyPolicyAcceptedByUser = false
    private var policyDialog: PolicyDialog? = null
    private var homeFragment: HomeFragment? = null
    private var sendFragment: SendFragment? = null
    private var backupFragment: BackupFragment? = null
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateViewModelGoogleAccount(mainViewModel)
        privacyPolicyAcceptedByUser = loadPrivacyPolicyAcceptedState()

        // during the first program start or until user has not accepted Privacy Policy
        if (!privacyPolicyAcceptedByUser) {
            policyDialog = policyDialog ?: PolicyDialog()
            policyDialog?.let { policyDialog: PolicyDialog ->
                policyDialog.setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
                policyDialog.setOnPrivacyPolicyAcceptedListener(this)
                loadFragment(policyDialog)
            }
        }

        mainViewModel.errorLiveData.observe(
            this,
            { errorEventWrapper ->
                errorEventWrapper
                    .getEventIfNotHandled()
                    ?.let { errorMessage ->
                        showAlertDialog(
                            this,
                            getString(R.string.error),
                            errorMessage
                        )
                    }
            }
        )

        homeFragment = homeFragment ?: HomeFragment()
        homeFragment?.let { homeFragment: HomeFragment ->
            loadFragment(homeFragment)
        }
        val navigationView = findViewById<BottomNavigationView>(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(this)
        // TODO 0015 handle app links
        // TODO 0016 handle application indexing
        val appLinkIntent = intent
        appLinkIntent.action
        appLinkIntent.data
    }

    private fun updateViewModelGoogleAccount(mainViewModel: MainViewModel) {
        intent.extras?.apply {
            getString(GOOGLE_ACCOUNT_ID)?.let { googleAccountId: String ->
                mainViewModel.googleAccountId = googleAccountId
            }
            getString(GOOGLE_EMAIL)?.let { googleEmail ->
                mainViewModel.googleEmail = googleEmail
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment: Fragment =
            when (item.itemId) {
                R.id.navigation_home ->
                    homeFragment ?: HomeFragment()
                R.id.navigation_send ->
                    sendFragment ?: SendFragment()
                R.id.navigation_backup ->
                    backupFragment ?: BackupFragment()
                else ->
                    homeFragment ?: HomeFragment()
            }
        return loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        if (fragment is AppCompatDialogFragment && !fragment.isAdded) {
            fragment.show(transaction, "")
        } else {
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
        return true
    }

    override fun onBackPressed() {
        openQuitDialog()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_BACK) {
            openQuitDialog()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun openQuitDialog() {
        with(Builder(this)) {
            setTitle(R.string.quit_dialog_message)
            setPositiveButton(R.string.exit_yes, this@MainActivity)
            setNegativeButton(R.string.exit_no, this@MainActivity)
            show()
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            BUTTON_POSITIVE -> finish()
            BUTTON_NEGATIVE -> dialog.dismiss()
            else -> dialog.dismiss()
        }
    }

    override fun onPrivacyPolicyAccepted(privacyPolicyAcceptedByUser: Boolean) {
        this.privacyPolicyAcceptedByUser = privacyPolicyAcceptedByUser
        savePrivacyPolicyAcceptedState(privacyPolicyAcceptedByUser)
    }

    private fun savePrivacyPolicyAcceptedState(privacyPolicyAcceptedByUser: Boolean) {
        getPreferences(MODE_PRIVATE).edit()
            ?.let { editor: Editor ->
                editor.putBoolean(PRIVACY_POLICY_ACCEPTED, privacyPolicyAcceptedByUser)
                editor.apply()
            }
    }

    private fun loadPrivacyPolicyAcceptedState(): Boolean {
        return getPreferences(MODE_PRIVATE).getBoolean(PRIVACY_POLICY_ACCEPTED, false)
    }

    companion object {
        const val PRIVACY_POLICY_ACCEPTED = "privacy policy accepted"
    }
}