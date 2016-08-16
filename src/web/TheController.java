package web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Activity;
import model.CalendarFactory;
import model.SiteSession;
import model.TargetGroup;
import model.TimeRange;
import model.User;
import model.Venue;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import security.AuthenticationException;
import security.Hasher;
import security.MissingTokenException;
import security.Randomizer;

import com.google.gson.Gson;

import dao.ActivityManager;
import dao.AuditManager;
import dao.SessionManager;
import dao.TargetGroupManager;
import dao.UserManager;
import dao.VenueManager;

@Controller
public class TheController {
	private void restoreToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = (String)request.getSession().getAttribute("sessionToken");
		if(token == null || token.length() == 0 ) {
			try {
				Hasher hash = new Hasher(Hasher.SHA256);
				Randomizer random = new Randomizer(1234);
				hash.updateHash(random.getRandomLong() + "","UTF-8");
				hash.updateHash(request.getRemoteAddr(),"UTF-8");
				token = hash.getHashBASE64();
				request.getSession().setAttribute("sessionToken",token);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String genHash(User u,String ip) {
		Hasher hash;
		try {
			hash = new Hasher(Hasher.SHA256);
			hash.updateHash(u.getId() + u.getUsername(),"UTF-8");
			hash.updateHash(ip,"UTF-8");
			String token = u.getId() + "$" + hash.getHashBASE64();
			return token;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String genHash(User u,SiteSession ss, String ip) {
		Hasher hash;
		try {
			hash = new Hasher(Hasher.SHA256);
			hash.updateHash(u.getId() + u.getUsername(),"UTF-8");
			hash.updateHash(ss.getId() + "","UTF-8");
			hash.updateHash(ip,"UTF-8");
			String token = u.getId() + "," + ss.getId() + "$" + hash.getHashBASE64();
			return token;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String genToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		SiteSession ss = (SiteSession)request.getSession().getAttribute("activeSession");
		String genHash = null;
		if( u != null ) {
			if( ss == null ) {
				genHash = genHash(u,request.getRemoteAddr());
			} else {
				genHash = genHash(u,ss,request.getRemoteAddr());
			}
			request.getSession().setAttribute("sessionToken",genHash); 
		}
		return genHash;
	}
	
	private User restoreSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = (User)request.getSession().getAttribute("sessionUser");
		restoreToken(request,response);
		System.out.println(u);
		if( u == null ) {
			Cookie[] cookies = request.getCookies();
			if( cookies != null ) {
				for(Cookie c : cookies) {
					if( c.getName().equals("asSessionToken") ) {
						try {
							int dollar = c.getValue().indexOf('$');
							String keys = c.getValue().substring(0,dollar);
							String[] parts = keys.split(",");
							u = UserManager.getUser(Integer.parseInt(parts[0]));
							SiteSession ss = null;
							if( parts.length > 1 ) {
								ss = SessionManager.getSession(Integer.parseInt(parts[1]));
							}
							if( u != null ) {
								String genHash = null;
								if( ss != null ) {
									genHash = genHash(u,ss,request.getRemoteAddr());
								} else {
									genHash = genHash(u,request.getRemoteAddr());
								}
								if( genHash.equals(c.getValue())) {
									AuditManager.setUser(u, request);
									request.getSession().invalidate();
									request.getSession(true).setAttribute("sessionUser",u);
									request.getSession().setAttribute("activeSession", ss);
									AuditManager.addActivity("refreshed their session.");
								} else {
									u = null;
									c.setMaxAge(0);
									response.addCookie(c);
									logoutUser(request,response);
									AuditManager.setUser(request);
									AuditManager.addActivity("had an invalid cookie and was logged out.");
								}
							} 
						} catch(SQLException se) {
							se.printStackTrace();
							logError(se);
						}
						break;
					}
				}
			}
		} else {
			if( u.isExpired() ) {
				logoutUser(request,response);
				AuditManager.addActivity("had their session expired.");
				AuditManager.setUser(request);
				return null;
			}
			u.refreshIdle();
		}
		return u;
	}
	
	private void checkToken(String token,HttpServletRequest request,HttpServletResponse response) throws MissingTokenException, ServletException, IOException {
		String sessionToken = (String)request.getSession().getAttribute("sessionToken");
		if( sessionToken == null ) {
			restoreSession(request,response);
			sessionToken = (String)request.getSession().getAttribute("sessionToken");
		}
		if(!sessionToken.equals(token)) {
			throw new MissingTokenException();
		}
	}
	
	private void logError(Exception e) {
		AuditManager.addActivity("ran into the error " + e.getMessage());
	}
	
	@RequestMapping("/")
	public void home(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		homePage(u,(SiteSession)request.getSession().getAttribute("activeSession"),request, response);
	}
	
	public void homePage(User u,SiteSession ss,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if( u == null ) {
			request.getRequestDispatcher("WEB-INF/view/index.jsp").forward(request, response);
		} else if( ss == null ) {
			try {
				SiteSession[] sessions = SessionManager.getSessions(u);
				request.setAttribute("sessions", sessions);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e);
			}
			request.getRequestDispatcher("WEB-INF/view/sessions.jsp").forward(request, response);
		} else {
			try {
				Activity[] acts = ActivityManager.getActivities(ss);
				request.setAttribute("activities",acts);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e);
			}
			request.getRequestDispatcher("WEB-INF/view/activities.jsp").forward(request, response);
		}
	}
	
	@RequestMapping("sessions")
	public void sessions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u == null ) {
			request.getRequestDispatcher("WEB-INF/view/index.jsp").forward(request, response);
		} else {
			try {
				SiteSession[] sessions = SessionManager.getSessions(u);
				request.setAttribute("sessions", sessions);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e);
			}
			request.getRequestDispatcher("WEB-INF/view/sessions.jsp").forward(request, response);
		}
	}
	
	@ResponseBody
	@RequestMapping("/login")
	public void login(@RequestParam("token") String token,
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u == null) {
			try {
				checkToken(token,request,response);
				u = UserManager.login(username, password);
				if( u != null ) {
					request.getSession().invalidate();
					request.getSession(true).setAttribute("sessionUser", u);
					String genHash = genToken(request,response);
					Cookie c = new Cookie("asSessionToken",genHash);
					c.setMaxAge(User.SESSION_EXPIRY * 60);
					c.setSecure(true);
					c.setHttpOnly(true);
					response.addCookie(c);
					AuditManager.setUser(u, request);
					AuditManager.addActivity("logged in.");
				} else {
					request.setAttribute("error","Invalid username/password combination");
					AuditManager.addActivity("failed to login to account " + username + ".");
				}
			} catch(SQLException se) {
				se.printStackTrace();
				logError(se);
			} catch (AuthenticationException e) {
				// TODO Auto-generated catch block
				AuditManager.addActivity("failed to login to account " + username + ".");
				request.setAttribute("error", "Invalid username/password combination");
			} catch (MissingTokenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				AuditManager.addActivity("had an invalid token on login.");
				request.setAttribute("error", "An unexpected error occured.");
			} 
		}
		home(request,response);
	}
	
	@RequestMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u != null) {
			logoutUser(request,response);
			AuditManager.addActivity("logged out.");
			AuditManager.setUser(request);
		}
		homePage(null,null,request,response);
	}
	
	public void logoutUser(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		request.getSession().invalidate();
		Cookie[] cookies = request.getCookies();
		for(Cookie c : cookies) {
			if( c.getName().equals("asSessionToken") ) {
				c.setMaxAge(0);
				response.addCookie(c);
			}
		}
		request.getSession(true);
		restoreToken(request,response);
	}
	
	@RequestMapping(value="register",method = RequestMethod.GET)
	public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u != null ) {
			home(request,response);
		} else {
			request.getRequestDispatcher("WEB-INF/view/register.jsp").forward(request, response);
		}
	}
	
	@ResponseBody
	@RequestMapping(value="register",method = RequestMethod.POST)
	public void register(@RequestParam("token") String token,
						@RequestParam("username") String username,
						@RequestParam("password") String password,
						@RequestParam("confirmPassword") String confirmPassword,
						@RequestParam("fname") String fname,
						@RequestParam("mi") String mi,
						@RequestParam("lname") String lname,
						@RequestParam("email") String email,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u != null ) {
			home(request,response);
		} else {
			try {
				checkToken(token,request,response);
				if( username.matches("^[A-Za-z0-9_-]+$") && fname.matches("^[A-Za-z ,.'-]+$") && 
						mi.matches("^[A-Za-z]{0,2}.?$") && lname.matches("^[A-Za-z ,.'-]+$") && 
						email.matches("^([-.a-zA-Z0-9_]+)@([-.a-zA-Z0-9_]+)[.]([a-zA-Z]{2,5})$") && 
						UserManager.checkPass(password) && password.equals(confirmPassword)) {
					UserManager.addUser(username, password, fname, mi, lname, email);
					AuditManager.addActivity("registered account " + username + ".");
					login(token,username,password,request,response);
				} else {
					AuditManager.addActivity("ran into data validation errors on register.");
					request.setAttribute("error","Failed to register account.");
					register(request,response);
				}
			} catch (SQLException | MissingTokenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e);
				request.setAttribute("error","An unexpected error occured.");
				register(request,response);
			} 
		}
	}
	
	@RequestMapping(value="targetGroup")
	public void targetGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			home(request,response);
		} else {
			try {
				request.setAttribute("tgs", TargetGroupManager.getAllTargetGroups(u));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request.getRequestDispatcher("WEB-INF/view/targetGroup.jsp").forward(request, response);
		}
	}
	
	@ResponseBody
	@RequestMapping("/addTG")
	public void addTG(@RequestParam("token") String token,
			@RequestParam("name") String name,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.getWriter().print("{exit:1}");
		} else {
			try {
				checkToken(token,request,response);
				if(name.matches("^[A-Za-z0-9.', _\\-]+$") && TargetGroupManager.addTargetGroup(u, name) ) {
					TargetGroup[] tgs = TargetGroupManager.getAllTargetGroups(u);
					response.getWriter().print((new Gson()).toJson(tgs[tgs.length - 1]));
					AuditManager.addActivity("added target group " + name + ".");
				} else {
					AuditManager.addActivity("ran into data validation errors on add target group.");
					response.getWriter().print("null");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e);
				response.getWriter().print("null");
			}			
		}
	}
	
	@RequestMapping(value="venue")
	public void venue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			home(request,response);
		} else {
			try {
				request.setAttribute("venues", VenueManager.getAllVenues(u));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request.getRequestDispatcher("WEB-INF/view/venue.jsp").forward(request, response);
		}
	}
	
	@ResponseBody
	@RequestMapping("/addVenue")
	public void addVenue(@RequestParam("token") String token,
			@RequestParam("name") String name,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.getWriter().print("{exit:1}");
		} else {
			try {
				checkToken(token,request,response);
				if(name.matches("^[A-Za-z0-9.', _\\-]+$") && VenueManager.addVenue(u, name) ) {
					Venue[] venues = VenueManager.getAllVenues(u);
					response.getWriter().print((new Gson()).toJson(venues[venues.length - 1]));
					AuditManager.addActivity("added venue " + name + ".");
				} else {
					AuditManager.addActivity("ran into data validation errors on add venue.");
					response.getWriter().print("null");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e);
				response.getWriter().print("null");
			}			
		}
	}

	@RequestMapping(value="/addSession",method=RequestMethod.GET)
	public void addSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			home(request,response);
		} else {
			request.getRequestDispatcher("WEB-INF/view/addSession.jsp").forward(request, response);
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/addSession",method=RequestMethod.POST)
	public void addSession(@RequestParam("token") String token,
			@RequestParam("name") String name,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam(value="sunday",required=false) boolean sunday,
			@RequestParam(value="monday",required=false) boolean monday,
			@RequestParam(value="tuesday",required=false) boolean tuesday,
			@RequestParam(value="wednesday",required=false) boolean wednesday,
			@RequestParam(value="thursday",required=false) boolean thursday,
			@RequestParam(value="friday",required=false) boolean friday,
			@RequestParam(value="saturday",required=false) boolean saturday,
			@RequestParam(value="bt[]",required=false) String[] blacktimes,
			@RequestParam(value="bd[]",required=false) String[] blackdates,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			home(request,response);
		} else {
			try {
				checkToken(token,request,response);
				String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[1-2][0-9]|3[0-1])\\/2[0-9]{3}$";
				String timeRegex = "^((0?|1)[0-9]|2[0-3])([0-5][0-9])$";
				
				if( name.matches("^[A-Za-z0-9.,' \\-]+$") && startDate.matches(dateRegex) && endDate.matches(dateRegex)) {
					boolean error = false;
					Calendar[] bds = null;
					if( blackdates != null ) {
						bds = new Calendar[blackdates.length];
						int i = 0;
						for(String s : blackdates ) {
							if( !s.matches(dateRegex) ) {
								error = true;
								break;
							} else {
								bds[i] = CalendarFactory.createCalendar(s);
								i++;
							}
						}
					}
					Calendar[] bts = null;
					Calendar[] bte = null;
					if( !error && blacktimes != null ) {
						bts = new Calendar[blacktimes.length];
						bte = new Calendar[blacktimes.length];
						int i = 0;
						for(String s : blacktimes) {
							String[] parts = s.split("-");
							if( parts[0].matches(timeRegex) && parts[1].matches(timeRegex)) {
								bts[i] = CalendarFactory.createCalendarTime(parts[0]);
								bte[i] = CalendarFactory.createCalendarTime(parts[1]);
								i++;
							} else {
								error = true;
								break;
							}
						}
					}
					
					if( !error ) {
						try {
							SessionManager.addSession(u, name, new boolean[] {
									sunday,monday,tuesday,wednesday,thursday,friday,saturday
							}, CalendarFactory.createCalendar(startDate), 
									CalendarFactory.createCalendar(endDate), bts, bte, bds);
							AuditManager.addActivity("added session " + name);
							home(request,response);
							return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logError(e);
							request.setAttribute("error", "Adding Session Failed");
							request.getRequestDispatcher("WEB-INF/view/addSession.jsp").forward(request, response);
							return;
						}
					}
				}
				AuditManager.addActivity("ran into data validation errors on add session.");
				request.setAttribute("error", "Data validation error.");
				request.getRequestDispatcher("WEB-INF/view/addSession.jsp").forward(request, response);
			} catch (MissingTokenException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				AuditManager.addActivity("had an invalid token on Add Session.");
				request.setAttribute("error", "An unexpected error occured.");
				home(request,response);
			}
		}
	}
	
	@RequestMapping(value="/editSession",method=RequestMethod.GET)
	public void editSession(@RequestParam("sessionId") int sessionId,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			home(request,response);
		} else {
			request.setAttribute("sessionId", sessionId);
			request.getRequestDispatcher("WEB-INF/view/editSession.jsp").forward(request, response);
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/editSession",method=RequestMethod.POST)
	public void editSession(@RequestParam("token") String token,
			@RequestParam("sessionId") int sessionId,
			@RequestParam("name") String name,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam(value="sunday",required=false) boolean sunday,
			@RequestParam(value="monday",required=false) boolean monday,
			@RequestParam(value="tuesday",required=false) boolean tuesday,
			@RequestParam(value="wednesday",required=false) boolean wednesday,
			@RequestParam(value="thursday",required=false) boolean thursday,
			@RequestParam(value="friday",required=false) boolean friday,
			@RequestParam(value="saturday",required=false) boolean saturday,
			@RequestParam(value="bt[]",required=false) String[] blacktimes,
			@RequestParam(value="bd[]",required=false) String[] blackdates,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			home(request,response);
		} else {
			try {
				checkToken(token,request,response);
				String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[1-2][0-9]|3[0-1])\\/2[0-9]{3}$";
				String timeRegex = "^((0?|1)[0-9]|2[0-3])([0-5][0-9])$";
				
				if( name.matches("^[A-Za-z0-9.,' \\-]+$") && startDate.matches(dateRegex) && endDate.matches(dateRegex)) {
					boolean error = false;
					Calendar[] bds = null;
					if( blackdates != null ) {
						bds = new Calendar[blackdates.length];
						int i = 0;
						for(String s : blackdates ) {
							if( !s.matches(dateRegex) ) {
								error = true;
								break;
							} else {
								bds[i] = CalendarFactory.createCalendar(s);
								i++;
							}
						}
					}
					Calendar[] bts = null;
					Calendar[] bte = null;
					if( !error && blacktimes != null ) {
						bts = new Calendar[blacktimes.length];
						bte = new Calendar[blacktimes.length];
						int i = 0;
						for(String s : blacktimes) {
							String[] parts = s.split("-");
							if( parts[0].matches(timeRegex) && parts[1].matches(timeRegex)) {
								bts[i] = CalendarFactory.createCalendarTime(parts[0]);
								bte[i] = CalendarFactory.createCalendarTime(parts[1]);
								i++;
							} else {
								error = true;
								break;
							}
						}
					}
					
					if( !error ) {
						try {
							SessionManager.editSession(u, sessionId, name, new boolean[] {
									sunday,monday,tuesday,wednesday,thursday,friday,saturday
							}, CalendarFactory.createCalendar(startDate), 
									CalendarFactory.createCalendar(endDate), bts, bte, bds);
							AuditManager.addActivity("edited session " + sessionId + ": " + name + ".");
							home(request,response);
							return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logError(e);
							request.setAttribute("error", "Editing Session Failed");
							editSession(sessionId,request,response);
							return;
						}
					}
				}
				AuditManager.addActivity("ran into data validation errors on edit session.");
				request.setAttribute("error", "Data validation error.");
				editSession(sessionId,request,response);
			} catch (MissingTokenException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				AuditManager.addActivity("had an invalid token on Edit Session.");
				request.setAttribute("error", "An unexpected error occured.");
				home(request,response);
			}
		}
	}
	
	@ResponseBody
	@RequestMapping("getSession")
	public void getSession(@RequestParam("token") String token,
						@RequestParam("sessionId") int sessionId,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		User u = restoreSession(request,response);
		if( u == null ) {
			response.getWriter().print("null");
		} else {
			try {
				checkToken(token,request,response);
				SiteSession ss = SessionManager.getSession(sessionId);
				if( ss.getUserId() == u.getId() ) {
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					Gson g = new Gson();
					String json = "{\"name\":\"" + ss.getName() + "\",\"startDate\":\"" + 
									sdf.format(ss.getStartDate().getTime()) + "\",\"endDate\": \"" + 
									sdf.format(ss.getEndDate().getTime()) + "\",\"blackDays\": " + 
									g.toJson(ss.getBlackDaysString()) + ",\"blackDates\" : [";
					List<Calendar> bd = ss.getBlackdates();
					for(int i = 0; i < bd.size(); i++) {
						if( i > 0 ) {
							json += ",";
						}
						json += "\"" + sdf.format(bd.get(i).getTime()) + "\"";
					}
					json += "],\"blackTimes\":[";
					TimeRange[] bts = ss.getBlacktimes();
					SimpleDateFormat tf = new SimpleDateFormat("HHmm");
					for(int i = 0; i < bts.length; i++) {
						if( i > 0 ) {
							json += ",";
						}
						json += "{\"startTime\":\"" + tf.format(bts[i].getStartTime().getTime()) + 
									"\",\"endTime\":\"" + tf.format(bts[i].getEndTime().getTime()) + "\"}";
					}
					json += "]}";
					response.getWriter().print(json);
				} else {
					AuditManager.addActivity("tried to get session " + ss.getId() + " that isn't theirs.");
					response.getWriter().print("null");
				}
			} catch (MissingTokenException e) {
				// TODO Auto-generated catch block
				AuditManager.addActivity("had an invalid token on Get Session.");
				response.getWriter().print("null");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logError(e);
				response.getWriter().print("null");
			}
		}
	}
	
	@RequestMapping(value="/setSession")
	public void setSession(@RequestParam("sessionId") int sessionId,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			home(request,response);
		} else {
			SiteSession ss;
			try {
				ss = SessionManager.getSession(sessionId);
				if( ss.getUserId() == u.getId()) {
					request.getSession().setAttribute("activeSession", ss);
					String token = genToken(request,response);
					Cookie c = new Cookie("asSessionToken",token);
					c.setMaxAge(User.SESSION_EXPIRY * 60);
					c.setHttpOnly(true);
					c.setSecure(true);
					response.addCookie(c);
					AuditManager.addActivity("set their session to session " + ss.getId() + ".");
				} else {
					AuditManager.addActivity("tried to access session " + ss.getId() + " which is not theirs.");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e);
				request.setAttribute("error", "An unexpected error occured");
			}
			home(request,response);
		}
	}
	
	@RequestMapping(value="addActivity",method=RequestMethod.GET)
	public void addActivity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u == null ) {
			home(request,response);
		} else {
			Venue[] vs;
			try {
				vs = VenueManager.getAllVenues(u);
				TargetGroup[] tgs = TargetGroupManager.getAllTargetGroups(u);
				request.setAttribute("venues", vs);
				request.setAttribute("targetGroups", tgs);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				SiteSession ss = (SiteSession)request.getSession().getAttribute("activeSession");
				request.setAttribute("startDate",sdf.format(ss.getStartDate().getTime()));
				request.setAttribute("endDate",sdf.format(ss.getEndDate().getTime()));
				request.setAttribute("blackdays", ss.getBlackDaysString());
				request.getRequestDispatcher("WEB-INF/view/addActivity.jsp").forward(request, response);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e);
				request.setAttribute("error", "An unexpected error occured");
				home(request,response);
			}
		}
	}
	
	@RequestMapping(value="addActivity",method=RequestMethod.POST)
	public void addActivity(@RequestParam("token") String token,
			@RequestParam("name") String name,
			@RequestParam("venue") int venue,
			@RequestParam("length") int length,
			@RequestParam("startTime") String str,
			@RequestParam("endTime") String etr,
			@RequestParam(value="sunday",required=false) boolean sunday,
			@RequestParam(value="monday",required=false) boolean monday,
			@RequestParam(value="tuesday",required=false) boolean tuesday,
			@RequestParam(value="wednesday",required=false) boolean wednesday,
			@RequestParam(value="thursday",required=false) boolean thursday,
			@RequestParam(value="friday",required=false) boolean friday,
			@RequestParam(value="saturday",required=false) boolean saturday,
			@RequestParam(value="dr[]",required=false) String[] dateRange,
			@RequestParam(value="tg[]",required=false) int[] targets,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u == null ) {
			home(request,response);
		} else {
			try {
				checkToken(token,request,response);
				String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[1-2][0-9]|3[0-1])\\/2[0-9]{3}$";
				String timeRegex = "^((0?|1)[0-9]|2[0-3])([0-5][0-9])$";
				Venue v = VenueManager.getVenue(venue);
				
				if( name.matches("^[A-Za-z0-9.,' \\-]+$") && str.matches(timeRegex) && etr.matches(timeRegex) &&
						v.getUserId() == u.getId() ) {
					boolean error = false;
					
					Calendar[] bds = null;
					if( dateRange != null ) {
						bds = new Calendar[dateRange.length];
						int i = 0;
						for(String s : dateRange ) {
							System.out.println(s + s.matches(dateRegex));
							if( !s.matches(dateRegex) ) {
								error = true;
								break;
							} else {
								bds[i] = CalendarFactory.createCalendar(s);
								i++;
							}
						}
					}
					
					TargetGroup[] tgs = new TargetGroup[0];
					if(targets != null ) {
						tgs = new TargetGroup[targets.length];
						for(int i = 0; !error && i < targets.length; i++) {
							TargetGroup tg = TargetGroupManager.getTargetGroup(targets[i]);
							if( tg.getUserId() != u.getId()) {
								error = true;
							} else {
								tgs[i] = tg;
							}
						}
					}
					
					if( !error ) {
						try {
							ActivityManager.addActivity((SiteSession)request.getSession().getAttribute("activeSession"), venue, name, length, new boolean[] {
									sunday, monday, tuesday, wednesday, thursday, friday, saturday
							}, CalendarFactory.createCalendarTime(str), CalendarFactory.createCalendarTime(etr), tgs, bds);
							AuditManager.addActivity("added activity " + name + ".");
							home(request,response);
							return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logError(e);
							request.setAttribute("error", "Adding Activity Failed");
							addActivity(request,response);
							return;
						}
					}
				}
				AuditManager.addActivity("ran into data validation errors on add activity.");
				request.setAttribute("error", "Data validation error.");
				addSession(request,response);
			} catch (MissingTokenException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				AuditManager.addActivity("had an invalid token on Add Activity.");
				request.setAttribute("error", "An unexpected error occured.");
				home(request,response);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logError(e1);
				request.setAttribute("error", "Adding Activity Failed");
				addActivity(request,response);
			}
		}
	}
}