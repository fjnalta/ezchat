package eu.ezlife.ezchat.ezchat.components.server;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

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
//            URL url = new URL("http://instant.ignorelist.com:8080/ezChatPush/rest/msg");
            URL url = new URL("http://192.168.0.11:8080/rest/msg");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("able", appId.toCharArray());
                }
            });

            JSONObject json = new JSONObject();
            json.put("userName",cutDomainFromUsername(this.userName));
            // TODO - change to contactName
            json.put("contactName",cutDomainFromUsername(this.userName));
            json.put("token",XMPPConnection.getUserToken());

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(json.toString());
            writer.flush();

            urlConnection.getInputStream();

//            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("PUSH: ", urlConnection.getResponseMessage().toString());
//            } else {

//                Log.d("PUSH: ", urlConnection.getResponseMessage().toString());
                // Server returned HTTP error code.
//            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
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
