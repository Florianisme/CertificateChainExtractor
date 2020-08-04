package de.florianisme.certificates.extractor;

import java.util.Arrays;

public class CertificateEntry {

    private final String encodedContent;

    private boolean isValid;
    private boolean isRootCertificate;
    private String fingerprint;
    private String subject;

    public CertificateEntry(String encodedContent) {
        this.encodedContent = encodedContent;
    }

    public String getEncodedContent() {
        return encodedContent;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isRootCertificate() {
        return isRootCertificate;
    }

    public void setRootCertificate(boolean rootCertificate) {
        isRootCertificate = rootCertificate;
    }
}
