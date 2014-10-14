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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    /* Used to denote the item type to pull */
    private static final int DEVICE_ROW     = 0;
    private static final int SECTION_HEADER = 1;

    /**
     * The holding fragment
     */
    private DeviceSelectorFragment parentFragment;

    /**
     * A set of all the devices found in a search
     */
    private Set<BluetoothDevice> foundDevices;

    /**
     * A list containing "Bluetooth Devices" and "Section Header" titles
     */
    private List<Object> listItems;

    /**
     * Default constructor for the adapter
     *
     * @param fragment Requires access to the calling fragment
     */
    public DeviceSelectorAdapter(DeviceSelectorFragment fragment) {
        parentFragment = fragment;
        foundDevices = new HashSet<BluetoothDevice>();
        listItems = new ArrayList<Object>();
        if (BluetoothUtility.connectedDevices().size() > 0) {
            listItems.add(parentFragment.getString(R.string.devices_connected_header));
            listItems.addAll(BluetoothUtility.connectedDevices());
        }
    }

    /**
     * Add a new device to the list of recently found, but not connected, devices.
     *
     * @param newDevice A device to add to the list of found devices.
     */
    public void newDeviceFound(BluetoothDevice newDevice) {
        if (foundDevices.add(newDevice)) {
            if (foundDevices.size() == 1) {
                listItems.add(parentFragment.getString(R.string.devices_available_header));
            }
            listItems.add(newDevice);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        /*do nothing if header is tapped*/
        if ((getItemViewType(position) != SECTION_HEADER)){
            /* Toggle the view */
            view.setSelected(!view.isSelected());

            System.out.println("Tapped on a Bluetooth device!");
        }

    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == SECTION_HEADER) {
            return getSectionHeader(position, convertView, parent);
        }
        else {
            return getRowItem(position, convertView, parent);
        }
    }

    /**
     * Returns the section header for this item
     */
    private View getSectionHeader(int position, View convertView, ViewGroup parent) {

        /* Recycle views */
        if (convertView == null || !convertView.getTag().equals(Integer.valueOf(SECTION_HEADER))){
            LayoutInflater layoutInflater = parentFragment.getActivity().getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.device_list_section, null);
            convertView.setTag(Integer.valueOf(SECTION_HEADER));
        }

        /* Assign the text */
        TextView sectionHeader = (TextView) convertView.findViewById(R.id.list_section_header_title);
        sectionHeader.setText((String) listItems.get(position));

        /* Return the view */
        return convertView;
    }

    /**
     * Returns the row item for the actual bluetooth device
     */
    private View getRowItem(int position, View convertView, ViewGroup parent) {

        /* Recycle views */
        if (convertView == null || !convertView.getTag().equals(Integer.valueOf(DEVICE_ROW))){
            LayoutInflater layoutInflater = parentFragment.getActivity().getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.device_list_item, null);
            convertView.setTag(Integer.valueOf(DEVICE_ROW));
        }

        /* Get the bluetooth device at this index */
        BluetoothDevice btDevice = (BluetoothDevice) listItems.get(position);

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
        if (listItems.get(position) instanceof String) {
            return SECTION_HEADER;
        }
        else {
            return DEVICE_ROW;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2; //Generic list items and section headers
    }

    @Override
    public boolean isEmpty() {
        return BluetoothUtility.connectedDevices().isEmpty() && foundDevices.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false; //Section headers are not enabled
    }

    @Override
    public boolean isEnabled(int position) {
        return true; //TODO section headers not enabled
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}


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
}
