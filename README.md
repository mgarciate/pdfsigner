# PDF Signer with iText

This project provides a utility to digitally sign PDF files using iText.

## Description

The PDF Signer utility takes a PDF file as input, signs it using a specified digital certificate, and then outputs the signed PDF. The signed PDF will be available as `files/output_signed.pdf`.

## Prerequisites

- Docker (for Docker method)
- Java (for script method)

## Preparing Your Files

Before running the utility, ensure you replace the following placeholder files with your actual files:

- `files/input.pdf`: Replace this file with the PDF you want to sign.
- `files/keystore.p12`: Replace this file with your digital certificate in PKCS#12 format.

**Note**: After replacing `keystore.p12`, make sure to update the keystore password in the code with your own password.

## Usage

There are two methods to run the PDF Signer:

### 1. Using Docker:

You can use Docker to build and run the PDF Signer. Here are the steps:

1. Build the Docker image:
   ```bash
   docker build -t pdfsigner-itext:latest .
   ```
2. Run the Docker container:
   ```bash
   docker run --rm -v "$(pwd)/files:/app/files" pdfsigner-itext:latest
   ```

After the above steps, the signed PDF will be available at `files/output_signed.pdf`.

### 2. Using run.sh script:

You can also use the provided run.sh script to run the PDF Signer. Here are the steps:

1. Give execution permission to the script:
   ```bash
   chmod +x run.sh
   ```
2. Run the script:
   ```bash
   ./run.sh
   ```

After running the script, the signed PDF will be available at `files/output_signed.pdf`.

## Alternatives

### pdfbox

PDFBox is an open-source Java tool for working with PDF documents. It allows creation, rendering, and signing of PDF files.

[Link](https://pdfbox.apache.org)

pom.xml

``` xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.24</version>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.68</version>
</dependency>

```
PDFSigner.java

``` java
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateChain;
import java.security.cert.X509Certificate;

public class PDFSigner {

    private static BouncyCastleProvider provider = new BouncyCastleProvider();

    public static void signPDF(String inputFile, String outputFile, String keystorePath, String keystorePassword) throws Exception {
        // Register BouncyCastle provider
        Security.addProvider(provider);

        // Load the keystore
        KeyStore keystore = KeyStore.getInstance("PKCS12", provider);
        char[] password = keystorePassword.toCharArray();
        keystore.load(new FileInputStream(keystorePath), password);
        String alias = keystore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password);
        Certificate[] certificateChain = keystore.getCertificateChain(alias);

        // Load the PDF
        PDDocument document = PDDocument.load(new FileInputStream(inputFile));
        SignatureOptions signatureOptions = new SignatureOptions();
        signatureOptions.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);

        // Set visible signature properties (if needed)
        PDVisibleSignDesigner visibleSignDesigner = new PDVisibleSignDesigner(inputFile, new FileOutputStream(outputFile), 1);
        visibleSignDesigner.signingPage(0).zoom(0).signatureFieldName("signature").width(700).height(50).location(20, 780);

        PDVisibleSigProperties visibleSigProperties = new PDVisibleSigProperties();
        visibleSigProperties.signerName("Name").signerLocation("Location").signatureReason("Reason").preferredSize(0).page(1).visualSignEnabled(true).setPdVisibleSignature(visibleSignDesigner).buildSignature();

        // Sign PDF
        ExternalSigningSupport externalSigning = document.saveIncrementalForExternalSigning(new FileOutputStream(outputFile));
        // Use the signature options
        document.addSignatureField(signatureOptions);
        byte[] cmsSignature = sign(externalSigning.getContent(), privateKey, certificateChain);
        externalSigning.setSignature(cmsSignature);

        document.close();
    }

    private static byte[] sign(byte[] content, PrivateKey privateKey, Certificate[] chain) throws Exception {
        // Use BouncyCastle for signing
        // Implementation of the actual signing process would go here

        // Dummy return for simplification, replace with actual signature bytes
        return new byte[0];
    }

    public static void main(String[] args) {
        try {
            signPDF("files/input.pdf", "files/output_signed.pdf", "keystore.p12", "badssl.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
