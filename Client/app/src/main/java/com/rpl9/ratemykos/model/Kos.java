package com.rpl9.ratemykos.model;

import java.util.ArrayList;
import java.util.Date;

public class Kos {
    public int kos_id;
    public String name;
    public String location;
    public double latitude;
    public double longitude;
    public String description;
    public ArrayList<Date> booked = new ArrayList<Date>();
    public ArrayList<Facility> facilities = new ArrayList<Facility>();
    public Kos_type kos_type;

}
