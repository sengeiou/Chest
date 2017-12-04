package com.stur.lib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MediaUtils {

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /*
    * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
    * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
    * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
    * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
    *
    * @param imagePath 图像的路径
    *
    * @param width 指定输出图像的宽度
    *
    * @param height 指定输出图像的高度
    *
    * @return 生成的缩略图
    *
    * at last, ImageView.setImageBitmap(bitmap), or saveThumbnail()
    */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static void saveThumbnail(Bitmap bitmap, String imagePath) {
        File file=new File(imagePath);
        try {
            FileOutputStream out=new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 得到本地图片文件
     * this method read the thumbnails file from path(sdcard/DCIM/.thumbsnails)
     * but YL/IVVI dont write thumbsnails into this path, while merge the ori pic and thumbsnails into one jpg
     * so this method cannot used in YL/IVVI devices
     * Compared with getImageThumbnail, this method read the existed thumbnails picture
     * and will consume less memory
     * @param context
     * @return
     */
    public static ArrayList<HashMap<String,String>> getAllPictures(Context context) {
        ArrayList<HashMap<String,String>> picturemaps = new ArrayList<>();
        HashMap<String,String> picturemap;
        ContentResolver cr = context.getContentResolver();
        //先得到缩略图的URL和对应的图片id
        Cursor cursor = cr.query(
                Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{
                        Thumbnails.IMAGE_ID,
                        Thumbnails.DATA
                },
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            do {
                picturemap = new HashMap<>();
                picturemap.put("image_id_path",cursor.getInt(0)+"");
                picturemap.put("thumbnail_path",cursor.getString(1));
                picturemaps.add(picturemap);
            } while (cursor.moveToNext());
            cursor.close();
        }
        //再得到正常图片的path
        for (int i = 0;i<picturemaps.size();i++) {
            picturemap = picturemaps.get(i);
            String media_id = picturemap.get("image_id_path");
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Media.DATA
                    },
                    MediaStore.Audio.Media._ID+"="+media_id,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                do {
                    picturemap.put("image_id",cursor.getString(0));
                    picturemaps.set(i,picturemap);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return picturemaps;
    }

    /**
     * 获取圆形缩略图 需将用户头像图片生成圆形的指定大小的drawable对象，用作actionbar的navigation icon。
     * 原始图片是方形的，尺寸144x144,若是直接调用setNavigationIcon()方法，图片将覆盖actionbar的大部分。
     * 综合网上各种制作缩略图和圆形图片的方法，总结出如下
     *
     * generate scaled circle view from drawable resources
     *
     * @param pContext
     *            application context
     * @param pRes
     *            drawable resources id
     * @param pRadius
     *            Radius of generated view,it will be scaled to this size
     * @return Drawable the circel view with specified radius
     */
    public static Drawable generateScaledCircleDrawable(Context pContext, int pRes, int pRadius) {
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(pContext.getResources(), pRes),
                2 * pRadius, 2 * pRadius, false);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(pContext.getResources(), bitmap);
        drawable.setCornerRadius(pRadius);// 设置圆角半径，当圆角半径为宽高的一半时即生成圆形图形
        return drawable;
    }

    /**
     * generate scaled circle view from drawable resources
     *
     * @param pContext
     *            application context
     * @param pBitmap
     *            bitmap used to generate circle drawable
     * @param pRadius
     *            Radius of generated view,it will be scaled to this size
     * @return Drawable the circel view with specified radius
     */
    public static Drawable generateScaledCircleDrawable(Context pContext, Bitmap pBitmap, int pRadius) {
        Bitmap bitmap = Bitmap.createScaledBitmap(pBitmap, 2 * pRadius, 2 * pRadius, false);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(pContext.getResources(), bitmap);
        drawable.setCornerRadius(pRadius);
        return drawable;
    }
}
