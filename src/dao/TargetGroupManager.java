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


public class TargetGroupManager {
	public static boolean addTargetGroup(User u, String targetGroup) throws SQLException {
		TargetGroup tg = getTargetGroup(u.getId(),targetGroup);
		if( tg == null ) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "INSERT INTO gs_target_group(userId,name) VALUES (?,?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,u.getId());
			ps.setString(2, targetGroup);
			ps.execute();
			con.close();
			return true;
		} 
		return false;
	}
	
	public static TargetGroup getTargetGroup(int id) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,userId,name FROM gs_target_group "
				+ "WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		TargetGroup tg = null;
		if(rs.next()) {
			tg = new TargetGroup(rs.getInt("id"),rs.getString("name"));
			tg.setUserId(rs.getInt("userId"));
		} 
		con.close();
		
		return tg;
	}
	
	public static TargetGroup getTargetGroup(String name) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,userId,name FROM gs_target_group "
				+ "WHERE name = BINARY ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, name);
		ResultSet rs = ps.executeQuery();
		TargetGroup tg = null;
		if(rs.next()) {
			tg = new TargetGroup(rs.getInt("id"),rs.getString("name"));
			tg.setUserId(rs.getInt("userId"));
		} 
		con.close();
		
		return tg;
	}
	
	public static TargetGroup getTargetGroup(int userId, String name) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,userId,name FROM gs_target_group "
				+ "WHERE name = BINARY ? AND userId = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, name);
		ps.setInt(2, userId);
		ResultSet rs = ps.executeQuery();
		TargetGroup tg = null;
		if(rs.next()) {
			tg = new TargetGroup(rs.getInt("id"),rs.getString("name"));
			tg.setUserId(rs.getInt("userId"));
		} 
		con.close();
		
		return tg;
	}
	
	public static TargetGroup[] getAllTargetGroups(User u) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,userId,name FROM gs_target_group "
				+ "WHERE userId = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, u.getId());
		ResultSet rs = ps.executeQuery();
		ArrayList<TargetGroup> tgs = new ArrayList<TargetGroup>();
		while(rs.next()) {
			tgs.add(new TargetGroup(rs.getInt("id"),rs.getString("name")));
			tgs.get(tgs.size() - 1).setUserId(rs.getInt("userId"));
		} 
		con.close();
		
		return tgs.toArray(new TargetGroup[0]);
	}
	
	public static boolean updateTargetGroup(int tgId, String targetGroup) throws SQLException {
		TargetGroup tg = getTargetGroup(targetGroup);
		if( tg == null || tg.getId() == tgId) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "UPDATE gs_target_group SET name = ? WHERE id = ? AND status = 1";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, targetGroup);
			ps.setInt(2,tgId);
			ps.execute();
			con.close();
			return true;
		} else {
			return false;
		}
	}
	
	public static void deleteTargetGroup(int tgId) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "UPDATE gs_target_group SET status = 0 WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,tgId);
		ps.execute();
		con.close();
	}
	
	public static boolean canDelete(User u,int tgId) throws SQLException {
		SiteSession[] sss = SessionManager.getSessions(u);
		for(SiteSession ss : sss) {
			Activity[] as = ActivityManager.getActivities(ss);
			for(Activity a : as) {
				for(TargetGroup tg : a.getTargetGroups()) {
					if( tg.getId() == tgId) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
