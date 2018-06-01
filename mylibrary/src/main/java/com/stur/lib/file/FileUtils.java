package com.stur.lib.file;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Xml;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * @return
     */
    public static String getExternalSDRoot() {

        Map<String, String> evn = System.getenv();

        return evn.get("SECONDARY_STORAGE");
    }

    /**
     * 返回内置SD卡或data分区下某目录的完整工作路径（推荐使用）
     * 如果有SD卡，返回/sdcard/dir/，否则返回/data/user/0/pakage_name/files/dir/
     * @param context can be null if externalStorage is mounted
     * @dir 工作目录名，如果传入为null则默认为stur
     * @return 返回完整工作路径
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
     * @param context can be null if externalStorage is mounted
     * @dir 工作目录名
     * @return 返回完成工作路径，如果有SD卡，返回/sdcard/stur/，否则返回/data/user/0/com.stur.chest/files/stur/
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
     * @param buffer
     * @param folder 根目录为/sdcard/，这里只填目录名即可，无需完整路径
     * @param fileName
     * @return
     * 注意要在manifest中配置权限，以及申请动态权限 READ_EXTERNAL_STORAGE
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
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * 获取文件大小
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
     * @return
     */
    public static boolean checkExternalSDExists() {

        Map<String, String> evn = System.getenv();
        return evn.containsKey("SECONDARY_STORAGE");
    }

    /**
     * 删除文件夹或者文件
     * @param folderPath 文件夹路径或者文件的绝对路径 如：/mnt/sdcard/def_ids/1.png
     */
    public static void deleteDirectory(String folderPath) {
        try {
            // 删除文件夹里所有的文件及文件夹
            deleteAllFile(folderPath);
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
     * @param path 文件夹路径或者文件的绝对路径 如：/mnt/sdcard/def_ids/1.png
     * 删除非本应用程序工作空间的目录时会因为file.list()= null而捕获NPE
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
     * @return
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 获取应用程序缓存文件夹下的指定目录
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
     * @param context
     * @param path 文件的全路径（包括文件名）
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

    /**
     * 将File转换为InputStream
     * @param path 文件的完整路径含文件名
     */
    public static InputStream tranferFileToInputStream(String path) throws IOException {
        File file = new File(path);
        InputStream input = new FileInputStream(file);
        return input;
    }

    /**
     * 将File、FileInputStream 转换为byte数组
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
     * @param byt
     * @return
     */
    public static InputStream transferByteArrayToInputStream(byte[] byt) {
        InputStream input = new ByteArrayInputStream(byt);
        return input;
    }

    /**
     * 将byte数组转换为File
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
     * 这里以un_settings_conf.xml为例
     * 如果是从assets中载入xml并解析可以参考IccidParser.loadIccidOverrides()
     * @param path
     */
    public static void loadXml(String path) throws IOException, XmlPullParserException {
        InputStream is = tranferFileToInputStream(path);
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is, "UTF-8");
        int event = pullParser.getEventType();// 触发第一个事件
        String iccid = null;
        String carrier = null;
        /*while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    mCarrierIccidMap = new HashMap<String, String>();  //开始读取xml时创建hm对象
                    break;
                case XmlPullParser.START_TAG:
                    if ("iccidOverride".equals(pullParser.getName())) {
                        iccid = pullParser.getAttributeValue(0);
                        carrier = pullParser.getAttributeValue(1);
                        //如果有body内容在下面解析，这个xml没有body，只有attr，所以不继续解析
                        if ("person".equals(pullParser.getName())) {
                            String personA = pullParser.nextText();
                        }
                        if ("age".equals(pullParser.getName())) {
                            String ageA = pullParser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("iccidOverride".equals(pullParser.getName())) {
                        mCarrierIccidMap.put(iccid, carrier);
                    }
                    break;
            }
            event = pullParser.next();
        }*/
    }
}
