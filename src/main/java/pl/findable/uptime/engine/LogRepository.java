package pl.findable.uptime.engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

interface LogRepository extends PagingAndSortingRepository<Log, Integer> {

	Page<Log> findBySiteIdOrderByDateDesc(Pageable pageable, int id);

}
