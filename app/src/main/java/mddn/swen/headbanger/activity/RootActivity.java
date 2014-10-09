package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import mddn.swen.headbanger.R;


public class RootActivity extends Activity {

    /**
     * Result code if Bluetooth needs to be enabled
     */
    private final Integer REQUEST_ENABLE_BT = 0xBADDAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        checkBluetoothAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Enable bluetooth request */
        if (requestCode == REQUEST_ENABLE_BT) {

            /* Failure, the app is now useless */
            if (resultCode != RESULT_OK && !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                displayBluetoothOffDialog();
            }
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

    /**
     * Queries the bluetooth adapter and request it be turned on if off, handles failure
     * if unavailable
     */
    private void checkBluetoothAdapter() {

        /* Get an instance of the adapter */
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        /* None exists, the app will be unable to do anything useful */
        if (adapter == null) {
           displayNoBluetoothDialog();
        }

        /* Bluetooth is turned off */
        else if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
