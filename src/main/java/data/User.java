package data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User class to help store the username and passcode on disk. 
 * @author Ben Shabowski
 * @since 1.0
 *
 */
@AllArgsConstructor
@Getter
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String username, password;
	
}
