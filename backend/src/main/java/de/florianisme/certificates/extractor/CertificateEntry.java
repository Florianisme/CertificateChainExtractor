package de.florianisme.certificates.extractor;

public class CertificateEntry {

    private final String encodedContent;

    private SecurityLevel securityLevel = SecurityLevel.SECURE;
    private String reasonPhrase;
    private boolean isRootCertificate;
    private String fingerprint;
    private String subject;
    private String name;

    public CertificateEntry(String encodedContent) {
        this.encodedContent = encodedContent;
    }

    public String getEncodedContent() {
        return encodedContent;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel valid) {
        securityLevel = valid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }
}
