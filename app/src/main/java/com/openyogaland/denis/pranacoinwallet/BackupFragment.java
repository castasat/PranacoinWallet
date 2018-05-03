package com.openyogaland.denis.pranacoinwallet;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BackupFragment extends Fragment
{
  // fields
  private Context context;
  private String idOfUser;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState)
    {
        /*
        just change the fragment_dashboard
        with the fragment you want to inflate
        like if the class is HomeFragment it should have R.layout.home_fragment
        if it is DashboardFragment it should have R.layout.fragment_dashboard
        */
        return inflater.inflate(R.layout.fragment_backup, container, false);
    }

    public final static int QR_CODE_DIMENSION = 500;

    Bitmap bitmap;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    
        // getting context
        context = getContext();
        if(context != null)
        {
          idOfUser = PranacoinWallet.getInstance(context).getIdOfUser();
        }

        final TextView textView1 = view.findViewById(R.id.textPrivaddr);

        final ImageView imageView = view.findViewById(R.id.imagePrivaddr);

        Button send = view.findViewById(R.id.buttonRequest);

        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String url1 = "http://95.213.191.196/api.php?action=getprivaddr&walletid="+idOfUser;

                final RequestQueue requestQueue1 = Volley.newRequestQueue(context);

                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                textView1.setText(response);
                                try
                                {
                                    bitmap = TextToImageEncode(response);
                                    imageView.setImageBitmap(bitmap);
                                }
                                catch (WriterException e)
                                {
                                    e.printStackTrace();
                                }
                                requestQueue1.stop();
                            }
                        }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        requestQueue1.stop();
                    }
                });

                requestQueue1.add(stringRequest1);
            }
        });
    }

    Bitmap TextToImageEncode(String Value) throws WriterException
    {
        BitMatrix bitMatrix;
        try
        {
            bitMatrix = new MultiFormatWriter().encode(Value,BarcodeFormat.QR_CODE,
                    QR_CODE_DIMENSION, QR_CODE_DIMENSION, null
            );

        } catch (IllegalArgumentException Illegalargumentexception)
        {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++)
        {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++)
            {
                pixels[offset + x] = bitMatrix.get(x, y) ? getResources().getColor(R.color.QRCodeBlackColor) :
                    getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}