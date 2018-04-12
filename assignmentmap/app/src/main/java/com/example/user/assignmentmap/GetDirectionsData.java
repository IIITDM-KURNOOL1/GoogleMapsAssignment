package com.example.user.assignmentmap;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;

/**
 * Created by user on 12-04-2018.
 */

public class GetDirectionsData extends AsyncTask<Object,String,String>
{
    GoogleMap mMap;
    String url;
    String googleDirectionData;
    LatLng latLng;


    @Override
    protected String doInBackground(Object... objects)
    {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        String[] directionList = null;
        DataParser parser = new DataParser();
        directionList = parser.parseDirections(s);
        displayDirections(directionList);

    }

    public void displayDirections(String[] directionList)
    {
        int count = directionList.length;
        for(int i=0;i<count;i++)
        {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionList[i]));

            mMap.addPolyline(options);
        }
    }
}