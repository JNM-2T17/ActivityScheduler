package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.BCrypt;
import model.User;


public class UserManager {
	public static boolean addUser(String username, String password, String fName, 
			String mi, String lName, String email) throws SQLException {
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
		return false;
	}
	
	public static User login(String username, String password) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,username,password,fName,mi,lName,email "
				+ "FROM gs_user "
				+ "WHERE username = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1,username);
		ResultSet rs = ps.executeQuery();
		if( rs.next() && BCrypt.checkpw(password, rs.getString("password"))) {
			User u = new User(rs.getInt("id"),rs.getString("username"),rs.getString("fName"),rs.getString("lName"),rs.getString("mi"),rs.getString("email"));
			return u;
		}
		con.close();
		return null;
	}
	
	public static void changePW(int userId,String password) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "UDPATE gs_user SET password = ? WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, BCrypt.hashpw(password,BCrypt.gensalt(12)));
		ps.setInt(2,userId);
		ps.execute();
		con.close();
	}
	
	public static void updateUser(int userId,String username, String password, String fName, 
			String mi, String lName, String email) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "UDPATE gs_user SET username = ?, fName = ?, lName = ?, mi = ?, email = ? WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, username);
		ps.setString(2, fName);
		ps.setString(3, mi);
		ps.setString(4, lName);
		ps.setString(5, email);
		ps.setInt(6, userId);
		ps.execute();
		con.close();
		if( password != null && password.length() > 0 ) {
			changePW(userId,password);
		}
	}
}
