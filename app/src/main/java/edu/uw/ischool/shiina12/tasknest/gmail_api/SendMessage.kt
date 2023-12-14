package edu.uw.ischool.shiina12.tasknest.gmail_api

import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Message
//import com.google.auth.GoogleCredentials
import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Properties
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/* Class to demonstrate the use of Gmail Send Message API */
//class SendMessage {
//    /**
//     * Send an email from the user's mailbox to its recipient.
//     *
//     * @param fromEmailAddress - Email address to appear in the from: header
//     * @param toEmailAddress   - Email address of the recipient
//     * @return the sent message, {@code null} otherwise.
//     * @throws MessagingException - if a wrongly formatted address is encountered.
//     * @throws IOException        - if service account credentials file not found.
//     */
//    @Throws(MessagingException::class, IOException::class)
//    fun sendEmail(fromEmailAddress: String, toEmailAddress: String): Message? {
//        /* Load pre-authorized user credentials from the environment.
//           TODO(developer) - See https://developers.google.com/identity for
//            guides on implementing OAuth2 for your application.*/
//        val credentials: GoogleCredentials = GoogleCredentials.getApplicationDefault()
//            .createScoped(GmailScopes.GMAIL_SEND)
//
//        // Create the gmail API client
//        val service: Gmail = Gmail.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credentials)
//            .setApplicationName("Gmail samples")
//            .build()
//
//        // Create the email content
//        val messageSubject = "Test message"
//        val bodyText = "lorem ipsum."
//
//        // Encode as MIME message
//        val props = Properties()
//        val session: Session = Session.getDefaultInstance(props, null)
//        val email = MimeMessage(session)
//        email.setFrom(InternetAddress(fromEmailAddress))
//        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(toEmailAddress))
//        email.subject = messageSubject
//        email.setText(bodyText)
//
//        // Encode and wrap the MIME message into a gmail message
//        val buffer = ByteArrayOutputStream()
//        email.writeTo(buffer)
//        val rawMessageBytes = buffer.toByteArray()
//        val encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes)
//        val message = Message()
//        message.raw = encodedEmail
//
//        try {
//            // Create send message
//            val sentMessage: Message = service.users().messages().send("me", message).execute()
//            println("Message id: " + sentMessage.id)
//            println(sentMessage.toPrettyString())
//            return sentMessage
//        } catch (e: GoogleJsonResponseException) {
//            // TODO(developer) - handle error appropriately
//            val error: GoogleJsonError = e.details
//            if (error.code == 403) {
//                System.err.println("Unable to send message: " + e.details)
//            } else {
//                throw e
//            }
//        }
//        return null
//    }
//}
