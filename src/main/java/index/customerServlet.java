package index;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * Servlet implementation class customerServlet
 */
@WebServlet("/customerServlet")
public class customerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public customerServlet() {
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
			ArrayList<ArrayList<Object>> a = search(keyword);
			JSONObject jsonObject = listToJsonObject("store_data", a);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();

			out.println(jsonObject);
			out.close();

		} else if (type.equals("purchase")) {
			List<String> products = parseJsonArray(request.getParameter("products"));
			String totalAmount = request.getParameter("totalAmount");
			Timestamp currentTime = new Timestamp(new Date().getTime());

			Result r = purchase(products, totalAmount, account);

			JSONObject jsonObject = new JSONObject();
			try {
				if (r.isSuccess == false) {
					jsonObject.put("status", "fail");
				} else {
					jsonObject.put("status", "success");
				}
				jsonObject.put("message", r.message);
				System.out.println(r.message);
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

	}

	private void updateLoginTime(String account, Timestamp loginTime) throws Exception {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strLoginTime = dateFormat.format(loginTime);

		String sql = "UPDATE order_history order_date = STR_TO_DATE('" + strLoginTime
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
	 * 
	 * @param products
	 * @param totalAmount
	 * @param account
	 * @return
	 */
	private Result purchase(List<String> products, String totalAmount, String account) {
		Result r = new Result();
		boolean isSuccess = false;
		String message = "";
		ArrayList<ArrayList<Object>> a = new ArrayList<>();
		ArrayList<ArrayList<Object>> c = new ArrayList<>();

		ArrayList<ArrayList<Object>> o = new ArrayList<>();
		QueryBean b = null;
		String cid = "";
		String sid = "";
		Vector<String> vtrParams1 = new Vector<>();

		String cidSql = "select uid from customer where account ='" + account + "'";

		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			c = b.querySQL(cidSql);
			cid = (String) c.get(0).get(0);

			String oid = "";
			String oidSql = "select max(order_id) from order_history";
			o = b.querySQL(oidSql);

			if (o.get(0).get(0) == "" || o == null) {
				oid = "1";
			} else {
				oid = (String) o.get(0).get(0);
				int intOid = Integer.parseInt(oid);
				intOid = intOid + 1;
				oid = Integer.toString(intOid);
			}
			System.out.println("oid " + oid);

			for (String st : products) {

				String[] productDetails = st.split(",");

				Vector<String> vtrParams2 = new Vector<>();

				String productName = productDetails[0];
				String storeName = productDetails[1];
				String n = (productDetails[2].replaceAll("[^0-9]", ""));
				System.out.println("n " + n);
				int number = Integer.parseInt(n);

				String sidSql = "select sid from store where store_name = '" + storeName + "'";

				ArrayList<ArrayList<Object>> s = new ArrayList<>();
				s = b.querySQL(sidSql);
				sid = (String) s.get(0).get(0);

				String stockSql = "select stock from product where product_name = '" + productName + "'";
				a = b.querySQL(stockSql);
				String sk = (String) a.get(0).get(0);
				int stock = Integer.parseInt(sk);
				int newStock = stock - number;
				String updateSql = "update product set stock = " + newStock + " where product_name = '" + productName
						+ "'";

				String detailSql = "insert into order_detail (order_id,product_name,quantity) values(?,?,?)";

				vtrParams2.add(oid);
				vtrParams2.add(productName);
				String num = String.valueOf(number);
				vtrParams2.add(num);

				boolean b1 = false;
				boolean b2 = false;

				b1=b.executeSQL(updateSql);

				b2=b.executeSQL(detailSql, vtrParams2);

				if (b1 && b2) {
					isSuccess = true;
					message = "successful";
				} else {
					message = "fail";
				}

			}
			String orderSql = "insert into order_history (order_id,customer_id,store_id,total) values(?,?,?,?)";
			vtrParams1.add(oid);
			vtrParams1.add(cid);
			vtrParams1.add(sid);
			vtrParams1.add(totalAmount);
			b.executeSQL(orderSql, vtrParams1);

			r.isSuccess = isSuccess;
			r.message = message;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
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

		return r;
	}

	/**
	 * 
	 * @param jsonArrayStr
	 * @return
	 */
	private List<String> parseJsonArray(String jsonArrayStr) {
		List<String> productList = new ArrayList<>();

		try {
			// Parse the input string to a JSON array
			JSONArray jsonArray = new JSONArray(jsonArrayStr);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject productObj = jsonArray.getJSONObject(i);

				// Extract details of the product
				String productName = productObj.getString("productName");
				String storeName = productObj.getString("storeName");
				String number = productObj.getString("number");

				// Create a comma-separated string for the product details
				String productDetail = productName + "," + storeName + "," + number;
				productList.add(productDetail);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return productList;
	}

	private String access_level(String account) {
		String access_level = "";
		String levelSql = "select access_level from account where account = '" + account + "'";
		ArrayList<ArrayList<Object>> a = new ArrayList<>();
		QueryBean b = null;
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			a = b.querySQL(levelSql);

			access_level = (String) a.get(0).get(0);

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

		return access_level;
	}

	private ArrayList<ArrayList<Object>> search(String keyword) {
		ArrayList<ArrayList<Object>> a = new ArrayList<>();
		QueryBean b = null;
		String regularSql = "select p.product_id,p.product_name,p.type,s.store_name,p.price,p.stock\n"
				+ " from product p,store s where s.sid=p.store_id ";
		String keywordSql = "select p.product_id,p.product_name,p.type,s.store_name,p.price,p.stock\n"
				+ " from product p,store s where s.sid=p.store_id and p.product_name  LIKE  '%" + keyword + "%'";

		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			if (keyword == null || keyword.equals("")) {
				a = b.querySQL(regularSql);
			} else {
				a = b.querySQL(keywordSql);
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
