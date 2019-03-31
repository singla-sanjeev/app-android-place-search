package com.homeaway.placesearch.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.homeaway.placesearch.APIInterface;
import com.homeaway.placesearch.BuildConfig;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.model.GenericResponse;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {
    private static final String TAG = LogUtils.makeLogTag(RetrofitUtils.class);

    private static RetrofitUtils sInstance;
    private static volatile Picasso mPicasso;

    public static final long LOW_PRIORITY_TIMEOUT = 30 * 1000; // 30 Seconds
    private static final long MEDIUM_PRIORITY_TIMEOUT = 60 * 1000; // 60 Seconds
    public static final long HIGH_PRIORITY_TIMEOUT = 120 * 1000; // 120 Seconds

    private long mRequestTimeOut = MEDIUM_PRIORITY_TIMEOUT;
    private ResponseType mResponseType = ResponseType.RESPONSE_TYPE_GSON;

    public enum ResponseType {
        RESPONSE_TYPE_GSON,
        RESPONSE_TYPE_STRING,
    }

    private RetrofitUtils() {

    }

    public static RetrofitUtils getInstance() {
        if (sInstance == null) {
            sInstance = new RetrofitUtils();
        }
        return sInstance;
    }

    public void setRequestTimeOut(long mConnectTimeOut) {
        this.mRequestTimeOut = mConnectTimeOut;
    }

    public void setResponseType(ResponseType responseType) {
        mResponseType = responseType;
    }

    public APIInterface getService(Context context) {
        if (context == null) {
            return null;
        }
        return buildRetrofitAdapter(context.getString(R.string.base_url), getOkHttpClient(context)).create(APIInterface.class);
    }

    public APIInterface getService(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) {
            return null;
        }
        return buildRetrofitAdapter(url, getOkHttpClient(context)).create(APIInterface.class);
    }

    public GenericResponse parseError(Context context, retrofit2.Response<?> response) {
        Converter<ResponseBody, GenericResponse> converter = buildRetrofitAdapter(context.getString(R.string.base_url), getOkHttpClient(context))
                .responseBodyConverter(GenericResponse.class, new Annotation[0]);

        GenericResponse genericResponse;

        try {
            genericResponse = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new GenericResponse();
        }
        return genericResponse;
    }

    public Picasso getPicassoImageDownloader(Context context) {
        if (mPicasso == null) {
            Picasso.Builder builder = new Picasso.Builder(context);
            OkHttpClient okHttp3Client = new OkHttpClient();
            OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttp3Client);
            builder.downloader(okHttp3Downloader);
            synchronized (Picasso.class) {
                mPicasso = builder.build();
            }
        }
        return mPicasso;
    }

    private Retrofit buildRetrofitAdapter(String baseUrl, OkHttpClient okHttpClient) {
        if (okHttpClient == null || TextUtils.isEmpty(baseUrl)) {
            return null;
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl);
        if (mResponseType == ResponseType.RESPONSE_TYPE_GSON) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();
            builder.addConverterFactory(GsonConverterFactory.create(gson));
        }
        builder.client(okHttpClient);
        return builder.build();
    }

    private TrustManager[] getTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
    }

    private OkHttpClient getOkHttpClient(Context context) {
        // Create an ssl socket factory with our all-trusting manager
        SSLSocketFactory sslSocketFactory = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                // Create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = getTrustManagers();
                // Install the all-trusting trust manager
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                if (sslContext != null) {
                    sslSocketFactory = sslContext.getSocketFactory();
                }
            } else {
                sslSocketFactory = new TLSSocketFactory();
            }
        } catch (NoSuchAlgorithmException ex) {
            LogUtils.error(TAG, ex.toString());
        } catch (KeyManagementException ex) {
            LogUtils.error(TAG, ex.toString());
        } catch (Exception ex) {
            LogUtils.error(TAG, ex.toString());
        }
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        if (sslSocketFactory != null) {
            httpBuilder.sslSocketFactory(sslSocketFactory, new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            });
        }
        httpBuilder.connectTimeout(mRequestTimeOut, TimeUnit.MILLISECONDS);
        httpBuilder.readTimeout(mRequestTimeOut, TimeUnit.MILLISECONDS);
        httpBuilder.writeTimeout(mRequestTimeOut, TimeUnit.MILLISECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // add logging as last interceptor
            httpBuilder.addInterceptor(logging);  // <-- this is the important line!
        }
        httpBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return httpBuilder.build();
    }

    private static class TLSSocketFactory extends SSLSocketFactory {

        private SSLSocketFactory internalSSLSocketFactory;

        public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            internalSSLSocketFactory = context.getSocketFactory();
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return internalSSLSocketFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return internalSSLSocketFactory.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket() throws IOException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket());
        }

        @Override
        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(s, host, port, autoClose));
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(address, port, localAddress, localPort));
        }

        private Socket enableTLSOnSocket(Socket socket) {
            if (socket != null && (socket instanceof SSLSocket)) {
                String[] protocols = new String[]{"TLSv1"};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    protocols = new String[]{"TLSv1.1", "TLSv1.2"};
                }
                ((SSLSocket) socket).setEnabledProtocols(protocols);
            }
            return socket;
        }
    }
}