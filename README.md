# 1 - Change PWD
cd src/main/resources

# 2 - Cleanup
rm keystore.p12
rm truststore.p12
rm cert.cer
rm cert.pem
rm key.pem

# 3 - Generate key pair
keytool -genkeypair -keyalg RSA -validity 3650 -keysize 2048 -keystore keystore.p12 -alias demo -storetype pkcs12 -keypass demopass -storepass demopass \
-dname "CN=localhost, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" \
-ext san=dns:localhost,ip:127.0.0.1

# 4 - Export certificate from key store
keytool -exportcert -keystore keystore.p12 -storepass demopass -alias demo -rfc -file cert.pem
# openssl pkcs12 -in keystore.p12 -out keystore.pem
# openssl pkcs12 -in keystore.p12 -out cert2.pem -clcerts -nokeys -passin pass:demopass
# openssl pkcs12 -in keystore.p12 -out key.pem -nodes -nocerts -passin pass:demopass

# 5 - Import certificate into trust store
keytool -import -trustcacerts -noprompt -alias demo -file cert.pem -keystore truststore.p12 -storetype pkcs12 -storepass demopass

############################

# 6 - Verify data (without "-storetype pkcs12" keytool blames that the store is of JKS type, which is weird)
# keytool -list -v -keystore keystore.p12 -storepass demopass -storetype pkcs12
# keytool -list -v -keystore truststore.p12 -storepass demopass -storetype pkcs12
# keytool -list -v -keystore /Library/Java/JavaVirtualMachines/.../Contents/Home/jre/lib/security/cacerts -storepass changeit
# keytool -printcert -file cert.pem

############################

# 7 - Create CA certificate
keytool -genkeypair -keyalg RSA -validity 3650 -keysize 2048 -keystore ca-keystore.p12 -alias demo -storetype pkcs12 -keypass demopass -storepass demopass \
-dname "CN=democa, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown"

# 8 - Create certificate signing request
keytool -certreq -alias demo -keystore keystore.p12 -storepass demopass -file ca-req.csr

# 9 - Create and sign request based on CSR
keytool -gencert -infile ca-req.csr -outfile cert-ca.pem -rfc -keystore ca-keystore.p12 -alias demo -storetype pkcs12 -keypass demopass -storepass demopass

# 10 - Export CA certificate from key store
keytool -exportcert -keystore ca-keystore.p12 -storepass demopass -alias demo -rfc -file ca-cert.pem

# 11 - Import CA certificate into trust store
keytool -import -trustcacerts -noprompt -alias demo -file cert.pem -keystore ca-truststore.p12 -storetype pkcs12 -storepass demopass

# 12 - Extract client key from key store ("nodes" is "no DES" and means no key encryption)
openssl pkcs12 -in keystore.p12 -nodes -nocerts -passin pass:demopass | openssl rsa -out key.pem

# 13 - Create key store with client key and certificated signed by CA
openssl pkcs12 -export -in cert-ca.pem -inkey key.pem -out keystore-ca.p12 -password pass:demopass
