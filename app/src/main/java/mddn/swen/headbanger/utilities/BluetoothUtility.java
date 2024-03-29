package mddn.swen.headbanger.utilities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import mddn.swen.headbanger.R;

/**
 * A set of utilities used for querying the current state of the Bluetooth adapter
 *
 * Created by John on 10/10/2014.
 */
public class BluetoothUtility {

    private static final String TAG = BluetoothUtility.class.getSimpleName();

    /**
     * Checks to see if a Bluetooth adapter is available on this device
     *
     * @return True if a bluetooth adapter (i.e. physical hardware) is available
     */
    public static boolean isBluetoothAvailable() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    /**
     * Checks to see if the Bluetooth adapter is currently turned on and available.
     *
     * @return True if the bluetooth adapter is on. False if not available or off.
     */
    public static boolean isBluetoothReady() {
        return isBluetoothAvailable() && BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    /**
     * Attempts to start device discovery, returning if it was successful or not
     *
     * @return True if the device is now searching
     */
    public static boolean startScan() {
        if (isBluetoothReady()) {
            if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                Log.d(TAG, "canceled scan");
            }
            Log.d(TAG, "started scan");
            return BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
        else{
            Log.d(TAG, "not scanning");
            return false;
        }
    }

    /**
     * Safely return the set of currently connected Bluetooth devices, will not return null on
     * failure but rather the empty set.
     *
     * @return A non-null set of connected devices - could be empty.
     */
    public static Set<BluetoothDevice> connectedDevices() {

        /* Query the adapter and return the current set */
        if (isBluetoothReady()) {
            return BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        }

        /* This actually failed, but don't return null */
        else{
            return new HashSet<BluetoothDevice>();
        }
    }

    /**
     * Displays a dialog when the user chooses not to turn on their bluetooth - but a bluetooth
     * adapter <i>is actually available</i>.
     *
     * @param displayingContext Context to display under
     */
    public static void bluetoothOffDialog(Context displayingContext) {
        if (displayingContext == null) {
            return;
        }
        new AlertDialog.Builder(displayingContext)
                .setTitle(displayingContext.getString(R.string.bluetooth_off_title))
                .setMessage(displayingContext.getString(R.string.bluetooth_off_message))
                .setPositiveButton(displayingContext.getString(R.string.bluetooth_off_dismiss_button), null)
                .show();
    }

    /**
     * Displays a dialog when there is <b>no bluetooth adapter on the device.</b>
     *
     * @param displayingContext Context to display under
     */
    public static void bluetoothUnavailableDialog(Context displayingContext) {
        if (displayingContext == null) {
            return;
        }

        new AlertDialog.Builder(displayingContext)
                .setTitle(displayingContext.getString(R.string.bluetooth_unavailable_title))
                .setMessage(displayingContext.getString(R.string.bluetooth_unavailable_message))
                .setPositiveButton(displayingContext.getString(R.string.bluetooth_unavailable_dismiss_button), null)
                .show();
    }
}
