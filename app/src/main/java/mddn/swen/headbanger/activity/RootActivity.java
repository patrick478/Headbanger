package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.fragment.DeviceSelectorFragment;
import mddn.swen.headbanger.utilities.BluetoothUtility;

/**
 * Root/launching activity
 * Created by John on 9/10/2014.
 */
public class RootActivity extends Activity {

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
        setContentView(R.layout.activity_root);
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
    public void onResume() {
        startBluetooth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Enable bluetooth request */
        if (requestCode == REQUEST_ENABLE_BT) {

            /* Failure, the app is now useless */
            if (resultCode != RESULT_OK && !BluetoothUtility.isBluetoothAvailable()) {
                displayBluetoothOffDialog();
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
            displayNoBluetoothDialog();
        }

        /* Bluetooth is turned off */
        else {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    REQUEST_ENABLE_BT);
        }
    }

    /**
     * Displays a dialog when the user chooses not to turn on their bluetooth - but a bluetooth
     * adapter <i>is actually available</i>.
     */
    private void displayBluetoothOffDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Bluetooth Disabled")
                .setMessage("This application requires access to your Bluetooth adapter - " +
                        "please enable it to continue using the app.")
                .setPositiveButton("Ok", null)
                .show();
    }

    /**
     * Displays a dialog when there is <b>no bluetooth adapter on the device.</b>
     */
    private void displayNoBluetoothDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Bluetooth Unsupported")
                .setMessage("Unfortunately, your device does not have a Bluetooth adapter " +
                        "available. This application requires a Bluetooth adapter to work.")
                .setPositiveButton("Ok", null)
                .show();
    }
}
