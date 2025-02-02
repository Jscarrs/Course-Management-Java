package group1;
import java.io.Serializable;


/**
 * Superclass Person
 */
class Person implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String email;
	
    public Person() {
        this.name = "";
        this.email = "";
    }

	public Person(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void displayDetails() {
		System.out.println("Name: " + name);
		System.out.println("Email: " + email);
	}
}