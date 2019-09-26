package com.openyogaland.denis.pranacoin_wallet_2_0.view

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.google.zxing.WriterException
import com.openyogaland.denis.pranacoin_wallet_2_0.async.GetBalanceFromNetTask
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnBalanceObtainedListener
import com.openyogaland.denis.pranacoin_wallet_2_0.listener.OnPublicAddressObtainedListener
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import com.openyogaland.denis.pranacoin_wallet_2_0.domain.QRCodeDomain.Companion.textToImageEncode

class
HomeFragment
  : Fragment(),
    OnPublicAddressObtainedListener,
    OnBalanceObtainedListener
{
  // fields
  private var sharedPreferences : SharedPreferences? = null
  private var editor : Editor? = null
  private var publicAddress = ""
  private var balance = ""
  private var publicAddressTextView : TextView? = null
  private var balanceAmountTextView : TextView? = null
  private var privateAddressQRCodeProgressBar : ProgressBar? = null
  private var publicAddressProgressBar : ProgressBar? = null
  private var publicAddressQRCodeImageView : ImageView? = null
  private var publicAddressQRCodeProgressBar : ProgressBar? = null
  
  override fun onCreateView(inflater : LayoutInflater,
                            container : ViewGroup?,
                            savedInstanceState : Bundle?) : View?
  {
    // local variables
    val getBalanceFromNetTask : GetBalanceFromNetTask
    
    // inflate fragment layout
    val view = inflater.inflate(R.layout.fragment_home, container, false)
    
    // find views by ids
    publicAddressTextView = view.findViewById(R.id.publicAddressTextView)
    balanceAmountTextView = view.findViewById(R.id.balanceAmountTextView)
    privateAddressQRCodeProgressBar = view.findViewById(R.id.balanceProgressBar)
    publicAddressProgressBar = view.findViewById(R.id.publicAddressProgressBar)
    publicAddressQRCodeImageView = view.findViewById(R.id.publicAddressQRCodeImageView)
    publicAddressQRCodeProgressBar = view.findViewById(R.id.publicAddressQRCodeProgressBar)
    
    // setting progress bars visible and imageView not-visible
    publicAddressProgressBar!!.visibility = View.VISIBLE
    privateAddressQRCodeProgressBar!!.visibility = View.VISIBLE
    publicAddressQRCodeImageView!!.visibility = View.GONE
    publicAddressQRCodeProgressBar!!.visibility = View.VISIBLE
    
    // show public address and balance
    context?.let {context : Context ->
      /*TODO String idOfUser = PranacoinWallet2.getInstance(context).getIdOfUser();
     
     if(PranacoinWallet2.hasConnection(context))
     {
       GetPublicAddressFromNetTask getPublicAddressFromNetTask =
           new GetPublicAddressFromNetTask(context, idOfUser);
       getPublicAddressFromNetTask.setOnPublicAddressObtainedListener(this);
   
       getBalanceFromNetTask = new GetBalanceFromNetTask(context, idOfUser);
       getBalanceFromNetTask.setOnBalanceObtainedListener(this);
     }
     else if(!PranacoinWallet2.hasConnection(context))
     {
       balance = loadBalance();
       showBalance(balance);
       publicAddress = loadPublicAddress();
       showPublicAddress(publicAddress);
       showQRCode(publicAddress);
     }*/
    }
    
    return view
  }
  
  private fun loadPublicAddress() : String
  {
    var result = ""
    
    activity
    ?.getPreferences(MODE_PRIVATE)
    ?.getString(PUBLIC_ADDRESS, "")
    ?.let {publicAddress : String ->
      result = publicAddress
    }
    
    return result
  }
  
  private fun loadBalance() : String
  {
    var result = ""
    
    activity
    ?.getPreferences(MODE_PRIVATE)
    ?.getString(BALANCE, "")
    ?.let {balance : String ->
      result = balance
    }
    
    return result
  }
  
  private fun savePublicAddress(publicAddress : String)
  {
    activity
    ?.getPreferences(MODE_PRIVATE)
    ?.edit()
    ?.let {editor : Editor ->
      editor.putString(PUBLIC_ADDRESS, publicAddress)
      editor.apply()
    }
  }
  
  private fun saveBalance(balance : String)
  {
    activity
    ?.getPreferences(MODE_PRIVATE)
    ?.edit()
    ?.let {editor : Editor ->
      editor.putString(BALANCE, balance)
      editor.apply()
    }
  }
  
  private fun showPublicAddress(publicAddress : String)
  {
    if(publicAddress.isNotEmpty())
    {
      publicAddressTextView?.text = publicAddress
      publicAddressProgressBar?.visibility = View.GONE
    }
  }
  
  private fun showBalance(balance : String)
  {
    if(balance.isNotEmpty())
    {
      balanceAmountTextView?.text = balance
      privateAddressQRCodeProgressBar?.visibility = View.GONE
    }
  }
  
  private fun showQRCode(publicAddress : String)
  {
    if(publicAddress.isNotEmpty())
    {
      try
      {
        context?.let {context : Context ->
          val bitmap = textToImageEncode(context, publicAddress, false)
          publicAddressQRCodeImageView?.setImageBitmap(bitmap)
          publicAddressQRCodeImageView?.visibility = View.VISIBLE
          publicAddressQRCodeProgressBar?.visibility = View.GONE
        }
      }
      catch(e : WriterException)
      {
        e.printStackTrace()
      }
      
    }
  }
  
  override fun onBalanceObtained(balance : String)
  {
    this.balance = balance
    showBalance(balance)
    saveBalance(balance)
  }
  
  override fun onPublicAddressObtained(publicAddress : String)
  {
    this.publicAddress = publicAddress
    showPublicAddress(publicAddress)
    showQRCode(publicAddress)
    savePublicAddress(publicAddress)
  }
  
  companion object
  {
    const val PUBLIC_ADDRESS = "public address"
    const val BALANCE = "balance"
  }
}