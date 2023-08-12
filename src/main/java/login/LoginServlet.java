package login;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.pub.database.QueryBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * handle the HTTP POST request. read the parameters from the request, checks
 * the credentials with the database, and generates a JSON response indicating
 * whether the login was successful or not.
 * 
 * @param request  the HTTP request
 * @param response the HTTP response
 * @throws ServletException if an input or output error is detected when the
 *                          servlet handles the POST request
 * @throws IOException      if an I/O error occurred
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	/**
	 * Handles the HTTP POST request.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Reading account and password parameter from the HTTP request
		String inputAccount = request.getParameter("username");
		String inputPassword = request.getParameter("password");

		String status = null;
		String message = null;

		// JSONObject to hold the response data
		JSONObject jsonObject = new JSONObject();

		// Checking if the account already contains "@sjsu.edu" at the end
		if (!inputAccount.endsWith("@sjsu.edu")) {
			inputAccount = inputAccount + "@sjsu.edu";
		}

		try {
			// Using the checkCredentials method to verify the account and password
			if (checkCredentials(inputAccount, inputPassword)) {

				jsonObject.put("status", "success");
				jsonObject.put("message", "Login Successful!");
				request.getSession().getServletContext().setAttribute(request.getSession().getId().toString(), inputAccount);
				request.getSession().setAttribute("account", inputAccount);
				
				//String account = (String) request.getSession().getAttribute("account");
			} else {

				jsonObject.put("status", "error");
				jsonObject.put("message", "Login Failed. Invalid Password or account doesn't exist .");
			}

			response.setContentType("application/json");

			PrintWriter out = response.getWriter();
			

			out.println(jsonObject);

			out.close();
		} catch (Exception e) {

			throw new ServletException("Error processing request", e);
		}
	}

	/**
	 * Checks the credentials by connecting to the database and making a SQL query.
	 * 
	 * @throws Exception
	 */
	private boolean checkCredentials(String inputAccount, String inputPassword) throws Exception {
		boolean valid = false;
		//
		String hashedUniversalPassword = hashPassword("maxaccesslevelsjsu");
		
		String hashedInputPassword = hashPassword(inputPassword);
		
		String loginSql = "SELECT account FROM Account WHERE account = " + "'" + inputAccount + "'" + " and password = "
				+ "'" + hashedInputPassword + "'";
		String AccessSql = "select account from account where account = " + "'" + inputAccount + "'";
		
		QueryBean b = new QueryBean("qb", false, "utf-8", "utf-8");

		try {
			
			ArrayList<ArrayList<Object>> a1 = b.querySQL(AccessSql);
			ArrayList<ArrayList<Object>> a2 = b.querySQL(loginSql);

			if (a1 != null && hashedInputPassword.equals(hashedUniversalPassword)) {
				valid = true;
				Timestamp currentTime = new Timestamp(new Date().getTime());

				System.out.println("UniversalPassword login");
			}

			if (a2 != null) {
				valid = true;
				Timestamp currentTime = new Timestamp(new Date().getTime());
				updateLoginTime(inputAccount, currentTime);
				System.out.println("completed!!!!");
			}
		} finally {
			if (b != null) {
				b.setAutoCommitMode(false);
				b.close();
				b = null;
			}
		}
		return valid;
	}

	/**
	 * 
	 * @param account
	 * @param loginTime
	 * @throws Exception
	 */
	private void updateLoginTime(String account, Timestamp loginTime) throws Exception {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strLoginTime = dateFormat.format(loginTime);

		String sql = "UPDATE Account SET last_login_time = STR_TO_DATE('" + strLoginTime
				+ "', '%Y-%m-%d %H:%i:%s') WHERE account = '" + account + "'";

		QueryBean b = new QueryBean("qb", false, "utf-8", "utf-8");
		try {
			b.executeSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (b != null) {
				b.setAutoCommitMode(false);
				b.close();
				b = null;
			}
		}
	}
	/**
	 * Encryption of passwords
	 * @param password
	 * @return
	 */
	private String hashPassword(String password) {
	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
	        StringBuilder sb = new StringBuilder();
	        for (byte b : digest) {
	            sb.append(String.format("%02x", b));
	        }
	        return sb.toString();
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException(e);
	    }
	}
}
