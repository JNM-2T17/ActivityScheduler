package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import model.User;

public class AuditManager {
	private String start = "Anonymous user "; 
	
	public AuditManager(User u, HttpServletRequest request) {
		setUser(u,request);
	}
	
	public AuditManager(HttpServletRequest request) {
		setUser(request);
	}
	
	public void setUser(User u,HttpServletRequest request) {
		start = u == null ? "Anonymous user " : "User with id#" + u.getId() + " - " + u.getUsername() + " and ip address " + request.getRemoteAddr() + " ";
	}
	
	public void setUser(HttpServletRequest request) {
		start = "Anonymous user with ip address " + request.getRemoteAddr() + " ";
	}
	
	public void addActivity(String activity) {
		Connection con = DBManager.getInstance().getConnection();
		try {
			String sql = "INSERT INTO gs_audit(activity) VALUES (?)";
			System.out.println(con);
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1,start + activity);
			ps.execute();
		} catch(SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				con.close();
			} catch(SQLException se) {
				se.printStackTrace();
			}
		}
	}
}
