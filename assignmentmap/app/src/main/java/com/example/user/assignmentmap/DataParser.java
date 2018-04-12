package com.example.user.assignmentmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 12-04-2018.
 */

public class DataParser
{
    private HashMap<String,String> getDuration(JSONArray googleDirectionJson)
    {
        HashMap<String,String> googleDirectionMap = new HashMap<>();
        String duration="";
        String distance="";

        try
        {
            duration = googleDirectionJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionMap.put("duration",duration);
            googleDirectionMap.put("distance",distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleDirectionMap;
    }

    private HashMap<String ,String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String ,String> googlePlaceMap = new HashMap<>();
        String placeId = "-NA-";
        String vicinity = "-NA-";
        String placeName ="-NA-";
        String current_opening ;
        StringBuilder timings  = new StringBuilder();
        StringBuilder types = new StringBuilder();
        String menu = null;
        String timing = null;
        String lattitude ;
        String longitude ;

        try
        {
            if (!googlePlaceJson.isNull("place_id"))
            {
                placeId = googlePlaceJson.getString("place_id");
            }
            if(!googlePlaceJson.isNull("vicinity"))
            {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            if(!googlePlaceJson.isNull("name"))
            {
                placeName = googlePlaceJson.getString("name");
            }

            lattitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            JSONObject object = googlePlaceJson.getJSONObject("opening_hours");
            current_opening = object.getString("open_now");

            JSONArray array = object.getJSONArray("weekday_text");
            if(array.length()>0)
            {
                for(int i=0;i<array.length();i++)
                {
                    String t = array.getString(i);
                    timings.append(t);
                    timings.append("\n");
                }
                timing = timings.toString();
            }

            JSONArray jsonArray = googlePlaceJson.getJSONArray("types");
            if(jsonArray.length()>0)
            {
                for(int i=0;i<jsonArray.length();i++)
                {
                    String string = jsonArray.getString(i);
                    types.append(string);
                    types.append("\n");
                }
                menu = types.toString();
            }


            googlePlaceMap.put("place_id",placeId);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("name",placeName);
            googlePlaceMap.put("open_now", String.valueOf(current_opening));
            googlePlaceMap.put("timings",timing);
            googlePlaceMap.put("menu",menu);
            googlePlaceMap.put("lattitude",lattitude);
            googlePlaceMap.put("longitude",longitude);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String ,String>> getPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String ,String>> placesList = new ArrayList<>();

        HashMap<String,String> placeMap = null;

        for(int i=0;i<count;i++)
        {
            try
            {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }


    public String[] parseDirections(String jsonData)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPaths(jsonArray);
    }

    public String[] getPaths(JSONArray googleStepsJson)
    {
        int count = googleStepsJson.length();
        String polySteps[] = new String[count];

        for(int i=0;i<count;i++)
        {
            try {
                polySteps[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polySteps;
    }

    public String getPath(JSONObject googlePathJson)
    {
        String polyLine = null;
        try {
            polyLine  = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyLine;
    }
}