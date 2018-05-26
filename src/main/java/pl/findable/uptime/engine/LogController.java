package pl.findable.uptime.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
	public Page<Log> getLogs(@PageableDefault(size = 100) Pageable pageable, @PathVariable String siteName) {
		Site site = siteRepository.findByName(siteName);
		return logRepository.findBySiteIdOrderByDateDesc(pageable, site.getId());
	}

}