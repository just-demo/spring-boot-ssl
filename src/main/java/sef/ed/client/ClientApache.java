package sef.ed.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

public class ClientApache {
    public static void main(String[] args) throws Exception {
//        System.setProperty("javax.net.debug", "ssl,handshake");
//        System.setProperty("javax.net.ssl.trustStore", getAbsolutePath("truststore.p12"));
//        System.setProperty("javax.net.ssl.trustStorePassword", "demopass");
//        System.setProperty("javax.net.ssl.trustStoreType", "pkcs12");
//        System.setProperty("javax.net.ssl.keyStore", getAbsolutePath("keystore.p12"));
//        System.setProperty("javax.net.ssl.keyStorePassword", "demopass");
//        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");

        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .loadTrustMaterial(readKeyStore("ca-truststore.p12"), null)
//                .loadKeyMaterial(readKeyStore("keystore.p12"), "demopass".toCharArray())
                .build();

        HttpClient httpClient = HttpClients.custom()
//                .useSystemProperties()
                .setSSLContext(sslContext)
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        HttpResponse response = httpClient.execute(new HttpGet("https://localhost:8080"));
        System.out.println(response.getStatusLine());
    }

    private static KeyStore readKeyStore(String fileName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream inputStream = ClientApache.class.getResourceAsStream("/" + fileName)) {
            keyStore.load(inputStream, "demopass".toCharArray());
        }
        return keyStore;
    }
}
