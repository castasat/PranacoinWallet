package com.openyogaland.denis.pranacoin_wallet_2_0.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.openyogaland.denis.pranacoin_wallet_2_0.R;
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPrivacyPolicyAcceptedListener;

public class PolicyFragment extends AppCompatDialogFragment implements OnCheckedChangeListener,
                                                                       OnClickListener
{
  // constants
  private final static String PRIVACY_POLICY_URL  = "file:///android_asset";
  private final static String PRIVACY_POLICY_NAME = "privacy_policy.html";
  // fields
  private Button                          nextButton;
  private OnPrivacyPolicyAcceptedListener onPrivacyPolicyAcceptedListener;
  
  /**
   * Called to have the fragment instantiate its user interface view.
   * This is optional, and non-graphical fragments can return null (which
   * is the default implementation).  This will be called between
   * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
   *
   * <p>If you return a View from here, you will later be called in
   * {@link #onDestroyView} when the view is being released.
   * @param inflater The LayoutInflater object that can be used to inflate
   * any views in the fragment,
   * @param container If non-null, this is the parent view that the fragment's
   * UI should be attached to.  The fragment should not add the view itself,
   * but this can be used to generate the LayoutParams of the view.
   * @param savedInstanceState If non-null, this fragment is being re-constructed
   * from a previous saved state as given here.
   * @return Return the View for the fragment's UI, or null.
   */
  @Override @Nullable
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.dialog_fragment_privacy_policy, container, false);
    // find views by ids
    WebView  webView                     = view.findViewById(R.id.webView);
    CheckBox acceptPrivacyPolicyCheckBox = view.findViewById(R.id.acceptPrivacyPolicyCheckBox);
    nextButton                           = view.findViewById(R.id.nextButton);
    // set listeners
    acceptPrivacyPolicyCheckBox.setOnCheckedChangeListener(this);
    nextButton.setOnClickListener(this);
    // load localized privacy policy
    webView.loadUrl(PRIVACY_POLICY_URL + getString(R.string.locale_asset_path) + PRIVACY_POLICY_NAME);
    return view;
  }
  
  /**
   * Called when the fragment is visible to the user and actively running.
   * This is generally tied to Activity.onResume of the containing Activity's lifecycle.
   */
  @Override
  public void onResume()
  {
    super.onResume();
    // expand the dialog width and height to the size of the window
    Window window = getDialog().getWindow();
    if (window != null)
    {
      LayoutParams params = window.getAttributes();
      params.width = LayoutParams.MATCH_PARENT;
      params.height = LayoutParams.MATCH_PARENT;
      window.setAttributes(params);
    }
  }
  
  @Override @NonNull
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
  {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    // request a window without the title
    Window window = dialog.getWindow();
    if (window != null)
    {
      window.requestFeature(Window.FEATURE_NO_TITLE);
    }
    return dialog;
  }
  
  /**
   * Called when the checked state of a compound button has changed.
   * @param buttonView The compound button view whose state has changed.
   * @param isChecked The new checked state of buttonView.
   */
  @Override
  public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked)
  {
    if (buttonView.getId() == R.id.acceptPrivacyPolicyCheckBox)
    {
      nextButton.setClickable(isChecked);
    }
  }
  
  @Override
  public void onCancel(@NonNull DialogInterface dialog)
  {
    if(getActivity() != null)
    {
      getActivity().finish();
    }
  }
  
  /**
   * Called when a view has been clicked.
   * @param view The view that was clicked.
   */
  @Override
  public void onClick(@NonNull View view)
  {
    if (view.getId() == R.id.nextButton)
    {
      onPrivacyPolicyAcceptedListener.onPrivacyPolicyAccepted(true);
      this.dismiss();
    }
  }
  
  // setter
  public void setOnPrivacyPolicyAcceptedListener(
      OnPrivacyPolicyAcceptedListener onPrivacyPolicyAcceptedListener)
  {
    this.onPrivacyPolicyAcceptedListener = onPrivacyPolicyAcceptedListener;
  }
}

