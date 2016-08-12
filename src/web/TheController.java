package web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.CalendarFactory;
import model.SiteSession;
import model.TargetGroup;
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
	
	private String genToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request,response);
		String genHash = null;
		if( u != null ) {
			genHash = genHash(u,request.getRemoteAddr());
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
							u = UserManager.getUser(Integer.parseInt(c.getValue().substring(0,dollar)));
							if( u != null ) {
								String genHash = genHash(u,request.getRemoteAddr());
								if( genHash.equals(c.getValue())) {
									System.out.println("RESTORED SESSION");
									request.getSession().setAttribute("sessionUser",u);
								} else {
									u = null;
									logoutUser(request,response);
								}
							} 
						} catch(SQLException se) {
							se.printStackTrace();
						}
						break;
					}
				}
			}
		} else {
			if( u.isExpired() ) {
				logoutUser(request,response);
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
	
	@RequestMapping("/")
	public void home(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = restoreSession(request, response);
		homePage(u,request, response);
	}
	
	public void homePage(User u,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if( u == null ) {
			request.getRequestDispatcher("WEB-INF/view/index.jsp").forward(request, response);
		} else {
			try {
				SiteSession[] sessions = SessionManager.getSessions(u);
				request.setAttribute("sessions", sessions);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
					c.setMaxAge(10080);
					c.setSecure(true);
					c.setHttpOnly(true);
					response.addCookie(c);
				} else {
					request.setAttribute("error","Invalid username/password combination");
				}
			} catch(SQLException se) {
				se.printStackTrace();
			} catch (AuthenticationException e) {
				// TODO Auto-generated catch block
				request.setAttribute("error", "Invalid username/password combination");
			} catch (MissingTokenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			restoreToken(request,response);
		}
		homePage(null,request,response);
	}
	
	public void logoutUser(HttpServletRequest request,HttpServletResponse response) {
		request.getSession().invalidate();
		Cookie[] cookies = request.getCookies();
		for(Cookie c : cookies) {
			if( c.getName().equals("asSessionToken") ) {
				c.setMaxAge(0);
				response.addCookie(c);
			}
		}
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
					login(token,username,password,request,response);
				} else {
					request.setAttribute("error","Failed to register account.");
					register(request,response);
				}
			} catch (SQLException | MissingTokenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		System.out.println("in AddTG");
		if( u == null ) {
			response.getWriter().print("{exit:1}");
		} else {
			try {
				checkToken(token,request,response);
				if(name.matches("^[A-Za-z0-9.', _\\-]+$") && TargetGroupManager.addTargetGroup(u, name) ) {
					TargetGroup[] tgs = TargetGroupManager.getAllTargetGroups(u);
					response.getWriter().print((new Gson()).toJson(tgs[tgs.length - 1]));
				} else {
					response.getWriter().print("null");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				} else {
					response.getWriter().print("null");
				}
			} catch (MissingTokenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	public void addSession(@RequestParam("name") String name,
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
						home(request,response);
						return;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						request.setAttribute("error", "Adding Session Failed");
						request.getRequestDispatcher("WEB-INF/view/addSession.jsp").forward(request, response);
						return;
					}
				}
			}
			request.setAttribute("error", "Data validation error.");
			request.getRequestDispatcher("WEB-INF/view/addSession.jsp").forward(request, response);
		}
	}
}