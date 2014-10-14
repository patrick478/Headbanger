package mddn.swen.headbanger.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.fragment.DeviceSelectorFragment;
import mddn.swen.headbanger.utilities.BluetoothUtility;

/**
 * Handles the grunt work of displaying the list view of available and connected devices.
 *
 * Created by John on 14/10/2014.
 */
public class DeviceSelectorAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    /**
     * The holding fragment
     */
    private DeviceSelectorFragment parentFragment;

    /**
     * A set of all the devices found in a search
     */
    private Set<BluetoothDevice> foundDevices;

    /**
     * Default constructor for the adapter
     *
     * @param fragment Requires access to the calling fragment
     */
    public DeviceSelectorAdapter(DeviceSelectorFragment fragment) {
        parentFragment = fragment;
        foundDevices = new HashSet<BluetoothDevice>();
    }

    /**
     * Add a new device to the list of recently found, but not connected, devices.
     *
     * @param newDevice A device to add to the list of found devices.
     */
    public void newDeviceFound(BluetoothDevice newDevice) {
        foundDevices.add(newDevice);
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        /* Toggle the view */
        view.setSelected(!view.isSelected());

        System.out.println("PRESSED!");
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    @Override
    public int getCount() {
        return BluetoothUtility.connectedDevices().size() + foundDevices.size();
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
            LayoutInflater layoutInflater = parentFragment.getActivity().getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.device_list_item, null);
        }

        //retrieve the Bluetooth device corresponding to position in list
        BluetoothDevice btDevice = (BluetoothDevice) (BluetoothUtility.connectedDevices().toArray())[position];

        /* Assign the item name */
        TextView itemName = (TextView) convertView.findViewById(R.id.bluetooth_device_list_name);
        itemName.setText(btDevice.getName());

        /* Assign the item icon */
        ImageView itemType = (ImageView) convertView.findViewById(R.id.bluetooth_device_list_type);
        itemType.setImageDrawable(getIconForDevice(btDevice));

        /* Return the view */
        return convertView;
    }

    /**
     * Returns the best-match drawable for the device type
     *
     * @param btDevice  The Bluetooth device to query
     * @return          A drawable matching the best type
     */
    private Drawable getIconForDevice(BluetoothDevice btDevice) {
        Resources res = parentFragment.getResources();
        Drawable icon;
        switch(btDevice.getBluetoothClass().getMajorDeviceClass()) {
            case BluetoothClass.Device.Major.PHONE:
                icon = res.getDrawable(R.drawable.phone);
                break;
            case BluetoothClass.Device.Major.PERIPHERAL:
                icon = res.getDrawable(R.drawable.headphone);
                break;
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                icon = res.getDrawable(R.drawable.speaker);
                break;
            default:
                icon = res.getDrawable(R.drawable.computer);
                break;
        }
        return icon;
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
        return BluetoothUtility.connectedDevices().isEmpty() && foundDevices.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
