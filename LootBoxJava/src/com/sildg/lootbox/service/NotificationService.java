package com.sildg.lootbox.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationService {
    private final String URL = "https://fcm.googleapis.com/fcm/send";
    private URL URLObject;
    private HttpURLConnection connection;

    public NotificationService() {
        try
        {
            this.URLObject = new URL(this.URL);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String createJson(String title, String message) {
        return "{\n" +
                "  \"notification\": \n" +
                "  {\n" +
                "    \"title\": \"" + title + "\",\n" +
                "    \"text\": \"" + message + "\"\n" +
                "  },\n" +
                "  \"priority\" : \"high\",\n" +
                "  \"to\" : \"/topics/ofertas\"\n" +
                "}";
    }

    public void sendNotification(String title, String message) throws IOException {
        
        this.connection = (HttpURLConnection) URLObject.openConnection();
        
        this.connection.setRequestMethod("POST");
        this.connection.setRequestProperty("Content-Type", "application/json");
        this.connection.setRequestProperty("Authorization", "key=AAAAnISleVw:APA91bEfKvy44OcMO4knYaby26HAEKAujSzXxYYFJGSgZrdusRQuGdUxBYr1JUn1CyqNdBmwBHTi4_-e2081v-yFMfNghW3yrW89LSqK4kBkJ2XUKK89WjuBAbMeN0a7BMU6lThRS5-_");
        this.connection.setDoOutput(true);

        String params = createJson(title, message);

        OutputStream os = this.connection.getOutputStream();
        os.write(params.getBytes());
        os.flush();
        os.close();

        int responseCode = this.connection.getResponseCode();

        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK)
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            String response;

            response = in.readLine();
            in.close();

            System.out.println(response.toString());
        }
        else
        {
            System.out.println("POST request not worked");
        }
        
        this.connection.disconnect();
    }
}
