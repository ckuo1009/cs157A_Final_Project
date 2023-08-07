package register;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.json.JSONObject;
import net.sf.json.JSONException;
import java.security.*;
import com.pub.database.QueryBean;
import java.util.Vector;

/**
 * Servlet implementation class registerServlet
 */
@WebServlet("/registerServlet")
public class registerServlet extends HttpServlet {

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public registerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String account = request.getParameter("account");
		String password = request.getParameter("password");
		String userType = request.getParameter("userType");
		String name = request.getParameter("name");
		JSONObject jsonObject = new JSONObject();

		String status = null;
		String message = null;
		try {

			// check if account or password is empty
			if (emptyInput(account, password) == true) {
				status = "fail";
				message = "account or password can not be empty!";

				// check if the account is in right form
			} else if (!isEmailValid(account)) {

				status = "fail";
				message = "Please register using a Sinbon email address. For example, use the format: 1234@sinbon.com";
				// check if the lengdth is >=6
			} else if (!isPasswordValid(password)) {
				status = "fail";
				message = "Password length is too short, please enter more than six characters";

			} else if (!register(account, password, name, userType)) {
				status = "fail";
				message = "Account already exists.";
			} else {
				status = "success";
				message = "Register successfully!\nThe webpage will redirect in two seconds.";

			}
			System.out.println("status:　" + status + "message:" + message);

			jsonObject.put("status", status);
			jsonObject.put("message", message);
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println(jsonObject);
			out.close();
		} catch (SQLException e) {
			try {
				jsonObject.put("status", "fail");
				jsonObject.put("message", "A database error occurred. Please try again later.");
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println(jsonObject);
				out.close();
			} catch (JSONException jsonException) {
				throw new ServletException("Error processing JSON", jsonException);
			} catch (org.json.JSONException js) {

				throw new ServletException("Error processing JSON", js);
			}
		} catch (Exception e) {
			throw new ServletException("Error processing request", e);
		}
	}

	/**
	 * check if account ends correctly xxxx@sinbon.com
	 * 
	 * @param account
	 * @return
	 */
	public boolean isEmailValid(String account) {
		if (account == null) {
			return false;
		}
		return account.endsWith("@sjsu.edu");
	}

	/**
	 * check if the size of password >=6
	 * 
	 * @param account
	 * @return
	 */
	public boolean isPasswordValid(String password) {
		if (password == null) {
			return false; // The password is null or empty, return false.
		}
		return password.length() >= 6;
	}

	/**
	 * check if the user registers with empty account or password.
	 * 
	 * @param account
	 * @param password
	 * @return
	 */
	public boolean emptyInput(String account, String password) {
		return account == null || account.trim().isEmpty() || password == null || password.trim().isEmpty();
	}

	/**
	 *
	 * @param account
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean register(String account, String password, String name, String type) throws Exception {

		boolean accountExists = false;
		boolean b1 = false;
		boolean b2 =false;
		String checkSql = "SELECT account  FROM account WHERE account = " + "'" + account + "'";
		QueryBean b = new QueryBean("qb", false, "utf-8", "utf-8");

		try {

			ArrayList<ArrayList<Object>> data = b.querySQL(checkSql);

			if (data != null) {
				accountExists = true;// account exists

			}

			if (accountExists) {
				// Account already exists, return false.
				return false;
			}

			String access_level = "";
			int id = -1;
			if (type.equals("customer")) {
				access_level = "2";
				ArrayList<ArrayList<Object>> uid = new ArrayList<>();
				 uid = b.querySQL("select max(uid) from customer");
				if (uid == null || uid.isEmpty() || uid.get(0).get(0) == null || ((String) uid.get(0).get(0)).isEmpty()) {
					id = 1;

				} else {
					id = Integer.parseInt((String) uid.get(0).get(0));
					id = id + 1;
				}
			} else {
				access_level = "3";
				ArrayList<ArrayList<Object>> sid = new ArrayList<>();
				sid = b.querySQL("select max(sid) from store");
				if(sid == null || sid.isEmpty() || sid.get(0).get(0) == null || ((String) sid.get(0).get(0)).isEmpty()) {
					id=1;
				}else {
					id = Integer.parseInt((String) sid.get(0).get(0));
				id = id + 1;
				}
			}
			System.out.println("id "+id);
			

			String insertSql1 = "INSERT INTO account (account, password, access_level) VALUES (?, ?, ?)";
			String insertSqlC = "INSERT INTO " + type
					+ " (uid, account, customer_name,access_level) VALUES (?,?,?,?)";
			String insertSqlS = "INSERT INTO " + type
					+ " (sid, account, store_name,access_level) VALUES (?,?,?,?)";
			Vector<String> vtrParams1 = new Vector<>();
			Vector<String> vtrParams2 = new Vector<>();
			String hashPassword = hashPassword(password);
		
			
			if(type.equals("customer")) {
				vtrParams1.add(account);
				vtrParams1.add(hashPassword);
				vtrParams1.add(access_level);
				String idString = String.valueOf(id);

				vtrParams2.add(idString);
				vtrParams2.add(account);
				vtrParams2.add(name);
				vtrParams2.add(access_level);

				 b1 = b.executeSQL(insertSql1, vtrParams1);
				 b2 = b.executeSQL(insertSqlC, vtrParams2);
			}else if(type.equals("store")) {
				vtrParams1.add(account);
				vtrParams1.add(hashPassword);
				vtrParams1.add(access_level);
				String idString = String.valueOf(id);

				vtrParams2.add(idString);
				vtrParams2.add(account);
				vtrParams2.add(name);
				vtrParams2.add(access_level);

				 b1 = b.executeSQL(insertSql1, vtrParams1);
				 b2 = b.executeSQL(insertSqlS, vtrParams2);
			}else {
				return false;
			}
			

			return b1 && b2;
		} catch (SQLException e) {
			throw new RuntimeException("Error checking if account exists or inserting account", e);
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
	 * 
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
