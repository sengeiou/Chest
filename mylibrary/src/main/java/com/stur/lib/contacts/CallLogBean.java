package com.stur.lib.contacts;

import java.io.Serializable;

/**
 * Created by guanxuejin on 2018/7/8.
 */

public class CallLogBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5720163825091386238L;


    private long id;

    private String phoneNumber;

    private String normalizedNumber;

    private long date; //calling time

    private int  duration;

    private int  type;

    private byte isNew;

    private String name;

    private int  numberType; //response to number type in contact

    private String numberLabel;

    private int missedCount;

    //it is coolpad field as follow
    private int moduleType; //type of network, such as CDMA

    private long callsStatId;

    private long contactId;

    private byte isPrivate;

    private long ringCount;

    private long confId;

    public long getConfId() {
        return confId;
    }

    public void setConfId(long confId) {
        this.confId = confId;
    }
    private char subNumOrder;

    public char getSubNumOrder() {
        return subNumOrder;
    }

    public void setSubNumOrder(char subNumOrder) {
        this.subNumOrder = subNumOrder;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte getIsNew() {
        return isNew;
    }

    public boolean isNew() {
        return isNew != 0;
    }

    public void setIsNew(byte isNew) {
        this.isNew = isNew;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberType() {
        return numberType;
    }

    public void setNumberType(int numberType) {
        this.numberType = numberType;
    }


    public int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public long getCallsStatId() {
        return callsStatId;
    }

    public void setCallsStatId(long callsStatId) {
        this.callsStatId = callsStatId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public byte getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(byte isPrivate) {
        this.isPrivate = isPrivate;
    }

    public long getRingCount() {
        return ringCount;
    }

    public void setRingCount(long ringCount) {
        this.ringCount = ringCount;
    }

    public String getNumberLabel() {
        return numberLabel;
    }

    public void setNumberLabel(String numberLabel) {
        this.numberLabel = numberLabel;
    }


    public void setMissedCount(int count) {
        this.missedCount = count;
    }

    public void increaseMissedCount() {
        this.missedCount += 1;
    }

    public int getMissedCount() {
        return missedCount;
    }

//	public String getNormalizeNumber() {//findbugs
//	    return normalizedNumber;
//	}

}