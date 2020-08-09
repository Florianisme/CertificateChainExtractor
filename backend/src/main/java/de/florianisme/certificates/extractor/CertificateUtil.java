package de.florianisme.certificates.extractor;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CertificateUtil {

	public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
	public static final String END_CERT = "-----END CERTIFICATE-----";

	public static final Pattern REGEX_NAME_PATTERN = Pattern.compile("CN=(?<name>.*)");

	public static String convertToPem(Certificate certificate) throws CertificateEncodingException {
		final Base64.Encoder encoder = Base64.getMimeEncoder(64, new byte[] { 0x0A });

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

	public static boolean isRootCertificate(X509Certificate x509Certificate) {
		return x509Certificate.getKeyUsage()[5];
	}

	public static String getName(X509Certificate x509Certificate) {
		final String subjectDn = x509Certificate.getSubjectDN().toString();

		final Matcher regexNameMatcher = REGEX_NAME_PATTERN.matcher(subjectDn.split(",")[0]);

		if (regexNameMatcher.matches()) {
			return regexNameMatcher.group("name");
		}
		return subjectDn;
	}
}
