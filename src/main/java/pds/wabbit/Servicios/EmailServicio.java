package pds.wabbit.Servicios;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import pds.wabbit.Entidades.Usuario;

@Service("emailService")
public class EmailServicio {

    private final String puerto = "587";
    private final String servidorSMTP = "smtp.gmail.com";
    private final String usuarioSMTP = "vr3148910@gmail.com";
    private final String claveSMTP = "741852rv";

    private Logger log = LoggerFactory.getLogger(EmailServicio.class);

    @Autowired
    private Configuration configuration;

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendEmail(Usuario user, Map<String, Object> model) throws MessagingException, IOException, TemplateException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("Enviando e-mail con asunto '{}' a '{}'", model.get("titulo").toString(), user.getEmail());
                try {
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
                    Template temp = configuration.getTemplate("email.ftl");
                    String html = FreeMarkerTemplateUtils.processTemplateIntoString(temp, model);
                    helper.setFrom("no-reply@wabbi.com", "Wabbi");
                    helper.setSubject(model.get("titulo").toString());
                    helper.setTo(user.getEmail());
                    helper.setText(html, true);
                    Address toAddress = new InternetAddress(user.getEmail());
                    mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);
                    Properties props = System.getProperties();
                    props.put("mail.transport.protocol", "smtp");
                    props.put("mail.smtp.port", puerto);
                    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.auth", "true");
                     
                    Session session = Session.getInstance(props, new GMailAuthenticator(usuarioSMTP, claveSMTP));
                    
                    Transport transport = session.getTransport();
                   
                    transport.connect(servidorSMTP, usuarioSMTP, claveSMTP);

                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

//        javaMailSender.send(mimeMessage);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }).start();
    }
}
