package web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import model.genetic.GeneticScheduleGenerator;
import model.genetic.ScheduleChromosome;

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
			Cookie c = new Cookie("asSessionToken",genHash);
			c.setHttpOnly(true);
			c.setSecure(true);
			c.setMaxAge(User.SESSION_EXPIRY * 60);
			response.addCookie(c);
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
									request.getSession().invalidate();
									request.getSession(true).setAttribute("sessionUser",u);
									request.getSession().setAttribute("auditor",new AuditManager(u, request));
									request.getSession().setAttribute("activeSession", ss);
									request.getSession().setAttribute("sessionToken", genHash);
									((AuditManager)request.getSession().getAttribute("auditor")).addActivity("refreshed their session.");
								} else {
									u = null;
									c.setMaxAge(0);
									response.addCookie(c);
									logoutUser(request,response);
									request.getSession().setAttribute("auditor",new AuditManager(request));
									((AuditManager)request.getSession().getAttribute("auditor")).addActivity("had an invalid cookie and was logged out.");
								}
							} 
						} catch(SQLException se) {
							se.printStackTrace();
							logError(se,request);
						}
						break;
					}
				}
			}
		} else {
			if( u.isExpired() ) {
				logoutUser(request,response);
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("had their session expired.");
				request.getSession().setAttribute("auditor",new AuditManager(request));
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
	
	private void logError(Exception e,HttpServletRequest request) {
		((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into the error " + e.getMessage());
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
				logError(e,request);
			}
			request.getRequestDispatcher("WEB-INF/view/sessions.jsp").forward(request, response);
		} else {
			try {
				Activity[] acts = ActivityManager.getActivities(ss);
				request.setAttribute("activities",acts);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
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
				logError(e,request);
			}
			request.getRequestDispatcher("WEB-INF/view/sessions.jsp").forward(request, response);
		}
	}
	
	@ResponseBody
	@RequestMapping("/checkUsername")
	public void checkUsername(@RequestParam("token") String token,
			@RequestParam("username") String username,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u;
		try {
			u = UserManager.getUser(username);
			response.getWriter().print(u == null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logError(e,request);
			response.getWriter().print(false);
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
					request.getSession().setAttribute("auditor",new AuditManager(u, request));
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("logged in.");
				} else {
					request.getSession().setAttribute("error","Invalid username/password combination");
					request.getSession().setAttribute("prompt",true);
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("failed to login to account " + username + ".");
				}
			} catch(SQLException se) {
				se.printStackTrace();
				logError(se,request);
			} catch (AuthenticationException e) {
				// TODO Auto-generated catch block
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("failed to login to account " + username + ".");
				request.getSession().setAttribute("error", "Invalid username/password combination");
				request.getSession().setAttribute("prompt",true);
			} catch (MissingTokenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("had an invalid token on login.");
				request.getSession().setAttribute("error", "An unexpected error occured.");
				request.getSession().setAttribute("prompt",true);
			} 
		}
		response.sendRedirect("/ActivityScheduler/.");
	}
	
	@RequestMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u != null) {
			((AuditManager)request.getSession().getAttribute("auditor")).addActivity("logged out.");
			logoutUser(request,response);
			request.getSession().setAttribute("auditor",new AuditManager(request));
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
			response.sendRedirect("/ActivityScheduler/.");
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
			response.sendRedirect("/ActivityScheduler/.");
		} else {
			try {
				checkToken(token,request,response);
				if( username.matches("^[A-Za-z0-9_-]+$") && fname.matches("^[A-Za-z ,.'-]+$") && 
						mi.matches("^[A-Za-z]{0,2}.?$") && lname.matches("^[A-Za-z ,.'-]+$") && 
						email.matches("^([-.a-zA-Z0-9_]+)@([-.a-zA-Z0-9_]+)[.]([a-zA-Z]{2,5})$") && 
						UserManager.checkPass(password) && password.equals(confirmPassword)) {
					if(UserManager.addUser(username, password, fname, mi, lname, email)) {
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("registered account " + username + ".");
						login(token,username,password,request,response);
					} else {
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to register account " + username + " which is already registered.");
						request.getSession().setAttribute("error", "Username is in use.");
						request.getSession().setAttribute("prompt", true);
						response.sendRedirect("/ActivityScheduler/register");
					}
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on register.");
					request.getSession().setAttribute("error","Failed to register account.");
					request.getSession().setAttribute("prompt",true);
					response.sendRedirect("/ActivityScheduler/register");
				}
			} catch (SQLException | MissingTokenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				request.getSession().setAttribute("error","An unexpected error occured.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/register");
			} 
		}
	}
	
	@RequestMapping(value="editAccount",method = RequestMethod.GET)
	public void editAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.sendRedirect("/ActivityScheduler/.");
		} else {
			request.setAttribute("u", u);
			request.getRequestDispatcher("WEB-INF/view/editAccount.jsp").forward(request, response);
		}
	}
	
	@ResponseBody
	@RequestMapping(value="editAccount",method = RequestMethod.POST)
	public void editAccount(@RequestParam("token") String token,
						@RequestParam("username") String username,
						@RequestParam("password") String password,
						@RequestParam("newPassword") String newPassword,
						@RequestParam("confirmPassword") String confirmPassword,
						@RequestParam("fname") String fname,
						@RequestParam("mi") String mi,
						@RequestParam("lname") String lname,
						@RequestParam("email") String email,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.sendRedirect("/ActivityScheduler/.");
		} else {
			try {
				checkToken(token,request,response);
				if( username.matches("^[A-Za-z0-9_-]+$") && fname.matches("^[A-Za-z ,.'-]+$") && 
						mi.matches("^[A-Za-z]{0,2}.?$") && lname.matches("^[A-Za-z ,.'-]+$") && 
						email.matches("^([-.a-zA-Z0-9_]+)@([-.a-zA-Z0-9_]+)[.]([a-zA-Z]{2,5})$") && 
						UserManager.checkPass(newPassword) && newPassword.equals(confirmPassword)) {
					try {
						if(UserManager.updateUser(u.getId(),username, password, newPassword, fname, mi, lname, email)) {
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("edited their account.");
							request.getSession().setAttribute("message", "Account successfully edited.");
							request.getSession().setAttribute("prompt", true);
							response.sendRedirect("/ActivityScheduler/.");
						} else {
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to register account " + username + " which is already registered.");
							request.getSession().setAttribute("error", "Username is in use.");
							request.getSession().setAttribute("prompt", true);
							response.sendRedirect("/ActivityScheduler/register");
						}
					} catch (AuthenticationException e) {
						// TODO Auto-generated catch block
						logError(e,request);
						e.printStackTrace();
					}
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on edit account.");
					request.getSession().setAttribute("error","Failed to edit account account.");
					request.getSession().setAttribute("prompt",true);
					response.sendRedirect("/ActivityScheduler/editAccount");
				}
			} catch (SQLException | MissingTokenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				request.getSession().setAttribute("error","An unexpected error occured.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/editAccount");
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
			response.getWriter().print(false);
		} else {
			try {
				checkToken(token,request,response);
				if(name.matches("^[A-Za-z0-9.', _\\-]+$") && TargetGroupManager.addTargetGroup(u, name) ) {
					TargetGroup[] tgs = TargetGroupManager.getAllTargetGroups(u);
					response.getWriter().print((new Gson()).toJson(tgs[tgs.length - 1]));
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("added target group " + name + ".");
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on add target group.");
					response.getWriter().print("null");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				response.getWriter().print("null");
			}			
		}
	}
	
	@ResponseBody
	@RequestMapping("/editTG")
	public void editTG(@RequestParam("token") String token,
			@RequestParam("id") int id,
			@RequestParam("name") String name,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.getWriter().print(false);
		} else {
			try {
				checkToken(token,request,response);
				TargetGroup tg = TargetGroupManager.getTargetGroup(id);
				if( tg.getUserId() == u.getId() ) {
					if(name.matches("^[A-Za-z0-9.', _\\-]+$") ) {
						if(TargetGroupManager.updateTargetGroup(id, name) ) {
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("updated target group " + id + ".");
							response.getWriter().print(true);
						} else {
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("failed to update target group " + id + ".");
							response.getWriter().print("That target group already exists.");
						}
					} else {
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on edit target group.");
						response.getWriter().print("Your target group name is invalid.");
					}
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to edit target group " + id + " which isn't theirs.");
					response.getWriter().print("That target group is not yours.");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				response.getWriter().print("An unexpected error occured.");
			}			
		}
	}
	
	@ResponseBody
	@RequestMapping("/deleteTG")
	public void deleteTG(@RequestParam("token") String token,
			@RequestParam("id") int id,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.getWriter().print(false);
		} else {
			try {
				checkToken(token,request,response);
				TargetGroup tg = TargetGroupManager.getTargetGroup(id);
				if( tg.getUserId() == u.getId() ) {
					if( TargetGroupManager.canDelete(u, id)) {
						TargetGroupManager.deleteTargetGroup(id);
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("deleted target group " + id + ".");
						response.getWriter().print(true);
					} else {
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to delete target group " + id + " which is in use.");
						response.getWriter().print("That target group is in use. Please remove it from all activities before deleting.");
					}
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to delete target group " + id + " which isn't theirs.");
					response.getWriter().print("That target group is not yours.");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				response.getWriter().print("An unexpected error occured.");
			}			
		}
	}
	
	@RequestMapping(value="venue")
	public void venue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.sendRedirect("/ActivityScheduler/.");
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
			response.getWriter().print(false);
		} else {
			try {
				checkToken(token,request,response);
				if(name.matches("^[A-Za-z0-9.', _\\-()]+$") && VenueManager.addVenue(u, name) ) {
					Venue[] venues = VenueManager.getAllVenues(u);
					response.getWriter().print((new Gson()).toJson(venues[venues.length - 1]));
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("added venue " + name + ".");
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on add venue.");
					response.getWriter().print("null");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				response.getWriter().print("null");
			}			
		}
	}
	
	@ResponseBody
	@RequestMapping("/editVenue")
	public void editVenue(@RequestParam("token") String token,
			@RequestParam("id") int id,
			@RequestParam("name") String name,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.getWriter().print(false);
		} else {
			try {
				checkToken(token,request,response);
				Venue v = VenueManager.getVenue(id);
				if( v.getUserId() == u.getId() ) {
					if(name.matches("^[A-Za-z0-9.', _\\-()]+$") ) {
						if(VenueManager.updateVenue(id, name) ) {
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("updated venue " + id + ".");
							response.getWriter().print(true);
						} else {
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("failed to update venue " + id + ".");
							response.getWriter().print("That venue already exists.");
						}
					} else {
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on edit venue.");
						response.getWriter().print("Your venue name is invalid.");
					}
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to edit venue " + id + " which isn't theirs.");
					response.getWriter().print("That venue is not yours.");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				response.getWriter().print("An unexpected error occured.");
			}			
		}
	}
	
	@ResponseBody
	@RequestMapping("/deleteVenue")
	public void deleteVenue(@RequestParam("token") String token,
			@RequestParam("id") int id,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.getWriter().print(false);
		} else {
			try {
				checkToken(token,request,response);
				Venue v = VenueManager.getVenue(id);
				if( v.getUserId() == u.getId() ) {
					if( VenueManager.canDelete(u, id)) {
						VenueManager.deleteVenue(id);
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("deleted venue " + id + ".");
						response.getWriter().print(true);
					} else {
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to delete venue " + id + " which is in use.");
						response.getWriter().print("That venue is in use. Please move all activities set here before deleting.");
					}
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to delete venue " + id + " which isn't theirs.");
					response.getWriter().print("That venue is not yours.");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				response.getWriter().print("An unexpected error occured.");
			}			
		}
	}

	@RequestMapping(value="/addSession",method=RequestMethod.GET)
	public void addSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.sendRedirect("/ActivityScheduler/.");
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
			response.sendRedirect("/ActivityScheduler/.");
		} else {
			try {
				checkToken(token,request,response);
				String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[1-2][0-9]|3[0-1])\\/2[0-9]{3}$";
				String timeRegex = "^((0?|1)[0-9]|2[0-3])([0-5][0-9])$";
				
				System.out.println(name + " " + name.matches("^[A-Za-z0-9.,' \\-]+$"));
				System.out.println(startDate + " " + startDate.matches(dateRegex));
				System.out.println(endDate + " " + endDate.matches(dateRegex));
				
				if( name.matches("^[A-Za-z0-9.,' \\-]+$") && startDate.matches(dateRegex) && endDate.matches(dateRegex)) {
					boolean error = false;
					Calendar[] bds = null;
					if( blackdates != null ) {
						bds = new Calendar[blackdates.length];
						int i = 0;
						for(String s : blackdates ) {
							System.out.println(s + " " + dateRegex);
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
							System.out.println(s + " " + parts[0].matches(timeRegex) + " " + parts[1].matches(timeRegex));
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
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("added session " + name);
							request.getSession().setAttribute("message", "Session was successfully added.");
							request.getSession().setAttribute("prompt", true);
							request.getSession().setAttribute("activeSession", null);
							genToken(request, response);
							response.sendRedirect("/ActivityScheduler/.");
							return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logError(e,request);
							request.getSession().setAttribute("error", "Adding Session Failed");
							request.getSession().setAttribute("prompt",true);
							response.sendRedirect("/ActivityScheduler/addSession");
							return;
						}
					}
				}
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on add session.");
				request.getSession().setAttribute("error", "Data validation error.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/addSession");
			} catch (MissingTokenException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("had an invalid token on Add Session.");
				request.getSession().setAttribute("error", "An unexpected error occured.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/addSession");
			}
		}
	}
	
	@RequestMapping(value="/editSession",method=RequestMethod.GET)
	public void editSession(@RequestParam("sessionId") int sessionId,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.sendRedirect("/ActivityScheduler/.");
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
			response.sendRedirect("/ActivityScheduler/.");
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
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("edited session " + sessionId + ": " + name + ".");
							SiteSession ss = SessionManager.getSession(sessionId);
							if( ss.getId() == ((SiteSession)request.getSession().getAttribute("activeSession")).getId()) {
								request.getSession().setAttribute("activeSession", ss);
							}
							request.getSession().setAttribute("message", "Session successfully edited.");
							request.getSession().setAttribute("prompt", true);
							response.sendRedirect("/ActivityScheduler/.");
							return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logError(e,request);
							request.getSession().setAttribute("error", "Editing Session Failed");
							request.getSession().setAttribute("prompt",true);
							response.sendRedirect("/ActivityScheduler/editSession?sessionId="+sessionId);
							return;
						}
					}
				}
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on edit session.");
				request.getSession().setAttribute("error", "Data validation error.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/editSession?sessionId=" + sessionId);
			} catch (MissingTokenException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("had an invalid token on Edit Session.");
				request.getSession().setAttribute("error", "An unexpected error occured.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/editSession?sessionId=" + sessionId);
			}
		}
	}
	
	@ResponseBody
	@RequestMapping("deleteSession")
	public void deleteSession(@RequestParam("token") String token,
			@RequestParam("id") int id,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		User u = restoreSession(request,response);
		if( u == null ) {
			response.getWriter().print(false);
		} else {
			SiteSession ss;
			try {
				checkToken(token,request,response);
				ss = SessionManager.getSession(id);
				if( ss != null ) {
					if( ss.getUserId() == u.getId() ) {
						SessionManager.deleteSession(ss.getId());
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("deleted session " + id + ".");
						if( ((SiteSession)request.getSession().getAttribute("activeSession")).getId() == ss.getId() ) {
							request.getSession().setAttribute("activeSession",null);
							String genHash = genHash(u, request.getRemoteAddr());
							request.getSession().setAttribute("sessionToken", genHash);
							Cookie c = new Cookie("asSessionToken",genHash);
							c.setHttpOnly(true);
							c.setSecure(true);
							c.setMaxAge(User.SESSION_EXPIRY * 60);
							response.addCookie(c);
						}
						request.getSession().setAttribute("message", "Session successfully deleted.");
						request.getSession().setAttribute("prompt", true);
						response.getWriter().print(true);
					} else {
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to delete session " + id + " which is not theirs.");
						response.getWriter().print("That session is not yours.");
					}
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to delete a nonexistent session " + id + ".");
					response.getWriter().print("That session does not exist.");
				}
			} catch (SQLException | MissingTokenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				response.getWriter().print("An unexpected error occured.");
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
			response.getWriter().print(false);
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
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to get session " + ss.getId() + " that isn't theirs.");
					response.getWriter().print("null");
				}
			} catch (MissingTokenException e) {
				// TODO Auto-generated catch block
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("had an invalid token on Get Session.");
				response.getWriter().print("null");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logError(e,request);
				response.getWriter().print("null");
			}
		}
	}
	
	@RequestMapping(value="/setSession")
	public void setSession(@RequestParam("sessionId") int sessionId,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		if( u == null ) {
			response.sendRedirect("/ActivityScheduler/.");
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
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("set their session to session " + ss.getId() + ".");
				} else {
					((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to access session " + ss.getId() + " which is not theirs.");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				request.getSession().setAttribute("error", "An unexpected error occured");
				request.getSession().setAttribute("prompt",true);
			}
			response.sendRedirect("/ActivityScheduler/.");
		}
	}
	
	@ResponseBody
	@RequestMapping("getActivity")
	public void getActivity(@RequestParam("token") String token,
							@RequestParam("actId") int actId,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u == null || request.getSession().getAttribute("activeSession")== null ) {
			response.getWriter().print(false);
		} else {
			try {
				checkToken(token,request,response);
				Activity a = ActivityManager.getActivity((SiteSession)request.getSession().getAttribute("activeSession"), actId);
				if( a != null ) {
					SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
					String json = "{\"name\":\"" + a.getName() + "\",\"length\":\"" + a.getLength() + 
									"\",\"venue\":\"" + a.getVenue().getId() + "\",\"startTime\":\"" + sdf.format(a.getStartTimeRange().getTime()) + "\",\"endTime\":\"" + 
									sdf.format(a.getEndTimeRange().getTime()) + "\",\"days\":[";
					int i = 0;
					for(String s : a.getDaysString()) {
						if( i > 0 ) {
							json += ",";
						}
						json += "\"" + s + "\"";
						
						i++;
					}
					json += "],\"tgs\":[";
					i = 0;
					for(TargetGroup tg : a.getTargetGroups()) {
						if( i > 0 ) {
							json += ",";
						}
						json += "\"" + tg.getId() + "\"";
						
						i++;
					}
					json += "],\"dateRange\":[";
					SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
					i = 0;
					for(Calendar d : a.getDateRange()) {
						if( i > 0 ) {
							json += ",";
						}
						json += "\"" + sdf2.format(d.getTime()) + "\"";
						
						i++;
					}
					json += "]}";
					response.getWriter().print(json);
				} else {
					response.getWriter().print("null");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				logError(e,request);
				response.getWriter().print("null");
			}
			
		}
	}
	
	@RequestMapping(value="addActivity",method=RequestMethod.GET)
	public void addActivity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u == null ) {
			response.sendRedirect("/ActivityScheduler/.");
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
				request.setAttribute("blackdates", ss.getBlackdatesString());
				request.getRequestDispatcher("WEB-INF/view/addActivity.jsp").forward(request, response);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e,request);
				request.getSession().setAttribute("error", "An unexpected error occured");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/.");
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
			response.sendRedirect("/ActivityScheduler/.");
		} else {
			try {
				checkToken(token,request,response);
				String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[1-2][0-9]|3[0-1])\\/2[0-9]{3}$";
				String timeRegex = "^((0?|1)[0-9]|2[0-3])([0-5][0-9])$";
				Venue v = VenueManager.getVenue(venue);
				SiteSession ss = (SiteSession)request.getSession().getAttribute("activeSession");
				
				
				if( name.matches("^[A-Za-z0-9.,' \\-:&]+$") && str.matches(timeRegex) && etr.matches(timeRegex) &&
						v.getUserId() == u.getId() ) {
					boolean error = false;
					
					Calendar[] bds = null;
					if( dateRange != null ) {
						bds = new Calendar[dateRange.length];
						int i = 0;
						for(String s : dateRange ) {
							if( !s.matches(dateRegex) ) {
								error = true;
								break;
							} else {
								bds[i] = CalendarFactory.createCalendar(s);
								for(Calendar c : ss.getBlackdates() ) {
									if( bds[i].equals(c)) {
										error = true;
										break;
									}
								}
								if( error ) {
									break;
								}
								i++;
							}
						}
					}
					
					TargetGroup[] tgs = new TargetGroup[0];
					if(!error && targets != null ) {
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
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("added activity " + name + ".");
							request.getSession().setAttribute("message", "Activity successfully added.");
							request.getSession().setAttribute("prompt", true);
							response.sendRedirect("/ActivityScheduler/.");
							return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logError(e,request);
							request.getSession().setAttribute("error", "Adding Activity Failed");
							request.getSession().setAttribute("prompt",true);
							response.sendRedirect("/ActivityScheduler/addActivity");
							return;
						}
					}
				}
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on add activity.");
				request.getSession().setAttribute("error", "Data validation error.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/addActivity");
			} catch (MissingTokenException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("had an invalid token on Add Activity.");
				request.getSession().setAttribute("error", "An unexpected error occured.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/addActivity");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logError(e1,request);
				request.getSession().setAttribute("error", "Adding Activity Failed");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/addActivity");
			}
		}
	}
	
	@RequestMapping(value="editActivity",method=RequestMethod.GET)
	public void editActivity(@RequestParam("id") String idS,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u == null ) {
			response.sendRedirect("/ActivityScheduler/.");
		} else {
			try {
				int id = Integer.parseInt(idS);
				request.setAttribute("actId", id);
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
					request.setAttribute("blackdates", ss.getBlackdatesString());
					request.getRequestDispatcher("WEB-INF/view/editActivity.jsp").forward(request, response);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logError(e,request);
					request.getSession().setAttribute("error", "An unexpected error occured");
					request.getSession().setAttribute("prompt",true);
					response.sendRedirect("/ActivityScheduler/.");
				}
			} catch (NumberFormatException nfe) {
				logError(nfe,request);
				response.sendRedirect("/ActivityScheduler/.");
			}
		}
	}
	
	@RequestMapping(value="editActivity",method=RequestMethod.POST)
	public void editActivity(@RequestParam("token") String token,
			@RequestParam("actId") int id,
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
			response.sendRedirect("/ActivityScheduler/.");
		} else {
			try {
				checkToken(token,request,response);
				String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[1-2][0-9]|3[0-1])\\/2[0-9]{3}$";
				String timeRegex = "^((0?|1)[0-9]|2[0-3])([0-5][0-9])$";
				Venue v = VenueManager.getVenue(venue);
				SiteSession ss = (SiteSession)request.getSession().getAttribute("activeSession");
				
				if( name.matches("^[A-Za-z0-9.,' \\-:&]+$") && str.matches(timeRegex) && etr.matches(timeRegex) &&
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
								for(Calendar c : ss.getBlackdates() ) {
									if( bds[i].equals(c)) {
										error = true;
										break;
									}
								}
								if( error ) {
									break;
								}
								i++;
							}
						}
					}
					
					TargetGroup[] tgs = new TargetGroup[0];
					if(!error && targets != null ) {
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
							ActivityManager.editActivity(id,(SiteSession)request.getSession().getAttribute("activeSession"), venue, name, length, new boolean[] {
									sunday, monday, tuesday, wednesday, thursday, friday, saturday
							}, CalendarFactory.createCalendarTime(str), CalendarFactory.createCalendarTime(etr), tgs, bds);
							((AuditManager)request.getSession().getAttribute("auditor")).addActivity("edited activity " + name + ".");
							request.getSession().setAttribute("message", "Activity successfully edited.");
							request.getSession().setAttribute("prompt", true);
							response.sendRedirect("/ActivityScheduler/.");
							return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logError(e,request);
							request.getSession().setAttribute("error", "Editing Activity Failed");
							request.getSession().setAttribute("prompt",true);
							response.sendRedirect("/ActivityScheduler/editActivity");
							return;
						}
					}
				}
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("ran into data validation errors on edit activity.");
				request.getSession().setAttribute("error", "Data validation error.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/editActivity?id=" + id);
			} catch (MissingTokenException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("had an invalid token on Add Activity.");
				request.getSession().setAttribute("error", "An unexpected error occured.");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/editActivity?id=" + id);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logError(e1,request);
				request.getSession().setAttribute("error", "Editing Activity Failed");
				request.getSession().setAttribute("prompt",true);
				response.sendRedirect("/ActivityScheduler/editActivity?id=" + id);
			}
		}
	}
	
	@ResponseBody
	@RequestMapping("deleteActivity")
	public void deleteActivity(@RequestParam("token") String token,
			@RequestParam("id") int id,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		User u = restoreSession(request,response);
		if( u == null ) {
			response.getWriter().print(false);
		} else {
			SiteSession ss = (SiteSession)request.getSession().getAttribute("activeSession");
			if( ss == null ) {
				response.getWriter().print(false);
				return;
			} else {
				try {
					checkToken(token,request,response);
					Activity a = ActivityManager.getActivity(ss, id);
					if( a != null ) {
						ActivityManager.deleteActivity(ss,a.getId());
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("deleted activity " + id + ".");
						request.getSession().setAttribute("message", "Activity successfully deleted.");
						request.getSession().setAttribute("prompt", true);
						response.getWriter().print(true);
					} else {
						((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to delete a nonexistent activity " + id + ".");
						response.getWriter().print("That activity does not exist within this session.");
					}
				} catch (SQLException | MissingTokenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logError(e,request);
					response.getWriter().print("An unexpected error occured.");
				}
			}
		}
	}
	
	@RequestMapping("genSched")
	public void genSched(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		if( u == null ) {
			response.getWriter().print(false);
		} else {
			SiteSession ss = (SiteSession)request.getSession().getAttribute("activeSession");
			if( ss != null ) {
				try {
					GeneticScheduleGenerator gsg = new GeneticScheduleGenerator(50, 0.14, 0.2, 0.4, 1000, ActivityManager.getActivities(ss));
					ScheduleChromosome sc = (ScheduleChromosome)gsg.generate();
					String json = "[";
					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
					SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm aa");
					for(int i = 0; i < sc.size(); i++) {
						if( i > 0 ) {
							json += ",";
						}
						if( sc.getActivity(i).getStartTime().getTimeInMillis() == 0 ){
							json += "{\"id\":\"" + sc.getActivity(i).getId() + 
									"\",\"startTime\":\"null\",\"endTime\":\"null\"}";
						} else {
							json += "{\"id\":\"" + sc.getActivity(i).getId() + 
									"\",\"startTime\":\"" + 
									sdf2.format(sc.getActivity(i).getStartTime().getTime()) + 
									"\",\"endTime\":\"" + 
									sdf.format(sc.getActivity(i).getEndTime().getTime()) +"\"}";
						}
					}
					json += "]";
					ActivityManager.assignDates(sc);
					response.getWriter().print(json);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logError(e,request);
					response.getWriter().print("null");
				}
			} else {
				((AuditManager)request.getSession().getAttribute("auditor")).addActivity("tried to generate a schedule without a session.");
				response.getWriter().print("null");
			}
		}
	}
}