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
 * Servlet implementation class storeServlet
 */
@WebServlet("/storeServlet")
public class storeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public storeServlet() {
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
		String account = (String) request.getSession().getAttribute("account");

		if (type.equals("search")) {
			String keyword = request.getParameter("keyword");
			ArrayList<ArrayList<Object>> a = search(account, keyword);
			JSONObject jsonObject = listToJsonObject("store_data", a);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();

			out.println(jsonObject);
			out.close();

		} else if (type.equals("add")) {

			String product_name = request.getParameter("product_name");
			String product_type = request.getParameter("product_type");
			String price = request.getParameter("price");
			String stock = request.getParameter("stock");

			JSONObject jsonObject = new JSONObject();

			Result r = add(product_name, product_type, price, stock, account);
			if (r.isSuccess == true) {

				try {
					jsonObject.put("status", "success");
					jsonObject.put("message", r.message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				try {
					jsonObject.put("status", "fail");
					jsonObject.put("message", r.message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();

			out.println(jsonObject);
			out.close();

		} else if (type.equals("deletion")) {
			String pid = request.getParameter("pid");
			Result r = delete(pid);
			JSONObject jsonObject = new JSONObject();

			if (r.isSuccess == false) {
				try {
					jsonObject.put("status", "fail");
					jsonObject.put("message", r.message);
					System.out.println(r.message);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				try {
					jsonObject.put("status", "success");
					jsonObject.put("message", r.message);
					System.out.println(r.message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();

			out.println(jsonObject);
			out.close();
		} else if (type.equals("edit")) {
			String pid = request.getParameter("pid");
			String product_name = request.getParameter("product_name");
			String product_type = request.getParameter("product_type");
			String price = request.getParameter("price");
			String stock = request.getParameter("stock");
			JSONObject jsonObject = new JSONObject();
			String status = "";

			Result r = edit(pid, product_name, product_type, price, stock);
			if (r.isSuccess) {

				status = "success";

			} else {
				status = "fail";
			}

			try {
				jsonObject.put("status", status);
				jsonObject.put("message", r.message);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();

				out.println(jsonObject);
				out.close();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

		}

	}

	private Result edit(String pid, String product_name, String product_type, String price, String stock) {
		Result r = new Result();
		boolean isSuccess = false;
		String message = "";
		String editSql = "update product set product_name = '" + product_name + "', " + "type = '" + product_type
				+ "', " + " price = " + price + " ," + "stock = " + stock + " where product_id = " + pid;

		QueryBean b = null;
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			boolean bl = b.executeSQL(editSql);

			if (bl) {
				isSuccess = true;
				message = "successful";

			} else {
				message = "fail";
			}

			r.isSuccess = isSuccess;
			r.message = message;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (b != null) {
				b.setAutoCommitMode(false);
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
	 * 
	 * @param pid
	 * @return r
	 */
	private Result delete(String pid) {
		Result r = new Result();
		boolean isSuccess = false;
		String message = "";
		String deleteSql = "delete from product where product_id = " + pid;
		QueryBean b = null;
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");

			boolean bl = b.executeSQL(deleteSql);

			if (bl) {

				isSuccess = true;
				message = "deletion successful";

			} else {
				message = "deletion failed";
			}

			r.isSuccess = isSuccess;
			r.message = message;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (b != null) {
				b.setAutoCommitMode(false);
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
	 * 
	 * @param product_name
	 * @param product_type
	 * @param price
	 * @param stock
	 * @param account
	 * @return
	 */
	private Result add(String product_name, String product_type, String price, String stock, String account) {
		Result r = new Result();
		boolean isSuccess = false;

		String message = "";
		String checkSql = "select product_name from product where product_name = '" + product_name + "'";
		String pidSql = "select max(product_id) from product";
		String insertSql = "insert into product(product_id,product_name,type,store_id,price,stock) values(?,?,?,?,?,?)";
		String sidSql = "select sid from store where account = '" + account + "'";
		int pid = 0;
		String ppid = "";
		String sid = "";
		ArrayList<ArrayList<Object>> check = new ArrayList<>();
		ArrayList<ArrayList<Object>> id = new ArrayList<>();
		ArrayList<ArrayList<Object>> a = new ArrayList<>();
		Vector<String> vtrParams = new Vector<>();
		QueryBean b = null;
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			check = b.querySQL(checkSql);
			if (check != null) {
				message = "prodcut name already exsits in the database";
				r.isSuccess = isSuccess;
				r.message = message;
			} else {

				id = b.querySQL(pidSql);
				if (id.get(0).get(0) == "" || id == null) {
					ppid = "1";
					a = b.querySQL(sidSql);
					sid = (String) a.get(0).get(0);

					vtrParams.add(ppid);
					vtrParams.add(product_name);
					vtrParams.add(product_type);
					vtrParams.add(sid);
					vtrParams.add(price);
					vtrParams.add(stock);

				} else {

					pid = Integer.parseInt((String) id.get(0).get(0));
					pid = pid + 1;

					String newPid = String.valueOf(pid);

					a = b.querySQL(sidSql);
					sid = (String) a.get(0).get(0);

					vtrParams.add(newPid);
					vtrParams.add(product_name);
					vtrParams.add(product_type);
					vtrParams.add(sid);
					vtrParams.add(price);
					vtrParams.add(stock);

				}

				boolean bl = b.executeSQL(insertSql, vtrParams);
				if (bl) {
					isSuccess = true;
					r.isSuccess = isSuccess;
					r.message = "insertion successful";

				} else {
					r.isSuccess = isSuccess;
					r.message = "insertion failed";

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (b != null) {
				b.setAutoCommitMode(false);
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
	 * 
	 * @param account
	 * @param keyword
	 * @return
	 */
	private ArrayList<ArrayList<Object>> search(String account, String keyword) {
		ArrayList<ArrayList<Object>> a = new ArrayList<>();
		QueryBean b = null;
		String keyword_sql = "select store.store_name,product.product_id,product.product_name,product.type,product.price,product.stock from product ,account,store "
				+ " where product.store_id = store.sid and " + " store.account= account.account and store.account= '"
				+ account + "'" + " and product.product_name LIKE  '%" + keyword + "%'";
		String regular_sql = "select store.store_name,product.product_id,product.product_name,product.type,product.price,product.stock from product ,account,store "
				+ " where product.store_id = store.sid and " + " store.account= account.account and store.account= '"
				+ account + "'";

		try {

			b = new QueryBean("qb", false, "utf-8", "utf-8");
			if (keyword == null || keyword.equals("")) {
				a = b.querySQL(regular_sql);

			} else {
				a = b.querySQL(keyword_sql);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (b != null) {
				b.setAutoCommitMode(false);
				try {
					b.close();
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			b = null;
		}

		return a;
	}

	/**
	 * including status
	 * 
	 * @param name JSONObject key value
	 * @param a    nested arrayList
	 * @return nested JSONArray
	 */
	private JSONObject listToJsonObject(String name, ArrayList<ArrayList<Object>> a) {
		JSONObject jsonObject = new JSONObject();
		String status = "";
		JSONArray jsonArray = new JSONArray();

		try {
			if (a == null) {
				status = "fail";
				jsonObject.put("status", status);
				jsonObject.put(name, jsonArray);
				return jsonObject;
			}

			for (ArrayList<Object> innerList : a) {
				JSONArray innerJsonArray = new JSONArray();
				for (Object data : innerList) {
					innerJsonArray.put(data);
				}
				jsonArray.put(innerJsonArray);
			}

			status = "success";
			jsonObject.put("status", status);
			jsonObject.put(name, jsonArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
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
