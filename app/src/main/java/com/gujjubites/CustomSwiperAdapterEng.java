package com.gujjubites;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.gujjubites.addNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 9/11/2017.
 */
public class CustomSwiperAdapterEng extends RecyclerView.Adapter<CustomSwiperAdapterEng.RecyclerViewHolder> {

    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    Context ctx;
    private List<addNews> addnews = new ArrayList<>();

    public CustomSwiperAdapterEng(Context ctx, List<addNews> addnews)
    {
        this.ctx = ctx;
        this.addnews = addnews;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType) {
            case NATIVE_EXPRESS_AD_VIEW_TYPE :
                View nativeExpressLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.ad_layout, parent, false);
                RecyclerViewHolder nativeExpressLayoutViewHolder  = new RecyclerViewHolder(nativeExpressLayoutView);
                return nativeExpressLayoutViewHolder;
            case MENU_ITEM_VIEW_TYPE:
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_layout, parent, false);
                RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
                return recyclerViewHolder;

        }
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                Glide.with(ctx).load(addnews.get(position).getImg_url()).fitCenter().into(holder.adImage);
                break;
            case MENU_ITEM_VIEW_TYPE:
            default:
                //holder.newsImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(ctx).load(addnews.get(position).getImg_url()).fitCenter().into(holder.newsImage);
                holder.newsTitle.setText(addnews.get(position).getTitle_eng());
                holder.newsDescription.setText(addnews.get(position).getDescription_eng());
                holder.newsSubmitter.setText("Bites by " + addnews.get(position).getUser());
                break;

        }

    }
    @Override
    public int getItemCount() {
        return addnews.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position == 0)
            return MENU_ITEM_VIEW_TYPE;
        else
            return (position % 3 == 0) ? NATIVE_EXPRESS_AD_VIEW_TYPE  : MENU_ITEM_VIEW_TYPE;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;
        TextView newsTitle;
        TextView newsDescription;
        TextView newsSubmitter;
        ImageView adImage;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            newsImage = (ImageView) itemView.findViewById(R.id.news_image);
            newsDescription = (TextView) itemView.findViewById(R.id.news_description);
            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsSubmitter = (TextView) itemView.findViewById(R.id.news_submitter);
            adImage = (ImageView) itemView.findViewById(R.id.ad_image);
        }
    }
}