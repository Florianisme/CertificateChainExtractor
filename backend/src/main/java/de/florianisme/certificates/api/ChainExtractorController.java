package de.florianisme.certificates.api;

import de.florianisme.certificates.extractor.CertificateEntry;
import de.florianisme.certificates.extractor.CertificateExtractor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Collection;

@RestController
public class ChainExtractorController {

    @GetMapping(path = "api/v1/getChain")
    public Collection<CertificateEntry> getCertificateChain(@PathParam("queryUrl") String queryUrl) throws IOException, KeyManagementException, NoSuchAlgorithmException, CertificateEncodingException {
        CertificateExtractor certificateExtractor = new CertificateExtractor();
        return certificateExtractor.getCertificateChain(queryUrl);
    }
}
