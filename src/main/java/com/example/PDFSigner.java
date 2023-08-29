package com.example;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class PDFSigner {

    public static void signPDF(String src, String dest, String keystorePath, String keystorePassword, String reason, String location) throws Exception {
        // Load the keystore
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, keystorePassword.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);

        // Create the PdfSigner
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        // Set signer options
        signer.setFieldName("signature1");

        // Get signature appearance and set location and reason
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setPageRect(new Rectangle(20, 780, 700, 50));

        // Create the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, null);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    public static void main(String[] args) {
        try {
            signPDF("files/input.pdf", "files/output_signed.pdf", "files/keystore.p12", "badssl.com", "Whatever reason", "Whatever location");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
