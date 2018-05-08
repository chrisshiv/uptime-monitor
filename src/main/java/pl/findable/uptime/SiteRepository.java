package pl.findable.uptime;

import org.springframework.data.repository.CrudRepository;

interface SiteRepository extends CrudRepository<Site, Integer> {

	Site findByName(String name);

}
