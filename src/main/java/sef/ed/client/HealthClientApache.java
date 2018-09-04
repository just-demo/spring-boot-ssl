package sef.ed.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

public class HealthClientApache {
    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.debug", "ssl,handshake");
//        System.setProperty("javax.net.ssl.trustStore", getAbsolutePath("truststore.p12"));
//        System.setProperty("javax.net.ssl.trustStorePassword", "demopass");
//        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
        // TODO: debug to see how HttpClient uses trustStore, but not uses keyStore
//        System.setProperty("javax.net.ssl.keyStore", getAbsolutePath("keystore.p12"));
//        System.setProperty("javax.net.ssl.keyStorePassword", "demopass");
//        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .loadTrustMaterial(readKeyStore("truststore.p12"), null)
                // TODO: what is there are multiple keys???
                .loadKeyMaterial(readKeyStore("keystore.p12"), "demopass".toCharArray())
//                .loadTrustMaterial(new File(getAbsolutePath("truststore.p12")), "demopass".toCharArray())
//                .loadKeyMaterial(new File(getAbsolutePath("keystore.p12")), "demopass".toCharArray(), "demopass".toCharArray())
                .build();

        HttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

//        HttpResponse response = httpClient.execute(new HttpGet("https://localhost:8080/health"));
        HttpResponse response = httpClient.execute(new HttpGet("https://127.0.0.1:8080/health"));
        System.out.println(response.getStatusLine());
    }

    private static KeyStore readKeyStore(String fileName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream inputStream = HealthClientApache.class.getResourceAsStream("/" + fileName)) {
            keyStore.load(inputStream, "demopass".toCharArray());
        }
        return keyStore;
    }
}
