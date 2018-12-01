package com.stur.gms.cts;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.format.DateUtils;

import com.stur.lib.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

public class CTSDemo {
	private Context mContext;
	
	public CTSDemo(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	private static final long TIMEOUT = 3 * DateUtils.SECOND_IN_MILLIS;
    private void runDownloadManagerTest(int chainResId, int keyResId) throws Exception {
        DownloadManager dm =
                (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        final SSLServerSocket serverSocket = bindTLSServer(chainResId, keyResId);
        FutureTask<Void> serverFuture = new FutureTask<Void>(new Callable() {
            @Override
            public Void call() throws Exception {
                Log.d(this, "server started at port: " + serverSocket.getLocalPort());
                runServer(serverSocket);
                return null;
            }
        });
        try {
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            new Thread(serverFuture).start();
            Uri destination = Uri.parse("https://localhost:" + serverSocket.getLocalPort());
            Log.d(this, "client connecting to port:" + serverSocket.getLocalPort());
            long id = dm.enqueue(new DownloadManager.Request(destination));
            try {
                serverFuture.get(TIMEOUT, TimeUnit.MILLISECONDS);
                Log.d(this, "success");
                // Check that the download was successful.
            } catch (InterruptedException e) {
                Log.d(this, "failed");
                // Wrap InterruptedException since otherwise it gets eaten by AndroidTest
                throw new RuntimeException(e);
            } finally {
                dm.remove(id);
            }
        } finally {
            serverFuture.cancel(true);
            try {
                serverSocket.close();
            } catch (Exception ignored) {}
        }
    }

    private SSLServerSocket bindTLSServer(int chainResId, int keyResId) throws Exception {
        // Load certificate chain.
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certs;
        try (InputStream is = mContext.getResources().openRawResource(chainResId)) {
            certs = fact.generateCertificates(is);
        }
        X509Certificate[] chain = new X509Certificate[certs.size()];
        int i = 0;
        for (Certificate cert : certs) {
            chain[i++] = (X509Certificate) cert;
        }

        // Load private key for the leaf.
        PrivateKey key;
        try (InputStream is = mContext.getResources().openRawResource(keyResId)) {
            ByteArrayOutputStream keyout = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int chunk_size;
            while ((chunk_size = is.read(buffer)) != -1) {
                keyout.write(buffer, 0, chunk_size);
            }
            is.close();
            byte[] keyBytes = keyout.toByteArray();
            key = KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        }

        // Create KeyStore based on the private key/chain.
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null);
        ks.setKeyEntry("name", key, null, chain);

        // Create SSLContext.
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tmf.init(ks);
        KeyManagerFactory kmf =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, null);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLServerSocket s = (SSLServerSocket) context.getServerSocketFactory().createServerSocket();
        s.bind(null);
        return s;
    }

    private static final String HTTP_RESPONSE =
            "HTTP/1.0 200 OK\r\nContent-Type: text/plain\r\nContent-length: 5\r\n\r\nhello";
    private void runServer(SSLServerSocket server) throws Exception {
        Socket s = server.accept();
        s.getOutputStream().write(HTTP_RESPONSE.getBytes());
        s.getOutputStream().flush();
        s.close();
    }

}
