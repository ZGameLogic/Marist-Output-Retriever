package data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String username, password;
	
}
