package com.intelligentSoftwares.Trichain.batterymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.itangqi.waveloadingview.WaveLoadingView;


public class MainActivity extends AppCompatActivity {
    int i;
    boolean isCharging, usbCharge, acCharge;
    WaveLoadingView mWaveLoadingView;
    TextView txtbattery_status, txtstatus, txtcharge_status, txtport, tips;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private Button run_app;
    Timer timer = new Timer();
    private String randomStr;
    private int cnt = 0;
    private float batteryLevel1;
    private float batteryLevel2;
    Date tym1;
    private String TAG = "Intelligent-Apps";
    private ImageView imgUsage;

    private boolean isTrue = true;
    private String charging, chargingMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, iFilter);
        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        txtbattery_status = (TextView) findViewById(R.id.txtbattery_status);
        run_app = (Button) findViewById(R.id.run_app);
        txtstatus = (TextView) findViewById(R.id.txtstatus);
        txtcharge_status = (TextView) findViewById(R.id.txtcharge_status);
        txtport = (TextView) findViewById(R.id.txtport);
        tips = findViewById(R.id.tips);
        imgUsage = findViewById(R.id.imgUsage);

        imgUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUsage.setImageResource(R.drawable.ic_info);
                Intent i = new Intent(MainActivity.this, TopBatteryApps.class);
                i.putExtra("first", isTrue);
                isTrue = !isTrue;
                startActivity(i);
            }
        });

        run_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RunningAppsActivity.class));
            }
        });

        i = getBatteryLevel(getApplicationContext());
        //battery level conditions
        if (i >= 50 && i <= 75) {
            txtstatus.setText("Battery Status: Moderate");
        } else if (i >= 75 && i <= 99) {
            txtstatus.setText("Battery Status: GOOD");
        } else if (i >= 20 && i <= 49) {
            txtstatus.setText("Battery Status: Draining");
        } else if (i >= 10 && i <= 20) {
            txtstatus.setText("Battery Status: LOW");
        } else if (i < 10) {
            txtstatus.setText("Battery Status: Critically Low, Please Charge the Device!");
        } else if (i == 100) {

            txtstatus.setText("Battery Status: Fully Charged");
        }
        mWaveLoadingView.setTopTitle(i + "%");
        mWaveLoadingView.setCenterTitleColor(Color.WHITE);
        mWaveLoadingView.setCenterTitle("");
        mWaveLoadingView.setBottomTitleSize(18);
        mWaveLoadingView.setProgressValue(0);
        mWaveLoadingView.setBorderWidth(5);
        mWaveLoadingView.setAmplitudeRatio(100);
        mWaveLoadingView.setWaveColor(Color.parseColor("#B2DFDB"));
        mWaveLoadingView.setBorderColor(Color.parseColor("#4CAF50"));
        mWaveLoadingView.setTopTitleStrokeColor(Color.WHITE);
        mWaveLoadingView.setTopTitleStrokeWidth(3);
        mWaveLoadingView.setWaterLevelRatio(0.2f);
        mWaveLoadingView.setAnimDuration(3000);
        mWaveLoadingView.pauseAnimation();
        mWaveLoadingView.resumeAnimation();
        mWaveLoadingView.cancelAnimation();
        mWaveLoadingView.startAnimation();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        progressStatus = getBatteryLevel(getApplicationContext());
                        mWaveLoadingView.setProgressValue(progressStatus);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String x = String.valueOf(i);
                        getChargingLevel(getApplicationContext());

                        //Battery charging conditions
                        if (isCharging == true && usbCharge == true) {
                            txtcharge_status.setText("Charging Status: Device Charging");
                            txtport.setText("Connected to a USB Port");
                        } else if (isCharging == true && acCharge == true) {
                            txtcharge_status.setText("Charging Status: Device Charging");
                            txtport.setText("Connected to an AC power source");
                        } else {
                            txtcharge_status.setText("Device Not Charging");
                            txtport.setText("Not Connected To Any Other Device");
                        }
                        if (isCharging == true && i == 100) {
                            txtcharge_status.setText("Charging Status: Charging Complete");
                        }

                    }
                });

            }
        }, 0, 100);
        startTipsTimer();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: ");
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1); //get the scale of battery (usually 100)
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1); //get the level of the battery

            // Are we charging or is the phone fully charged?
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;


            float percentage = level / (float) scale; //calculate the battery percentage of the battery according to the scale

            /*We need to get the current time and get the current battery percentage. We can easily know the next battery percentage;
             * if it's charging we get the upper percentage and vice-versa. */

            if (cnt < 1) {
                batteryLevel2 = percentage + (float) 0.0001; //if the phone is charging, this is the next battery level we are expecting
                batteryLevel1 = percentage - (float) 0.0001; //if it is discharging, this is the next battery level we are expecting

                tym1 = Calendar.getInstance().getTime();
                Log.e(TAG, "onReceive: tym1 " + tym1.getTime());
                //get Time 1
                cnt++;
            }

            //Define what happens when the phone is charging as well as when it's not charging
            if (isCharging) {
                Log.e(TAG, "onReceive: charging");
                Log.e(TAG, "onReceive: percent" + percentage);
                Log.e(TAG, "onReceive: batterylevel2" + batteryLevel2);
                Log.e(TAG, "onReceive: charging");
                charging = "Charging";

                if (percentage >= batteryLevel2) {
                    //get Time 2
                    float p_left = (float) 1 - percentage;
                    Date tym2 = Calendar.getInstance().getTime();
                    Log.e(TAG, "onReceive: tym2 " + tym2.getTime());
                    long tym_diff = tym2.getTime() - tym1.getTime();
                    long mills = Math.abs(tym_diff);

                    long rem = (long) (mills * p_left * 100);

                    int hours = (int) (rem / (1000 * 60 * 60));
                    int mins = (int) (rem / (1000 * 60)) % 60;
                    long secs = (int) (rem / 1000) % 60;
                    Log.e(TAG, "onReceive: " + hours);
                    txtbattery_status.setText("Full charge in: " + hours + "H " + mins + "m ");

                    cnt = 0;
                } else {
                    Log.e(TAG, "onReceive: else");
                    //get Time 2
                    float p_left = (float) 1 - percentage;
                    Date tym2 = Calendar.getInstance().getTime();//13245233546
                    Log.e(TAG, "onReceive: tym2 " + tym2.getTime());
                    long tym_diff = tym2.getTime() - (tym1.getTime() - new Random().nextInt(65758));
                    Log.e(TAG, "onReceive: time diff" + tym_diff);
                    long mills = Math.abs(tym_diff);

                    Log.e(TAG, "onReceive: abs millis " + mills);

                    long rem = (long) (mills * p_left * 100);

                    Log.e(TAG, "onReceive: remaining millis " + rem);

                    int hours = (int) (rem / (1000 * 60 * 60));
                    int mins = (int) (rem / (1000 * 60)) % 60;
                    long secs = (int) (rem / 1000) % 60;
                    Log.e(TAG, "onReceive: h " + hours);
                    Log.e(TAG, "onReceive: m " + mins);
                    txtbattery_status.setText("Full charge in: " + hours + "H " + mins + "m ");

                    cnt = 0;
                }
            } else {
                Log.e(TAG, "onReceive: not charging");

                charging = "Not Charging";
                if (percentage <= batteryLevel1) {
                    //get Time 2

                    Date tym2 = Calendar.getInstance().getTime();

                    long tym_diff = tym2.getTime() - tym1.getTime();
                    long mills = Math.abs(tym_diff);

                    long rem = (long) (mills * percentage * 100);

                    int hours = (int) (rem / (1000 * 60 * 60));
                    int mins = (int) (rem / (1000 * 60)) % 60;
                    long secs = (int) (rem / 1000) % 60;
                    txtbattery_status.setText(hours + "H" + mins + "m" + secs + "s");

                    cnt = 0;
                } else {

                    Log.e(TAG, "onReceive: else");
                    //get Time 2
                    float p_left = (float) 1 - percentage;
                    Date tym2 = Calendar.getInstance().getTime();//13245233546
                    Log.e(TAG, "onReceive: tym2 " + tym2.getTime());
                    long tym_diff = tym2.getTime() - (tym1.getTime() - 65758 * 2);
                    Log.e(TAG, "onReceive: time diff" + tym_diff);
                    long mills = Math.abs(tym_diff);

                    Log.e(TAG, "onReceive: abs millis " + mills);

                    long rem = (long) (mills * p_left * 100);

                    Log.e(TAG, "onReceive: remaining millis " + rem);

                    int hours = (int) (rem / (1000 * 60 * 60));
                    int mins = (int) (rem / (1000 * 60)) % 60;
                    long secs = (int) (rem / 1000) % 60;
                    Log.e(TAG, "onReceive: h " + hours);
                    Log.e(TAG, "onReceive: m " + mins);
                    txtbattery_status.setText("Discharge in: " + hours + "H " + mins + "m ");
/*
                    Date tym2 = Calendar.getInstance().getTime();

                    long tym_diff = tym2.getTime() - tym1.getTime();
                    long mills = Math.abs(tym_diff);

                    long rem = (long) (mills * percentage * 100);

                    int hours = (int) (rem / (1000 * 60 * 60));
                    int mins = (int) (rem / (1000 * 60)) % 60;
                    long secs = (int) (rem / 1000) % 60;
                    txtbattery_status.setText(hours + "H" + mins + "m" + secs + "s");*/
                }

            }

            //mTextViewPercentage.setText("" + mProgressStatus + "%");

        }
    };

    private void startTipsTimer() {
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        //Called each time when 1000 milliseconds (1 second) (the period parameter)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                String[] array = getResources().getStringArray(R.array.tips);
                                randomStr = array[new Random().nextInt(array.length)];
                                Log.i("tag", "Tip" + randomStr);
                                tips.setText(randomStr);
                            }
                        });
                    }

                },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                60000);

    }

    public int getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        return i = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

    }

    //Checking the charging status.
    public void getChargingLevel(Context bl) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = bl.registerReceiver(null, ifilter);
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

// How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
    }


}