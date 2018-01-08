package com.stur.lib.web;

import android.content.Context;

import com.stur.lib.Log;
import com.stur.lib.Utils;
import com.stur.lib.network.NetworkUtils;
import com.stur.lib.network.WifiUtils;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.Map;

public class HttpServer {
    /*cookie等信息上传url*/
    public static final String cookieUrl = "http://192.168.1.110:8080/HttpServer/FinishServlets";

    // 获取来访者的ip地址
    private static String ipAddress = "";

    private Context mContext;

    public HttpServer(Context context){
        mContext = context;
    }

    // 开启监听
    public void execute(int port) throws Exception {
        Thread t = new RequestListenerThread(port);
        t.setDaemon(false);
        t.start();
    }

    // 自定义HttpRequest的处理类，我们需要继承HttpRequestHandler接口
    class WebServiceHandler implements HttpRequestHandler {

        public WebServiceHandler() {
            super();
        }

        // 在这个方法中我们就可以处理请求的业务逻辑
        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context)
                throws HttpException, IOException {

            // 获取请求方法
            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            // 获取请求的uri
            String target = request.getRequestLine().getUri();
            Map<String, String> params = URIUtil.getParam(target);
            // 拼接参数
            StringBuilder param = new StringBuilder();
            param.append("cookie=");
            if (params.get("cookie") != null) {
                param.append(params.get("cookie"));
            }
            param.append("&imei=");
            param.append(Utils.getImeiInfo(mContext));
            param.append("&visitor_ip=");
            param.append(ipAddress);
            param.append("&local_ip=");
            param.append(NetworkUtils.getLocalIpAddress());
            param.append("&route_mac=");
            param.append(WifiUtils.getRouteMac(mContext));
            param.append("&route_ssid=");
            param.append(WifiUtils.getRouteSSID(mContext));
            param.append("&timetag=");
            param.append(System.currentTimeMillis() + "");

            // 上报数据
            /*try {
                if (NetworkUtils.checkNetWorkState(mContext)) {
                    boolean flag = HttpUtils.uploadRequest(cookieUrl, param.toString());
                    if (flag) {
                        Log.d(this, "uploadRequest success");
                    } else {
                        Log.d(this, "uploadRequest failed");
                    }
                }
            } catch (Exception e) {
                Log.d(this, "Exception: " + e.getMessage() + " uploadRequest failed");
            }*/

            // get请求方式(我们这里只处理get方式)
            if (method.equals("GET")) {
                response.setStatusCode(HttpStatus.SC_OK);
                StringEntity entity = new StringEntity("Request Success!!");
                response.setEntity(entity);
            } else if (method.equals("POST")) {
                response.setStatusCode(HttpStatus.SC_OK);
                StringEntity entity = new StringEntity("Request Success!!");
                response.setEntity(entity);
            } else {
                throw new MethodNotSupportedException(method + " method not supported");
            }
        }

    }

    /**
     * 自定一个监听的线程
     *
     * @author weijiang204321
     *
     */
    class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams params;
        private final HttpService httpService;

        public RequestListenerThread(int port) throws IOException {

            /********************** 下面就是模板代码了 ***********************/
            this.serversocket = new ServerSocket(port);

            HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] { new ResponseDate(),
                    new ResponseServer(), new ResponseContent(), new ResponseConnControl() });
            this.params = new BasicHttpParams();
            this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                    .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // 这只请求头信息
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            // WebServiceHandler用来处理webservice请求,这里我们可以注册多个处理器Handler的
            reqistry.register("*", new WebServiceHandler());

            this.httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(),
                    new DefaultHttpResponseFactory());
            httpService.setParams(this.params);
            // 为http服务设置注册好的请求处理器。
            httpService.setHandlerResolver(reqistry);

        }

        @Override
        public void run() {
            Log.i(this, "Listening on port " + this.serversocket.getLocalPort());
            Log.i(this, "Thread.interrupted = " + Thread.interrupted());
            while (!Thread.interrupted()) {
                try {
                    // 建立Http连接
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    ipAddress = socket.getInetAddress().getHostAddress();
                    Log.i(this, "Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);
                    // 开启工作线程
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    Log.e(this, "Exception: InterruptedIOException");
                    break;
                } catch (IOException e) {
                    Log.e(this, "I/O error initialising connection thread: " + e.getMessage());
                    break;
                }
            }
        }
    }

    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        @Override
        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }
    }

}