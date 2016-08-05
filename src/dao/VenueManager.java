package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.TargetGroup;
import model.User;
import model.Venue;


public class VenueManager {
	public static boolean addVenue(User u, String venue) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id FROM gs_venue WHERE userId = ? AND name = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,u.getId());
		ps.setString(2,venue);
		ResultSet rs = ps.executeQuery();
		if( !rs.next() ) {
			sql = "INSERT INTO gs_venue(userId,name) VALUES (?,?)";
			ps = con.prepareStatement(sql);
			ps.setInt(1, u.getId());
			ps.setString(2, venue);
			ps.execute();
			con.close();
			return true;
		} 
		con.close();
		return false;
	}
	
	public static Venue[] getAllVenues(User u) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,name FROM gs_venue WHERE userId = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,u.getId());
		ResultSet rs = ps.executeQuery();
		ArrayList<Venue> tgs = new ArrayList<Venue>();
		while(rs.next()) {
			tgs.add(new Venue(rs.getInt("id"),rs.getString("name")));
		} 
		con.close();
		
		return tgs.toArray(new Venue[0]);
	}
	
	public static void updateVenue(int venueId, String venue) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "UPDATE gs_venue SET name = ? WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, venue);
		ps.setInt(2,venueId);
		ps.execute();
		con.close();
	}
	
	public static void deleteVenue(int venueId) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "UPDATE gs_venue SET status = 0 WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,venueId);
		ps.execute();
		con.close();
	}
}
