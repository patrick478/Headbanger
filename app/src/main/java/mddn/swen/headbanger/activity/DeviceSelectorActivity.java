package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.fragment.DeviceSelectorFragment;
import mddn.swen.headbanger.utilities.BluetoothUtility;

/**
 * Selects a device
 * Created by John on 9/10/2014.
 */
public class DeviceSelectorActivity extends Activity {

    /**
     * The selector fragment
     */
    DeviceSelectorFragment deviceSelectorFragment;

    /**
     * Result code if Bluetooth needs to be enabled
     */
    private final Integer REQUEST_ENABLE_BT = 0xBADDAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_selector);
        if (savedInstanceState == null) {
            deviceSelectorFragment = (DeviceSelectorFragment) getFragmentManager()
                    .findFragmentById(R.id.fragment_device_selector);
        }
        startBluetooth();
    }

    /**
     * Checks the current state of the Bluetooth adapter and informs the device selector if it is
     * available.
     */
    private void startBluetooth() {
        if (BluetoothUtility.isBluetoothReady()) {
            deviceSelectorFragment.bluetoothReady();
        }
        else {
            handleNotReadyBluetooth();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Enable bluetooth request */
        if (requestCode == REQUEST_ENABLE_BT) {

            /* Failure, the app is now useless */
            if (resultCode != RESULT_OK && !BluetoothUtility.isBluetoothAvailable()) {
                BluetoothUtility.bluetoothOffDialog();
            }

            /* Inform the fragment that data should be available */
            else {
                startBluetooth();
            }
        }
    }

    /**
     * Bluetooth adapter is reportedly unavailable, check why.
     */
    private void handleNotReadyBluetooth() {

        /* None exists, the app will be unable to do anything useful */
        if (!BluetoothUtility.isBluetoothAvailable()) {
            BluetoothUtility.bluetoothUnavailableDialog();
        }

        /* Bluetooth is turned off */
        else {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    REQUEST_ENABLE_BT);
        }
    }
}
