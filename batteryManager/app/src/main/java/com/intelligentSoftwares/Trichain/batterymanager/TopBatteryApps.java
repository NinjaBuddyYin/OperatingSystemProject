package com.intelligentSoftwares.Trichain.batterymanager;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TopBatteryApps extends AppCompatActivity {
    private RecyclerView listView;
    private UsageStatsManager mUsageStatsManager;
    List<UsageStats> queryUsageStats;
    List<SimpleItem> appList = new ArrayList<>();
    TextView tvNoApps;
    TopAppsAdapter appsAdapter;
    private PackageManager packageManager;
    private AdapterListAnimation adapter;
    ActivityManager amg;
    private ApplicationInfo ai;
    UsageStats usageStats;
    String packageName;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    private boolean isTrue = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_apps);
        listView = findViewById(R.id.app_list1);
        tvNoApps = findViewById(R.id.tvNoApps);

        // using Activity service to list all process
        amg = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isTrue = extras.getBoolean("first");
        }
        checkCommonApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkCommonApps() {
        for (String pkgnme : getResources().getStringArray(R.array.heavy_apps)) {
            if (appInstalled(pkgnme)) {
                try {
                    ApplicationInfo app = this.getPackageManager().getApplicationInfo(pkgnme, 0);
                    SimpleItem i = new SimpleItem((String) this.getPackageManager().getApplicationLabel(app), pkgnme,
                            this.getPackageManager().getApplicationIcon(app));
                    if (isTrue) appList.add(i);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        appsAdapter = new TopAppsAdapter(this, appList, 1);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(appsAdapter);

        if (appList.size() == 0) {
            tvNoApps.setVisibility(View.VISIBLE);
        } else {
            tvNoApps.setVisibility(View.GONE);
        }

        appsAdapter.setOnItemClickListener(new TopAppsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, SimpleItem obj, int position) {
                Toast.makeText(getApplicationContext(), "Application " + obj.getAppName() + " - STOPPED", Toast.LENGTH_SHORT).show();
                amg.killBackgroundProcesses(obj.getPkgname());
                appList.remove(position);
                appsAdapter.notifyDataSetChanged();
            }
        });

    }

    private boolean appInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static class TopAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final String TAG = "AdapterListAnimation";
        private List<SimpleItem> items = new ArrayList<>();

        private Context ctx;
        private OnItemClickListener mOnItemClickListener;
        private int animation_type = 0;

        public interface OnItemClickListener {
            void onItemClick(View view, SimpleItem obj, int position);
        }

        public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
            this.mOnItemClickListener = mItemClickListener;
        }

        public TopAppsAdapter(Context context, List<SimpleItem> items, int animation_type) {
            this.items = items;
            ctx = context;
            this.animation_type = animation_type;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView image;
            public TextView cost, kill;
            public TextView name;
            public View lyt_parent;

            public ViewHolder(View v) {
                super(v);
                image = (ImageView) v.findViewById(R.id.appLogo);
                cost = (TextView) v.findViewById(R.id.cost);
                kill = (TextView) v.findViewById(R.id.kill);
                name = (TextView) v.findViewById(R.id.appName);
                lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_app, parent, false);
            vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ViewHolder) {
                SimpleItem currentItem = items.get(position);
                // Get the data item for this position
                //UsageStats apps = getItem(position);

                ((ViewHolder) holder).name.setText(currentItem.getAppName());
                ((ViewHolder) holder).image.setImageDrawable(currentItem.getAppIcon());
                ((ViewHolder) holder).kill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "onClick: ");
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(view, items.get(position), position);
                        }
                    }
                });
                setAnimation(holder.itemView, position);

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
}
