package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

/**
 * Event DB
 * Schema SQL representant un evenement
 */
@Entity //(indices = {@Index("admin")},foreignKeys = {
        //@ForeignKey(entity = UserDB.class,
        //parentColumns = "EMAIL",
       // childColumns = "admin")
//})
public class EventDB implements Serializable{
    @PrimaryKey
    @NonNull
    @ColumnInfo
    private String eventID;

    @ColumnInfo
    private String admin;

    @ColumnInfo
    private String eventName;

    @ColumnInfo
    private String date;

    @ColumnInfo
    private String debutTime;

    @ColumnInfo
    private String eventType;

    @ColumnInfo
    private String location;

    @ColumnInfo
    private double longitude;

    @ColumnInfo
    private double latitude;

    //Constructeurs
    public EventDB(){}
    public EventDB(@NonNull String eventID, String admin, String eventName, String date, String debutTime, String eventType, String location, double longitude, double latitude){
        this.eventID = eventID;
        this.admin = admin;
        this.eventName = eventName;
        this.date = date;
        this.debutTime = debutTime;
        this.eventType = eventType;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    @Override
    public String toString(){
        String description = "Description: Id: "+this.eventID+" admin: "+this.admin+" eventName: "+this.eventName+" date: "+this.date+" debutTime: "+this.debutTime+" eventType: "+this.eventType+" location: "+this.location+ " long: "+this.longitude+ " lat: "+this.latitude;
        return description;
    }

    //Getters and Setters


    @NonNull
    public String getEventID() {
        return eventID;
    }

    public void setEventID(@NonNull String eventID) {
        this.eventID = eventID;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDebutTime() {
        return debutTime;
    }

    public void setDebutTime(String debutTime) {
        this.debutTime = debutTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
