package com.stur.lib.web;

import android.content.Context;

import com.stur.lib.GsonUtils;
import com.stur.lib.Log;
import com.stur.lib.constant.StConstant;
import com.stur.lib.dto.UserAccountDTO;
import com.stur.lib.exception.ParameterException;
import com.stur.lib.file.FileUtils;
import com.stur.lib.network.WifiUtils;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;
import org.nanohttpd.webserver.SimpleWebServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by guanxuejin on 2016/2/14.
 * 这里用的是org.nanohttpd的库来实现，可以设置超时时间等
 * https://github.com/NanoHttpd/nanohttpd
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

    /**
     * @param session HttpSession是java里Session概念的实现，
     *                简单来说一个Session就是一次httpClient->httpServer的连接，
     *                当连接close后session就结束了，如果没结束则session会一直存在。
     *                这点从这里的代码也能看到：如果socket不close或者exec没有抛出异常
     *                （异常有可能是client段断开连接）session会一直执行exec方法
     *                读取socket数据流的前8192个字节，因为http协议中头部最长为8192
     * @return
     */
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String action = uri.substring(uri.indexOf(FileUtils.PATH_ROOT) + 1);
        Log.d(this, "OnRequest: [action]" + action + " [uri]" + session.getUri());
        if (action == null || "/".equals(action)) {
            return response404("HomePage");
        }

        //通过http下载已知文件格式的走if分支，未知文件格式直接重定向到固定返回内容
        if (action.length() == 0) {    //如果直接访问 192.168.1.107:8088 走这个分支
            return Response.newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, "Welcome to Sturmegezhutz!");
        } else if (action.endsWith(".jpg") || action.endsWith("mp4") || action.endsWith("apk") || action.endsWith("html")) {
            return super.serve(session);
        } else if (action.endsWith("test")) {  //测试内容，通过 192.168.1.100:8088/test 访问
            return responseContent(session);
        } else if (action.endsWith("card/apdu")) {  //调试使用
            Map<String, String> map = new HashMap<String, String>();
            try {
                List<String> obj1 = new ArrayList<String>();
                obj1.add("obj1.1");
                obj1.add("obj1.2");
                List<String> obj2 = new ArrayList<String>();
                obj2.add("obj2.1");
                obj2.add("obj2.2");
                Map<String, List<String>> gxj = new HashMap<String, List<String>>();
                gxj.put("key1", obj1);
                gxj.put("key2", obj2);
                Log.d(this, "parseBodyFixedLength begin");
                session.parseBodyFixedLength();
                Map<String, List<String>> list = session.getParameters();
                Log.d(this, "parseBodyFixedLength complete");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
            map = session.getParms();
            String ret = map.get("card_id");
            return null;
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
        Response response = Response.newFixedLengthResponse(content);
        return response;
    }

    //这里返回的响应码是200 OK，body中显示文件找不到
    public Response response404(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Can't Found apk at " + url + " !");
        builder.append("</body></html>\n");
        return Response.newFixedLengthResponse(builder.toString());
    }

    //测试网络接口及GsonUtil的用法
    public Response responseContent(IHTTPSession session) {
        //如果需要解析POST请求中的body，需要先parseBody，再getParms或者getQueryParameterString
        Map<String, String> map = new HashMap<String, String>();
        try {
            session.parseBody(map);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        map = session.getParms();
        final String json = map.get("test");

        //发送json
        UserAccountDTO uat = new UserAccountDTO();
        try {
            uat.setId("1");
        } catch (ParameterException e) {
            e.printStackTrace();
        }
        return Response.newFixedLengthResponse(GsonUtils.toJson(uat));
    }

}
