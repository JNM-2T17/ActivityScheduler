package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import model.Activity;
import model.Activity.Builder;
import model.CalendarFactory;
import model.SiteSession;
import model.TargetGroup;
import model.Venue;


public class ActivityManager {
	public static void addActivity(SiteSession ss, int venue, String name, int length, 
							boolean[] days, Calendar startTime, Calendar endTime,
							TargetGroup[] targets, Calendar[] dates) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "INSERT INTO gs_activity(sessionId,venueId,name,length,days,startTimeRange,endTimeRange) VALUES (?,?,?,?,?,?,?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,ss.getId());
		ps.setInt(2,venue);
		ps.setString(3,name);
		ps.setInt(4,length);
		ps.setString(5, SessionManager.stringifyDays(days));
		ps.setTime(6, new Time(startTime.getTime().getTime()));
		ps.setTime(7, new Time(endTime.getTime().getTime()));
		ps.execute();
		sql = "SELECT id FROM gs_activity WHERE status = 1 AND sessionId = ? ORDER BY dateAdded DESC LIMIT 1";
		ps = con.prepareStatement(sql);
		ps.setInt(1,ss.getId());
		ResultSet rs = ps.executeQuery();
		if( rs.next() ) {
			int actId = rs.getInt("id");
			if(dates != null && dates.length > 0 ) {
				sql = "INSERT INTO gs_activity_date(actId,actDate) VALUES ";
				for(int i = 0; i < dates.length; i++) {
					sql += (i > 0 ? "," : "") + "(?,?)";
				}
				ps = con.prepareStatement(sql);
				for(int i = 0; i < dates.length; i++) {
					ps.setInt(2 * i + 1, actId);
					ps.setDate(2 * i + 2, new Date(dates[i].getTime().getTime()));
				}
				ps.executeQuery();
			}
			
			if( targets != null && targets.length > 0 ) {
				sql = "INSERT INTO gs_activity_target_group(groupId,actId) VALUES ";
				for(int i = 0; i < targets.length; i++) {
					sql += (i > 0 ? "," : "") + "(?,?)";
				}
				ps = con.prepareStatement(sql);
				for(int i = 0; i < targets.length; i++) {
					ps.setInt(2 * i + 1, targets[i].getId());
					ps.setInt(2 * i + 2, actId);
				}
				ps.executeQuery();
			}
		} 
		con.close();	
	}
	
	public static Activity getActivity(SiteSession ss,int actId) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		try {
			String sql = "SELECT sessionId, venueId, V.name AS venue, A.name, length, days, startTimeRange, endTimeRange, assignedTime "
					+ "FROM gs_activity A INNER JOIN gs_venue V ON A.venueId = V.id AND A.status = 1 AND V.status = 1 "
					+ "WHERE A.id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,actId);
			ResultSet rs = ps.executeQuery();
			if(rs.next() ) {
				Builder ab = new Activity.Builder(rs.getString("name"), rs.getInt("length"), rs.getString("days"), CalendarFactory.createCalendar(rs.getTimestamp("startTimeRange").getTime()), 
									CalendarFactory.createCalendar(rs.getTimestamp("endTimeRange").getTime()), new Venue(rs.getInt("venueId"),rs.getString("venue")), 
									ss);
				Timestamp ts = rs.getTimestamp("assignedTime");
				if( ts != null ) {
					ab.setStartTime(CalendarFactory.createCalendar(ts.getTime()));
				}
				
				sql = "SELECT TG.id, TG.name "
					+ "FROM gs_activity_target_group ATG INNER JOIN gs_target_group TG ON ATG.groupId = TG.id AND ATG.status = 1 AND TG.status = 1 "
					+ "WHERE actId = ?";
				ps = con.prepareStatement(sql);
				ps.setInt(1,actId);
				rs = ps.executeQuery();
				while(rs.next()) {
					ab.addTargetGroup(new TargetGroup(rs.getInt("id"),rs.getString("name")));
				}
				sql = "SELECT actDate "
						+ "FROM gs_activity_date " 
						+ "WHERE status = 1 AND actId = ?";
				ps = con.prepareStatement(sql);
				ps.setInt(1,actId);
				rs = ps.executeQuery();
				while(rs.next()) {
					ab.addDate(CalendarFactory.createCalendar(rs.getTimestamp("actDate").getTime()));
				}
				return ab.buildActivity();
			}
			return null;
		} finally {
			con.close();
		}
	}
}
