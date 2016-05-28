import java.io.IOException;
import java.nio.file.*;
import java.text.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

public class TimestampLogAnalyzer {

	private Set<Long>	timestampsInRange;
	private long		range		= 5 * 1000;
	private int			maxInRange	= 0;

	public TimestampLogAnalyzer() {
		timestampsInRange = new HashSet<>();
		this.readLines();
		System.out.println(this.maxInRange);
	}

	public static void main(String[] arg) {
		new TimestampLogAnalyzer();
	}

	private void readLines() {
		DateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z",
				Locale.ENGLISH);
		Path path = Paths.get("timestamps.txt");
		try (Stream<String> lines = Files.lines(path)) {
			// convert string to date as utc timestamp
			lines.mapToLong(s -> {
				long date = 0l;
				try {
					date = format.parse(s).getTime();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return date;
			})
					// sort
					.sorted().forEachOrdered(i -> {
						this.removeTimestampsOutOfRange(i);
						this.timestampsInRange.add(i);
						this.maxInRange = Math.max(this.maxInRange,
								this.timestampsInRange.size());
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void removeTimestampsOutOfRange(long i) {
		this.timestampsInRange.removeIf(e -> {
			return i - e > this.range;
		});
	}
}
