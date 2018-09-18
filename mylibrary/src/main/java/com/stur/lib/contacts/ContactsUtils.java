package com.stur.lib.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.stur.lib.Log;
import com.stur.lib.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sturmegezhutz on 2018/7/7.
 */

public class ContactsUtils {
    private static final int MAX_HISTORY_RECORD = 99;

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static String[] getAppropriateProjections() {
        boolean isCoolpadDevice = true;

        String filedId = "_id";
        String filedNumber = "number";
        String filedName = "name";
        String filedType = "type";
        String filedDate = "date";
        String fileDuration = "duration";

        // coolpad column
        String filedModuletype = "moduletype";
        String fileRing = "ring_count";

        String[] coolpadprojection = { filedId, filedNumber, filedName,
                filedType, filedDate, fileDuration, filedModuletype, fileRing };
        String[] defaultprojection = { filedId, filedNumber, filedName,
                filedType, filedDate, fileDuration };
        return isCoolpadDevice ? coolpadprojection
                : defaultprojection;
    }

    public static void queryContacts(Context context) {
        //联系人的hash表，以contact表的_ID作为key值
        Map<String, ContactSummaryBean> summaryMap = null;
        ContentResolver resolver = context.getContentResolver();
        // Uri raw_contacts_uri = ContactsContract.Contacts.CONTENT_URI;
        Uri raw_contacts_uri = Uri.parse("content://com.android.contacts/contacts");
        // Uri data_uri = ContactsContract.Data.CONTENT_URI;
        Uri data_uri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resolver.query(raw_contacts_uri, null, null, null, null);
        while (cursor.moveToNext()) {
            ContactSummaryBean contact = null;
            String contact_id = cursor.getString(cursor.getColumnIndex("name_raw_contact_id"));
            if (null != contact_id) {
                Cursor data_cursor = resolver.query(data_uri, null, "raw_contact_id=?", new String[]{contact_id},
                        null);
                Log.d(getTag(), "contact_id : " + contact_id);
                contact = new ContactSummaryBean();
                while (data_cursor.moveToNext()) {
                    String data1 = data_cursor.getString(data_cursor.getColumnIndex("data1"));
                    String mimetype = data_cursor.getString(data_cursor.getColumnIndex("mimetype"));
                    if (!TextUtils.isEmpty(data1)) {
                        Log.d(getTag(), "data1 : " + data1);
                    }
                    if (!TextUtils.isEmpty(mimetype)) {
                        Log.d(getTag(), "mimetype : " + mimetype);
                    }

                    if (mimetype.equals("vnd.android.cursor.item/name")) {
                        contact.setDisplayName(data1);
                    } else if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                        // 作此判断用来获取一个姓名保存多个手机号码的记录
                    }
                }
                data_cursor.close();
                summaryMap.put(contact_id, contact);
            }
        }
        cursor.close();
    }

    /**
     * 得到联系人通话记录
     * @param contactId
     * @return
     */
    public static List<CallLogBean> getCallLogsByContactId(Context context, long contactId, int count) {
        boolean isPrivateMode = false;
        boolean isPhoneModel30 = false;

        List<CallLogBean> result = new ArrayList<CallLogBean>();
        CallLogBean bean = null;
        String filedId = "_id";
        String filedNumber = "number";
        String filedName = "name";
        String filedType = "type";
        String filedDate = "date";
        String fileDuration="duration";

        //coolpad column
        String filedModuletype="moduletype";
        String fileRing="ring_count";

        ContentResolver resolver = context.getContentResolver();
        String selection = new String(" contactid = " + contactId + " ");
        if (!isPrivateMode) {
            selection += " AND isprivate=0";
        }
        String sortOrder = " date DESC limit " + count;
        Cursor cursor = null;
        try {
            cursor = resolver.query(Uri.parse("content://call_log/calls"),
                    getAppropriateProjections(), selection, null, sortOrder);
            if ((cursor != null) && (cursor.getCount() > 0)) {
                int idIndex = cursor.getColumnIndex(filedId);
                int numberIndex = cursor.getColumnIndex(filedNumber);
                int nameIndex = cursor.getColumnIndex(filedName);
                int typeIndex = cursor.getColumnIndex(filedType);
                int dateIndex = cursor.getColumnIndex(filedDate);
                int durationIndex = cursor.getColumnIndex(fileDuration);
                // coolpad column
                int moduletypeIndex = cursor.getColumnIndex(filedModuletype);
                int ringIndex=cursor.getColumnIndex(fileRing);

                while (cursor.moveToNext()) {
                    bean = new CallLogBean();
                    bean.setId(cursor.getLong(idIndex));
                    //bean.setPhoneNumber(StringUtil.trimToEmpty(cursor.getString(numberIndex)));
                    String phone = StringUtils.trimToEmpty(cursor.getString(numberIndex));
                    char subNumOrder = '0';
                    if(!TextUtils.isEmpty(phone)){
                        if(phone.startsWith("12583") && phone.length() > 6){
                            subNumOrder = phone.charAt(5);
                            phone = phone.substring(6, phone.length());
                        }
                    }
                    bean.setSubNumOrder(subNumOrder);
                    bean.setPhoneNumber(phone);
                    bean.setName(StringUtils.trimToEmpty(cursor.getString(nameIndex)));
                    bean.setType(cursor.getInt(typeIndex));
                    bean.setDate(cursor.getLong(dateIndex));
                    bean.setDuration(cursor.getInt(durationIndex));
                    // coolpad column
                    if (moduletypeIndex > 0) {
                        int moduleType = cursor.getInt(moduletypeIndex);
                        if (isPhoneModel30) {
                            moduleType &= 0x0000FFFF;
                        }
                        bean.setModuleType(moduleType);
                    }
                    if (ringIndex > 0) {
                        bean.setRingCount(cursor.getInt(ringIndex));
                    }
                    result.add(bean);

                    if (result.size() > MAX_HISTORY_RECORD) {
                        break;
                    }
                }
            } else {
                Log.d(getTag(), "NO Call Log Records!");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(getTag(), "getCallLogsByContactId size = " + result.size());
        return result;
    }
}
