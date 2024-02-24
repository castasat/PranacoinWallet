package io.github.castasat.pranacoin_wallet.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.createBitmap
import androidx.core.content.ContextCompat.getColor
import com.google.zxing.BarcodeFormat.QR_CODE
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.openyogaland.denis.pranacoin_wallet_2_0.R
import io.github.castasat.pranacoin_wallet.application.PranacoinWallet2.Companion.crashlytics
import io.github.castasat.pranacoin_wallet.application.PranacoinWallet2.Companion.log

object QRCodeUtil {
    private const val QR_CODE_DIMENSION = 500

    @Throws(WriterException::class)
    fun textToImageEncode(
        context: Context,
        address: String,
        isPrivate: Boolean
    ): Bitmap? {
        val applicationContext = context.applicationContext

        // define colors
        val blackColor = getColor(applicationContext, R.color.QRCodeBlackColor)
        val whiteColor = getColor(applicationContext, R.color.QRCodeWhiteColor)
        val redColor = getColor(applicationContext, R.color.QRCodeRedColor)

        val bitMatrix: BitMatrix
        val qrCodeColor = if (isPrivate) redColor else blackColor

        try {
            bitMatrix = MultiFormatWriter()
                .encode(
                    address,
                    QR_CODE,
                    QR_CODE_DIMENSION,
                    QR_CODE_DIMENSION,
                    null
                )
        } catch (exception: IllegalArgumentException) {
            log("QRCodeUtil.textToImageEncode(): ")
            exception.printStackTrace()
            crashlytics(exception)
            return null
        }

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) qrCodeColor else whiteColor
            }
        }
        val bitmap =
            createBitmap(
                bitMatrixWidth,
                bitMatrixHeight,
                ARGB_8888
            )

        bitmap.setPixels(
            pixels, 0,
            QR_CODE_DIMENSION,
            0, 0,
            bitMatrixWidth,
            bitMatrixHeight
        )
        return bitmap
    }
}