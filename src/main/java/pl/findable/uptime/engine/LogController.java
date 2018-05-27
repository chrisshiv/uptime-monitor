package pl.findable.uptime.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/log")
class LogController {

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private LogRepository logRepository;

	@GetMapping("{siteName}")
	public Page<Log> getLogs(
			@PathVariable String siteName,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "1") int zoom) {
		page = Math.max(page, 0);
		zoom = Math.max(Math.min(zoom, 10), 1);
		int factor = (int) Math.pow(2, zoom - 1);
		size = Math.max(size, 1) * factor;
		Pageable pageable = new PageRequest(page, size);
		Site site = siteRepository.findByName(siteName);
		Page<Log> dbPage = logRepository.findBySiteIdOrderByDateDesc(pageable, site.getId());
		if (zoom == 1) {
			return dbPage;
		}
		List<Log> content = dbPage.getContent();
		List<Log> newContent = reduceList(content, factor);
		return new PageImpl<Log>(newContent, null, dbPage.getTotalElements()) {
			private static final long serialVersionUID = -6731561261184586676L;

			@Override
			public boolean isFirst() {
				return dbPage.isFirst();
			}

			@Override
			public boolean isLast() {
				return dbPage.isLast();
			}

			@Override
			public int getTotalPages() {
				return dbPage.getTotalPages();
			}
		};
	}

	/**
	 * @param content original list
	 * @param factor how much to reduce
	 * @return reduced list
	 */
	List<Log> reduceList(List<Log> content, double factor) {
		List<Log> newContent = new ArrayList<>(content.size() / (int)factor + 1);
		for (int i = 0; i < content.size(); i += factor) {
			List<Log> timePart = content.subList(i, Math.min(i + (int)factor, content.size()));
			double average = timePart.stream().mapToLong(log -> log.getResponseTime()).average().orElse(0);
			Log representant = new Log();
			representant.setContentLength(timePart.get(0).getContentLength());
			representant.setStatus(timePart.get(0).getStatus());
			representant.setError(timePart.get(0).getError());
			representant.setDate(timePart.get(0).getDate());
			representant.setResponseTime((long) average);
			Optional<Log> max = timePart.stream().max((log1, log2) -> Long.compare(log1.getResponseTime(), log2.getResponseTime()));
			if (max.isPresent() && max.get().getResponseTime() > 2 * (average * factor - max.get().getResponseTime()) / (factor - 1)) {
				representant = max.get();
			}
			newContent.add(representant);
		}
		return newContent;
	}

}