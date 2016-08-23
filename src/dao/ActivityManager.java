package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import model.Activity;
import model.Activity.Builder;
import model.CalendarFactory;
import model.SiteSession;
import model.TargetGroup;
import model.User;
import model.Venue;
import model.genetic.ScheduleChromosome;


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
				ps.execute();
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
				ps.execute();
			}
		} 
		con.close();	
	}
	
	public static Activity getActivity(SiteSession ss,int actId) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		try {
			String sql = "SELECT A.id,sessionId, venueId, V.name AS venue, A.name, length, days, startTimeRange, endTimeRange, assignedTime "
					+ "FROM gs_activity A INNER JOIN gs_venue V ON A.venueId = V.id AND A.status = 1 AND V.status = 1 "
					+ "WHERE A.id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,actId);
			ResultSet rs = ps.executeQuery();
			if(rs.next() ) {
				if( ss.getId() == rs.getInt("sessionId")) {
					Builder ab = new Activity.Builder(rs.getInt("id"),rs.getString("name"), rs.getInt("length"), rs.getString("days"), CalendarFactory.createCalendar(rs.getTimestamp("startTimeRange").getTime()), 
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
			}
			return null;
		} finally {
			con.close();
		}
	}
	
	public static void blackoutDates(Calendar[] blackdates,SiteSession ss) throws SQLException {
		if( blackdates != null & blackdates.length > 0 ) {
			Activity[] acts = getActivities(ss);
			if( acts.length > 0 ) {
				Connection con = DBManager.getInstance().getConnection();
				String sql = "UPDATE gs_activity_date SET status = 0 WHERE actID IN (";
				int i = 0;
				for(Activity a : acts ) {
					if( i > 0 ) {
						sql += ",";
					}
					sql += "?";
					i++;
				}
				sql += ") AND actDate IN (";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				i = 0;
				for(Calendar c : blackdates) {
					if( i > 0 ) {
						sql += ",";
					}
					sql += "?"; 
					i++;
				}
				sql += ")";
				PreparedStatement ps = con.prepareStatement(sql);
				i = 1;
				for(Activity a : acts) {
					ps.setInt(i,a.getId());
					i++;
				}
				for(Calendar c : blackdates) {
					ps.setString(i,sdf.format(c.getTime()));
					i++;
				}
				System.out.println(ps);
				ps.execute();
			}
		} 
	}
	
	public static Activity[] getActivities(SiteSession ss) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		String sql = "SELECT id FROM gs_activity WHERE status = 1 AND sessionId = ? ORDER BY assignedTime";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1,ss.getId());
		ResultSet rs = ps.executeQuery();
		ArrayList<Activity> acts = new ArrayList<Activity>();
		while(rs.next()) {
			acts.add(getActivity(ss,rs.getInt("id")));
		}
		return acts.toArray(new Activity[0]);
	}
	
	public static void assignDates(ScheduleChromosome sc) throws SQLException {
		Connection con = DBManager.getInstance().getConnection();
		for(int i = 0; i < sc.size(); i++) {
			String sql = "UPDATE gs_activity SET assignedTime = ? WHERE id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			SimpleDateFormat allf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if( sc.getActivity(i).getStartTime().getTimeInMillis() == 0 ) {
				ps.setString(1,null);
			} else {
				ps.setString(1,allf.format(sc.getActivity(i).getStartTime().getTime()));
			}
			ps.setInt(2, sc.getActivity(i).getId());
			ps.execute();
		}
		con.close();
	}
	
	public static void deleteActivity(SiteSession ss, int id) throws SQLException {
		Activity a = getActivity(ss,id);
		if( a != null ) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "UPDATE gs_activity SET status = 0 WHERE id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,id);
			ps.execute();
			
			sql = "UPDATE gs_activity_date SET status = 0 WHERE actID = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1,id);
			ps.execute();
			
			sql = "UPDATE gs_activity_target_group SET status = 0 WHERE actID = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1,id);
			ps.execute();
			
			con.close();
		}
	}
	
	public static boolean editActivity(int id,SiteSession ss, int venue, String name, int length, 
			boolean[] days, Calendar startTime, Calendar endTime,
		TargetGroup[] targets, Calendar[] dates) throws SQLException {
		Activity a = getActivity(ss, id);
		if( a != null ) {
			Connection con = DBManager.getInstance().getConnection();
			String sql = "UPDATE gs_activity SET venueId = ?,name = ?,length = ?,days = ?,startTimeRange = ?,endTimeRange = ?,assignedTime = NULL WHERE id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,venue);
			ps.setString(2,name);
			ps.setInt(3,length);
			ps.setString(4, SessionManager.stringifyDays(days));
			ps.setTime(5, new Time(startTime.getTime().getTime()));
			ps.setTime(6, new Time(endTime.getTime().getTime()));
			ps.setInt(7,a.getId());
			System.out.println(ps);
			ps.execute();
			
			if(dates != null && dates.length > 0 ) {
				boolean[] hit1 = new boolean[dates.length];
				boolean[] hit2 = new boolean[a.getDateRange().length];
				
				for(int i = 0; i < hit1.length; i++) {
					for(int j = 0; j < hit2.length; j++) {
						if( dates[i].equals(a.getDateRange()[j])) {
							hit1[i] = hit2[j] = true;
							break;
						}
					}
				}
				
				sql = "INSERT INTO gs_activity_date(actId,actDate) VALUES ";
				int curr = 0;
				for(int i = 0; i < dates.length; i++) {
					if(!hit1[i]) {
						sql += (curr > 0 ? "," : "") + "(?,?)";
						curr++;
					}
				}
				if( curr > 0 ) {
					ps = con.prepareStatement(sql);
					curr = 0;
					for(int i = 0; i < dates.length; i++) {
						if( !hit1[i] ) {
							ps.setInt(2 * curr + 1, id);
							ps.setDate(2 * curr + 2, new Date(dates[i].getTime().getTime()));
							curr++;
						}
					}
					System.out.println(ps);
					ps.execute();
				}
				sql = "UPDATE gs_activity_date SET status = 0 WHERE actId = ? AND actDate IN (";
				curr = 0;
				for(int i = 0; i < a.getDateRange().length; i++) {
					if( !hit2[i] ) {
						if( curr > 0 ) {
							sql += ",";
						}
						sql += "?";
						curr++;
					}
				}
				sql += ")";
				if( curr > 0 ) {
					ps = con.prepareStatement(sql);
					ps.setInt(1,id);
					curr = 2;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					for(int i = 0; i < a.getDateRange().length; i++) {
						if( !hit2[i] ) {
							ps.setString(curr,sdf.format(a.getDateRange()[i].getTime()));
							curr++;
						}
					}
					System.out.println(ps);
					ps.execute();
				}
			}
			
			if( targets != null && targets.length > 0 ) {
				boolean[] hit1 = new boolean[targets.length];
				boolean[] hit2 = new boolean[a.getTargetGroups().size()];
				
				for(int i = 0; i < hit1.length; i++) {
					for(int j = 0; j < hit2.length; j++) {
						if( targets[i].getId() == a.getTargetGroups().get(j).getId() ) {
							hit1[i] = hit2[j] = true;
							break;
						}
					}
				}
				
				sql = "INSERT INTO gs_activity_target_group(groupId,actId) VALUES ";
				
				int curr = 0;
				for(int i = 0; i < targets.length; i++) {
					if( !hit1[i] ) {
						sql += (curr > 0 ? "," : "") + "(?,?)";
						curr++;
					}
				}
				if( curr > 0 ) {
					ps = con.prepareStatement(sql);
					curr = 0;
					for(int i = 0; i < targets.length; i++) {
						if(!hit1[i]) {
							ps.setInt(2 * curr + 1, targets[i].getId());
							ps.setInt(2 * curr + 2, id);
							curr++;
						}
					}
					System.out.println(ps);
					ps.execute();
				}
				
				sql = "UPDATE gs_activity_target_group SET status = 0 WHERE actId = ? AND groupId IN (";
				curr = 0;
				for(int i = 0; i < hit2.length; i++) {
					if(!hit2[i]) {
						if( curr > 0 ) {
							sql += ",";
						}
						sql += "?";
						curr++;
					}
				}
				sql += ")";
				if( curr > 0 ) {
					ps = con.prepareStatement(sql);
					ps.setInt(1,id);
					curr = 2;
					for(int i = 0; i < hit2.length; i++) {
						if(!hit2[i]) {
							ps.setInt(curr,a.getTargetGroups().get(i).getId());
							curr++;
						}
					}
					System.out.println(ps);
					ps.execute();
				}
			}
			con.close();
		} 
		return false;
	}
}
