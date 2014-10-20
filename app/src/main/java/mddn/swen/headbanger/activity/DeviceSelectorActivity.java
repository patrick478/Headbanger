package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.UUID;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.fragment.DeviceSelectorFragment;
import mddn.swen.headbanger.utilities.BluetoothUtility;

/**
 * Selects a device TODO this class is to be removed
 * Created by John on 9/10/2014.
 */
public class DeviceSelectorActivity extends Activity implements View.OnClickListener, BluetoothAdapter.LeScanCallback{


    private static final String TAG = DeviceSelectorActivity.class.getSimpleName();

    private static final String DEVICE_NAME = "HMSoft";
    private static final UUID HMSOFT_SERVICE = UUID.fromString("C8870DF0-D414-7F96-31DB-CF7F4C04E689"); //HMSoft service
    private static final UUID HMSOFT_DATA_CHAR = UUID.fromString("C8870DF1-D414-7F96-31DB-CF7F4C04E689"); //data characteristic
    private static final UUID HMSOFT_CONFIG_CHAR = UUID.randomUUID();
    private static final UUID HMSOFT_CAL_CHAR = UUID.randomUUID();
    private static final UUID CONFIG_DESCRIPTOR = UUID.randomUUID();

    private BluetoothGatt mConnectedGatt;

    private TextView connectionStatusTextView;

    private ProgressDialog mProgress;

    private BluetoothAdapter mBluetoothAdapter;

    private HashMap<String, BluetoothDevice> mDevices;




    /**
     * The "scan" button, for discovering new devices
     */
    private Button scanButton;

    /**
     * Result code if Bluetooth needs to be enabled
     */
    private final Integer REQUEST_ENABLE_BT = 0xBADDAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_selector);
        if (savedInstanceState == null) {

            /* Set up view elements */
            scanButton = (Button) this.findViewById(R.id.scan_devices_button);
            scanButton.setOnClickListener(this);
            connectionStatusTextView = (TextView) findViewById(R.id.headset_connection_status);

            /*
         * Bluetooth in Android 4.3 is accessed via the BluetoothManager, rather than
         * the old static BluetoothAdapter.getInstance()
         */
            BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = manager.getAdapter();

            mDevices = new HashMap<String, BluetoothDevice>();
        }

        /*
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         *
         * TODO: delete this from release version
         *
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        /* Need a progress dialog to show when there is connection stuff happening in the background */
        mProgress = new ProgressDialog(this);
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        startBluetooth();
    }

    /**
     * Ask the user to enable Bluetooth if it is not already switched on
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Enable bluetooth request */
        if (requestCode == REQUEST_ENABLE_BT) {

            /* Failure, the app is now useless */
            if (resultCode != RESULT_OK || !BluetoothUtility.isBluetoothAvailable()) {
                BluetoothUtility.bluetoothOffDialog(this);
            }

            /* Inform the fragment that data should be available */
            else {
                startBluetooth();
            }
        }
    }


    /**
     * Check the current state of the Bluetooth adapter so that we can start scanning for devices
     */
    private void startBluetooth() {
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "start scan now");
            startScan();
        }
        else {
            handleNotReadyBluetooth();
        }
    }


    /**
     * Bluetooth adapter is reportedly unavailable, check why.
     */
    private void handleNotReadyBluetooth() {

        /* None exists, the app will be unable to do anything useful */
        if (!BluetoothUtility.isBluetoothAvailable()) {
            BluetoothUtility.bluetoothUnavailableDialog(this);
        }

        /* Bluetooth is turned off */
        else {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    REQUEST_ENABLE_BT);
        }
    }


    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };
    
    /**
     * Start searching for available devices
     */
    private void startScan(){
        mBluetoothAdapter.startLeScan(new UUID[] {}, this);
        setProgressBarIndeterminateVisibility(true);

        /* Keep the scan time short by stopping it after a prescribed interval */
        mHandler.postDelayed(mStopRunnable, 30000);
    }

    /**
     * Stop the scan
     */
    private void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        setProgressBarIndeterminateVisibility(false);

        mHandler.postDelayed(mStartRunnable, 2500);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scan_devices_button){
            /* Start searching for headset again if user taps the "Scan" button */
            mDevices.clear();
            startScan();
        }
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {

        try{
            Log.i(TAG, "New LE Device: " + bluetoothDevice.getName() + " @ " + rssi);
            /*
             * We are looking for a particular headset only, so validate the name
             * that each device reports before making a connection
             */
            if (DEVICE_NAME.equals(bluetoothDevice.getName())) {
//                stopScan();
                mDevices.put(bluetoothDevice.hashCode() + "", bluetoothDevice);

//                Log.d(TAG, "The device's UUID:");
//                Log.d(TAG, bluetoothDevice.getUuids()[0].getUuid().toString());

                if (bluetoothDevice.getBondState()==BluetoothDevice.BOND_BONDED) {
                    connectToDevice(bluetoothDevice);
                }
                else{
                    bluetoothDevice.createBond();
                    if (bluetoothDevice.getBondState()==BluetoothDevice.BOND_BONDED) {
                        connectToDevice(bluetoothDevice);
                    }
                }
            }
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private void connectToDevice(BluetoothDevice bluetoothDevice) {
        Log.i(TAG, "Try to connect to " + bluetoothDevice.getName());
                /*
                 * Make a connection with the device using the special LE-specific
                 * connectGatt() method, passing in a callback for GATT events
                 */
        mConnectedGatt = bluetoothDevice.connectGatt(this, false, mGattCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Make sure dialog is hidden
        mProgress.dismiss();
        //Cancel any scans in progress
        mHandler.removeCallbacks(mStopRunnable);
        mHandler.removeCallbacks(mStartRunnable);
        mBluetoothAdapter.stopLeScan(this);
    }


    @Override
    protected void onStop() {
        //Disconnect from any active connection attempt
        if (mConnectedGatt != null) {
            mConnectedGatt.disconnect();
            mConnectedGatt = null;
        }

        super.onStop();
    }


    /*
     * In this callback, we've created a bit of a state machine to enforce that only
     * one characteristic be read or written at a time until all of our sensors
     * are enabled and we are registered to get notifications.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

//        /* State Machine Tracking */
//        private int mState = 0;
//
//        private void reset() { mState = 0; }
//
//        private void advance() { mState++; }

        /*
         * Send an enable command to the headset by writing a configuration
         * characteristic.  This is specific to the SensorTag to keep power
         * low by disabling sensors you aren't using.
         */
        private void enableReadChar(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
//            Log.d(TAG, "Enabling sensor");
            characteristic = gatt.getService(HMSOFT_SERVICE)
                    .getCharacteristic(HMSOFT_DATA_CHAR);
            characteristic.setValue(new byte[] {0x02});

            gatt.writeCharacteristic(characteristic);
        }

        /*
         * Read the data characteristic's value for each sensor explicitly
         */
        private void readData(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
//                    Log.d(TAG, "Reading headset data");
                    characteristic = gatt.getService(HMSOFT_SERVICE)
                            .getCharacteristic(HMSOFT_DATA_CHAR);

            gatt.readCharacteristic(characteristic);
        }

        /*
         * Enable notification of changes on the data characteristic for headset
         * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
         * configuration descriptor.
         */
        private void setNotifyDataChanged(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
//            Log.d(TAG, "Set notify headset update");
            characteristic = gatt.getService(HMSOFT_SERVICE)
                    .getCharacteristic(HMSOFT_DATA_CHAR);

            //Enable local notifications
            gatt.setCharacteristicNotification(characteristic, true);
            //Enabled remote notifications
            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            Log.d(TAG, "Connection State Change: "+status+" -> "+connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Once successfully connected, we must next discover all the services on the
                 * device before we can read and write their characteristics.
                 */

                connectionStatusTextView.setText("Device connected :D");
                gatt.discoverServices();
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                /*
                 * If at any point we disconnect, send a message to clear the weather values
                 * out of the UI
                 */
                connectionStatusTextView.setText("Device disconnected :(");
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                /*
                 * If there is a failure at any stage, simply disconnect
                 */
                connectionStatusTextView.setText("Device disconnected :(");
                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            /*
             * With services discovered, we are going to start reading data
             */
            setNotifyDataChanged(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //For each read, print the data to log
            if (HMSOFT_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, characteristic));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //After writing the enable flag, next we read the initial value
            readData(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*
             * After notifications are enabled, all updates from the device on characteristic
             * value changes will be posted here.  Similar to read, we hand these up to the
             * UI thread to update the display.
             */
            if (HMSOFT_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, characteristic));
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //Once notifications are enabled, we move to the next sensor and start over with enable
            enableReadChar(gatt);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(TAG, "Remote RSSI: "+rssi);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };


    /*
     * We have a Handler to process event results on the main thread
     */
    private static final int MSG_PROGRESS = 201;
    private static final int MSG_DISMISS = 202;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.obj.getClass().getSimpleName().equals("BluetoothDevice")){
                BluetoothDevice device = (BluetoothDevice) msg.obj;
                mDevices.put(device.getAddress(), device);
            }

            BluetoothGattCharacteristic characteristic;
            switch (msg.what) {
                case MSG_PROGRESS:
                    mProgress.setMessage((String) msg.obj);
                    if (!mProgress.isShowing()) {
                        mProgress.show();
                    }
                    break;
                case MSG_DISMISS:
                    mProgress.hide();
                    break;
            }
        }
    };


}
