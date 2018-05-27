package pl.findable.uptime.engine;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class LogControllerTest {

	private static final long RESPONSE_TIME = 123;

	LogController logController = new LogController();

	@Test
	public void itShould_reduce5elementsTo2elementsForFactor4() {
		// given
		List<Log> original = prepareLogList(5);

		// when
		List<Log> reduced = logController.reduceList(original, 4);

		// then
		assertEquals(2, reduced.size());
	}

	@Test
	public void itShould_countAverageValue() {
		// given
		List<Log> original = prepareLogList(4);

		// when
		List<Log> reduced = logController.reduceList(original, 4);

		// then
		assertEquals(RESPONSE_TIME, reduced.get(0).getResponseTime());
	}

	@Test
	public void itShould_returnMaxValueWhenBigDifference() {
		// given
		List<Log> original = prepareLogList(4);
		original.get(3).setResponseTime(RESPONSE_TIME * 5);

		// when
		List<Log> reduced = logController.reduceList(original, 4);

		// then
		assertEquals(RESPONSE_TIME * 5, reduced.get(0).getResponseTime());
	}

	private List<Log> prepareLogList(int count) {
		return IntStream
				.range(0, count)
				.mapToObj(i -> {
					Log log = new Log();
					log.setResponseTime(RESPONSE_TIME + 20 * (i % 2 == 0 ? 1 : -1));
					return log;
				})
				.collect(Collectors.toList());
	}

}
