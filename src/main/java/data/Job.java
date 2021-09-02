package data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Simple class to hold job information
 * @author Ben Shabowski
 * @since 1.0
 *
 */
@Getter
@AllArgsConstructor
public class Job {
	// Job name
	private String jobName;
	
	/**
	 * To String method
	 * We cannot use Lombok for this as JavaFX expects just one thing
	 */
	public String toString() {
		return jobName;
	}
	
}
