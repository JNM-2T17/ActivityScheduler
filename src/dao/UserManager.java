package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import security.AuthenticationException;
import model.BCrypt;
import model.User;


public class UserManager {
	public static boolean checkPass(String password) {
		if( password.length() < 8 ) {
			return false;
		}
		
		boolean cap = false;
		boolean low = false;
		boolean num = false;
		
		for(int i = 0; i < password.length(); i++) {
			if( password.substring(i,i + 1).matches("[A-Z]")) {
				cap = true;
			} else if(password.substring(i,i + 1).matches("[a-z]")) {
				low = true;
			} else if(password.substring(i,i + 1).matches("[0-9]")) {
				num = true;
			}
		}
		
		
		return (cap && low && num);
	}
	
	public static boolean addUser(String username, String password, String fName, 
			String mi, String lName, String email) throws SQLException {
		User u = getUser(username);
		if( u == null ) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "SELECT id FROM gs_user WHERE username = ? AND status = 1";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1,username);
			ResultSet rs = ps.executeQuery();
			if( !rs.next() ){
				sql = "INSERT INTO gs_user(username,password,fName,mi,lName,email) VALUES (?,?,?,?,?,?)";
				ps = con.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, BCrypt.hashpw(password, BCrypt.gensalt(12)));
				ps.setString(3, fName);
				ps.setString(4, mi);
				ps.setString(5, lName);
				ps.setString(6, email);
				ps.execute();
				con.close();
				return true;
			}
			con.close();
		}
		return false;
	}
	
	public static User login(String username, String password) throws SQLException, AuthenticationException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,username,password,fName,mi,lName,email "
				+ "FROM gs_user "
				+ "WHERE username = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1,username);
		ResultSet rs = ps.executeQuery();
		if( rs.next() ) {
			if( BCrypt.checkpw(password, rs.getString("password")) ) {
				User u = new User(rs.getInt("id"),rs.getString("username"),rs.getString("fName"),rs.getString("lName"),rs.getString("mi"),rs.getString("email"));
				return u;
			} else {
				throw new AuthenticationException();
			}
		}
		con.close();
		return null;
	}
	
	public static User getUser(int id) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,username,fName,mi,lName,email "
				+ "FROM gs_user "
				+ "WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,id);
		ResultSet rs = ps.executeQuery();
		if( rs.next()) {
			User u = new User(rs.getInt("id"),rs.getString("username"),rs.getString("fName"),rs.getString("lName"),rs.getString("mi"),rs.getString("email"));
			return u;
		}
		con.close();
		return null;
	}
	
	public static User getUser(String username) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,username,fName,mi,lName,email "
				+ "FROM gs_user "
				+ "WHERE username = BINARY ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1,username);
		ResultSet rs = ps.executeQuery();
		if( rs.next()) {
			User u = new User(rs.getInt("id"),rs.getString("username"),rs.getString("fName"),rs.getString("lName"),rs.getString("mi"),rs.getString("email"));
			return u;
		}
		con.close();
		return null;
	}
	
	public static void changePW(int userId,String password) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "UPDATE gs_user SET password = ? WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, BCrypt.hashpw(password,BCrypt.gensalt(12)));
		ps.setInt(2,userId);
		ps.execute();
		con.close();
	}
	
	public static boolean updateUser(int userId,String username, String password, String newPassword, String fName, 
			String mi, String lName, String email) throws SQLException, AuthenticationException {
		User u = getUser(username);
		if( u == null || u.getId() == userId) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "UPDATE gs_user SET username = ?, fName = ?, lName = ?, mi = ?, email = ? WHERE id = ? AND status = 1";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, fName);
			ps.setString(4, mi);
			ps.setString(3, lName);
			ps.setString(5, email);
			ps.setInt(6, userId);
			ps.execute();
			con.close();
			if( newPassword.length() > 0 && login(username,password) != null ) {
				changePW(userId,newPassword);
			}
			return true;
		}
		return false;
	}
}
