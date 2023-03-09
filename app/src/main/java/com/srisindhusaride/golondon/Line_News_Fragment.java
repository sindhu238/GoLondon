//package com.srisindhusaride.golondon;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import androidx.core.app.Fragment;
//import androidx.fragment.app.Fragment;
//
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import static com.srisindhusaride.golondon.R.id.reason;
//
///**
// * @since  16/03/17.
// */
//
//public class Line_News_Fragment extends Fragment {
//
//    private TextView mReasonTV;
//    private TextView mLineNameTV;
//
//    private View mRootView = null;
//    private final String TAG = "LineNews";
//    private static String mReason;
//    private static String mLineName;
//
//    public static Line_News_Fragment init(String linenName, String reason) {
//        mReason = reason;
//        if (mReason != null)
//            mReason = reason.split(": ")[1];
//        mLineName = linenName + " line";
//        return  new Line_News_Fragment();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//       if (mRootView == null) {
//           mRootView = inflater.inflate(R.layout.line_news_fragment, container, false);
//           mReasonTV = (TextView) mRootView.findViewById(reason);
//           mLineNameTV = (TextView) mRootView.findViewById(R.id.lineName);
//       }
//        DisplayMetrics metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        ViewGroup.LayoutParams layoutParams =  mRootView.getLayoutParams();
//        layoutParams.height = metrics.heightPixels - metrics.heightPixels/3;
//        mRootView.setLayoutParams(layoutParams);
//       mLineNameTV.setText(mLineName);
//        if (mReason != null)
//            mReasonTV.setText(mReason);
//        else
//            mReasonTV.setText("Service is good");
//
//        return mRootView;
//    }
//}
