/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.utilities.HeadsetGattAttributes;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends Activity implements View.OnClickListener {
    private static final String TAG = DeviceScanActivity.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private TextView searchStatusDisplay;
    private ImageButton scanButton;
    private Button skipButton;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 30 seconds.
    private static final long SCAN_PERIOD = 30000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        getActionBar().setTitle(R.string.title_scanning);
        mHandler = new Handler();
        searchStatusDisplay = (TextView) findViewById(R.id.device_scan_status);
        scanButton = (ImageButton) findViewById(R.id.headbanger_scanning_icon);
        skipButton = (Button) findViewById(R.id.skip_scan_button);

        scanButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

//        image = (ImageView)findViewById(R.id.headbanger_scanning_icon);
//        image.setScaleX(1f);
//        image.setScaleY(1f);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        if (!mScanning) {
//            menu.findItem(R.id.menu_stop).setVisible(false);
//            menu.findItem(R.id.menu_scan).setVisible(true);
//            menu.findItem(R.id.menu_refresh).setActionView(null);
//        } else {
//            menu.findItem(R.id.menu_stop).setVisible(true);
//            menu.findItem(R.id.menu_scan).setVisible(false);
//            menu.findItem(R.id.menu_refresh).setActionView(
//                    R.layout.actionbar_indeterminate_progress);
//        }
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_scan:
//                mLeDeviceListAdapter.clear();
//                scanLeDevice(true);
//                break;
//            case R.id.menu_stop:
//                scanLeDevice(false);
//                break;
//        }
//        return true;
//    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        scanButton.clearAnimation();
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
//        if (device == null) return;
//        final Intent intent = new Intent(this, DeviceControlActivity.class);
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//        if (mScanning) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mScanning = false;
//        }
//        startActivity(intent);
//    }

    private void scanLeDevice(final boolean enable) {
        /**
         * Small animation for when searching for headphones
         */
        Animation bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.scanning_icon_anim);
        scanButton.startAnimation(bounce);

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    invalidateOptionsMenu();
                    scanButton.setEnabled(true);
                    searchStatusDisplay.setText(getString(R.string.headset_not_found));
                    scanButton.clearAnimation();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            scanButton.setEnabled(false);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private void connectDevice(BluetoothDevice device){
        if (device == null) return;
        searchStatusDisplay.setText(getString(R.string.headset_connecting));
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (device != null){
                        Log.d(TAG, "Found device - check whether it is the one we want");
                        if (device.getName()==null){
                            Log.d(TAG, "name is null -__-");
                        }
                        else{
                            if (device.getName().equals(HeadsetGattAttributes.DEVICE_NAME)){
                                Log.d(TAG, "Found device " + device.getName() + "! Will now connect to it.");
                                connectDevice(device);
                            }
                            else {
                                Log.d(TAG, "Found device " + device.getName() + " - not the one we're after :(");
                            }
                        }

                    }
                }
            };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.headbanger_scanning_icon){
            searchStatusDisplay.setText("Searching for headset...");
            scanLeDevice(true);
        }

        else if (view.getId() == R.id.skip_scan_button){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }
}