package self.ed.client;

import self.ed.util.FileUtils;

import java.net.HttpURLConnection;
import java.net.URL;

public class ClientNative {
    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", FileUtils.getAbsolutePath("truststore.p12"));
        System.setProperty("javax.net.ssl.trustStorePassword", "demopass");
        System.setProperty("javax.net.ssl.trustStoreType", "pkcs12");
        System.setProperty("javax.net.ssl.keyStore", FileUtils.getAbsolutePath("keystore.p12"));
        System.setProperty("javax.net.ssl.keyStorePassword", "demopass");
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");

        HttpURLConnection connection = (HttpURLConnection) new URL("https://localhost:8080").openConnection();
        System.out.println(connection.getResponseCode());
    }
}
