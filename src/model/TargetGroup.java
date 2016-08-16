package model;

public class TargetGroup {
	private int id;
	private int userId;
	private String name;

	public TargetGroup() {
		super();
	}

	public TargetGroup(String name) {
		super();
		this.name = name;
	}

	public TargetGroup(int id, String name) {
		super();
		this.id = id;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	
}
