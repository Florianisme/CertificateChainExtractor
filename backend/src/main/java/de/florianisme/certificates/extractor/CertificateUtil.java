package de.florianisme.certificates.extractor;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Collections2;

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

	public static void checkAndSetValidity(X509Certificate x509Certificate, CertificateEntry certificateEntry, int chainLength) {
		certificateEntry.setReasonPhrase("");
		certificateEntry.setSecurityLevel(SecurityLevel.SECURE);
		try {
			// Expired/Not yet valid
			x509Certificate.checkValidity();

			if(checkIfSelfSigned(x509Certificate, chainLength)) {
				certificateEntry.setSecurityLevel(SecurityLevel.SELF_SIGNED);
				certificateEntry.setReasonPhrase("Certificate is self-signed");
			}
		} catch (CertificateExpiredException e) {
			certificateEntry.setSecurityLevel(SecurityLevel.EXPIRED);
			certificateEntry.setReasonPhrase("Certificate has expired");
		} catch (CertificateNotYetValidException e) {
			certificateEntry.setSecurityLevel(SecurityLevel.EXPIRED);
			certificateEntry.setReasonPhrase("Certificate is not yet valid");
		}
	}

	private static boolean checkIfSelfSigned(X509Certificate x509Certificate, int chainLength) {
		return chainLength == 1 && x509Certificate.getIssuerDN().equals(x509Certificate.getSubjectDN());
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
		if (x509Certificate.getKeyUsage() != null && x509Certificate.getKeyUsage().length >= 6) {
			return x509Certificate.getKeyUsage()[5];
		} else {
			return false;
		}
	}

	public static String getName(X509Certificate x509Certificate) {
		final String subjectDn = x509Certificate.getSubjectDN().toString();

		final Matcher regexNameMatcher = REGEX_NAME_PATTERN.matcher(subjectDn.split(",")[0]);

		if (regexNameMatcher.matches()) {
			return regexNameMatcher.group("name");
		}
		return subjectDn;
	}

	public static List<X509Certificate> sortX509Chain(X509Certificate[] certs) throws SortingException {

		LinkedList<X509Certificate> sortedCerts = new LinkedList<X509Certificate>();
		LinkedList<X509Certificate> unsortedCerts = new LinkedList<X509Certificate>(Arrays.asList(certs));
		// take the first argument of the unsorted List, remove it, and set it
		// as the first element of the sorted List
		sortedCerts.add(unsortedCerts.pollFirst());
		int escapeCounter = 0;

		while (!unsortedCerts.isEmpty()) {
			int initialSize = unsortedCerts.size();
			// take the next element of the unsorted List, remove it, and test
			// if it can be added either at the beginning or the end of the
			// sorted list. If it cannot be added at either side put it back at
			// the end of the unsorted List. Go ahead until there are no more
			// elements in the unsorted List
			X509Certificate currentCert = unsortedCerts.pollFirst();
			if (currentCert.getIssuerX500Principal().equals(sortedCerts.peekFirst().getSubjectX500Principal())) {
				sortedCerts.offerFirst(currentCert);
			} else if (currentCert.getSubjectX500Principal().equals(sortedCerts.peekLast().getIssuerX500Principal())) {
				sortedCerts.offerLast(currentCert);
			} else {
				unsortedCerts.offerLast(currentCert);
			}

			// to prevent a endless loop, the following construct escapes the
			// loop if no change is made after each remaining, yet unsorted,
			// certificate has been tested twice if it fits the chain
			if (unsortedCerts.size() == initialSize) {
				escapeCounter++;
				if (escapeCounter >= (2 * initialSize)) {
					throw new SortingException();
				}
			} else {
				escapeCounter = 0;
			}
		}
		return sortedCerts;
	}
}
