package eu.ezlife.ezchat.ezchat.components.server;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.sasl.packet.SaslStreamElements;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajo on 05.04.17.
 */

public class PushMessageConnection extends AsyncTask<String, String, String> {

    private final String appId = "crWd-zEsn3I:APA91bG31FzgnPcwxSgTLeiXySl7oWq_x-neCEO_nXXEQaTovr8GnNxSiLo93MJYc4xpHN3khFkJQUDF5kFBU7BjpGsrBxwQ4shDxvGfNaH_Qwx2b08uk3zRxIy_MWzGiFSzjby36mUZ";

    private String userName;
    private String contactName;

    public PushMessageConnection(String userName, String contactName) {
        this.userName = userName;
        this.contactName = contactName;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            // create JSON Body
            JSONObject json = new JSONObject();
            json.put("userName", cutDomainFromUsername(this.userName));
            json.put("contactName", cutDomainFromUsername(this.contactName));
            json.put("token", XMPPConnection.getUserToken().trim());

//            URL url = new URL("http://instant.ignorelist.com:8080/ezChatPush/rest/msg");
            URL url = new URL("http://10.0.150.24:8080/rest/msg");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            // Set Basic HTTP Authentication
            String userCredentials = "able" + ":" + appId.toCharArray();
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            conn.setRequestProperty("Authorization", basicAuth);

            // Create Writer
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("PUSH: ", conn.getResponseMessage().toString());
            } else {
                Log.d("PUSH: ", conn.getResponseMessage().toString());
                // Server returned HTTP error code.
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String cutDomainFromUsername(String username) {
        String str = username;
        int dotIndex = str.indexOf("@");
        str = str.substring(0, dotIndex);
        return str;
    }
}
