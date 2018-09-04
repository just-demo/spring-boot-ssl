package sef.ed.client;

import java.net.HttpURLConnection;
import java.net.URL;

import static sef.ed.client.FileUtils.getAbsolutePath;

public class HealthClientNative {
    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", getAbsolutePath("truststore.p12"));
        System.setProperty("javax.net.ssl.trustStorePassword", "demopass");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
        System.setProperty("javax.net.ssl.keyStore", getAbsolutePath("keystore.p12"));
        System.setProperty("javax.net.ssl.keyStorePassword", "demopass");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        HttpURLConnection connection = (HttpURLConnection) new URL("https://localhost:8080/health").openConnection();
        System.out.println(connection.getResponseCode());

    }
}
