package permisson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import com.pub.database.QueryBean;

import org.json.JSONArray;

/**
 * Servlet implementation class permissionServlet.
 * This class is used to verify the permissions of an account to access specific pages.
 */
@WebServlet("/permissionServlet")
public class permissionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * Servlet constructor.
     */
    public permissionServlet() {
        super();
    }

    /**
     * Process a GET request from the client.
     * @param request the HttpServletRequest that encapsulates the request from the client.
     * @param response the HttpServletResponse that encapsulates the response to the client.
     */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		// Get parameters from request
		String url = request.getParameter("pageName");
		String sessionId = request.getSession().getId();
		String account = (String) request.getSession().getServletContext().getAttribute(sessionId);

		// Prepare for JSON response
		JSONObject jsonObject = new JSONObject();
		String status = null;
		String message = null;
		
		
		
		try {
			// Check user's page access privilege
			Result r = checkPagePrivilege(url, sessionId, account);
			if (r.isSuccess()) {
				status = "success";
				message = r.getMessage();
			} else {
				status = "fail";
				message = r.getMessage();
			}
			
			
			//傳送 主選單 副選單 資料

			// Prepare JSON response
			response.setContentType("application/json");
			System.out.println("status "+ status);
		
			jsonObject.put("status", status);
			jsonObject.put("message", message);
			jsonObject.put("level", r.getAcessLevel());
			
			
			if(status.equals("success") ) {
				
				
				// main and sub menu that the user can access
				ArrayList<String> a1 = mainMenuList(account);
				ArrayList<String> a2 = subMenuList(account);
				
				JSONArray main = listToJsonArray(a1);
				JSONArray sub = listToJsonArray(a2);
				
				jsonObject.put("mainMenu", main);
				jsonObject.put("subMenu", sub);
			}

			// Write JSON response back to client
			PrintWriter out = response.getWriter();
			out.println(jsonObject);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Process a POST request from the client by calling the doGet method.
     * @param req the HttpServletRequest that encapsulates the request from the client.
     * @param resp the HttpServletResponse that encapsulates the response to the client.
     */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
	
	
	
	
	
	private JSONArray listToJsonArray(ArrayList<String> list) {
	    // 創建一個新的 JSONArray
	    JSONArray jsonArray = new JSONArray();

	    // 迭代 ArrayList 並添加到 JSONArray
	    for (String str : list) {
	        jsonArray.put(str);
	    }

	    // 返回 JSONArray
	    return jsonArray;
	}
	private ArrayList<String> mainMenuList(String account) throws Exception  {
		
		//query list for main menu
		String sql = "select b.id " + "from account a,mmenu_access b,main_menu c "
				+ "where a.access_level =b.access_level and " + "a.account = " + "'" + account + "'" +" and b.id=c.id and status = 'y'";
		ArrayList<String> al = new ArrayList<>();
		
		QueryBean b = null;
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			ArrayList<ArrayList<Object>> a = b.querySQL(sql);

			if(a==null) { // prevent a is null
				al.add("logout");
			}
			else {

			for(ArrayList<Object> innerList : a) {
			    if(innerList.size() > 0) { // 檢查是否有元素存在
			        Object obj = innerList.get(0); // 取得每一個內層列表的第一個元素
			        if(obj instanceof String) { // 檢查該元素是否為String
			            al.add((String)obj); // 將其加入到目標列表中
			        }
			    }
			}
			
			al.add("logout");
			
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(b!=null) {
				 b.close();
				 b=null;
			 }
		}

		
		
		
		System.out.println("main");
		for(int i = 0 ; i<al.size();i++) {
			System.out.println(al.get(i));
		}
		 
		
		return al;
		
	}
	private ArrayList<String> subMenuList(String account) throws Exception{

		
		// query list for sub menu
		String sql = "select c.page_id " + "from account a,access_level b,page c "
				+ "where a.access_level =b.access_level and b.page_html =  c.page_html  and " + "a.account = " + "'" + account + "'"+" and c.status='y'";
		ArrayList<String> al = new ArrayList<>();
		
		QueryBean b = null;
		try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			ArrayList<ArrayList<Object>> a = b.querySQL(sql);

			if(a==null) {
				al.add("empty");
			}
			else {
			
			for(ArrayList<Object> innerList : a) {
			    if(innerList.size() > 0) { // 檢查是否有元素存在
			        Object obj = innerList.get(0); // 取得每一個內層列表的第一個元素
			        if(obj instanceof String) { // 檢查該元素是否為String
			            al.add((String)obj); // 將其加入到目標列表中
			        }
			    }
			}
			for(int i =0; i < al.size();i++) {
				System.out.println(al.get(i));
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(b!=null) {
				 b.close();
				 b=null;
			 }
		}
		

		
		
		
		
		System.out.println("sub");
		for(int i = 0 ; i<al.size();i++) {
			System.out.println(al.get(i));
		}
		 
		
		return al;
		
	}
	/**
	 * Check if a user has the privilege to access a specific page.
	 * @param url the URL of the page to access.
	 * @param sessionId the ID of the session.
	 * @param account the account of the user.
	 * @return a Result object containing the result of the check.
	 * @throws Exception if an error occurs during the check.
	 */
	private Result checkPagePrivilege(String url, String sessionId, String account) throws Exception {
		boolean isSuccess = false;
		String message = "";
		String levelSql="select access_level from account where account = "+"'"+account+"'";
		QueryBean b = new QueryBean("qb", false, "utf-8", "utf-8");
		
		int  level =0;
		try {
			
			ArrayList<ArrayList<Object>> a1 = b.querySQL(levelSql);
			if(a1==null) {
				level = -10;
			}
			else {
			level = Integer.parseInt((String) a1.get(0).get(0));
			}
			
						
			System.out.println("here comes arraylist");
//			for(ArrayList<Object> innerList : a1) {

//			    
//			    for(Object obj : innerList) {
//			        System.out.print(obj + " ");
//			    }
//
//			    System.out.println(); //打印一行后换行
//			}
			
			if(level==0) {
				isSuccess = true;
				message = "manager account!!";
				System.out.println(message);
				Result r = new Result(isSuccess, message,level);
				return r; 
			}
		
			else if(level==-1) {
				isSuccess = false;
				message = "Your account has been suspended!!";
				Result r = new Result(isSuccess, message,level);
			} 	
			else if (account == null || sessionId == null) {
				System.out.println("login problem");
				isSuccess = false;
				message = "You must be logged in to access this page!!";
			}
			// Check if user has access to the page
			else if (!PagePrivilegeHelper(account, url)) {
				System.out.println("permissson problem");
				isSuccess = false;
				message = "You do not have permission to access this page!!";
			} else  {
				System.out.println("level "+level);
				isSuccess = true;
				message = "success!!";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(b!=null) {
				 b.close();
				 b=null;
			 }
		}
		// Return result
		Result result = new Result(isSuccess, message,level);
		return result;
	}

	/**
	 * Check if an account has the privilege to access a specific page.
	 * @param account the account to check.
	 * @param url the URL of the page to access.
	 * @return true if the account has access to the page, false otherwise.
	 * @throws Exception 
	 */
	private boolean PagePrivilegeHelper(String account, String url) throws Exception {
		
		String sql = "select b.page_html " + "from account a,access_level b "
				+ "where a.access_level =b.access_level and " + "a.account = " + "'" + account + "'";
		
		
		QueryBean b = new QueryBean("qb", false, "utf-8", "utf-8");
		ArrayList<ArrayList<Object>> a1 = b.querySQL(sql);
		try {
			//首頁不擋
			if(url.equals("index.html")) {
				return true;
			}
			
			for(ArrayList<Object> innerList : a1) {

		    
		    for(Object obj : innerList) {
		        System.out.print(obj + " ");
		    }

		    System.out.println(); 
		}
			
			
			// Check if URL is found in the results
			boolean urlFound = a1.stream().flatMap(List::stream).map(Object::toString).anyMatch(url::equals);
			System.out.println("url found:"+urlFound);
			return urlFound;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//close resources
			if(b!=null) {
				 b.close();
				 b=null;
			 }
		}

		return false;
	}
	
	

	/**
	 * An internal class used to encapsulate the result of a permission check.
	 */
	private class Result {
	    private boolean isSuccess;
	    private String message;
	    private  int accessLevel;
	   
	    
	    
	    public Result() {
	    	isSuccess=false;
	    	message="unknown";
	    	
	    	
	    }
	    
	    /**
	     * Constructor for the Result class.
	     * @param isSuccess a boolean indicating the success of the operation.
	     * @param message a string containing a message about the result.
	     */
	    public Result(boolean isSuccess, String message , int accessLevel) {
	        this.isSuccess = isSuccess;
	        this.message = message;
	        this.accessLevel=accessLevel;
	    }

	    // Getters
	    public boolean isSuccess() {
	        return isSuccess;
	    }

	    public String getMessage() {
	        return message;
	    }
	    public int getAcessLevel() {
	    	return accessLevel;
	    }
	}

	
}
