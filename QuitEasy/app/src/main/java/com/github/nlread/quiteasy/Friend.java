package com.github.nlread.quiteasy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class Friend implements Serializable{
    public int id;
    public String firstName;
    public String lastName;
    public String username;
    public ArrayList<Campaign> campaigns;
    public Date lastTimeEncouraged;

    public Friend(int id, String firstName, String lastName, String username, ArrayList<Campaign> campaigns, Date lastTimeEncouraged){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.campaigns = campaigns;
        this.lastTimeEncouraged = lastTimeEncouraged;
    }
}
