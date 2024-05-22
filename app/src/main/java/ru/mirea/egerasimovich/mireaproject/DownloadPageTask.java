package ru.mirea.egerasimovich.mireaproject;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadPageTask extends AsyncTask<String, Void, String> {
    TextView weather;
    TextView city;
    TextView region;
    TextView country;
    TextView timezone;
    View view;

    //https://api.open-meteo.com/v1/forecast?latitude=55.75&longitude=37.62&current_weather=true
    public DownloadPageTask(View view) {
        this.view=view;

        this.weather=view.findViewById(R.id.weather);
        this.city=view.findViewById(R.id.city);
        this.region=view.findViewById(R.id.region);
        this.country=view.findViewById(R.id.country);
        this.timezone=view.findViewById(R.id.timezone);// сохраняем ссылку на binding
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        weather.setText("Загружаем...");
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadIpInfo(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    @Override
    protected void onPostExecute(String result) {
        weather.setText("Результат");
        Log.d(MainActivity.class.getSimpleName(), result);
        try {
            JSONObject responseJson = new JSONObject(result);
            Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
            city.setText("city: " + responseJson.getString("city"));
            region.setText("region: " + responseJson.getString("region"));
            country.setText("country: " + responseJson.getString("country"));
            timezone.setText("timezone: " + responseJson.getString("timezone"));
            String[] loc=responseJson.getString("loc").split(",");
            new Weather(view).execute(
                    "https://api.open-meteo.com/v1/forecast?latitude="+loc[0]+"&longitude="+loc[1]+"&current_weather=true"); // запуск нового потока
            Log.d(MainActivity.class.getSimpleName(), "Response: " + "https://api.open-meteo.com/v1/forecast?latitude="+loc[0]+"&longitude="+loc[1]+"&current_weather=true");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }

    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage() + ". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}
