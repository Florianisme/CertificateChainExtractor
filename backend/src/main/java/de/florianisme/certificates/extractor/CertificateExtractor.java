package de.florianisme.certificates.extractor;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.*;
import java.util.*;

public class CertificateExtractor {

    public Collection<CertificateEntry> getCertificateChain(String url) throws KeyManagementException, NoSuchAlgorithmException, IOException {
        Map<String, CertificateEntry> certificates = new HashMap<>();

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(getSslContext(certificates))
                .build();
        HttpGet request = new HttpGet(url);

        httpClient.execute(request);

        return certificates.values();
    }

    private SSLContext getSslContext(Map<String, CertificateEntry> certificates) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                for (X509Certificate x509Certificate : x509Certificates) {
                    String fingerprint = CertificateUtil.getFingerprint(x509Certificate);

                    CertificateEntry certificateEntry = new CertificateEntry(CertificateUtil.convertToPem(x509Certificate));
                    certificateEntry.setSubject(x509Certificate.getSubjectDN().toString());
                    certificateEntry.setValid(CertificateUtil.checkValidity(x509Certificate));
                    certificateEntry.setFingerprint(fingerprint);
                    certificateEntry.setRootCertificate(CertificateUtil.isRootCertificate(x509Certificate));

                    certificates.put(fingerprint, certificateEntry);
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, new SecureRandom());

        return sslContext;
    }
}
