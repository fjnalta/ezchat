package eu.ezlife.ezchat.ezchat.components.server;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

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

    private String userName;
    private String contactName;
    private String token;

    public PushMessageConnection(String userName, String contactName, String token) {
        this.userName = userName;
        this.contactName = contactName;
        this.token = token;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject json = new JSONObject();
        try {
            json.put("userName",this.userName);
            json.put("contactName",this.contactName);
            json.put("token",this.token);


            URL url = new URL("http://localhost:8080/msg/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("howtodoinjava", "password".toCharArray());
                }
            });
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write("message=" + json);

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // OK

            } else {

                // Server returned HTTP error code.
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
