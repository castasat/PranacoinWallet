@file:Suppress("DEPRECATION")

package io.github.castasat.pranacoin_wallet.view.activity

import android.app.AlertDialog.Builder
import android.content.DialogInterface
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
import io.github.castasat.pranacoin_wallet.R
import io.github.castasat.pranacoin_wallet.listener.OnPrivacyPolicyAcceptedListener
import io.github.castasat.pranacoin_wallet.view.activity.GoogleSignInActivity.Companion.GOOGLE_ACCOUNT_ID
import io.github.castasat.pranacoin_wallet.view.activity.GoogleSignInActivity.Companion.GOOGLE_EMAIL
import io.github.castasat.pranacoin_wallet.view.dialog.AlertDialogUtil.showAlertDialog
import io.github.castasat.pranacoin_wallet.view.dialog.PolicyDialog
import io.github.castasat.pranacoin_wallet.view.fragment.BackupFragment
import io.github.castasat.pranacoin_wallet.view.fragment.HomeFragment
import io.github.castasat.pranacoin_wallet.view.fragment.SendFragment
import io.github.castasat.pranacoin_wallet.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener,
    OnPrivacyPolicyAcceptedListener {
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

        mainViewModel.errorLiveData.observe(this) { errorEventWrapper ->
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

        homeFragment = homeFragment ?: HomeFragment()
        homeFragment?.let { homeFragment: HomeFragment ->
            loadFragment(homeFragment)
        }
        val navigationView = findViewById<BottomNavigationView>(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(this)
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

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
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
            setPositiveButton(R.string.exit_yes) { _, _ ->
                finish()
            }
            setNegativeButton(R.string.exit_no) { dialog: DialogInterface, _ -> dialog.dismiss() }
            show()
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