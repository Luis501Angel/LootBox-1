package com.sildg.lootbox;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class HttpUrl {

    private final String URL = "https://sildg-notifications.firebaseio.com/location.json";
    private URL URLObject;
    private HttpURLConnection connection;

    public HttpUrl() {
        try
        {
            this.URLObject = new URL(this.URL);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void get() {

        try {
            this.connection = (HttpURLConnection) URLObject.openConnection();
            this.connection.setRequestMethod("GET");
            this.connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            System.out.println("GET Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void post(String location, double lat, double lon) {

        try {
            this.connection = (HttpURLConnection) URLObject.openConnection();
            this.connection.setRequestMethod("POST");
            this.connection.setRequestProperty("Content-Type", "application/json");
            this.connection.setDoOutput(true);

            String params = createJson("location", 19, 18);

            OutputStream os = this.connection.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            int responseCode = this.connection.getResponseCode();

            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
                String response;

                response = in.readLine();
                in.close();

                System.out.println(response.toString());
            } else {
                System.out.println("POST request not worked");
            }

            this.connection.disconnect();
        }
        catch (IOException e) {

        }
    }

    private String createJson(String nombre, double lat, double lon) {
        return "{\n" +
                "\t\"nombre\": \"" + nombre + "\",\n" +
                "\t\"lat\": " + lat + ",\n" +
                "\t\"lon\": " + lon + "\n" +
                "}";
    }

    public void access(String nombre, double lat, double lon) {
        DatabaseReference mDatabase;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Location location = new Location("Cordoba", 18,17);

        mDatabase.child("location").child("child").setValue(new Location());
    }

    @IgnoreExtraProperties
    class Location {
        public String nombre;
        public String direccion;
        public double lat;
        public double lon;

        public Location(){}

        public Location(String nombre, String direccion, double lat, double lon) {
            this.nombre = nombre;
            this.direccion = direccion;
            this.lat = lat;
            this.lon = lon;
        }
    }
}
