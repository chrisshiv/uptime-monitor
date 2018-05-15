package pl.findable.uptime.engine;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

interface LogRepository extends CrudRepository<Log, Integer> {

	List<Log> findTop100BySiteIdOrderByDateDesc(int id);

}
