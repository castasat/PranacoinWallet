package io.github.castasat.pranacoin_wallet.view.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import io.github.castasat.pranacoin_wallet.R

object AlertDialogUtil {
    fun showAlertDialog(context: Context, title: String, message: String) {
        AlertDialog
            .Builder(context)
            .setIcon(R.mipmap.ic_launcher_round)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton("ОК") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}