package model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class User {
	public static final int IDLE_EXPIRY = 120;
	public static final int SESSION_EXPIRY = 10080;
	private int id;
	private String username;
	private String fName;
	private String lName;
	private String mi;
	private String email;
	private Calendar endIdle;
	private Calendar endSession;
	
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
		refreshIdle();
		setSessionExpiry();
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
	
	private void setSessionExpiry() {
		Date d = new Date();
		endSession = Calendar.getInstance();
		endSession.setTime(d);
		endSession.add(Calendar.MINUTE, SESSION_EXPIRY);
	}
	
	public void refreshIdle() {
		Date d = new Date();
		endIdle = Calendar.getInstance();
		endIdle.setTime(d);
		endIdle.add(Calendar.MINUTE, IDLE_EXPIRY);
	}

	public boolean isExpired() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MMM/dd/yy hh:mm:ss aa");
		System.out.println("Current Time: " + df.format(now.getTime()));
		System.out.println("Idle Expiry: " + df.format(endIdle.getTime()));
		System.out.println("Session Expiry: " + df.format(endSession.getTime()));
		return endSession.compareTo(now) <= 0 || endIdle.compareTo(now) <= 0;
	}
}
