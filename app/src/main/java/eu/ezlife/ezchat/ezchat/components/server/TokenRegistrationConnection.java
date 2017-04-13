package eu.ezlife.ezchat.ezchat.components.server;

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

    // TODO - set appid in preferences
    private final String appId = "crWd-zEsn3I:APA91bG31FzgnPcwxSgTLeiXySl7oWq_x-neCEO_nXXEQaTovr8GnNxSiLo93MJYc4xpHN3khFkJQUDF5kFBU7BjpGsrBxwQ4shDxvGfNaH_Qwx2b08uk3zRxIy_MWzGiFSzjby36mUZ";
    private final String cloudTokenUrl = "http://10.0.150.24:8080/rest/token";

    private String userName;
    private String token;


    public TokenRegistrationConnection(String token, String userName) {
        this.token = token;
        this.userName = userName;
    }

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
            String userCredentials = cutDomainFromUsername(this.userName) + ":" + appId.toCharArray();
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

    // TODO - clean this shit
    private String cutDomainFromUsername(String username) {
        String str = username;
        int dotIndex = str.indexOf("@");
        str = str.substring(0, dotIndex);
        return str;
    }
}
