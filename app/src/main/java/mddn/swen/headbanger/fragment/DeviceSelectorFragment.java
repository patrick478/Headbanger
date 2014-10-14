package mddn.swen.headbanger.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.adapter.DeviceSelectorAdapter;
import mddn.swen.headbanger.utilities.BluetoothUtility;

/**
 * Provides a simple list view allowing the user to select the headphones to connect to
 *
 * Created by John on 10/10/2014.
 */
public class DeviceSelectorFragment extends Fragment {

    /**
     * Reference the list view
     */
    @InjectView(R.id.bt_device_list_view)
    ListView devicePickerListView;

    /**
     * The list adapter that will be responsible for displaying and receiving user I/O
     */
    private DeviceSelectorAdapter listAdapter;

    /**
     * Object responsible for listening to the availability of BT devices/
     */
    private DeviceFoundListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_selector, container, false);
        ButterKnife.inject(this, view);
        listAdapter = new DeviceSelectorAdapter(this);
        devicePickerListView.setOnItemClickListener(listAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        getActivity().unregisterReceiver(listener);
    }

    /**
     * To be called once the bluetooth adapter is available, displays the list of currently
     * connected devices and begins a search for more.
     */
    public void bluetoothReady() {
        devicePickerListView.setAdapter(listAdapter);
        try {
            getActivity().unregisterReceiver(listener);
        } catch (Exception e){}
        listener = new DeviceFoundListener();
        IntentFilter btFound =  new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(listener, btFound);
        if (!BluetoothUtility.startScan()) {
            bluetoothStartScanFailed();
        }
    }

    /**
     * Handles the instance where the bluetooth scan failed. Should never have to call here
     */
    private void bluetoothStartScanFailed() {
        new AlertDialog.Builder(this.getActivity())
                .setTitle(getString(R.string.bluetooth_scan_fail_title))
                .setMessage(getString(R.string.bluetooth_scan_fail_message))
                .setPositiveButton(getString(R.string.bluetooth_scan_fail_dismiss_button), null)
                .show();
    }

    /**
     * Listens for the Broadcast events that find new Bluetooth pairs
     */
    private class DeviceFoundListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listAdapter.newDeviceFound(newDevice);
            }
        }
    }
}
