package mddn.swen.headbanger.utilities;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Handles opening connections to an endpoint.
 *
 * Note, this is a pretty old school way of doing this and shouldn't be realistically considered
 * for an actual release.
 *
 * Created by John on 21/10/2014.
 */
public class Networker {

    /**
     * The address of the server
     */
    private static final String SERVER_ADDRESS = "http://128.199.203.148";

    /**
     * Callers should register for a response.
     */
    public interface ConnectionListener {

        /**
         * Called when the server returns, if the payload is malformed or empty, expect null.
         *
         * @param response The JSONObject from the server.
         */
        public void onResponse(JSONObject response);
    }

    /**
     * Arbitrary endpoint opener.
     *
     * @param endpoint Endpoint to open
     * @param params Parameters to send as post, may be null
     * @param listener Listener interested in response, may be null
     */
    public static void openConnection(final String endpoint,
                                      final Map<String, String> params,
                                      final ConnectionListener listener) {
        new Thread() {
            @Override
            public void run() {
                String urlString = SERVER_ADDRESS +
                        (endpoint.startsWith("/") ? "" : "/") +
                        endpoint +
                        (endpoint.endsWith(".php") ? "" : ".php");
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    if (params != null) {
                        Networker.addParamsToConnection(params, connection);
                    }
                    else {
                        connection.setDoOutput(false);
                    }
                    InputStreamReader inputReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(inputReader);
                    String streamString = "";
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        streamString += inputLine;
                    }
                    if (listener != null) {
                        listener.onResponse(streamString != null ? new JSONObject(streamString) : null);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onResponse(null);
                    }
                }
            }
        }.start();
    }

    /**
     * Helper method to add the parameters to the HTTPURLConnection
     *
     * @param params        Parameters to add
     * @param connection    Connection to add to
     */
    private static void addParamsToConnection(Map<String, String> params, HttpURLConnection connection) {
        try {
            connection.setRequestMethod("POST");
            String paramsString = "";
            for (String key : params.keySet()) {
                if (paramsString.length() > 0) {
                    paramsString += "&";
                }
                String query = "";
                query += URLEncoder.encode(key, "UTF-8");
                query += "=";
                query += URLEncoder.encode(params.get(key), "UTF-8");
                paramsString += query;
            }
            OutputStream outStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
            writer.write(paramsString);
            writer.flush();
            writer.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
