package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Activity;
import model.SiteSession;
import model.TargetGroup;
import model.User;
import model.Venue;


public class VenueManager {
	public static boolean addVenue(User u, String venue) throws SQLException {
		Venue v = getVenue(venue);
		if( v == null || v.getUserId() != u.getId() ) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "INSERT INTO gs_venue(userId,name) VALUES (?,?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, u.getId());
			ps.setString(2, venue);
			ps.execute();
			con.close();
			return true;
		} 
		return false;
	}
	
	public static Venue getVenue(int id) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,userId,name FROM gs_venue WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,id);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			Venue v = new Venue(rs.getInt("id"),rs.getString("name"));
			v.setUserId(rs.getInt("userId"));
			con.close();
			return v;
		} 
		con.close();
		return null;
	}
	
	public static Venue getVenue(String name) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,userId,name FROM gs_venue WHERE name = BINARY ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1,name);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			Venue v = new Venue(rs.getInt("id"),rs.getString("name"));
			v.setUserId(rs.getInt("userId"));
			con.close();
			return v;
		} 
		con.close();
		return null;
	}
	
	public static Venue[] getAllVenues(User u) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,userId,name FROM gs_venue WHERE userId = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,u.getId());
		ResultSet rs = ps.executeQuery();
		ArrayList<Venue> tgs = new ArrayList<Venue>();
		while(rs.next()) {
			tgs.add(new Venue(rs.getInt("id"),rs.getString("name")));
			tgs.get(tgs.size() - 1).setUserId(rs.getInt("userId"));
		} 
		con.close();
		
		return tgs.toArray(new Venue[0]);
	}
	
	public static boolean updateVenue(int venueId, String venue) throws SQLException {
		Venue v = getVenue(venue);
		if( v == null || v.getId() == venueId) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "UPDATE gs_venue SET name = ? WHERE id = ? AND status = 1";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, venue);
			ps.setInt(2,venueId);
			ps.execute();
			con.close();
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean canDelete(User u,int venueId) throws SQLException {
		SiteSession[] sss = SessionManager.getSessions(u);
		for(SiteSession ss : sss) {
			Activity[] as = ActivityManager.getActivities(ss);
			for(Activity a : as) {
				if( a.getVenue().getId() == venueId) {
					return false;
				}
			}
		}
		return true;
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
