package pl.hubertkarbowy.androidrnlu.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import pl.hubertkarbowy.androidrnlu.MainActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;

import com.example.hubert.myapplication.R;

/**
 * Created by hubert on 15.04.18.
 */

public class TCPClient {

    private static String TAG = "TCPClient";
    private Socket connectionSocket;

    private String nlIntent = null;
    private Handler handler;
    private MainActivity a = null;
    private SharedPreferences prefs;
    private String serverIp;
    private int serverPort;
    private String currentLocale = "pl_PL";
    private boolean isNlIntentPreformatted = false;

    private FulfilledIntent nluResult;
    // tutaj mapa z kontekstem klienta


    public TCPClient(String nlIntent, Handler handler, MainActivity a) {
        this(nlIntent, handler, a, false);
    }

    public TCPClient(String nlIntent, Handler handler, MainActivity a, boolean preformattingFlag) {
        this.nlIntent=nlIntent;
        this.handler=handler;
        this.a=a;
        prefs = PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());
        serverIp = prefs.getString("server_addr", "192.168.1.14");
        serverPort = Integer.parseInt(prefs.getString("server_port", "55100"));
        currentLocale = prefs.getString("culture", "pl_PL");
        this.isNlIntentPreformatted = preformattingFlag;
    }

    public class ConnectRunnable implements Runnable {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIp);
                connectionSocket = new Socket();
                connectionSocket.connect(new InetSocketAddress(serverAddr, serverPort), 5000);
                OutputStream os = connectionSocket.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                PrintWriter pw = new PrintWriter(os);
                String line;
                StringBuffer sb = new StringBuffer();
                final String decodedNlIntentAndSlots;
                final String displayAndSpokenText;
                line = br.readLine(); // Resp: SimpleRnlu Server
                br.readLine(); // protocol info
                br.readLine(); // protocol info

                pw.println(currentLocale);
                pw.flush(); // TODO: 1) w serwerze ACK + readLine, 2) Sprawdzenie czy ACK czy unsupported culture
                pw.println("/def"); // tu bedziemy dawac /sc i parametry
                pw.flush();
                if (isNlIntentPreformatted) pw.println(nlIntent);
                else pw.println("/cmd " + nlIntent);
                pw.flush();
                decodedNlIntentAndSlots = br.readLine();
                displayAndSpokenText = br.readLine();
                pw.println("/q");
                pw.flush();
                Log.d(TAG, "Response: " + displayAndSpokenText);
                nluResult = new FulfilledIntent(ServerResponses.extractDisplayText(displayAndSpokenText),
                        ServerResponses.extractSpokenText(displayAndSpokenText),
                        ServerResponses.extractNlIntent(decodedNlIntentAndSlots),
                        ServerResponses.extractNlSlots(decodedNlIntentAndSlots),
                        decodedNlIntentAndSlots.contains("ELICITATION_ACTION") ? true : false);

//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        a.setAndReadResponse(ServerResponses.extractDisplayText(displayAndSpokenText),
//                                ServerResponses.extractSpokenText(displayAndSpokenText),
//                                ServerResponses.extractNlIntent(decodedNlIntentAndSlots),
//                                ServerResponses.extractNlSlots(decodedNlIntentAndSlots));
//
////                        a.executeIntent(ServerResponses.extractNlIntent(decodedNlIntentAndSlots),
////                                        ServerResponses.extractNlSlots(decodedNlIntentAndSlots));
//                    }
//                });

                connectionSocket.close();

            } catch (Exception e) {
                nluResult = new FulfilledIntent("Network error", "Network error", "NoOp", null, false);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        a.setAndReadResponse(a.getString(R.string.networkError), a.getString(R.string.networkError), "NoOp", null);
//                    }
//                });
            }
        }
    }

    public FulfilledIntent sendQuery() {
        Thread t = new Thread(new ConnectRunnable());
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException e) {}
        Log.d("KONIEC", "Tu sie konczy");
        return nluResult;
    }

}
