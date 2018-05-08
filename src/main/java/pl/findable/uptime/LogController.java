package pl.findable.uptime;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/log")
class LogController {

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private LogRepository logRepository;

	@GetMapping("{siteName}")
	public List<Log> getLogs(@PathVariable String siteName) {
		Site site = siteRepository.findByName(siteName);
		return logRepository.findTop100BySiteIdOrderByDateDesc(site.getId());
	}

}