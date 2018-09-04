package sef.ed.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

public class HealthClient {
    public static void main(String[] args) throws Exception {
//        System.setProperty("javax.net.debug", "ssl,handshake");
        System.setProperty("javax.net.ssl.trustStore", HealthClient.class.getResource("/truststore.p12").getPath());
        System.setProperty("javax.net.ssl.trustStorePassword", "demopass");


        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();

        HttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

//        HttpResponse response = httpClient.execute(new HttpGet("https://localhost:8080/health"));
        HttpResponse response = httpClient.execute(new HttpGet("https://127.0.0.1:8080/health"));
        System.out.println("==================================================");
        System.out.println(response.getStatusLine());
        System.out.println("==================================================");
    }
}
