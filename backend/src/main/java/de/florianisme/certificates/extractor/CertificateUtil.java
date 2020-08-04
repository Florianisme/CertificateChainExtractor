package de.florianisme.certificates.extractor;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Base64;

public class CertificateUtil {

    public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERT = "-----END CERTIFICATE-----";

    public static String convertToPem(Certificate certificate) throws CertificateEncodingException {
        final Base64.Encoder encoder = Base64.getMimeEncoder(64, new byte[]{0x0A});

        final byte[] rawCrtText = certificate.getEncoded();
        final String encodedCertText = new String(encoder.encode(rawCrtText), StandardCharsets.US_ASCII);
        return BEGIN_CERT + "\n" + encodedCertText + "\n" + END_CERT;
    }

    public static boolean checkValidity(X509Certificate x509Certificate) {
        try {
            x509Certificate.checkValidity();
            return true;
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            return false;
        }
    }

    public static String getFingerprint(X509Certificate x509Certificate) {
        try {
            return DatatypeConverter.printHexBinary(
                    MessageDigest.getInstance("SHA-1").digest(
                            x509Certificate.getEncoded())).toLowerCase();
        } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
