package pl.findable.uptime.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

	private static final int SCHEDULER_RATE = 5 * 60 * 1000;

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private Caller caller;

	@Scheduled(fixedRate = SCHEDULER_RATE)
  public void reportCurrentTime() {
		siteRepository.findAll().forEach(site -> {
    	caller.checkSite(site);
    });
  }
}
