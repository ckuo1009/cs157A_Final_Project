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
import org.json.JSONException;

import java.util.Vector;

/**
 * Servlet implementation class addPageServlet
 */
@WebServlet("/addPageServlet")
public class addPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public addPageServlet() {
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

		if (type.equals("page_search")) {

			try {
				JSONObject jsonObject = new JSONObject();
				ArrayList<ArrayList<Object>> a1 = getPageInfo();
				JSONArray jsonArray = new JSONArray();

				for (ArrayList<Object> innerList : a1) {
					JSONArray innerJsonArray = new JSONArray();
					for (Object data : innerList) {
						innerJsonArray.put(data);
					}
					jsonArray.put(innerJsonArray);
				}

				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");

				jsonObject.put("page_data", jsonArray);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();

				out.println(jsonObject);

				out.close();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (type.equals("main_search")) {
			try {
				JSONObject jsonObject = new JSONObject();
				ArrayList<ArrayList<Object>> a1 = getMainInfo();
				JSONArray jsonArray = new JSONArray();

				for (ArrayList<Object> innerList : a1) {
					JSONArray innerJsonArray = new JSONArray();
					for (Object data : innerList) {
						innerJsonArray.put(data);
					}
					jsonArray.put(innerJsonArray);
				}

				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");

				jsonObject.put("main_data", jsonArray);
				PrintWriter out = response.getWriter();
				out.println(jsonObject);
				out.close();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (type.equals("add_page")) {
			ArrayList<ArrayList<Object>> a = getPrev();
			JSONArray jsonArray = convertToJSONArray(a);

			JSONObject jsonObject = new JSONObject();

			try {
				jsonObject.put("menu_list", jsonArray);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.println(jsonObject);
				out.close();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		else if (type.equals("add_page_Confirm")) {
			JSONObject jsonObject = new JSONObject();
			String status = "";

			String page_name = request.getParameter("page_name");
			String html = request.getParameter("html");
			String page_id = request.getParameter("page_id");
			String prev_name = request.getParameter("prev_name");

			// String page_name,String html, String page_id,String prev_name

			try {
				Result r = addPage(page_name, html, page_id, prev_name);
				if (r.isSuccess) {
					status = "success";

				} else {
					status = "fail";
				}
				jsonObject.put("status", status);
				jsonObject.put("message", r.getMessage());
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.println(jsonObject);
				out.close();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (type.equals("add_main_Confirm")) {

			String main_menu_id = request.getParameter("main_menu_id");
			String main_menu_name = request.getParameter("main_menu_name");

			JSONObject jsonObject = new JSONObject();
			String status = "";

			try {
				Result r = addMainMenu(main_menu_id, main_menu_name);
				if (r.isSuccess) {
					status = "success";

				} else {
					status = "fail";
				}
				jsonObject.put("status", status);
				jsonObject.put("message", r.getMessage());
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.println(jsonObject);
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if(type.equals("page_status")){
			String page_id = request.getParameter("page_id");
			String page_status = request.getParameter("status");
			JSONObject jsonObject = new JSONObject();
			String status = "";
			
			try {
				Result r = page_status(page_id, page_status);
				
				if (r.isSuccess) {
					status = "success";

				} else {
					status = "fail";
				}
				jsonObject.put("status", status);
				jsonObject.put("message", r.getMessage());
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.println(jsonObject);
				out.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}else if (type.equals("main_menu_status")) {
			String menu_id = request.getParameter("menu_id");
			String menu_status = request.getParameter("status");
			JSONObject jsonObject = new JSONObject();
			String status = "";
			try {
				Result r = main_menu_status(menu_id, menu_status);
				
				if (r.isSuccess) {
					status = "success";

				} else {
					status = "fail";
				}
				jsonObject.put("status", status);
				jsonObject.put("message", r.getMessage());
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.println(jsonObject);
				out.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else {
			
		}
	}
	
	private Result main_menu_status(String menu_id,String status) {
		Result r = new Result();
		QueryBean b = null;
		String message = "";
		Boolean isSuccess = false;
		String new_status="";
		String sql = "update main_menu set status = ? where id = ?";
		
Vector<String> vtrParams = new Vector<>();
		
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			if(status.equals("y")) {
				
				new_status="n";
				
			}
			else {
				new_status="y";
			}
			vtrParams.add(new_status);
			vtrParams.add(menu_id);
			
			isSuccess= b.executeSQL(sql, vtrParams);
			
			if(isSuccess) {
				message="successful";
			}
			else {
				message="fail, sql error";
			}
			
			r.isSuccess=isSuccess;
			r.message=message;
			
			
			
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b = null;
			}

		}
		
		
		
		
		
		
		return r;
	}
	
	/**
	 * page _status
	 * @param page_id
	 * @param status
	 * @return
	 */
	private Result page_status(String page_id ,String status) {
		Result r = new Result();
		QueryBean b = null;
		String message = "";
		Boolean isSuccess = false;
		String new_status="";
		String sql = "update page set status = ? where page_id = ?";
		
		
		Vector<String> vtrParams = new Vector<>();
		
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			if(status.equals("y")) {
				
				new_status="n";
				
			}
			else {
				new_status="y";
			}
			vtrParams.add(new_status);
			vtrParams.add(page_id);
			
			isSuccess= b.executeSQL(sql, vtrParams);
			
			if(isSuccess) {
				message="successful";
			}
			else {
				message="fail, sql error";
			}
			
			r.isSuccess=isSuccess;
			r.message=message;
			
			
			
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b = null;
			}

		}
		
		
		return r;
	}
	
	

	/**
	 * add page
	 * 
	 * @param data
	 * @return
	 */
	private JSONArray convertToJSONArray(ArrayList<ArrayList<Object>> data) {
		JSONArray jsonArray = new JSONArray();

		// 遍歷外層 ArrayList
		for (ArrayList<Object> innerList : data) {
			// 遍歷內層 ArrayList
			for (Object obj : innerList) {
				jsonArray.put(obj);
			}
		}

		return jsonArray;
	}

	/**
	 * add page
	 * 
	 * @return
	 */
	private ArrayList<ArrayList<Object>> getPrev() {

		QueryBean b = null;
		String message = "";
		Boolean isSuccess = false;
		String sql = "select name from main_menu";
		ArrayList<ArrayList<Object>> a = new ArrayList<>();

		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			a = b.querySQL(sql);
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b = null;
			}

		}

		return a;
	}

	/**
	 * main info
	 * 
	 * @return
	 */

	private ArrayList<ArrayList<Object>> getMainInfo() {

		QueryBean b = null;
		String message = "";
		Boolean isSuccess = false;
		String sql = "select name,id,status from main_menu";
		ArrayList<ArrayList<Object>> a = new ArrayList<>();

		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			a = b.querySQL(sql);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b = null;
			}

		}

		return a;

	}

	/**
	 * page
	 * @return a
	 */
	private ArrayList<ArrayList<Object>> getPageInfo() {

		QueryBean b = null;
		String message = "";
		Boolean isSuccess = false;
		String sql = "select page_name,page_html,page_id,prev_id,prev_name,status from page";
		ArrayList<ArrayList<Object>> a = new ArrayList<>();

		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			a = b.querySQL(sql);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b = null;
			}

		}

		return a;

	}

	private Result addMainMenu(String main_menu_id, String main_menu_name) {
		Result r = new Result();
		QueryBean b = null;
		String message = "";
		Boolean isSuccess = false;
		//default 
		String status ="y";

		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			

			String sql = "insert into main_menu values(?,?,?)";
			
			Vector<String> vtrParams = new Vector<>();
			vtrParams.add(main_menu_id);
			vtrParams.add(main_menu_name);
			vtrParams.add(status);

			isSuccess = b.executeSQL(sql, vtrParams);

			if (isSuccess) {
				message = "main insertion Successful";
			}

			else {
				message = "insertion fail";
			}

			r.isSuccess = isSuccess;
			r.message = message;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b = null;
			}

		}

		return r;

	}

	/**
	 * page
	 * 
	 * @param html
	 * @param page_id
	 * @param prev_id
	 * @param prev_name
	 * @return
	 */
	private Result addPage(String page_name, String html, String page_id, String prev_name) {
		Result r = new Result();
		QueryBean b = null;
		String message = "";
		Boolean isSuccess = false;
		String status="y";
		ArrayList<ArrayList<Object>> a = new ArrayList<>();

		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			String prevIdSql = "select id from main_menu where name = '" + prev_name + "'";
			String sql = "insert into page values(?,?,?,?,?,?)";
			a = b.querySQL(prevIdSql);
			String prevId = (String) a.get(0).get(0);
			System.out.println("previd " + prevId);
			Vector<String> vtrParams = new Vector<>();

			vtrParams.add(html);
			vtrParams.add(page_name);
			vtrParams.add(page_id);
			vtrParams.add(prevId);
			vtrParams.add(prev_name);
			vtrParams.add(status);

			isSuccess = b.executeSQL(sql, vtrParams);

			if (isSuccess) {
				message = "page insertion Successful";
			}

			else {
				message = "insertion fail";
			}

			r.isSuccess = isSuccess;
			r.message = message;

		} catch (Exception e) {
			System.out.println("add page error ");
			e.printStackTrace();
		} finally {

			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b = null;
			}
		}

		return r;
	}

	private class Result {
		private boolean isSuccess;
		private String message;

		/**
		 * Constructor for the Result class.
		 * 
		 * @param isSuccess a boolean indicating the success of the operation.
		 * @param message   a string containing a message about the result.
		 */

		public Result() {
			isSuccess = false;
			message = "unknown";

		}

		public Result(boolean isSuccess, String message) {
			this.isSuccess = isSuccess;
			this.message = message;

		}

		// Getters
		public boolean isSuccess() {
			return isSuccess;
		}

		public String getMessage() {
			return message;
		}

	}

}
