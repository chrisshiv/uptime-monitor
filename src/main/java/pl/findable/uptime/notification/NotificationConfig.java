package pl.findable.uptime.notification;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
class NotificationConfig {

	@Autowired
	private Environment env;

	@Bean
	JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(env.getProperty("smtp_host"));
		mailSender.setPort(Integer.parseInt(env.getProperty("smtp_port")));
		mailSender.setUsername(env.getProperty("smtp_username"));
		mailSender.setPassword(env.getProperty("smtp_password"));

		Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");;

    return mailSender;
	}

	@Bean
	MailService mailService() {
		return new MailServiceImpl(env.getProperty("smtp_username"));
	}
}
