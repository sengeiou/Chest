package com.stur.lib.web;

import android.content.Context;

import com.stur.lib.Log;
import com.stur.lib.constant.StConstant;
import com.stur.lib.file.FileUtils;
import com.stur.lib.network.WifiUtils;

import java.io.File;
import java.util.List;

import fi.iki.elonen.SimpleWebServer;

/**
 * Created by guanxuejin on 2016/2/14.
 */
public class NanoHttpdServer extends SimpleWebServer {

    /**
     * 初始化时注意默认端口，客户端访问时需要带端口访问，手机上使用80端口权限被拒，所以默认为8088
     * 访问时可以在浏览器输入诸如：192.168.1.100:8088/Google.html 来访问
     * @param wwwroot http服务端的文件根目录路径，比如需要访问的html、jpg、mp3、apk等文件都以此目录为根目录
     * @return
     */
    public NanoHttpdServer(Context context, File wwwroot) {
        super(WifiUtils.getIp(context), StConstant.DEFAULT_WEB_SERVER_PORT, wwwroot, false);
        Log.d(this, "NanoHttpdServer");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String action = uri.substring(uri.indexOf(FileUtils.PATH_ROOT) + 1);
        Log.d(this, "OnRequest: [action]" + action + " [uri]" + session.getUri());
        if (action == null || "/".equals(action)) {
            return response404("HomePage");
        }

        //通过http下载已知文件格式的走if分支，未知文件格式直接重定向到固定返回内容
        if (action.endsWith(".jpg") || action.endsWith("mp4") || action.endsWith("apk") || action.endsWith("html")) {
            return super.serve(session);
        } else if (action.endsWith("test")) {  //测试内容，通过 192.168.1.100:8088/test 访问
            return responseContent();
        } else {
            String filePath = "";
            String root = FileUtils.getWorkPath(null, FileUtils.DATA_PATH_STUR);
            File rootFile = new File(root);
            List<String> fileList = FileUtils.searchFile(rootFile, StConstant.CONTROLLING_APK);
            if(fileList != null && fileList.size() > 0) {
                for (String fileName : fileList) {
                    Log.e(this, "[fileName]" + fileName);
                    filePath = fileName;
                }
            }
            return redirectFile(filePath);
        }
    }

    private Response redirectFile(String fileName) {
        String filePath = FileUtils.getWorkPath(null, FileUtils.DATA_PATH_STUR) + File.separator + fileName;
        Log.i(this, "redirectFile to " + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            return response404(filePath);
        }
        String content = "<!DOCTYPE html><html><head><script language=\"JavaScript\">" +
                "self.location='" + fileName +
                "'; </script> <title>Waiting</title></head><body></body></html>";
        Response response = new Response(content);
        return response;
    }

    //这里返回的响应码是200 OK，body中显示文件找不到
    public Response response404(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Can't Found apk at " + url + " !");
        builder.append("</body></html>\n");
        return new Response(builder.toString());
    }

    public Response responseContent() {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Can't Found apk at " + url + " !");
        builder.append("</body></html>\n");
        return new Response(builder.toString());
    }

}
