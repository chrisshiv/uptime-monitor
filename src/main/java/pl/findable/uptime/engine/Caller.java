package pl.findable.uptime.engine;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pl.findable.uptime.notification.MailService;

@Service
class Caller {

	private static final int MAX_CONNECTION_TIME = 5000;

	private static final Logger logger = LoggerFactory.getLogger(Caller.class);

	@Autowired
	private LogRepository logRepository;
	@Autowired
	private MailService mailer;
	@Autowired
	private HttpComponentsClientHttpRequestFactory requestFactory;

	private Map<Integer, Status> statusMap = new ConcurrentHashMap<>();

	public Status getStatus(int id) {
		return statusMap.getOrDefault(id, Status.UNKNOWN);
	}

	@Async
	void checkSite(Site site) {
		Status lastStatus = statusMap.getOrDefault(site.getId(), Status.UP);
		Date startTime = new Date();
		Log log = new Log();
		log.setSiteId(site.getId());
		log.setDate(startTime);
		try {
			ResponseEntity<String> entity = new RestTemplate(requestFactory).getForEntity(site.getUrl(), String.class);
			log.setStatus(entity.getStatusCodeValue());
			log.setResponseTime(new Date().getTime() - startTime.getTime());
			if (entity.getBody() != null) {
				log.setContentLength(entity.getBody().getBytes().length);
			}
			if (log.getResponseTime() > MAX_CONNECTION_TIME) {
				switch (lastStatus) {
					case LONG_CONNECTION_TIMEOUT:
						break;
					case CONNECTION_TIMEOUT:
						log.setError("Connection timeout twice " + log.getResponseTime());
						statusMap.put(site.getId(), Status.LONG_CONNECTION_TIMEOUT);
						break;
					default:
						statusMap.put(site.getId(), Status.CONNECTION_TIMEOUT);
				}
			} else {
				statusMap.put(site.getId(), Status.UP);
			}
		} catch (Exception e) {
			if (lastStatus != Status.DOWN) {
				logger.error(e.getMessage(), e);
				log.setError(e.getMessage());
				statusMap.put(site.getId(), Status.DOWN);
			}
		}
		if (log.getError() != null) {
			mailer.sendWarning(site, log.getError());
		}
		logRepository.save(log);
	}

	public enum Status { UP, DOWN, CONNECTION_TIMEOUT, LONG_CONNECTION_TIMEOUT, UNKNOWN }

}
