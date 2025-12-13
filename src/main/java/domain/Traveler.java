package domain;

import java.io.Serializable;
import javax.persistence.Entity;



@Entity
public class Traveler extends User implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Traveler (String email, String password, String name, String surname) {
		super(email, password, name, surname);
	}
	
	public Traveler() {
		super();
	}
}