package eu.ezlife.ezchat.ezchat.components.restServices;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.util.stringencoder.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ajo on 13.04.17.
 */

public class TokenRegistrationConnection extends AsyncTask<String, String, String> {

    private final String cloudTokenUrl = "https://ezlife.eu:5238/ezChatPush/rest/token";

    private String userName;
    private String token;

    public TokenRegistrationConnection(String token, String userName) {
        this.token = token;
        this.userName = userName;
    }

    // TODO - work with asynchronous status codes in constructor
    @Override
    protected String doInBackground(String... params) {
        try {
            // create JSON Body
            JSONObject json = new JSONObject();
            json.put("id", null);
            json.put("username", cutDomainFromUsername(this.userName));
            json.put("token", token.trim());

            URL url = new URL(cloudTokenUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            // Set Basic HTTP Authentication
            String userCredentials = cutDomainFromUsername(this.userName) + ":" + "APPID";
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            conn.setRequestProperty("Authorization", basicAuth);

            // Create Writer
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("TokenRegistration: ", conn.getResponseMessage().toString());
            } else {
                Log.d("TokenRegistration: ", conn.getResponseMessage().toString());
                // Server returned HTTP error code.
            }

            // USE NEW FIREBASE TOKEN
            //FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MyActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            //    @Override
            //    public void onSuccess(InstanceIdResult instanceIdResult) {
            //        String newToken = instanceIdResult.getToken();
            //        Log.e("newToken",newToken);

            //    }
            //});

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    // TODO - clean this shit
    private String cutDomainFromUsername(String username) {
        String str = username;
        int dotIndex = str.indexOf("@");
        str = str.substring(0, dotIndex);
        return str;
    }
}
