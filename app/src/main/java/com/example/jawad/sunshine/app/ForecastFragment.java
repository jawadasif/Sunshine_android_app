package com.example.jawad.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forcastArray = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        List<String> weekForecast = new ArrayList<String>(
                Arrays.asList(forcastArray)
        );

        ArrayAdapter<String> mForcastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forcast,
                        R.id.list_item_forcast_textview,
                        weekForecast
                );
        ListView listView = (ListView)rootView.findViewById(R.id.listview_forcast);
        listView.setAdapter(mForcastAdapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        @Override
        protected Void doInBackground(Void... params) {
//            These tow need to be declared outside try catch
//        so that they can be close in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

//        Will contain the raw JSON response as string.
            String forcastJsonStr = null;

            try {
//            Construct the URL for the OpenWeatherMap query
//            Possible parameters are available on QWM' forecast API page, at
//            http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=Dhaka&mode=json&units=metric&cnt=7");

//            Create the request to OpenweatherMap, and open the connection
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

//            Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream==null){
//                nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line=reader.readLine()) != null){
//                Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                But it does make debugging a *lot* easier if you print out the completed
//                buffer for debugging.
                    buffer.append(line+"\n");
                }
                if(buffer.length() == 0){
//                Stream is empty. No point in parsing
                    return null;
                }
                forcastJsonStr = buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG,"Error",e);
//            If the code didn't successfully get the weather daata,
//            there is no point in attempting to parse it
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceHoderFragment", "Error closing Stream", e);
                    }
                }
            }
            return null;
        }
    }
}
