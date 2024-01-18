package es.upm.etsiinf.pui.pui_newsmanager.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import es.upm.etsiinf.pui.pui_newsmanager.model.Image;
import es.upm.etsiinf.pui.pui_newsmanager.model.ModelManager;

public class NetUtil {

    public static String getURLText(String url, String authToken, int parametersLength) throws Exception {

        URL website = new URL(url);
        URLConnection connection = website.openConnection();

        //Connection settings
        //connection.setInstanceFollowRedirects(false);
        //connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", authToken);
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(parametersLength));
        connection.setUseCaches (false);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        Log.i("NetUtil", response.toString());

        return response.toString();
    }
}
