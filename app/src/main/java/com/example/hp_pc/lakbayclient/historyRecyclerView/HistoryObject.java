package com.example.hp_pc.lakbayclient.historyRecyclerView;

/**
 * Created by HP-PC on 17/03/2018.
 */

public class HistoryObject {
    private String rideID;
    private String time;

    public HistoryObject(String rideID, String time){
        this.rideID = rideID;
        this.time = time;
    }

    public  String getRideID(){return rideID;}
    public  void setRideID(String rideID){this.rideID = rideID;}

    public  String getTime(){return time;}
    public  void setTime(String time){this.time = time;}
}
