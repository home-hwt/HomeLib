package com.guo.androidlib.net;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
import com.guo.androidlib.util.GHLog;

public class HomeDefaultSSLSocket extends SSLSocketFactory {

	private SSLContext sslContext = SSLContext.getInstance("TLS");

	private static KeyStore trustStore;

	static {
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
		} catch (Throwable e) {
			GHLog.gHLog(GHLog.LOG_ERROR,e.getMessage()+"");
		}
	}
	
	private HomeDefaultSSLSocket()
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		// TODO Auto-generated constructor stub
		super(trustStore);
		TrustManager trustAllCerts = new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
            }
        };
        sslContext.init(null, new TrustManager[]{trustAllCerts}, null);

        this.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);//‘ –ÌÀ˘”–
	}
	
	private static HomeDefaultSSLSocket instance;

    public static HomeDefaultSSLSocket getSocketFactory() {
        if (instance == null) {
            try {
                instance = new HomeDefaultSSLSocket();
            } catch (Throwable e) {
            	GHLog.gHLog(GHLog.LOG_ERROR,e.getMessage()+"");
            }
        }
        return instance;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}
