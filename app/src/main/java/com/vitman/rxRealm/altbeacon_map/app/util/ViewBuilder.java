package com.vitman.rxRealm.altbeacon_map.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vitman.rxRealm.altbeacon_map.app.R;

/**
 * Created by Victor Artemjev on 16.06.2015.
 */
public class ViewBuilder {
    private Context mContext;
    private BitmapHelper mBitmapHelper;

    public ViewBuilder(Context context) {
        mContext = context;
    }

   public TextView getZoneTitleTextView(int beaconId) {
        TextView textView = new TextView(mContext);
        textView.setText("Zone " + beaconId);
        textView.setTextColor(Color.parseColor(mContext.getString(R.color.pink)));
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        textView.setTextSize(mContext.getResources().getDimension(R.dimen.text_size));
        return textView;
    }

    public TextView getCustomerRemainsTextView(int customerNumber) {
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER | Gravity.TOP);
        textView.setText("+" + customerNumber + " more");
        textView.setBackgroundColor(Color.BLUE);
        textView.setTextSize(mContext.getResources().getDimension(R.dimen.text_size));
        return textView;
    }

    public ImageView getMarker(Bitmap bitmap, String userId, int markerSize) {
        RoundedImageView marker = new RoundedImageView(mContext);
        marker.setScaleType(ImageView.ScaleType.CENTER_CROP);
        marker.setBorderWidth((float) markerSize / 8);
        if (userId.equals("current_user_id")) {
            marker.setBorderColor(mContext.getResources().getColor(R.color.pink));
        } else {
            marker.setBorderColor(mContext.getResources().getColor(R.color.black));
        }
        marker.mutateBackground(true);
        marker.setImageBitmap(bitmap);
        marker.setOval(true);
        return marker;
    }
}
