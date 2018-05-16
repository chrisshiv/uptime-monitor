package pl.findable.uptime.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.stringtemplate.v4.ST;

import pl.findable.uptime.engine.Site;

class MailServiceImpl implements MailService {

	private static final String WARN_SUBJECT = "ExecMonitor: {site.name} is DOWN";
	private static final String WARN_TEMPLATE = "Site {site.name} is down because of {error}.";

	private final String from;

	@Autowired
	private JavaMailSender mailSender;

	MailServiceImpl(String from) {
		this.from = from;
	}

	@Override
	public void sendWarning(Site site, String error) {
		String subject = new ST(WARN_SUBJECT, '{', '}').add("site", site).add("error", error).render();
		String content = new ST(WARN_TEMPLATE, '{', '}').add("site", site).add("error", error).render();

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo("krzysztof.andrzejczak@findable.pl");
		message.setSubject(subject);
		message.setText(content);
		mailSender.send(message);
	}

}
