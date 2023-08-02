package index;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import com.pub.database.QueryBean;
import org.json.JSONArray;

import java.util.Vector;

/**
 * Servlet implementation class manaServlet
 */
@WebServlet("/manaServlet")
public class manaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public manaServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = request.getParameter("type");
		
		String level = request.getParameter("level");
		String account = request.getParameter("user");
		String message = null;

		// 用戶資訊
		if (type.equals("search")) {
			try {
				
				JSONObject jsonObject = new JSONObject();
				ArrayList<ArrayList<Object>> a1 = getUserInfo();
				JSONArray jsonArray = new JSONArray();

				for (ArrayList<Object> innerList : a1) {
					JSONArray innerJsonArray = new JSONArray();
					for (Object data : innerList) {
						innerJsonArray.put(data);
					}
					jsonArray.put(innerJsonArray);
				}

			
				
				jsonObject.put("data", jsonArray);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();

				out.println(jsonObject);

				out.close();

			} catch (Exception e) {

				e.printStackTrace();
			}

			// 修改權限
		} else if (type.equals("modify")) {

			try {
				JSONObject jsonObject = new JSONObject();
				

				message = setPermisson(level, account);
			
				jsonObject.put("message", message);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();

				out.println(jsonObject);

				out.close();
			} catch (Exception e) {

				e.printStackTrace();
			}

		} else {
		}

	}

	/**
	 * select whole users info
	 * 
	 * @return
	 * @throws Exception
	 */
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<ArrayList<Object>> getUserInfo() throws Exception {
		String sql = "select account , access_level , last_login_time from account";
		String sql1 = "select a.account  , b.level_name ,a.last_login_time from account a,levelinfo b where b.access_level = a.access_level";
		QueryBean b = new QueryBean("qb", false, "utf-8", "utf-8");
		ArrayList<ArrayList<Object>> a1 = b.querySQL(sql1);

		try {

		} catch (Exception e) {
			System.out.println("system error");
			e.printStackTrace();
		} finally {
			if (b != null) {
				b.setAutoCommitMode(false);
				b.close();
			}
			b = null;
		}
		return a1;
	}

	/**
	 * modify users permissions
	 * 
	 * @param level
	 * @param account
	 * @return
	 * @throws Exception
	 */
	private String setPermisson(String type, String account) throws Exception {
		String level = null;
		String message = null;

		String levelSql = "select access_level from levelinfo where level_name = "+"'"+type+"'";
		String sql = "update account set access_level = ? where account = ? ";
		Vector<String> vtrParams = new Vector<>();
		QueryBean b = new QueryBean("qb", false, "utf-8", "utf-8");
		ArrayList<ArrayList<Object>> a1=b.querySQL(levelSql);
		level= (String) a1.get(0).get(0);
		vtrParams.add(level);
		vtrParams.add(account);

		try {
			Boolean a = b.executeSQL(sql, vtrParams);
			System.out.println("成功: " + a);
			if (a == true) {
				message = "successfully modify";
			} else {
				message = "error";
			}
		} catch (Exception e) {
			System.out.println("system error");
		} finally {
			if (b != null) {
				b.setAutoCommitMode(false);
				b.close();
			}
			b = null;
		}

		return message;
	}
}
