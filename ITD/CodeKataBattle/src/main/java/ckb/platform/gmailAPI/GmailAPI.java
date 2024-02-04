package ckb.platform.gmailAPI;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Set;

/*
 * GOOGLE ACCOUNT:
 * mail: codekatabattle.platform@gmail.com
 * password: CKB202430L!
 *
 * GOOGLE CLOUD CONSOLE:
 * https://console.cloud.google.com/apis/credentials?authuser=1&project=ckbplatform
 *
 * GMAIL API:
 * The first time you use the application you need to get a token, run the main function in this class
 * A lin will appear in the console, copy and open it in a Incognito Window
 * Follow the command and hit all the continue and trust buttons, when the token is available an email is sent to CKB
 *
 * This procedure has to be followed even when the gmail error 404 start to appear, before doing it remember to delete folder tokens
 * The 404 error appear because this application is not published yet and is in a test environment
 *
 * RESOURCES:
 * https://blog.sebastian-daschner.com/entries/sending-emails-gmail-api-java
 * https://developers.google.com/gmail/api/auth/scopes?hl=it
 *
 */

public class GmailAPI {

    private static final Logger logger = LoggerFactory.getLogger(GmailAPI.class);
    private static final String CKB_Email = "codekatabattle.platform@gmail.com";
    private final Gmail service;

    public GmailAPI() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("CodeKataBattle1")
                .build();
    }

    // Get OAuth2 Credential for Gmail API
    private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory) throws IOException {
        // Load client secrets from file
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(GmailAPI.class.getResourceAsStream("/client_secret.json")));

        // If for the first time the user is not authenticated is redirected to the browser
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        //returns an authorized Credential object
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    // Send email from CKB to receiver
    public void sendEmail(String subject, String body, String receiver) throws IOException, MessagingException {
        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(CKB_Email));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(receiver));
        email.setSubject(subject);
        email.setText(body);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        // Send the gmail message
        try {
            message = service.users().messages().send("me", message).execute();
            logger.info("Message id: " + message.getId());
            logger.info(message.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                logger.error("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException, MessagingException {
        GmailAPI gmailAPI = new GmailAPI();
        gmailAPI.sendEmail("GetTokenEmail", "Token received successfully", CKB_Email);
    }
}
