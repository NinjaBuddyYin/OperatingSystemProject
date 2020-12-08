package com.intelligentSoftwares.Trichain.batterymanager;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class RunningAppsActivity extends AppCompatActivity {
    private RecyclerView listView;
    private UsageStatsManager mUsageStatsManager;
    List<UsageStats> queryUsageStats;
    private PackageManager packageManager;
    private AdapterListAnimation adapter;
    ActivityManager amg;
    private ApplicationInfo ai;
    UsageStats usageStats;
    String packageName;
    private int animation_type = ItemAnimation.BOTTOM_UP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_apps);
        listView = findViewById(R.id.app_list);

        mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(),
                System.currentTimeMillis());


        // using Activity service to list all process
        amg = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        adapter = new AdapterListAnimation(this, queryUsageStats, animation_type);
        if (queryUsageStats.size() == 0) {
            startActivity(new Intent(this, MainActivity.class));
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterListAnimation.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UsageStats obj, int position) {
                //android.os.Process.killProcess(position);

                usageStats = (UsageStats) queryUsageStats.get(position);
                //queryUsageStats = (UsageStats) parent.getItemAtPosition(position);
                packageName = usageStats.getPackageName();

                packageManager = getApplicationContext().getPackageManager();


                try {
                    ai = packageManager.getApplicationInfo(packageName, 0);
                } catch (final PackageManager.NameNotFoundException e) {
                    ai = null;
                }
                final String appName = (String) (ai != null ? packageManager.getApplicationLabel(ai) : "(unknown)");

                AlertDialog.Builder adb = new AlertDialog.Builder(RunningAppsActivity.this);
                adb.setTitle("Stop " + appName + " ?");
                adb.setMessage("Are you sure you want to stop " + appName + " from running in background? ");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Application" + packageName + " - STOPPED", Toast.LENGTH_SHORT).show();
                        amg.killBackgroundProcesses(packageName);
                        queryUsageStats.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                    }
                });
                adb.show();

                adapter.notifyDataSetChanged();
            }
        });

    }
}
