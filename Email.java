package ¿...?;

import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class provide the necessary to send an email
 *
 * @author jhcore
 */
@Named
public class Email {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(Email.class);

  private final Properties properties;

  @Value("${blendapp.smtp.protocol}")
  private String protocol;

  @Value("${blendapp.smtp.host}")
  private String host;

  @Value("${blendapp.smtp.port}")
  private String port;

  private static final String TRUE = "true";

  @Value("${blendapp.smtp.username}")
  private String username;

  @Value("${blendapp.smtp.auth}")
  private String authentication;

  /**
   * The constructor.
   */
  public Email() {

    this.properties = new Properties();
  }

  @PostConstruct
  void init() {

    this.properties.put("mail.smtp.host", this.host);
    this.properties.put("mail.smtp.port", this.port);
    this.properties.put("mail.smtp.starttls.enable", Email.TRUE);
    this.properties.put("mail.smtp.auth", Email.TRUE);

  }

  /**
   * Method to send a basic email
   *
   * @param to destiny email
   * @param subject of the email
   * @param msg the message of the email (the text)
   * @return true if the email was send successfully and false otherwise.
   */
  public Boolean sendEmail(String to, String subject, String msg) {

    Boolean success = Boolean.FALSE;
    final Session session = getSession();
    Transport transport;
    Message message;
    try {
      message = constructMessage(session, to, subject, msg);
      transport = session.getTransport(this.protocol);
      transport.connect(this.host, this.username, this.authentication);
      transport.sendMessage(message, message.getAllRecipients());
      success = Boolean.TRUE;
    } catch (MessagingException e) {
      LOG.error("Error sending the message to {}. Exception: {}", to, e);
    }

    return success;
  }

  private Message constructMessage(Session session, String to, String subject, String msg) throws MessagingException {

    final Message message = new MimeMessage(session);
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    message.setFrom(new InternetAddress(this.username));
    message.setSubject(subject);
    message.setText(msg);
    message.setSentDate(new Date());

    return message;
  }

  private Session getSession() {

    return Session.getInstance(this.properties, null);
  }

}
