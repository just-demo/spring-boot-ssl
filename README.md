cd src/main/resources

keytool -genkeypair -keyalg RSA -alias test -keystore keystore.p12 -validity 3650 -keysize 2048 -storetype pkcs12 -keypass demopass -storepass demopass \
-dname "CN=localhost, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown"

keytool -export -keystore keystore.p12 -storepass demopass -alias test -file cert.cer

keytool -import -trustcacerts -noprompt -alias test -file cert.cer -keystore truststore.p12 -storetype pkcs12 -storepass demopass

keytool -list -v -keystore keystore.p12 -storepass demopass
keytool -list -v -keystore truststore.p12 -storepass demopass