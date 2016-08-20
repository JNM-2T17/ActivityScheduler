package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import model.CalendarFactory;
import model.SiteSession;
import model.TimeRange;
import model.User;

public class SessionManager {
	public static boolean addSession(User u,String name, boolean[] blackdays, Calendar startDate,Calendar endDate,
			Calendar[] blackTimeStarts,Calendar[] blackTimeEnds,Calendar[] blackDates) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "INSERT INTO gs_session(userId,name,blackdays,startDate,endDate) VALUES (?,?,?,?,?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,u.getId());
		ps.setString(2,name);
		ps.setString(3, stringifyDays(blackdays));
		ps.setDate(4,new Date(startDate.getTime().getTime()));
		ps.setDate(5,new Date(endDate.getTime().getTime()));
		ps.execute();
		sql = "SELECT id FROM gs_session WHERE userId = ? AND status = 1 ORDER BY dateAdded DESC LIMIT 1";
		ps = con.prepareStatement(sql);
		ps.setInt(1, u.getId());
		ResultSet rs = ps.executeQuery();
		if( rs.next() ) {
			int sessionId = rs.getInt("id");
			if( blackTimeStarts != null && blackTimeEnds != null && blackTimeStarts.length == blackTimeEnds.length && blackTimeStarts.length > 0 ) {
				sql = "INSERT INTO gs_blacktime(sessionId,startTime,endTime) VALUES ";
				for(int i = 0; i < blackTimeEnds.length; i++) {
					if( i > 0 ) {
						sql += ",";
					}
					sql += "(?,?,?)";
				}
				ps = con.prepareStatement(sql);
				for(int i = 0; i < blackTimeStarts.length; i++) {
					ps.setInt(3 * i + 1,sessionId);
					ps.setTime(3 * i + 2,new Time(blackTimeStarts[i].getTime().getTime()));
					ps.setTime(3 * i + 3,new Time(blackTimeEnds[i].getTime().getTime()));
				}
				ps.execute();
			}
			
			if( blackDates != null && blackDates.length > 0 ) {
				sql = "INSERT INTO gs_blackdate(sessionId,blackDate) VALUES ";
				for(int i = 0; i < blackDates.length; i++) {
					if( i > 0 ) {
						sql += ",";
					}
					sql += "(?,?)";
				}
				ps = con.prepareStatement(sql);
				for(int i = 0; i < blackDates.length; i++) {
					ps.setInt(2 * i + 1,sessionId);
					ps.setDate(2 * i + 2,new Date(blackDates[i].getTime().getTime()));
				}
				ps.execute();
			}
			con.close();
			return true;
		}
		con.close();
		return false;
	}
	
	public static SiteSession getSession(int sessionId) throws SQLException {
		SiteSession ss = null;
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id, userId,name, blackdays, startDate, endDate "
				+ "FROM gs_session WHERE id = ? AND status = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,sessionId);
		ResultSet rs = ps.executeQuery();
		if( rs.next() ) {
			ss = new SiteSession(rs.getInt("id"),rs.getInt("userId"),rs.getString("name"),rs.getString("blackdays"),
					CalendarFactory.createCalendar(rs.getTimestamp("startDate").getTime()),
					CalendarFactory.createCalendar(rs.getTimestamp("endDate").getTime()));
			sql = "SELECT startTime, endTime FROM gs_blacktime WHERE sessionId = ? AND status = 1";
			ps = con.prepareStatement(sql);
			ps.setInt(1,ss.getId());
			rs = ps.executeQuery();
			while(rs.next()) {
				ss.addBlackTime(CalendarFactory.createCalendar(rs.getTimestamp("startTime").getTime()), 
						CalendarFactory.createCalendar(rs.getTimestamp("endTime").getTime()));
			}
			sql = "SELECT blackDate FROM gs_blackdate WHERE sessionId = ? AND status = 1";
			ps = con.prepareStatement(sql);
			ps.setInt(1,ss.getId());
			rs = ps.executeQuery();
			while(rs.next()) {
				ss.addBlackdate(CalendarFactory.createCalendar(rs.getTimestamp("blackDate").getTime()));
			}
			//get Activities
		}
		con.close();
		return ss;
	}
	
	public static SiteSession[] getSessions(User u) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id "
				+ "FROM gs_session WHERE userId = ? AND status = 1 ORDER BY dateAdded DESC";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,u.getId());
		ResultSet rs = ps.executeQuery();
		ArrayList<SiteSession> ss = new ArrayList<SiteSession>();
		while( rs.next() ) {
			ss.add(getSession(rs.getInt("id")));
		}
		con.close();
		return ss.toArray(new SiteSession[0]);
	}
	
	public static boolean editSession(User u,int id, String name, boolean[] blackdays, Calendar startDate,Calendar endDate,
			Calendar[] blackTimeStarts,Calendar[] blackTimeEnds,Calendar[] blackDates) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		SiteSession ss = getSession(id);
		if( ss != null && ss.getUserId() == u.getId() ) {
			String sql = "UPDATE gs_session SET name = ?,blackdays = ?,startDate = ?,endDate = ? WHERE id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1,name);
			ps.setString(2, stringifyDays(blackdays));
			ps.setDate(3,new Date(startDate.getTime().getTime()));
			ps.setDate(4,new Date(endDate.getTime().getTime()));
			ps.setInt(5,ss.getId());
			ps.execute();
			TimeRange[] currentBT = ss.getBlacktimes();
			blackTimeStarts = blackTimeStarts == null || blackTimeStarts.length == 0 ? new Calendar[0] : blackTimeStarts;
			blackTimeEnds = blackTimeEnds == null || blackTimeEnds.length == 0 ? new Calendar[0] : blackTimeEnds;
			boolean[] hit1 = new boolean[currentBT.length];
			boolean[] hit2 = new boolean[blackTimeStarts.length];
			for(int i = 0; i < currentBT.length; i++ ) {
				for(int j = 0; j < blackTimeStarts.length; j++) {
					if( currentBT[i].getStartTime().equals(blackTimeStarts[j]) && 
						currentBT[i].getEndTime().equals(blackTimeEnds[j])) {
						hit1[i] = true;
						hit2[j] = true;
						break;
					}
				}
			}
			
			sql = "UPDATE gs_blacktime SET status = 0 WHERE sessionId = ? AND status = 1 AND (";
			String body = "";
			for(int i = 0; i < hit1.length; i++) {
				if( !hit1[i] ) {
					body += (body.length() == 0 ? "" : " OR ") + "starttime = ? AND endtime = ?";
				}
			}
			
			if( body.length() > 0 ) {
				sql += body + ")";
				ps = con.prepareStatement(sql);
				int index = 1;
				ps.setInt(index++,ss.getId());
				for(int i = 0; i < hit1.length; i++) {
					if( !hit1[i] ) {
						ps.setTime(index++, new Time(currentBT[i].getStartTime().getTimeInMillis()));
						ps.setTime(index++, new Time(currentBT[i].getEndTime().getTimeInMillis()));
					}
				}
				ps.execute();
			}
			
			sql = "INSERT INTO gs_blacktime(sessionId,startTime,endTime) VALUES ";
			body = "";
			for(int i = 0; i < hit2.length; i++) {
				if( !hit2[i] ) {
					body += (body.length() == 0 ? "" : ",") + "(?,?,?)";
				}
			}
			
			if( body.length() > 0 ) {
				sql += body;
				ps = con.prepareStatement(sql);
				int index = 1;
				for(int i = 0; i < hit2.length; i++) {
					if( !hit2[i] ) {
						ps.setInt(index++,ss.getId());
						ps.setTime(index++,new Time(blackTimeStarts[i].getTimeInMillis()));
						ps.setTime(index++,new Time(blackTimeEnds[i].getTimeInMillis()));
					}
				}
				ps.execute();
			}
			
			Calendar[] currentBD = ss.getBlackdates().toArray(new Calendar[0]);
			blackDates = blackDates == null ? new Calendar[0] : blackDates;
			hit1 = new boolean[currentBD.length];
			hit2 = new boolean[blackDates.length];
			
			for(int i = 0; i < currentBD.length; i++) {
				for(int j = 0; j < blackDates.length; j++) {
					if( currentBD[i].equals(blackDates[j])) {
						hit1[i] = hit2[j] = true;
						break;
					}
				}
			}
			
			sql = "UPDATE gs_blackdate SET status = 0 WHERE sessionId = ? AND status = 1 AND (";
			body = "";
			for(int i = 0 ; i < hit1.length; i++) {
				if( !hit1[i]) {
					body += (body.length() == 0 ? "" : " OR ") + "blackdate = ?";
				}
			}
			
			if( body.length() > 0 ) {
				sql += body + ")";
				ps = con.prepareStatement(sql);
				int index = 1;
				ps.setInt(index++,ss.getId());
				for(int i = 0 ; i < hit1.length; i++) {
					if( !hit1[i]) {
						ps.setDate(index++,new Date(currentBD[i].getTimeInMillis()));
					}
				}
				ps.execute();
			}
			
			sql = "INSERT INTO gs_blackdate(sessionId,blackdate) VALUES ";
			body = "";
			for(int i = 0; i < hit2.length; i++) {
				if( !hit2[i]) {
					body += (body.length() == 0 ? "" : ",") + "(?,?)";
				}
			}
			
			if( body.length() > 0 ) {
				sql += body;
				ps = con.prepareStatement(sql);
				int index = 1;
				for(int i = 0; i < hit2.length; i++) {
					if( !hit2[i]) {
						ps.setInt(index++,ss.getId());
						ps.setDate(index++,new Date(blackDates[i].getTimeInMillis()));
					}
				}
				ps.execute();
			}
			return true;
		} else {
			con.close();
			return false;
		}
	}
	
	public static void deleteSession(int sessionId) throws SQLException {
		SiteSession ss = getSession(sessionId);
		if( ss != null ) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "UPDATE gs_session SET status = 0 WHERE id = ? AND status = 1";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,sessionId);
			ps.execute();
			sql = "UPDATE gs_blacktime SET status = 0 WHERE sessionId = ? AND status = 1";
			ps = con.prepareStatement(sql);
			ps.setInt(1,sessionId);
			ps.execute();
			sql = "UPDATE gs_blackdate SET status = 0 WHERE sessionId = ? AND status = 1";
			ps = con.prepareStatement(sql);
			ps.setInt(1,sessionId);
			ps.execute();
			sql = "SELECT id FROM gs_activity WHERE sessionId = ? AND status = 1";
			ps = con.prepareStatement(sql);
			ps.setInt(1,sessionId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				ActivityManager.deleteActivity(ss,rs.getInt("id"));
			}
			con.close();
		}
	}
	
	public static String stringifyDays(boolean[] days) {
		String ret = "";
		for(int i = 0; i < days.length; i++) {
			if( i > 0 ) {
				ret += ",";
			}
			ret += days[i] ? "1" : "0";
		}
		return ret;
	}
}
