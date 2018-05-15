package pl.findable.uptime.engine;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/site")
class SiteController {

	@Autowired
	private SiteRepository siteRepository;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Site> getSites() {
		return StreamSupport.stream(siteRepository.findAll().spliterator(), false).collect(Collectors.toList());
	}

	@PostMapping
	public Site addSite(Site site) {
		site.setId(0);
		return siteRepository.save(site);
	}

	@PutMapping("{id}")
	public Site updateSite(@PathVariable int id, Site site) {
		site.setId(id);
		return siteRepository.save(site);
	}

}