package com.stur.lib.web;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.List;

import com.stur.lib.Constant;
import com.stur.lib.Log;
import com.stur.lib.file.FileUtils;
import com.stur.lib.network.WifiUtils;

import fi.iki.elonen.SimpleWebServer;

/**
 * Created by 80375140 on 2017/4/18.
 */
public class NanoHttpdServer extends SimpleWebServer {

    public NanoHttpdServer(Context context, File wwwroot) {
        super(WifiUtils.getIp(context), Constant.DEFAULT_WEB_SERVER_PORT, wwwroot, false);
        Log.d(this, "NanoHttpdServer");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String action = uri.substring(uri.indexOf(Constant.REQUEST_ROOT) + 1);
        Log.d(this, "OnRequest: [action]" + action + " [uri]" + session.getUri());
        if (action == null || "/".equals(action)) {
            return response404("HomePage");
        }
        // TODO: 2017/4/18 file name
        if (action.endsWith(".jpg")||action.endsWith("mp4")||action.endsWith("apk")) {
            return super.serve(session);
        } else {
            String filePath = "";
            String root = Environment.getExternalStorageDirectory() + Constant.IVVI_PATH;
            File rootFile = new File(root);
            List<String> fileList = FileUtils.searchFile(rootFile, Constant.CONTROLLING_APK);
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
        String filePath = Environment.getExternalStorageDirectory() + Constant.IVVI_PATH + fileName;
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

    public Response response404(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Can't Found apk at " + url + " !");
        builder.append("</body></html>\n");
        return new Response(builder.toString());
    }

}
