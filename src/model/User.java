package model;

public class User {
	private int id;
	private String username;
	private String fName;
	private String lName;
	private String mi;
	private String email;
	public User() {
		super();
	}
	
	public User(int id, String username, String fName, String lName, String mi,
			String email) {
		super();
		this.id = id;
		this.username = username;
		this.fName = fName;
		this.lName = lName;
		this.mi = mi;
		this.email = email;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getMi() {
		return mi;
	}
	public void setMi(String mi) {
		this.mi = mi;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
