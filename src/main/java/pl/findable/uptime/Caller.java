package pl.findable.uptime;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
class Caller {

	private static final Logger logger = LoggerFactory.getLogger(Caller.class);

	@Autowired
	private LogRepository logRepository;

	@Async
	void checkSite(Site site) {
		Date startTime = new Date();
		Log log = new Log();
		log.setSiteId(site.getId());
		log.setDate(startTime);
		try {
			ResponseEntity<String> entity = new RestTemplate().getForEntity(site.getUrl(), String.class);
			log.setStatus(entity.getStatusCodeValue());
			log.setResponseTime(new Date().getTime() - startTime.getTime());
			if (entity.getBody() != null) {
				log.setContentLength(entity.getBody().getBytes().length);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			log.setError(e.getMessage());
		}
		logRepository.save(log);
	}


}
