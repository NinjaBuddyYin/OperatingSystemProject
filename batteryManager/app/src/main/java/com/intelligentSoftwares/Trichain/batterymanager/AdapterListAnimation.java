package com.intelligentSoftwares.Trichain.batterymanager;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterListAnimation extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AdapterListAnimation";
    private PackageManager pm;
    private ApplicationInfo ai;
    private List<UsageStats> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, UsageStats obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListAnimation(Context context, List<UsageStats> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageView appclose;
        public TextView name;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.appLogo);
            appclose = (ImageView) v.findViewById(R.id.appclose);
            name = (TextView) v.findViewById(R.id.appName);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            UsageStats currentItem = (UsageStats) items.get(position);
            // Get the data item for this position
            //UsageStats apps = getItem(position);
            pm = ctx.getPackageManager();
            try {
                ai = pm.getApplicationInfo(currentItem.getPackageName(), 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
            try {

                final Drawable appIcon = (Drawable) (ai != null ? pm.getApplicationIcon(ai) : "");
                // Lookup view for data population
                TextView tvName = (TextView) ((OriginalViewHolder) holder).name;
                ImageView appLogo = ((OriginalViewHolder) holder).image;

                //tvName.setText(currentItem.getPackageName());
                tvName.setText(applicationName);
                appLogo.setImageDrawable(appIcon);
                view.appclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "onClick: ");
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(view, items.get(position), position);
                        }
                    }
                });
                setAnimation(view.itemView, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}