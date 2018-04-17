package pl.hubertkarbowy.androidrnlu.util;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static pl.hubertkarbowy.androidrnlu.util.SimpleRnluClientSettings.*;

/**
 * Created by hubert on 15.04.18.
 */

public class ServerQuery implements Runnable {

    private String nlIntent = null;
    private final TextView outDisplay;
    private Context context;

    public ServerQuery(Context context, String nlIntent, TextView outDisplay) {
        this.context = context;
        this.nlIntent=nlIntent;
        this.outDisplay=outDisplay;
    }

    public void run () {
//        RequestQueue queue = Volley.newRequestQueue(context);
//        final String url = serverIp+":"+serverPort;
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                outDisplay.setText(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                outDisplay.setText("Error talking to " + url);
//            }
//        }
//        );
//        queue.add(stringRequest);
        try {
            InetAddress serverAddr = InetAddress.getByName(serverIp);
            Socket connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(serverAddr, serverPort), 5000);
            Log.d("ZZZZZ", "Już");
            connectionSocket.close();
            outDisplay.setText("Connected and closed successfully");

        } catch (Exception e) {
            // outDisplay.setText("Error talking to " + serverIp+":"+serverPort + "\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
