package pl.findable.uptime.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

	private static final int SCHEDULER_RATE = 1 * 60 * 1000;
	private static final long SLEEP_MILLIS = 100;

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private Caller caller;

	@Scheduled(fixedRate = SCHEDULER_RATE)
  public void reportCurrentTime() {
		siteRepository.findAll().forEach(site -> {
    	caller.checkSite(site);
    	try {
				Thread.sleep(SLEEP_MILLIS);
			} catch (Exception e) {
			}
    });
  }
}
