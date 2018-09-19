package com.stur.lib.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Xml;

import com.stur.lib.IntegerUtil;
import com.stur.lib.Log;
import com.stur.lib.StringUtils;
import com.stur.lib.constant.StConstant;
import com.stur.lib.os.OsUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FileUtils {
    /**
     * 完整目录
     */
    public static final String PATH_ROOT = "/";
    public static final String PATH_CAMERA = "/sdcard/DCIM/Camera";
    //离线log开启的情况下不能直接删除log文件，否则LogService没法写入到之前的文件，Log无法打印，除非再次关闭并开启log开关
    public static final String PATH_LOG_QC = "/sdcard/log/";
    public static final String PATH_LOG_MTK = "/storage/emulated/0/mtklog/";

    /**
     * 数据目录
     */
    public static final String DATA_PATH_STUR = "stur";
    public static final String DATA_PATH_IVVI = "ivvi";

    /**
     * 图片目录
     */
    public static String IMAGES_PATH = "images";

    /**
     * 图片缓存目录 ImageLoader
     */
    public static String IMAGE_CACHE_PATH = "imageCache";

    /**
     * 日志文件目录名称
     */
    public final static String APP_LOG_PATH = "logs";

    /**
     * 临时目录名称
     */
    public final static String APP_TEMP_PATH = "temp";

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * 返回内置SD卡路径
     */
    public static String getSDRoot() {
        // 判断是否挂载了SD卡
        String root = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            root = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            Log.e(getTag(), "no SDCard founded");
        }
        return root;
    }

    /**
     * 获取手机外置SD卡的根目录,非机身自带的SD卡，一般为空
     * 如果是需要访问内置SD卡，请使用@getWorkPath 或者 @getSDRoot
     *
     * @return
     */
    public static String getExternalSDRoot() {

        Map<String, String> evn = System.getenv();

        return evn.get("SECONDARY_STORAGE");
    }

    /**
     * 返回内置SD卡或data分区下某目录的完整工作路径（推荐使用）
     * 如果有SD卡，返回/sdcard/dir/，否则返回/data/user/0/pakage_name/files/dir/
     *
     * @param context can be null if externalStorage is mounted
     * @return 返回完整工作路径
     * @dir 工作目录名，如果传入为null则默认为stur
     */
    public static String getWorkPath(Context context, String dir) {
        // 判断是否挂载了SD卡
        if (dir == null) {
            dir = DATA_PATH_STUR;
        }
        String path = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dir
                    + File.separator;
        } else {
            File basePath = context.getFilesDir();
            if (basePath == null) {
                basePath = context.getCacheDir();
            }
            path = basePath.getAbsolutePath() + File.separator + dir + File.separator;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 返回data分区下某目录的完整路径
     *
     * @param context can be null if externalStorage is mounted
     * @return 返回完成工作路径，如果有SD卡，返回/sdcard/stur/，否则返回/data/user/0/com.stur.chest/files/stur/
     * @dir 工作目录名
     */
    public static String getDataPath(Context context) {
        String dataPath = null;
        File basePath = context.getFilesDir();
        if (basePath == null) {
            basePath = context.getCacheDir();
        }
        dataPath = basePath.getAbsolutePath();
        return dataPath;
    }

    /*
    * create file with name of fileNamePath, and increase the num following "_"
    * on the next calling. new File only for allocate, createNewFile is create
    * empty file on the disk
    */
    public static File getIncreasedSaveFile(String fileNamePath) {
        File file = new File(fileNamePath);
        int n;
        if (file.exists()) {
            String fileName = fileNamePath.substring(0, fileNamePath.lastIndexOf("."));
            String extension = fileNamePath.substring(fileNamePath.lastIndexOf("."), fileNamePath.length());
            String number = null;
            if (fileName.contains("-")) {
                number = fileName.substring(fileName.lastIndexOf("-"), fileName.length());
            }
            if (number == null || number.length() == 0 || !isNum(number)) {
                n = 0;
            } else {
                n = Integer.valueOf(number);
            }
            do {
                n++;
                StringBuffer buffer = new StringBuffer();
                buffer.append(fileName);
                buffer.append("_");
                buffer.append(n);
                buffer.append(extension);
                file = new File(buffer.toString());
            } while (file.exists());
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 生产文件 如果文件所在路径不存在则生成路径
     */
    public static File buildFile(String fileName, boolean isDirectory) {
        File target = new File(fileName);
        if (isDirectory) {
            target.mkdirs();
        } else {
            if (!target.getParentFile().exists()) {
                Log.i(getTag(), target.getParentFile().getAbsolutePath() + ": dir is not exists");
                target.getParentFile().mkdirs();
                target = new File(target.getAbsolutePath());
            }
        }
        return target;
    }

    /**
     * 创建文件
     *
     * @param folderPath
     * @param fileName
     * @return
     */
    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName);
    }

    /**
     * 在sdcard/Android/data/<package>/cache/目录下，创建临时文件
     *
     * @param context
     * @param type
     * @return
     */
    public static File createTempFile(Context context, String type) {
        return new File(context.getExternalCacheDir(), System.currentTimeMillis() + "." + type);
    }

    public static List<String> searchFile(File root, String keyWords) {
        List<String> result = new ArrayList<>();
        File[] listFiles = root.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File file : listFiles) {
                if (file.toString().indexOf(keyWords) >= 0) {
                    result.add(file.getName());
                }
            }
            if (result.size() > 0) {
                return result;
            }
        }
        return null;
    }

    public static boolean isValidFileName(String fileName) {
        String pattern = "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$";
        if (fileName == null || fileName.length() > 255) {
            return false;
        } else {
            return fileName.matches(pattern);
        }
    }

    public static boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @param context
     * @param fileName
     * @param content
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";

        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readInStream(InputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i(getTag(), e.getMessage());
        }
        return null;
    }

    /**
     * 向手机写图片
     *
     * @param buffer
     * @param folder   根目录为/sdcard/，这里只填目录名即可，无需完整路径
     * @param fileName
     * @return 注意要在manifest中配置权限，以及申请动态权限 READ_EXTERNAL_STORAGE
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */
    public static boolean writeFile(byte[] buffer, String folder, String fileName) {
        boolean writeSucc = false;

        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        String folderPath = "";
        if (sdCardExist) {
            folderPath = Environment.getExternalStorageDirectory() + File.separator + folder + File.separator;
        } else {
            writeSucc = false;
        }

        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(folderPath + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            writeSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writeSucc;
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取文件后缀
     * @param fileName
     * @return
     */
    public static String getFileSuffixName(String fileName) {
        int pointLct = fileName.lastIndexOf(".");
        String strFileSuffixName = fileName.substring(pointLct);
        return strFileSuffixName;
    }

    /**
     * 获取文件文件名（去除后缀名）
     */
    public static String getFilePrefix(String fileName) {
        //如果想获得不带点的后缀，变为fileName.lastIndexOf(".")+1
        String suffixfix = fileName.substring(fileName.lastIndexOf("."));
        int num = suffixfix.length();//得到后缀名长度
        return fileName.substring(0, fileName.length() - num);//得到文件名。去掉了后缀
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;

        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取文件大小
     *
     * @param size 字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * InputStream to bytes
     *
     * @param in
     * @return
     * @throws java.io.IOException
     */
    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    /**
     * 检查文件是否存在
     *
     * @param filename
     * @return
     */
    public static boolean checkExists(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return false;
        }
        return new File(filename).exists();
    }

    /**
     * 检查文件是否存在
     *
     * @param file
     * @return
     */
    public static boolean checkExists(File file) {
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }

    /**
     * 新建目录
     *
     * @param directoryName
     * @return
     */
    public static boolean createDirectory(String directoryName) {
        boolean status;
        if (!directoryName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + directoryName);
            status = newPath.mkdir();
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 检查是否安装外置的SD卡
     *
     * @return
     */
    public static boolean checkExternalSDExists() {

        Map<String, String> evn = System.getenv();
        return evn.containsKey("SECONDARY_STORAGE");
    }

    /**
     * 删除文件夹或者文件
     *
     * @param folderPath 文件夹路径或者文件的绝对路径 如：/mnt/sdcard/def_ids/1.png
     */
    public static void deleteDirectory(String folderPath) {
        try {
            // 删除文件夹里所有的文件及文件夹
            deleteAllFile(folderPath);  //如果此路径名表示一个目录，则此目录必须为空才能删除
            File lastFile = new File(folderPath);
            if (lastFile.exists()) {
                // 最后删除空文件夹
                lastFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path 文件夹路径或者文件的绝对路径 如：/mnt/sdcard/def_ids/1.png
     *             删除非本应用程序工作空间的目录时会因为file.list()= null而捕获NPE
     */
    public static void deleteAllFile(String path) {
        // 在内存开辟一个文件空间，但是没有创建
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            String[] tempList = file.list();
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    // 先删除文件夹里面的文件
                    deleteAllFile(path + "/" + tempList[i]);
                    // 再删除空文件夹
                    deleteDirectory(path + "/" + tempList[i]);
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isFile()) {
                try {
                    Log.i(getTag(), fileName);
                    newPath.delete();
                    status = true;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    status = false;
                }
            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除空目录
     * <p/>
     * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     *
     * @return
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    public static boolean deleteOfflineLogs() {
        boolean status = false;
        try {
            if (OsUtils.isHardWareVendorQualcomm()) {
                deleteAllFile(PATH_LOG_QC);
            } else if (OsUtils.isHardWareVendorMediaTek()) {
                deleteAllFile(PATH_LOG_MTK);
            }
            status = true;
        } catch (SecurityException se) {
            se.printStackTrace();
            status = false;
        }
        return status;
    }

    /**
     * 重命名
     *
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean renamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            Log.i(getTag(), filePath);
            f.delete();
            return true;
        }
        return false;
    }

    /**
     * 清空一个文件夹
     *
     * @param filePath
     */
    public static void clearFileWithPath(String filePath) {
        List<File> files = FileUtils.listPathFiles(filePath);
        if (files.isEmpty()) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                clearFileWithPath(f.getAbsolutePath());
            } else {
                f.delete();
            }
        }
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        // 过滤掉以.开始的文件夹
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * 获取一个文件夹下的所有文件
     *
     * @param root
     * @return
     */
    public static List<File> listPathFiles(String root) {
        List<File> allDir = new ArrayList<File>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        File[] files = path.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile())
                    allDir.add(f);
                else
                    listPath(f.getAbsolutePath());
            }
        }
        return allDir;
    }

    public static ArrayList<HashMap<String, String>> listImgFiles(String root) {
        ArrayList<HashMap<String, String>> imgList = new ArrayList<HashMap<String, String>>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        File[] files = path.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("image_id", f.getPath());
                    hm.put("path", f.getPath());
                    imgList.add(hm);
                } else {
                    listPath(f.getAbsolutePath());
                }
            }
        }
        return imgList;
    }

    /**
     * 创建目录
     *
     * @param newPath
     */
    public static PathStatus createPath(String newPath) {
        File path = new File(newPath);
        if (path.exists()) {
            return PathStatus.EXITS;
        }
        if (path.mkdir()) {
            return PathStatus.SUCCESS;
        } else {
            return PathStatus.ERROR;
        }
    }

    /**
     * 截取路径名
     *
     * @return
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 获取应用程序缓存文件夹下的指定目录
     *
     * @param context
     * @param dir
     * @return
     */
    public static String getAppCache(Context context, String dir) {
        String savePath = context.getCacheDir().getAbsolutePath() + "/" + dir + "/";
        File savedir = new File(savePath);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        savedir = null;
        return savePath;
    }

    /**
     * 获取目录文件个数
     *
     * @param dir
     * @return
     */
    public long getFileList(File dir) {
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getFileList(file);// 递归
                count--;
            }
        }
        return count;
    }

    public enum PathStatus {
        SUCCESS, EXITS, ERROR
    }

    /**
     * read file from assets/filename and write it to
     * /sdcard/stur/image/filename
     */
    public static String getAssetsToPath(Context context, String filename) {
        File f = new File(getDataPath(context) + filename);
        Log.d(getTag(), "getAssetsToPath: " + f.getPath());
        if (!f.exists()) {
            f.delete();
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);

            fos = new FileOutputStream(f);
            fos.write(buffer);
        } catch (Exception e) {
            Log.e(getTag(), "write assets file error!", e);
            return null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return f.getAbsolutePath();
    }

    /**
     * read files from assets/oldPath and copy them to newPath
     *
     * @param context
     * @param srcPath 源文件夹或文件路径，但仅限于assets目录下存放的原始资源文件
     * @param dstPath
     * @return
     * @throws IOException
     */
    public static boolean copyFiles(Context context, String srcPath, String dstPath) throws IOException {
        boolean isCopy = true;
        AssetManager mAssetManger = context.getAssets();
        String[] fileNames = mAssetManger.list(srcPath);// 获取assets目录下的所有文件及有文件的目录名

        if (fileNames.length > 0) {// 如果是目录,如果是具体文件则长度为0
            File file = new File(dstPath);
            file.mkdirs();// 如果文件夹不存在，则递归
            for (String fileName : fileNames) {
                if (srcPath == "") // assets中的oldPath是相对路径，不能够以“/”开头
                    copyFiles(context, fileName, dstPath + "/" + fileName);
                else
                    copyFiles(context, srcPath + "/" + fileName, dstPath + "/" + fileName);
            }
        } else {// 如果是文件
            InputStream is = mAssetManger.open(srcPath);
            FileOutputStream fos = new FileOutputStream(new File(dstPath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        }
        return isCopy;
    }

    /**
     * 拷贝一个文件到指定的目录
     *
     * @param context
     * @param srcPath 源文件全路径（包括文件名），不限于assets目录，仅单个文件
     * @param dstPath 目的文件全路径（包括文件名）
     */
    public static void copyFile(Context context, String srcPath, String dstPath) throws IOException {
        FileInputStream fis = new FileInputStream(srcPath);
        FileOutputStream fos = new FileOutputStream(dstPath);

        byte[] buffer = new byte[1024];
        int byteCount = 0;
        while ((byteCount = fis.read(buffer)) != -1) {// 循环从输入流读取 buffer字节
            fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
        }
        fos.flush();// 刷新缓冲区
        fis.close();
        fos.close();
    }

    /**
     * 读取assets目录的字符文件
     *
     * @param context
     * @param filename
     * @return
     */
    public static String getAssetsToString(Context context, String filename) {
        StringBuilder buf = new StringBuilder();
        BufferedReader in = null;
        try {
            InputStream json = context.getAssets().open(filename);
            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buf.toString();
    }

    /**
     * 是否存在SD卡
     *
     * @return
     */
    public static boolean isExsitSDCard() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 取得空闲SD卡空间大小
     *
     * @return MB
     */
    public static long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        /* 获取block的SIZE */
        long blockSize = stat.getBlockSize();
        /* 空闲的Block的数量 */
        long availableBlocks = stat.getAvailableBlocks();
        /* 返回bit大小值 */
        return availableBlocks * blockSize / 1024 / 1024;
    }

    /**
     * 获取程序图片目录
     *
     * @return
     */
    public static String getImagePath(Context context) {
        String images = getDataPath(context) + IMAGES_PATH + File.separator;
        File fileDir = new File(images);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return images;
    }

    /**
     * 获取程序图片缓存目录 不可见图片 ImageLoader
     *
     * @return
     */
    public static String getImageCachePath(Context context) {
        String images = getDataPath(context) + IMAGE_CACHE_PATH + File.separator;
        File fileDir = new File(images);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return images;
    }

    /**
     * 获取程序图片目录
     *
     * @return
     */
    public static String getLogPath(Context context) {
        String logs = getDataPath(context) + APP_LOG_PATH + File.separator;
        File fileDir = new File(logs);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return logs;
    }

    /**
     * 获取程序临时目录
     *
     * @return
     */
    public static String getTempPath(Context context) {
        String temp = getDataPath(context) + APP_TEMP_PATH + File.separator;
        File fileDir = new File(temp);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return temp;
    }

    /**
     * 获取目录的所有大小
     *
     * @return
     */
    public static long getAppDataSize(Context context) {
        String path = getDataPath(context);
        if (StringUtils.isEmpty(path))
            return 0L;

        File filePath = new File(getDataPath(context));

        return FileUtils.getDirSize(filePath);
    }

    /**
     * 清空所有app数据
     */
    public static void clearAppData(Context context) {
        // TODO:清除服务器返回的JSON数据缓存

        // 清除Universal_image_loader中的图片缓存
        File cacheFile = context.getCacheDir();
        if (cacheFile != null) {
            FileUtils.deleteAllFile(cacheFile.getAbsolutePath());
        }
        FileUtils.deleteAllFile(getDataPath(context));
    }

    /**
     * 保存Bitmap到sdcard
     *
     * @param b
     */
    public static void saveBitmap(Context context, Bitmap b) {
        String path = getDataPath(context);
        long dataTake = System.currentTimeMillis();
        String jpegName = path + "/" + dataTake + ".jpg";
        Log.i(getTag(), "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Log.i(getTag(), "saveBitmap success");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(getTag(), "saveBitmap: failed");
            e.printStackTrace();
        }
    }

    /**
     * 调用系统的分享接口将文件分享出去，Android N之后需要借助FileProvider生成Uri再暴露出去
     *
     * @param context
     * @param path    文件的全路径（包括文件名）
     */
    public static void shareFile(Context context, String path) {
        File f = new File(path);
        //调用android分享窗口
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        Uri fileUri;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 第二个参数即是在清单文件中配置的authorities
            fileUri = FileProvider.getUriForFile(context, StConstant.FILE_PROVIDER_AUTH, f);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri = Uri.fromFile(f);
        }
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void shareApk(Context context, String pkgName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(pkgName, 0);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // 系统应用
            } else {
                String dir = applicationInfo.sourceDir;
                Log.d(getTag(), "dir = " + dir);
                Uri appUri = Uri.parse("file://" + applicationInfo.sourceDir);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, appUri);
                intent.setType("application/vnd.android.package-archive");
                String title = "Share";
                context.startActivity(Intent.createChooser(intent, title));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将File转换为InputStream
     *
     * @param path 文件的完整路径含文件名
     */
    public static InputStream tranferFileToInputStream(String path) throws IOException {
        File file = new File(path);
        InputStream input = new FileInputStream(file);
        return input;
    }

    /**
     * 将File、FileInputStream 转换为byte数组
     *
     * @param path 文件的完整路径含文件名
     */
    public static byte[] tranferFileToByteArray(String path) throws IOException {
        File file = new File(path);
        InputStream input = new FileInputStream(file);
        byte[] byt = new byte[input.available()];
        input.read(byt);
        return byt;
    }

    /**
     * 将byte数组转换为InputStream
     *
     * @param byt
     * @return
     */
    public static InputStream transferByteArrayToInputStream(byte[] byt) {
        InputStream input = new ByteArrayInputStream(byt);
        return input;
    }

    /**
     * 将byte数组转换为File
     *
     * @param byt
     * @param path 文件的完整路径，包含文件名
     * @return
     */
    public static void transferByteArrayToFile(byte[] byt, String path) throws IOException {
        File file = new File(path);
        OutputStream output = new FileOutputStream(file);
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
        bufferedOutput.write(byt);
    }

    /**
     * 从一个指定绝对路径中载入xml并解析
     * 这里以un_settings_conf.xml为例，如果是从assets中载入xml并解析可以参考IccidParser.loadIccidOverrides()
     * 读取xml配置，并将配置结果以 Map<String,List<Pair<String, Integer>>> 的格式存储，
     * 其中相同父节点的页面名放在同一个List下，并以父节点名String作为key
     * List中的节点定义：String-子页面名。Integer-配置值：0为不显示，1为显示但是不使能，2为使能
     *
     * @param path 配置文件路径
     */
    public static final String UN_SETTINGS_CONF_ROOT = "UNSettingsConf";

    public static Map<String, List<Pair<String, Integer>>> loadSettingsConfigXml(String path) throws IOException, XmlPullParserException {
        InputStream is = tranferFileToInputStream(path);
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is, "UTF-8");
        int event = pullParser.getEventType();// 触发第一个事件
        Map<String, List<Pair<String, Integer>>> confMap = new HashMap<String, List<Pair<String, Integer>>>();
        String version = "";
        recursionParseSettingsConf(event, pullParser, confMap, null, UN_SETTINGS_CONF_ROOT);
        return confMap;
    }

    /**
     * 递归进行解析XML文件
     *
     * @param eventType      当前标记类型
     * @param parser         XmlPullParser
     * @param map            传入的map对象，解析的内容会写入map里
     * @param parentPageName 父页面的名称
     * @param thisPageName   当前页面的名称
     * @return
     */
    public static void recursionParseSettingsConf(int eventType, XmlPullParser parser, Map<String, List<Pair<String, Integer>>> map, String parentPageName, String thisPageName) {
        String visibility = "";
        try {
            while (eventType != XmlPullParser.END_DOCUMENT) {  //只要不是文档结束事件就一直循环
                try {
                    String tagName = parser.getName() == null ? "" : parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:  //触发开始文档事件
                            break;
                        case XmlPullParser.START_TAG:  //触发开始元素事件
                            if (tagName.equals(UN_SETTINGS_CONF_ROOT)) {
                                String version = parser.getAttributeValue(0);  //解析版本号，可能后面用得上
                            } else if (thisPageName.equals(tagName)) {  //如果记录的当前页面名称和tag名称一致的话，说明还没有进入body解析部分，这里负责解析属性
                                int count = parser.getAttributeCount();  //解析属性个数，目前只有visibility一个
                                for (int i = 0; i < count; i++) {
                                    String attrName = parser.getAttributeName(i);  //属性名为"visibility"
                                    visibility = parser.getAttributeValue(i);
                                }

                                //把解析的内容写入map
                                Pair pair = Pair.create(thisPageName, IntegerUtil.getInt(visibility));
                                List<Pair<String, Integer>> pairList = map.get(parentPageName);
                                if (pairList == null) {
                                    Log.d(getTag(), "it's the first time to put this list to hm!");
                                    pairList = new ArrayList<Pair<String, Integer>>();
                                }
                                pairList.add(pair);
                                map.put(parentPageName, pairList);
                            } else {  //如果当前页面名称和tag名称不一致，说明已经进入到了body解析部分开始递归
                                //只在XmlPullParser.START_TAG分支下进行递归，当前页面作为下一次递归的父页面
                                recursionParseSettingsConf(eventType, parser, map, thisPageName, tagName);
                            }
                            break;
                        case XmlPullParser.END_TAG:  //触发结束元素事件
                            //如果当前页面名称和tag名称一致，说明这一级的递归任务已经完成了，可以return
                            if (thisPageName.equals(tagName)) {
                                Log.d(getTag(), thisPageName + " level of recursion complete");
                                return;
                            }
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 功能说明：利用递归以及JAVA反射，实现的XML通用解析方法。
     * 要求1：XML中的节点标签必须与Class 的类名一致，不区分大小写。
     * 要求2：XML中的节点标签中的子标签必须与Class中的属性名一致，区分大小写，如需要不区分大小写自行更改判断。
     * 使用说明：以记录学生档案的xml为例，按照如下步骤使用：
     * 1. 假如待解析的xml格式如下：
     * <?xml version="1.0"  encoding="UTF-8"?>
     * <root>
     * <student id="1"  group="1">
     * <name>zhangsan</name>
     * <sex>male</sex>
     * <userEntity id="5">
     * <name>yyyy</name>
     * <xxx>xxxx</xxx>
     * </userEntity>
     * </student>
     * </root>
     * 2. 根据XML文件创建对应的Bean：
     * public class Student {private int id;  private String name;...}
     * pulbic class UserEntity {...}
     * 3. 根据业务情况从网络/本地获取XML流: InputStream  in=null；
     * 4. 如果XML中student节点只会有一个,使用parseData方法: Student student=parseData(in,Student.class,"root");
     * 5. 如果XML 中 student 节点有多个,使用 parseList方法: List<Student> listData=parseList(in,Student.class,"root");
     * 6. 注意：属性为List时必须声明其为ArrayList或LinkedList
     * 函数说明：将XML中的数据转换成<T>类型数据
     * 如果XML中根目录下一级节点只会有一个,使用parseData方法
     * 如果XML中根目录下一级节点有多个,使用 parseList方法
     *
     * @param in     XML流数据
     * @param mClass 将XML中的数据转换成的对象类型
     * @param root   XML中的根节点TagName
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static <T> T parseData(InputStream in, Class<T> mClass, String root) {
        T entity = null;
        if (mClass != null && in != null) {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(in, "UTF-8");
                int eventType = parser.getEventType();
                entity = recursionParse(eventType, parser, null, mClass, root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    /**
     * 将XML中的数据转换成List<T>数据
     * 如果XML中根目录下一级节点只会有一个,使用parseData方法
     * 如果XML中根目录下一级节点有多个,使用 parseList方法
     *
     * @param in     XML流数据
     * @param mClass 将XML中的数据转换成的对象类型
     * @param root   XML中的根节点TagName
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static <T> List<T> parseList(InputStream in, Class<T> mClass, String root) {
        List<T> listData = null;
        if (mClass != null && in != null) {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(in, "UTF-8");
                int eventType = parser.getEventType();
                listData = new ArrayList<T>();
                recursionParse(eventType, parser, listData, mClass, root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listData;
    }


    /**
     * 递归进行解析XML文件
     *
     * @param eventType 当前标记类型
     * @param parser    XmlPullParser
     * @param listData  List<T> 集合
     * @param mClass    将XML中的数据转换成的对象类型
     * @param root      XML中的父节点TagName
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SuppressLint("DefaultLocale")
    public static <T> T recursionParse(int eventType, XmlPullParser parser, List<T> listData, Class<T> mClass, String root) {
        T entity = null;
        Field field = null;
        String className = "";
        try {
            className = mClass.getSimpleName().toLowerCase();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                try {
                    String tagName = parser.getName() == null ? "" : parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            if (tagName.toLowerCase().equals(className)) {
                                try {
                                    entity = mClass.newInstance();
                                    int count = parser.getAttributeCount();
                                    for (int i = 0; i < count; i++) {
                                        String attributeName = parser.getAttributeName(i);
                                        try {
                                            field = mClass.getDeclaredField(attributeName);
                                            setFieldData(entity, field, null, parser.getAttributeValue(i));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (!tagName.equals(root)) {
                                try {
                                    if (entity != null) {
                                        field = mClass.getDeclaredField(tagName);
                                        int type = setFieldData(entity, field, parser, null);
                                        switch (type) {
                                            case 0:
                                                field.set(entity, recursionParse(eventType, parser, null, field.getType(), field.getName()));
                                                break;
                                            case 1:
                                                List tempList = (List) field.getType().newInstance();
                                                recursionParse(eventType, parser, tempList, getListGenericityType(field), field.getName());
                                                List oldList = (List) field.get(entity);
                                                if (oldList != null && tempList != null) {
                                                    oldList.addAll(tempList);
                                                } else {
                                                    oldList = tempList;
                                                }
                                                field.set(entity, oldList);
                                                break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (tagName.toLowerCase().equals(className)) {
                                if (listData != null) {
                                    listData.add(entity);
                                }
                                if (tagName.equals(root)) {
                                    return entity;
                                }

                            } else if (tagName.equals(root)) {
                                return entity;
                            }
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 为 <T> 类型对象属性赋值
     *
     * @param entity
     * @param field
     * @param parser
     * @param value
     * @return
     */
    private static <T> int setFieldData(T entity, Field field, XmlPullParser parser, String value) {
        if (entity != null && field != null) {
            try {
                field.setAccessible(true);
                Class<?> type = field.getType();
                String typeName = type.getSimpleName();
                if (type.isPrimitive() || "String".equals(typeName)) {
                    value = parser == null ? value : parser.nextText();
                    if ("String".equals(typeName)) {
                        field.set(entity, value);
                    } else if ("int".equals(typeName)) {
                        field.setInt(entity, Integer.parseInt(value));
                    } else if ("boolean".equals(typeName)) {
                        boolean isFlag = false;
                        try {
                            int booleanInt = Integer.parseInt(value);
                            if (booleanInt == 1) {
                                isFlag = true;
                            }
                        } catch (Exception e) {
                            if ("true".equalsIgnoreCase(value)) {
                                isFlag = true;
                            }
                        }
                        field.setBoolean(entity, isFlag);
                    } else if ("float".equals(typeName)) {
                        field.setFloat(entity, Float.parseFloat(value));
                    } else if ("double".equals(typeName)) {
                        field.setDouble(entity, Double.parseDouble(value));
                    }
                } else {
                    if ("ArrayList".equals(typeName) || "LinkedList".equals(typeName)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 获取 ArrayList<T> OR LinkedList<T> 泛型类型
     *
     * @param field
     * @return
     */
    public static Class<?> getListGenericityType(Field field) {
        try {
            Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                Type[] actualTypes = paramType.getActualTypeArguments();
                for (Type aType : actualTypes) {
                    if (aType instanceof Class) {
                        return (Class<?>) aType;
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取对应文件的Uri
     * @param intent 相应的Intent
     * @param file   文件对象
     * @return
     */
    public static Uri getUri(Context context, Intent intent, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判断版本是否在7.0以上
            uri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider",
                    file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 各种文件的类型的DataType，DataType用于指定Intent中的数据类型，
     * 不同类型的文件有不同的DataType，传入相应类型的DataType，系统会搜寻可以打开该文件的软件，
     * 比如传入视频的DataType，即"video/*"，那么系统将会搜寻本机中可以播放视频的软件，
     * 如果没有安装其他视频播放软件，则系统会默认使用自带的播放视频软件，
     * 如果你还安装了其他视频播放软件，如手机QQ影音，那么就会弹出选择框让你选择使用哪个软件打开。
     */
    private static final String DATA_TYPE_ALL = "*/*";//未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
    private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
    private static final String DATA_TYPE_VIDEO = "video/*";
    private static final String DATA_TYPE_AUDIO = "audio/*";
    private static final String DATA_TYPE_HTML = "text/html";
    private static final String DATA_TYPE_IMAGE = "image/*";
    private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
    private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
    private static final String DATA_TYPE_WORD = "application/msword";
    private static final String DATA_TYPE_CHM = "application/x-chm";
    private static final String DATA_TYPE_TXT = "text/plain";
    private static final String DATA_TYPE_PDF = "application/pdf";

    /**
     * 是否音频文件
     * @param filePath
     * @return
     */
    public static boolean isAudioFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"m4a", "mp3", "mid", "xmf", "ogg", "wav", "aac"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否视频文件
     * @param filePath
     * @return
     */
    public static boolean isVideoFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"3gp", "mp4"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否图片文件
     * @param filePath
     * @return
     */
    public static boolean isImageFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"jpg", "gif", "png", "jpeg", "bmp"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否APK文件
     * @param filePath
     * @return
     */
    public static boolean isApkFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"apk"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否html文件
     * @param filePath
     * @return
     */
    public static boolean isHtmlFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"html", "htm"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否PPT文件
     * @param filePath
     * @return
     */
    public static boolean isPptFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"ppt"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否Excel文件
     * @param filePath
     * @return
     */
    public static boolean isExcelFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"xls"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否WORD文件
     * @param filePath
     * @return
     */
    public static boolean isWordFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"doc"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否PDF文件
     * @param filePath
     * @return
     */
    public static boolean isPdfFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"pdf"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否CHM文件
     * @param filePath
     * @return
     */
    public static boolean isChmFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"chm"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否TXT文件
     * @param filePath
     * @return
     */
    public static boolean isTxtFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            //文件不存在: 可能已经被移动或者删除
            return false;
        }
        /* 取得扩展名 */
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase(Locale.getDefault());
        String[] audioTypeArray = {"txt"};
        if (Arrays.asList(audioTypeArray).contains(suffix)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开文件，还有问题，后面再调试
     * 打开文件的处理步骤如下：
     * 截取得到文件的后缀名
     * 根据后缀名判断对应的文件属于哪种DataType，调用对应产生封装好的intent的方法，获取到intent；
     * 调用startActivity()方法，传入intent
     * @param filePath 文件的全路径，包括到文件名
     */
    public static void openFile(Context context, String filePath) {
        /* 依扩展名的类型决定MimeType */
        Intent intent = null;
        if (isAudioFile(filePath)) {
            intent =  generateVideoAudioIntent(context, filePath,DATA_TYPE_AUDIO);
        } else if (isVideoFile(filePath)) {
            intent = generateVideoAudioIntent(context, filePath,DATA_TYPE_VIDEO);
        } else if (isImageFile(filePath)) {
            intent = generateCommonIntent(context, filePath,DATA_TYPE_IMAGE);
        } else if (isApkFile(filePath)) {
            intent = generateCommonIntent(context, filePath,DATA_TYPE_APK);
        }else if (isHtmlFile(filePath)){
            intent = getHtmlFileIntent(filePath);
        } else if (isPptFile(filePath)) {
            intent = generateCommonIntent(context, filePath,DATA_TYPE_PPT);
        } else if (isExcelFile(filePath)) {
            intent = generateCommonIntent(context, filePath,DATA_TYPE_EXCEL);
        } else if (isWordFile(filePath)) {
            intent = generateCommonIntent(context, filePath,DATA_TYPE_WORD);
        } else if (isPdfFile(filePath)) {
            intent = generateCommonIntent(context, filePath,DATA_TYPE_PDF);
        } else if (isChmFile(filePath)) {
            intent = generateCommonIntent(context, filePath,DATA_TYPE_CHM);
        } else if (isTxtFile(filePath)) {
            intent = generateCommonIntent(context, filePath, DATA_TYPE_TXT);
        } else {
            intent = generateCommonIntent(context, filePath,DATA_TYPE_ALL);
        }
        context.startActivity(intent);
    }

    /**
     * 产生打开视频或音频的Intent
     * @param filePath 文件路径
     * @param dataType 文件类型
     * @return
     */
    public static Intent generateVideoAudioIntent(Context context, String filePath, String dataType){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        File file = new File(filePath);
        intent.setDataAndType(getUri(context, intent, file), dataType);
        return intent;
    }

    /**
     * 产生打开网页文件的Intent
     * @param filePath 文件路径
     * @return
     */
    public static Intent generateHtmlFileIntent(String filePath) {
        Uri uri = Uri.parse(filePath)
                .buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content")
                .encodedPath(filePath)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, DATA_TYPE_HTML);
        return intent;
    }

    /**
     * 产生除了视频、音频、网页文件外，打开其他类型文件的Intent
     * @param filePath 文件路径
     * @param dataType 文件类型
     * @return
     */
    public static Intent generateCommonIntent(Context context, String filePath, String dataType) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(filePath);
        Uri uri = getUri(context, intent, file);
        intent.setDataAndType(uri, dataType);
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
}
