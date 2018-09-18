package com.stur.lib.contacts;

import java.io.Serializable;

/**
 * 联系人概要信息实体类. 只包含联系人的概要信息, 一般在显示列表时用.
 * 与ContactBean的区别:ContactBean包含一个联系人的所有数据,如RawContact的数据.
 * Created by Sturmegezhutz on 2018/7/7.
 */

public class ContactSummaryBean implements Serializable {
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -4924753253575692368L;

    //~ Instance fields --------------------------------------------------------

    /** 联系人id */
    private long id;

    /** 用于选择号码或者选择邮件等数据类型的时候使用*/
    private long dataID;

    private int dataType;

    /** 联系人显示名 */
    private String displayName;

    /** 是否为收藏(加星)联系人 */
    private boolean starred;

    /** 是否为私密联系人 */
    private boolean isSecret;

    /** 联系次数 */
    private int timesContacted;

    /** 联系人IM在线状态 */
    private int contactPresence;

    /** 联系人头像ID,data表外键 */
    private long photoID;

    /** 该联系人是否有电话号码 */
    private int hasPhoneNumber;

    /** 该联系人的默认电话号码(主电话或第一个电话) */
    private String defaultTel;

    /** 查询字段 */
    private String lookup;

    /** 添加字段，首字母,标签信息 */
//    private String firstWord;

//    /** 添加字段，高亮开始位置 */
//    private int highLightFirst;
//
//    /** 添加字段，高亮结束位置 */
//    private int highLightLast;
//
//    /** 添加字段，号码高亮开始位�? */
//    private int numHighLightFirst;
//
//    /** 添加字段，号码高亮结束位�? */
//    private int numHighLightLast;
    /** 添加字段，联系人最后联系时间 */
    private long lastContactedTime;

    /**随机头像索引*/
    private int randomPhotoIndex;

    /*
     *
     */
    private long lastUpdatedTime;

    /**
     * sns标识
     */
//    private String snsName;

    /**
     * 最新状态信息
     */
//    private String snsLastStatusText;

    /**
     * 最新状态信息ID
     */
//    private int statusUpdateId = -1;


    /**
     * @return the statusUpdateId
     */
    public int getStatusUpdateId() {
        return 0;
    }

    /**
     * @param statusUpdateId the statusUpdateId to set
     */
    public void setStatusUpdateId(int statusUpdateId) {
    }
    /**
     * @return isSnsContact
     */
    public boolean isSnsContact() {
        return false;
    }

    /**
     * @return the snsName
     */
    public String getSnsName() {
        return "";
    }

    /**
     * @param snsName the snsName to set
     */
    public void setSnsName(String snsName) {
    }

    /**
     * @return the snsLastStatusText
     */
    public String getSnsLastStatusText() {
        return "";
    }

    /**
     * @param snsLastStatusText the snsLastStatusText to set
     */
    public void setSnsLastStatusText(String snsLastStatusText) {
//        this.snsLastStatusText = snsLastStatusText;
    }

    /**
     *
     * @return the _id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @return randomPhotoIndex
     */
    public int getRandomPhotoIndex() {
        return randomPhotoIndex;
    }

    /**
     *
     * @param randomPhotoIndex randomPhotoIndex
     */
    public void setRandomPhotoIndex(int randomPhotoIndex) {
        this.randomPhotoIndex = randomPhotoIndex;
    }

    /**
     *
     * @param id the _id to set
     */
    public void setId(final long id) {
        this.id = id;
    }

    /**
     *
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param displayName the displayName to set
     */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @return the starred
     */
    public boolean isStarred() {
        return starred;
    }

    /**
     *
     * @param starred the starred to set
     */
    public void setStarred(final boolean starred) {
        this.starred = starred;
    }

    /**
     * @return the isSecret
     */
    public boolean isSecret() {
        return isSecret;
    }

    /**
     * @param isSecret the isSecret to set
     */
    public void setSecret(boolean isSecret) {
        this.isSecret = isSecret;
    }

    /**
     *
     * @return the timesContacted
     */
    public int getTimesContacted() {
        return timesContacted;
    }

    /**
     *
     * @param timesContacted the timesContacted to set
     */
    public void setTimesContacted(final int timesContacted) {
        this.timesContacted = timesContacted;
    }

    /**
     *
     * @return the contactPresence
     */
    public int getContactPresence() {
        return contactPresence;
    }

    /**
     *
     * @param contactPresence the contactPresence to set
     */
    public void setContactPresence(final int contactPresence) {
        this.contactPresence = contactPresence;
    }

    /**
     *
     * @return the photoID
     */
    public long getPhotoID() {
        return photoID;
    }

    /**
     *
     * @param photoID the photoID to set
     */
    public void setPhotoID(final long photoID) {
        this.photoID = photoID;
    }

    /**
     *
     * @return the hasPhoneNumber
     */
    public boolean isHasPhoneNumber() {
        return hasPhoneNumber != 0;
    }

    /**
     *
     * @param hasPhoneNumber the hasPhoneNumber to set
     */
    public void setHasPhoneNumber(final boolean hasPhoneNumber) {
        this.hasPhoneNumber = hasPhoneNumber ? 1 : 0;
    }

    public int getHasPhoneNumber() {
        return hasPhoneNumber;
    }

    /**
     *
     * @return the defaultTel
     */
    public String getDefaultTel() {
        return defaultTel;
    }

    /**
     *
     * @param defaultTel the defaultTel to set
     */
    public void setDefaultTel(final String defaultTel) {
        this.defaultTel = defaultTel;
    }

    /**
     *
     * @return the lookup
     */
    public String getLookup() {
        return lookup;
    }

    /**
     *
     * @param lookup the lookup to set
     */
    public void setLookup(final String lookup) {
        this.lookup = lookup;
    }

    /**
     *
     * @return the firstWord
     */
//    public String getFirstWord() {
//        return firstWord;
//    }

    /**
     *
     * @param firstWord the firstWord to set
     */
//    public void setFirstWord(final String firstWord) {
//        this.firstWord = firstWord;
//    }

//    /**
//     *
//     * @return the highLightFirst
//     */
//    public int getHighLightFirst() {
//        return highLightFirst;
//    }
//
//    /**
//     *
//     * @param highLightFirst the highLightFirst to set
//     */
//    public void setHighLightFirst(final int highLightFirst) {
//        this.highLightFirst = highLightFirst;
//    }
//
//    /**
//     *
//     * @return the highLightLast
//     */
//    public int getHighLightLast() {
//        return highLightLast;
//    }
//
//    /**
//     *
//     * @param highLightLast the highLightLast to set
//     */
//    public void setHighLightLast(final int highLightLast) {
//        this.highLightLast = highLightLast;
//    }

    /**
     *
     * @return the lastContactedTime
     */
    public long getLastContactedTime() {
        return lastContactedTime;
    }

    /**
     *
     * @param lastContactedTime the lastContactedTime to set
     */
    public void setLastContactedTime(final long lastContactedTime) {
        this.lastContactedTime = lastContactedTime;
    }

    /**
     *
     * @return the numHighLightFirst
     */
    public int getNumHighLightFirst() {
        return 0;
    }

    /**
     *
     * @param numHighLightFirst the numHighLightFirst to set
     */
    public void setNumHighLightFirst(int numHighLightFirst) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return the numHighLightLast
     */
    public int getNumHighLightLast() {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param numHighLightLast the numHighLightLast to set
     */
    public void setNumHighLightLast(int numHighLightLast) {

    }


    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    /**    **/
    private static final int THIRTY_NUMBER = 32;

    /**
     *
     *
     * @return
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (int) (id ^ (id >>> THIRTY_NUMBER));
        result = (prime * result)
                + ((defaultTel == null) ? 0 : defaultTel.hashCode());

        return result;
    }

    /**
     *
     *
     * @param obj
     *
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        ContactSummaryBean other = (ContactSummaryBean) obj;

        if (id != other.id) {
            return false;
        }

        if (photoID != other.photoID) {
            return false;
        }

        if (defaultTel == null) {
            if (other.defaultTel != null) {
                return false;
            }
        } else if (!defaultTel.equals(other.defaultTel)) {
            return false;
        }

        return true;
    }

    public ContactSummaryBean cloneSelf() {
        ContactSummaryBean bean = new ContactSummaryBean();
        bean.id = this.id;
        bean.dataID = this.dataID;
        bean.dataType = this.dataType;
        bean.displayName = this.displayName;
        bean.starred = this.starred;
        bean.isSecret = this.isSecret;
        bean.timesContacted = this.timesContacted;
        bean.contactPresence = this.contactPresence;
        bean.photoID = this.photoID;
        bean.hasPhoneNumber = this.hasPhoneNumber;
        bean.defaultTel = this.defaultTel;
        bean.lookup = this.lookup;
        bean.lastContactedTime = this.lastContactedTime;
        bean.randomPhotoIndex = this.randomPhotoIndex;
        bean.lastUpdatedTime = this.lastUpdatedTime;
        return bean;
    }

    public long getDataID() {
        return dataID;
    }

    public void setDataID(long dataID) {
        this.dataID = dataID;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
