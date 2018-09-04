# 1 - Change PWD
cd src/main/resources

# 2 - Cleanup
rm keystore.p12
rm cert.cer
rm truststore.p12

# 3 - Generate key pair
keytool -genkeypair -keyalg RSA -alias test -keystore keystore.p12 -validity 3650 -keysize 2048 -storetype pkcs12 -keypass demopass -storepass demopass \
-dname "CN=localhost, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" \
-ext san=ip:127.0.0.1

# 4 - Export certificate from key store
keytool -export -keystore keystore.p12 -storepass demopass -alias test -file cert.cer

# 5 - Import certificate into trust store
keytool -import -trustcacerts -noprompt -alias test -file cert.cer -keystore truststore.p12 -storetype pkcs12 -storepass demopass

# 6 - Verify data
# keytool -list -v -keystore keystore.p12 -storepass demopass
# keytool -list -v -keystore truststore.p12 -storepass demopass