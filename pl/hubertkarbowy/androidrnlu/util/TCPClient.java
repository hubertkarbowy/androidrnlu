package pl.hubertkarbowy.androidrnlu.util;

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

import static pl.hubertkarbowy.androidrnlu.util.SimpleRnluClientSettings.*;

/**
 * Created by hubert on 15.04.18.
 */

public class TCPClient {

    private static String TAG = "TCPClient";
    private Socket connectionSocket;

    private String nlIntent = null;
    private Handler handler;
    private MainActivity a = null;
    // tutaj mapa z kontekstem klienta


    public TCPClient(String nlIntent, Handler handler, MainActivity a) {
        this.nlIntent=nlIntent;
        this.handler=handler;
        this.a=a;
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
                final String decodedIntent;
                final String displayAndSpokenText;
                line = br.readLine(); // Resp: SimpleRnlu Server
                br.readLine(); // protocol info
                br.readLine(); // protocol info

                pw.println("pl_PL");
                pw.flush();
                pw.println("/def"); // tu bedziemy dawac /sc i parametry
                pw.flush();
                pw.println("/cmd " + nlIntent);
                pw.flush();
                decodedIntent = br.readLine();
                displayAndSpokenText = br.readLine();
                pw.println("/q");
                pw.flush();
                Log.d(TAG, "Odpowiedź: " + displayAndSpokenText);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        a.setAndReadResponse(ServerResponses.extractDisplayText(displayAndSpokenText),
                                             ServerResponses.extractSpokenText(displayAndSpokenText));
                    }
                });

                connectionSocket.close();

            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        a.setAndReadResponse("Ups, coś nie poszło.", "Czynność niedostępna."); // TODO: Externalize strings
                    }
                });
            }
        }
    }

    public void sendQuery() {
        new Thread(new ConnectRunnable()).start();
    }

}
