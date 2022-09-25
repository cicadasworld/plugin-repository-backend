package gtcloud.plugin.repository.config;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Value("${ok.http.connect-timeout:60}")
    private Integer connectTimeout;

    @Value("${ok.http.read-timeout:60}")
    private Integer readTimeout;

    @Value("${ok.http.write-timeout:60}")
    private Integer writeTimeout;

    @Value("${ok.http.max-idle-connections:200}")
    private Integer maxIdleConnections;

    @Value("${ok.http.keep-alive-duration:300}")
    private Long keepAliveDuration;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            //.sslSocketFactory(sslSocketFactory(), x509TrustManager())
            //.hostnameVerifier((hostname, session) -> true)
            //.authenticator(authenticator())
            .retryOnConnectionFailure(true)
            .connectionPool(pool())
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .build();
    }

    @Bean
    public Authenticator authenticator() {
        return (route, response) -> {
            Request request = response.request();
            return request.newBuilder()
                    //.header("Authorization", Credentials.basic(USERNAME, PASSWORD))
                    .build();
        };
    }

    @Bean
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    @Bean
    public SSLSocketFactory sslSocketFactory() {
        try {
            //  Trust any link
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public ConnectionPool pool() {
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
    }
}
