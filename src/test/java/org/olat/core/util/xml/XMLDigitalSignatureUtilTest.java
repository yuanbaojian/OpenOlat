/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.core.util.xml;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;
import org.olat.core.util.FileUtils;
import org.olat.core.util.crypto.CryptoUtil;
import org.olat.core.util.crypto.X509CertificatePrivateKeyPair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * Initial date: 16 févr. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class XMLDigitalSignatureUtilTest {
	
	/**
	 * Preliminary test which read the certificate. The method is used in
	 * every other test of the class.
	 * 
	 * @throws Exception
	 */
	@Test
	public void readCertificatePrivateKeyPair() throws Exception {
		X509CertificatePrivateKeyPair keyPair = getCertificatePrivateKeyPair();
		Assert.assertNotNull(keyPair);
		Assert.assertNotNull(keyPair.getPrivateKey());
		Assert.assertNotNull(keyPair.getX509Cert());
	}
	
	/**
	 * Check if the cycle sign -> validation works
	 * 
	 * @throws Exception
	 */
	@Test
	public void signDetachedAndValidate() throws Exception {
		X509CertificatePrivateKeyPair certificateInfo = getCertificatePrivateKeyPair();
		
		URL xmlUrl = XMLDigitalSignatureUtilTest.class.getResource("assessmentResult.xml");
		File xmlFile = new File(xmlUrl.toURI());
		String xmlUri = xmlUrl.toURI().toString();

		File xmlSignatureFile = File.createTempFile("assessment-result", "_signature.xml");
		XMLDigitalSignatureUtil.signDetached(xmlUri, xmlFile, xmlSignatureFile, null,
				null, certificateInfo.getX509Cert(), certificateInfo.getPrivateKey());

		Assert.assertTrue(xmlSignatureFile.length() > 0);
		
		boolean valid = XMLDigitalSignatureUtil.validate(xmlUri, xmlFile, xmlSignatureFile,
				certificateInfo.getX509Cert().getPublicKey());
		Assert.assertTrue(valid);

		//clean up
		Files.deleteIfExists(xmlSignatureFile.toPath());
	}
	
	@Test
	public void signDetachedAndValidate_exoticUri() throws Exception {
		X509CertificatePrivateKeyPair certificateInfo = getCertificatePrivateKeyPair();
		
		URL xmlUrl = XMLDigitalSignatureUtilTest.class.getResource("assessmentResult.xml");
		File xmlFile = new File(xmlUrl.toURI());
		String xmlUri = "http://localhost:8081/RepositoryEntry/688455680/CourseNode/95133178953589/TestSession/2693/assessmentResult.xml";

		File xmlSignatureFile = File.createTempFile("assessment-result", "_signature.xml");
		XMLDigitalSignatureUtil.signDetached(xmlUri, xmlFile, xmlSignatureFile, null,
				null, certificateInfo.getX509Cert(), certificateInfo.getPrivateKey());

		Assert.assertTrue(xmlSignatureFile.length() > 0);
		
		boolean valid = XMLDigitalSignatureUtil.validate(xmlUri, xmlFile, xmlSignatureFile,
				certificateInfo.getX509Cert().getPublicKey());
		Assert.assertTrue(valid);

		//clean up
		Files.deleteIfExists(xmlSignatureFile.toPath());
	}
	
	/**
	 * Test if the signature can be detached and imported in an other
	 * DOM structure.
	 * 
	 * @throws Exception
	 */
	@Test
	public void signDetachedAndValidate_containSignatureDocument() throws Exception {
		X509CertificatePrivateKeyPair certificateInfo = getCertificatePrivateKeyPair();
		
		URL xmlUrl = XMLDigitalSignatureUtilTest.class.getResource("assessmentResult.xml");
		File xmlFile = new File(xmlUrl.toURI());
		String xmlUri = "http://localhost:8081/RepositoryEntry/688455680/CourseNode/95133178953589/TestSession/2693/assessmentResult.xml";
		
		Document signatureDocument =  XMLDigitalSignatureUtil.createDocument();
		Node rootNode = signatureDocument.appendChild(signatureDocument.createElement("assessmentTestSignature"));
		Node courseNode = rootNode.appendChild(signatureDocument.createElement("course"));
		courseNode.appendChild(signatureDocument.createTextNode("Very difficult test"));

		File xmlSignatureFile = File.createTempFile("assessment-result", "_signature.xml");
		XMLDigitalSignatureUtil.signDetached(xmlUri, xmlFile, xmlSignatureFile, signatureDocument,
				null, certificateInfo.getX509Cert(), certificateInfo.getPrivateKey());

		Assert.assertTrue(xmlSignatureFile.length() > 0);
		
		boolean valid = XMLDigitalSignatureUtil.validate(xmlUri, xmlFile, xmlSignatureFile,
				certificateInfo.getX509Cert().getPublicKey());
		Assert.assertTrue(valid);
		
		//load the signature and check that the course info and the Signature is there
		Document reloadSignatureDocument = XMLDigitalSignatureUtil.getDocument(xmlSignatureFile);
		NodeList courseNl = reloadSignatureDocument.getElementsByTagName("course");
		Assert.assertEquals(1, courseNl.getLength());
		NodeList signatureNl = reloadSignatureDocument.getElementsByTagName("Signature");
		Assert.assertEquals(1, signatureNl.getLength());

		//clean up
		Files.deleteIfExists(xmlSignatureFile.toPath());
	}
	
	@Test
	public void signDetachedAndValidate_notValid() throws Exception {
		X509CertificatePrivateKeyPair certificateInfo = getCertificatePrivateKeyPair();
		
		URL xmlUrl = XMLDigitalSignatureUtilTest.class.getResource("assessmentResult.xml");
		File xmlFile = new File(xmlUrl.toURI());
		String xmlUri = xmlUrl.toURI().toString();

		File xmlSignatureFile = File.createTempFile("assessment-result", "_signature.xml");
		XMLDigitalSignatureUtil.signDetached(xmlUri, xmlFile, xmlSignatureFile, null,
				null, certificateInfo.getX509Cert(), certificateInfo.getPrivateKey());
		Assert.assertTrue(xmlSignatureFile.length() > 0);
		
		URL xmlTamperedUrl = XMLDigitalSignatureUtilTest.class.getResource("assessmentResult_tampered.xml");
		File xmlTamperedFile = new File(xmlTamperedUrl.toURI());
		boolean valid = XMLDigitalSignatureUtil.validate(xmlUri, xmlTamperedFile, xmlSignatureFile,
				certificateInfo.getX509Cert().getPublicKey());
		Assert.assertFalse(valid);

		//clean up
		Files.deleteIfExists(xmlSignatureFile.toPath());
	}
	
	@Test
	public void signAndValidate() throws Exception {
		X509CertificatePrivateKeyPair certificateInfo = getCertificatePrivateKeyPair();
		
		URL xmlUrl = XMLDigitalSignatureUtilTest.class.getResource("assessmentResult.xml");
		File xmlFile = new File(xmlUrl.toURI());

		File xmlSignedFile = File.createTempFile("assessment-result", "_signed.xml");
		XMLDigitalSignatureUtil.signEmbedded(xmlFile, xmlSignedFile,
				certificateInfo.getX509Cert(), certificateInfo.getPrivateKey());

		Assert.assertTrue(xmlSignedFile.length() > 0);
		
		boolean valid = XMLDigitalSignatureUtil.validate(xmlSignedFile,
				certificateInfo.getX509Cert().getPublicKey());
		Assert.assertTrue(valid);

		//clean up
		Files.deleteIfExists(xmlSignedFile.toPath());
	}
	
	/**
	 * Check that the signature validate the data too by slightly changing a value in
	 * the signed XML file.
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void signAndValidate_notValid() throws Exception {
		X509CertificatePrivateKeyPair certificateInfo = getCertificatePrivateKeyPair();
		
		URL xmlUrl = XMLDigitalSignatureUtilTest.class.getResource("assessmentResult.xml");
		File xmlFile = new File(xmlUrl.toURI());

		File xmlSignedFile = File.createTempFile("assessment-result", "_signed.xml");
		XMLDigitalSignatureUtil.signEmbedded(xmlFile, xmlSignedFile,
				certificateInfo.getX509Cert(), certificateInfo.getPrivateKey());

		Assert.assertTrue(xmlSignedFile.length() > 0);
		
		//the xml is signed and valid
		boolean valid = XMLDigitalSignatureUtil.validate(xmlSignedFile,
				certificateInfo.getX509Cert().getPublicKey());
		Assert.assertTrue(valid);
		
		//change it a little bit
		String xml = FileUtils.load(xmlSignedFile, "UTF-8");
		String rogueXml = xml.replace("test7501c21c-c3db-468d-b5b8-c40339aaf323.xml", "test7501c21c-468d-b5b8-c40339aaf323.xml");
		Assert.assertNotEquals(xml, rogueXml);

		File xmlRogueFile = File.createTempFile("assessment-result", "_rogue.xml");
		FileUtils.save(xmlRogueFile, rogueXml, "UTF-8");
		//the xml is not valid
		boolean validity = XMLDigitalSignatureUtil.validate(xmlRogueFile,
				certificateInfo.getX509Cert().getPublicKey());
		Assert.assertFalse(validity);
		
		//clean up
		Files.deleteIfExists(xmlSignedFile.toPath());
		Files.deleteIfExists(xmlRogueFile.toPath());
	}

	private X509CertificatePrivateKeyPair getCertificatePrivateKeyPair() throws Exception {
		URL certificateUrl = XMLDigitalSignatureUtilTest.class.getResource("certificate.pfx");
		File certificate = new File(certificateUrl.toURI());
		return CryptoUtil.getX509CertificatePrivateKeyPairPfx(certificate, "");
	}
}
