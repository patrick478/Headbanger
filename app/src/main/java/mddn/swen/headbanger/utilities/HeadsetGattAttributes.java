package mddn.swen.headbanger.utilities;

import java.util.HashMap;

/**
 * Created by Pragya on 22/10/14.
 */
public class HeadsetGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEADSET_TRACKER = "C8870DF0-D414-7F96-31DB-CF7F4C04E689"; //"00002a37-0000-1000-8000-00805f9b34fb";
    public static String HEADSET_CHARACTERISTIC_CONFIG = "C8870DF1-D414-7F96-31DB-CF7F4C04E689"; //"00002902-0000-1000-8000-00805f9b34fb";

    public static final String DEVICE_NAME = "HMSoft";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Headset Tracking Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEADSET_TRACKER, "Headset Tracking Data");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
