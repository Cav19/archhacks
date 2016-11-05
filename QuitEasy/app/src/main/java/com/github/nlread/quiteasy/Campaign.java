package com.github.nlread.quiteasy;

import java.io.Serializable;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class Campaign implements Serializable{
    public String campaignType;
    public int campaignID;

    public Campaign(String campaignType, int campaignID){
        this.campaignType = campaignType;
        this.campaignID = campaignID;
    }
}
