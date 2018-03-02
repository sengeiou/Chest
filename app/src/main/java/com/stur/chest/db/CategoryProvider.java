package com.stur.chest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.stur.chest.dto.CategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class CategoryProvider {
	private static String LOG_TAG = "CategoryProvider";
	private static String DB_NAME = "etdz.db";
	private static int DB_VERSION = 2;
	private SQLiteDatabase db;
	private SqliteHelper dbHelper;

	public CategoryProvider(Context context) {
		dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public void Close() {
		db.close();
		dbHelper.close();
	}

	public List<CategoryDTO> getProductInfoList() {
		List<CategoryDTO> proInfoList = new ArrayList<CategoryDTO>();
		Cursor cursor = db.query(SqliteHelper.TB_NAME, null, null, null, null,
				null, CategoryDTO.ID + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CategoryDTO proInfo = new CategoryDTO();
			proInfo.setId(cursor.getInt(SqliteHelper.COL_ID));
			proInfo.setName(cursor.getString(SqliteHelper.COL_NAME));
			proInfo.setParentId(cursor.getInt(SqliteHelper.COL_PARAID));
			proInfo.setImg(cursor.getString(SqliteHelper.COL_IMG));
			proInfoList.add(proInfo);
			cursor.moveToNext();
		}
		cursor.close();
		return proInfoList;
	}

	// update product info
	public int updateProductInfo(CategoryDTO pi) {
		ContentValues values = new ContentValues();
		values.put(CategoryDTO.ID, pi.getId());
		values.put(CategoryDTO.NAME, pi.getName());
		values.put(CategoryDTO.PARENT_ID, pi.getParentId());
		values.put(CategoryDTO.IMG, pi.getImg());
		int id = db.update(SqliteHelper.TB_NAME, values, CategoryDTO.ID + "="
				+ pi.getId(), null);
		logd("UpdateProductInfo id = " + id);
		return id;
	}

	// update product info by id
	public int updateProductInfo(int id, String name, int paraId, String img) {
		ContentValues values = new ContentValues();
		values.put(CategoryDTO.ID, id);
		values.put(CategoryDTO.NAME, name);
		values.put(CategoryDTO.PARENT_ID, paraId);
		values.put(CategoryDTO.IMG, img);
		int updatedId = db.update(SqliteHelper.TB_NAME, values, CategoryDTO.ID
				+ "=?", new String[] { String.valueOf(id) });
		logd("UpdateProductInfo2 id = " + id);
		return id;
	}

	// insert item
	public Long insertProductInfo(CategoryDTO pi) {
		Long uid = 0l;
		if(!isDupIdExist(pi.getId())){
			ContentValues values = new ContentValues();
			values.put(CategoryDTO.ID, pi.getId());
			values.put(CategoryDTO.NAME, pi.getName());
			values.put(CategoryDTO.PARENT_ID, pi.getParentId());
			values.put(CategoryDTO.IMG, pi.getImg());
			uid = db.insert(SqliteHelper.TB_NAME, CategoryDTO.ID, values);
			logd("SaveProductInfo"+ uid);
		} else {
			updateProductInfo(pi);
			logd("dup id, so update db");
		}
		return uid;
	}

	// delete item
	public int DeleteProductInfo(int id) {
		int delId = db.delete(SqliteHelper.TB_NAME, CategoryDTO.ID + "=?",
				new String[] { String.valueOf(id) });
		logd("DelProductInfo id = " + delId);
		return delId;
	}
	public void deleteAllCategories(){
		db.needUpgrade(DB_VERSION+1);
	}
	
	public boolean isDupIdExist(int id){
		List<CategoryDTO> proInfoList = getProductInfoList();
		for (CategoryDTO pi: proInfoList) {
			if (id == pi.getId()) {
				return true;
			}
		}
		return false;
	}

	/*
	 * get parentId of product info by id
	 * return -1 if target id not found
	 */
	public int getParentById(int id){
		List<CategoryDTO> proInfoList = getProductInfoList();
		for (CategoryDTO pi: proInfoList) {
			if (id == pi.getId()) {
				return pi.getParentId();
			}
		}
		return -1;
	}
	
	public List<CategoryDTO> getProductInfoListByParentId(int paraId) {
		List<CategoryDTO> proInfoList = new ArrayList<CategoryDTO>();
		Cursor cursor = db.query(SqliteHelper.TB_NAME, null, CategoryDTO.PARENT_ID
				+ "=?", new String[] {String.valueOf(paraId)}, null, null, null);
		if(cursor != null){
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CategoryDTO proInfo = new CategoryDTO();
				proInfo.setId(cursor.getInt(SqliteHelper.COL_ID));
				proInfo.setName(cursor.getString(SqliteHelper.COL_NAME));
				proInfo.setParentId(cursor.getInt(SqliteHelper.COL_PARAID));
				proInfo.setImg(cursor.getString(SqliteHelper.COL_IMG));
				proInfoList.add(proInfo);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return proInfoList;
	}

	private void logd(String str) {
		Log.d(LOG_TAG, str);
	}

	private void loge(String str) {
		Log.e(LOG_TAG, str);
	}
}