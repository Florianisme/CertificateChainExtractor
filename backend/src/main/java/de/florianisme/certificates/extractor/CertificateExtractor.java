package de.florianisme.certificates.extractor;

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
import java.util.ArrayList;
import java.util.List;

public class CertificateExtractor {

    public List<CertificateEntry> getCertificateChain(String url) throws KeyManagementException, NoSuchAlgorithmException, IOException {
        List<CertificateEntry> certificates = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(getSslContext(certificates))
                .build();
        HttpGet request = new HttpGet(url);

        httpClient.execute(request);

        return certificates;
    }

    private SSLContext getSslContext(List<CertificateEntry> certificates) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                for (X509Certificate x509Certificate : x509Certificates) {
                    CertificateEntry certificateEntry = new CertificateEntry(CertificateUtil.convertToPem(x509Certificate));
                    certificateEntry.setSubject(x509Certificate.getSubjectDN().toString());
                    certificateEntry.setValid(CertificateUtil.checkValidity(x509Certificate));
                    certificateEntry.setFingerprint(CertificateUtil.getFingerprint(x509Certificate));

                    certificates.add(certificateEntry);
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
