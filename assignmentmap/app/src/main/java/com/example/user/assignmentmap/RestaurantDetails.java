package com.example.user.assignmentmap;

/**
 * Created by user on 11-04-2018.
 */
public class RestaurantDetails
{
    public String mName;
    public String mVicinity;
    public String menu;
    public String timings;
    public String contactInfo;
    public String lattitude;
    public String longitude;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName)
    {
        this.mName = mName;
    }

    public String getmVicinity()
    {
        return mVicinity;
    }

    public void setmVicinity(String mVicinity)
    {
        this.mVicinity = mVicinity;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}