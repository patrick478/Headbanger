package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.utilities.BluetoothUtility;

/**
 * Provides a simple list view allowing the user to select the headphones to connect to
 *
 * Created by John on 10/10/2014.
 */
public class DeviceSelectorFragment extends Fragment implements AdapterView.OnItemClickListener,
        ListAdapter {

    /**
     * Reference the list view
     */
    @InjectView(R.id.bt_device_list_view)
    ListView devicePickerListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_selector, container, false);
        ButterKnife.inject(this, view);
        devicePickerListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO respond to the user pressing on this
    }

    /**
     * To be called once the bluetooth adapter is available, displays the lsit of currently
     * connected devices and begins a search for more.
     */
    public void bluetoothReady() {
        devicePickerListView.setAdapter(this);
        BluetoothUtility.startScan();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return BluetoothUtility.connectedDevices().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //recycle views that already exist
        if (convertView == null){
            LayoutInflater layoutInflater = this.getActivity().getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.device_list_item, null);
        }

        //retrieve the Bluetooth device corresponding to position in list
        BluetoothDevice btDevice = (BluetoothDevice) (BluetoothUtility.connectedDevices().toArray())[position];

        TextView itemName = (TextView) convertView.findViewById(R.id.bluetooth_item_name);

        itemName.setText(btDevice.getName());


        return convertView;

    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
