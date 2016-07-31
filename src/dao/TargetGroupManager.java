package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.TargetGroup;
import model.User;


public class TargetGroupManager {
	public static boolean addTargetGroup(User u, String targetGroup) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id FROM gs_target_group WHERE name = ? AND userId = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1,targetGroup);
		ps.setInt(2, u.getId());
		ResultSet rs = ps.executeQuery();
		if( !rs.next() ) {
			sql = "INSERT INTO gs_target_group(userId,name) VALUES (?,?)";
			ps = con.prepareStatement(sql);
			ps.setInt(1,u.getId());
			ps.setString(2, targetGroup);
			ps.execute();
			con.close();
			return true;
		} 
		con.close();
		return false;
	}
	
	public static TargetGroup[] getAllTargetGroups(User u) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id,name FROM gs_target_group "
				+ "WHERE userId = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, u.getId());
		ResultSet rs = ps.executeQuery();
		ArrayList<TargetGroup> tgs = new ArrayList<TargetGroup>();
		while(rs.next()) {
			tgs.add(new TargetGroup(rs.getInt("id"),rs.getString("name")));
		} 
		con.close();
		
		return tgs.toArray(new TargetGroup[0]);
	}
	
	public static void updateTargetGroup(int tgId, String targetGroup) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "UPDATE gs_target_group SET name = ? WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, targetGroup);
		ps.setInt(2,tgId);
		ps.execute();
		con.close();
	}
	
	public static void deleteTargetGroup(int tgId) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "UPDATE gs_target_group SET status = 0 WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,tgId);
		ps.execute();
		con.close();
	}
}
