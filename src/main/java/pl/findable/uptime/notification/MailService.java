package pl.findable.uptime.notification;

import pl.findable.uptime.engine.Site;

public interface MailService {

	void sendWarning(Site site, String error);

}