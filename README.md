##################################################
################## PREPARATION ###################
##################################################

# Change PWD
cd src/main/resources

# Cleanup
rm keystore.p12
rm truststore.p12
rm cert.pem
rm key.pem

##################################################
################## VERIFICATION ##################
##################################################

# Verify data (without "-storetype pkcs12" keytool blames that the store is of JKS type, which is weird)
# keytool -list -v -keystore keystore.p12 -storepass demopass -storetype pkcs12
# keytool -list -v -keystore truststore.p12 -storepass demopass -storetype pkcs12
# keytool -list -v -keystore /Library/Java/JavaVirtualMachines/.../Contents/Home/jre/lib/security/cacerts -storepass changeit
# keytool -printcert -file cert.pem
# keytool -printcertreq -file cert-req.csr

# Verify that certificate matches private key:
# openssl x509 -noout -modulus -in cert.pem | openssl md5
# openssl rsa -noout -modulus -in key.pem | openssl md5

##################################################
######## GENERATE SELF-SIGNED CERTIFICATE ########
##################################################

# 1 - Generate key pair
keytool -genkeypair -keyalg RSA -validity 3650 -keysize 2048 -keystore keystore.p12 -alias demo -storetype pkcs12 -keypass demopass -storepass demopass \
-dname "CN=localhost, OU=Demo UO, O=Demo O, L=Demo L, ST=Demo ST, C=Demo C" \
-ext san=dns:localhost,ip:127.0.0.1 \
-ext eku=serverAuth,clientAuth
# -ext eku=1.3.6.1.5.5.7.3.1,1.3.6.1.5.5.7.3.2
# https://www.alvestrand.no/objectid/1.3.6.1.5.5.7.3.1.html
# http://oid-info.com/get/1.3.6.1.5.5.7.3.1

# 2 - Export certificate from key store
keytool -exportcert -keystore keystore.p12 -storepass demopass -alias demo -rfc -file cert.pem
# openssl pkcs12 -in keystore.p12 -out keystore.pem
# openssl pkcs12 -in keystore.p12 -out cert2.pem -clcerts -nokeys -passin pass:demopass
# openssl pkcs12 -in keystore.p12 -out key.pem -nodes -nocerts -passin pass:demopass

# 3 - Import certificate into trust store
keytool -import -trustcacerts -noprompt -alias demo -file cert.pem -keystore truststore.p12 -storetype pkcs12 -storepass demopass

##################################################
######### GENERATE CA-SIGNED CERTIFICATE #########
##################################################

# 1 - Create CA key pair
keytool -genkeypair -keyalg RSA -validity 3650 -keysize 2048 -keystore ca-keystore.p12 -alias demo -storetype pkcs12 -keypass demopass -storepass demopass \
-dname "CN=democa, OU=Demo CA UO, O=Demo CA O, L=Demo CA L, ST=Demo CA ST, C=Demo CA C"

# 2 - Generate user key pair
keytool -genkeypair -keyalg RSA -validity 3650 -keysize 2048 -keystore keystore.p12 -alias demo -storetype pkcs12 -keypass demopass -storepass demopass \
-dname "CN=localhost, OU=Demo UO, O=Demo O, L=Demo L, ST=Demo ST, C=Demo C"

# 3 - Create certificate signing request
keytool -certreq -alias demo -keystore keystore.p12 -storepass demopass -file cert-req.csr \
-ext san=dns:localhost,ip:127.0.0.1

# 4 - Create and sign certificate based on CSR
keytool -gencert -infile cert-req.csr -outfile cert-ca.pem -rfc -keystore ca-keystore.p12 -alias demo -storetype pkcs12 -keypass demopass -storepass demopass

# 5 - Extract user key from key store ("nodes" is "no DES" and means no key encryption)
openssl pkcs12 -in keystore.p12 -nodes -nocerts -passin pass:demopass | openssl rsa -out key.pem

# 6 - Create key store with user key and certificated signed by CA
openssl pkcs12 -export -in cert-ca.pem -inkey key.pem -out keystore-ca.p12 -password pass:demopass

# 7 - Export CA certificate from key store
keytool -exportcert -keystore ca-keystore.p12 -storepass demopass -alias demo -rfc -file ca-cert.pem

# 8 - Import CA certificate into trust store
keytool -import -trustcacerts -noprompt -alias demo -file ca-cert.pem -keystore ca-truststore.p12 -storetype pkcs12 -storepass demopass