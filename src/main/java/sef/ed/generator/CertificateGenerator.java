package sef.ed.generator;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Date;

import static java.time.ZonedDateTime.now;
import static org.bouncycastle.asn1.x509.Extension.extendedKeyUsage;
import static org.bouncycastle.asn1.x509.Extension.subjectAlternativeName;
import static org.bouncycastle.asn1.x509.GeneralName.dNSName;
import static org.bouncycastle.asn1.x509.GeneralName.iPAddress;
import static org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_clientAuth;
import static org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_serverAuth;

public class CertificateGenerator {
    public static void main(String[] args) throws Exception {
        String targetDir = "src/main/resources";

        // Generate
        KeyPair keyPair = generateKeyPair();
        Certificate certificate = generateCertificate(keyPair);

        // Build and save key store
        KeyStore keyStore = newKeyStore();
        keyStore.setKeyEntry("demo", keyPair.getPrivate(), "demopass".toCharArray(), new Certificate[]{certificate});
        saveKeyStore(keyStore, Paths.get(targetDir).resolve("keystore.p12"));

        // Build and save trust store
        KeyStore trustStore = newKeyStore();
        trustStore.setCertificateEntry("demo", certificate);
        saveKeyStore(trustStore, Paths.get(targetDir).resolve("truststore.p12"));
    }

    private static KeyStore newKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null);
        return keyStore;
    }

    private static void saveKeyStore(KeyStore keyStore, Path path) throws Exception {
        try (FileOutputStream out = new FileOutputStream(path.toFile())) {
            keyStore.store(out, "demopass".toCharArray());
        }
    }

    private static KeyPair generateKeyPair() throws Exception {
        return KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }

    private static Certificate generateCertificate(KeyPair keyPair) throws Exception {
        // Set basic certificate attributes
        X500Name subject = new X500Name("CN=localhost, OU=Java UO, O=Java O, L=Java L, ST=Java ST, C=Java C");
        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                subject,
                BigInteger.ONE,
                Date.from(now().toInstant()),
                Date.from(now().plusYears(1).toInstant()),
                subject,
                SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded())
        );

        // Set Extended Key Usage
        ASN1EncodableVector purposes = new ASN1EncodableVector();
        purposes.add(id_kp_serverAuth);
        purposes.add(id_kp_clientAuth);
        builder.addExtension(extendedKeyUsage, false, new DERSequence(purposes));

        // Set Subject Alternative Name
        GeneralNames subjectAltName = new GeneralNames(new GeneralName[]{
                new GeneralName(dNSName, "localhost"),
                new GeneralName(iPAddress, "127.0.0.1")
        });
        builder.addExtension(subjectAlternativeName, false, subjectAltName.getEncoded());

        // Build signer
        ContentSigner signer = new JcaContentSignerBuilder("SHA1WithRSA").build(keyPair.getPrivate());

        // Generate
        return new JcaX509CertificateConverter().getCertificate(builder.build(signer));
    }
}
