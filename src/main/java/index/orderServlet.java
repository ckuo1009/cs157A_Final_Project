package index;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

import java.util.HashMap;
import java.util.Vector;


/**
 * Servlet implementation class orderServlet
 */
@WebServlet("/orderServlet")
public class orderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public orderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String account = (String) request.getSession().getAttribute("account");
		String type = request.getParameter("type");
		
		
		if(type.equals("customer_search")) {
			
			ArrayList<ArrayList<Object>> a = customer_search(account);
			JSONObject jsonObject = listToJsonObject("customer_data", a);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();

			out.println(jsonObject);
			out.close();
			
		}else if(type.equals("store_search")) {
			ArrayList<ArrayList<Object>> a = store_search(account);
			JSONObject jsonObject = listToJsonObject("store_data", a);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();

			out.println(jsonObject);
			out.close();
		}
	
	}
	
	private ArrayList<ArrayList<Object>> store_search(String account){
		ArrayList<ArrayList<Object>> a = new ArrayList<>();
		QueryBean b = null;
		
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			String sql = "select o.order_id,o.customer_id,o.store_id ,o.total,o.order_date from order_history o , account a ,store s \n"
					+ " where o.customer_id = s.sid \n"
					
					+ " and s.access_level = a.access_level and a.account = '"+account+"'";
			
			a=b.querySQL(sql);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return a;
	}
	
	/**
	 * 
	 * @return a
	 */
	private ArrayList<ArrayList<Object>> customer_search(String account){
		ArrayList<ArrayList<Object>> a = new ArrayList<>();
		QueryBean b = null;
		
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			String sql = "select o.order_id,o.customer_id,o.store_id ,o.total,o.order_date from order_history o ,account a,customer c \n"
					+ " where o.customer_id = c.uid \n"
					
					+ " and c.access_level = a.access_level and a.account = '"+account+"'";
			
			a=b.querySQL(sql);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
