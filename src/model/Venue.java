package model;

public class Venue {
	private int id;
	private String name;
	public Venue() {
		super();
	}
	public Venue(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public Venue(String name) {
		super();
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
