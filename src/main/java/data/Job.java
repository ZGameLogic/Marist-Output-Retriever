package data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Job {

	private String jobName;
	
	
	public String toString() {
		return jobName;
	}
	
}
