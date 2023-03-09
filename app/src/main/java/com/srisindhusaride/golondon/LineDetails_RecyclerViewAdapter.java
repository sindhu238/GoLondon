//package com.srisindhusaride.golondon;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
///**
// * @since  14/03/17.
// */
//
//class LineDetails_RecyclerViewAdapter extends RecyclerView.Adapter<LineDetails_RecyclerViewAdapter.ViewHolder> {
//
//    private Context mContext;
//    private ArrayList<String> mList= new ArrayList<>();
//
//
//    LineDetails_RecyclerViewAdapter(Multimap<String, String> data, String spinnerSelected) {
//        if (data != null)
//        for (String key : data.keySet()) {
//            String keyTemp;
//            if (key.contains("::") && key.contains("&harr;"))
//            keyTemp = key.split("::")[1].replace("&harr;","-")+ "     ";
//            else if (key.contains("&harr;"))
//                keyTemp = key.replace("&harr;","-")+ "     ";
//            else
//                keyTemp = key + "     ";
//
//            if (spinnerSelected != null && keyTemp.contentEquals(spinnerSelected)) {
//                for (String value: data.get(key)) {
//                    mList.add(value);
//                }
//            }
//        }
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        mContext = parent.getContext();
//        TextView textView = (TextView) LayoutInflater.from(mContext)
//                .inflate(R.layout.linedetails_layout, parent, false);
//        return new ViewHolder(textView);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.setIsRecyclable(false);
//
//        String text;
//        if (mList.get(position).contains("(") && mList.get(position).contains(")")) {
//            String toBeRemovedString = mList.get(position).substring(mList.get(position).indexOf("(")
//                    , mList.get(position).indexOf(")")+1);
//            text = mList.get(position).replace(toBeRemovedString,"").split("Underground Station")[0];
//        } else {
//            text = mList.get(position).split("Underground Station")[0];
//        }
//        holder.textView.setText(text);
//
////        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.next_stations);
////        ColorFilter colorFilter = new LightingColorFilter(ContextCompat.getColor(mContext,
////                android.R.color.transparent), ContextCompat.getColor(mContext,
////                        R.color.colorAccent));
////        drawable.setColorFilter(colorFilter);
////        holder.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textView;
//
//        ViewHolder(TextView textView){
//            super(textView);
//            this.textView = textView;
//        }
//    }
//}