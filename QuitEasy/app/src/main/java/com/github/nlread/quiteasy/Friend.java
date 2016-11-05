package com.github.nlread.quiteasy;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class Friend implements Serializable{
    public String username;
    public Campaign[] campaigns;
    public Date lastTimeEncouraged;

    public Friend(String username, Campaign[] campaigns, Date lastTimeEncouraged){
        this.username = username;
        this.campaigns = campaigns;
        this.lastTimeEncouraged = lastTimeEncouraged;
    }
}
