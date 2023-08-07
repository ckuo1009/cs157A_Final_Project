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
 * Servlet implementation class pageManaServlet
 */
@WebServlet("/pageManaServlet")
public class pageManaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public pageManaServlet() {
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
		String level_name = request.getParameter("level_name");
		
		
		//addConfirm
		
		
		
		
//		JSONObject jsonObject = new JSONObject();
		

		if (type.equals("search")) {
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			ArrayList<ArrayList<Object>> a;
			try {
				a = getData();
				for (ArrayList<Object> innerList : a) {
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
		} else if (type.equals("add")) {  //給前台main sub menu list
			JSONObject jsonObject = new JSONObject();
			try {

				 ArrayList<ArrayList<Object>> a = buildMenuStructure();
				 jsonObject = convertToJSON(a);
				 response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					PrintWriter out = response.getWriter();

					out.println(jsonObject);

					out.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		else if (type.equals("addConfirm")) {// insertion
			
			JSONObject jsonObject = new JSONObject();
			String permissionType = request.getParameter("permissionType");
	        List<String> selectedMainMenu = null;
	        List<String> selectedSubMenu = null;
	        String status = "";
	        String id="";

	        if(request.getParameter("selectedMainMenu") != null) {
	            selectedMainMenu = parseJsonArray(request.getParameter("selectedMainMenu"));
	        }

	        if(request.getParameter("selectedSubMenu") != null) {
	            selectedSubMenu = parseJsonArray(request.getParameter("selectedSubMenu"));
	        }
	        
	        Result r = insertData(permissionType, selectedMainMenu, selectedSubMenu,0,id);
	        
	        
	        if(r.isSuccess) {
	        	status="success";
	        }
	        else {
	        	status="fail";
	        }
				
				try {
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
				   
	        
		}else if (type.equals("delete")) {
			JSONObject jsonObject = new JSONObject();
			String id = request.getParameter("id");
			Boolean isSuccess = false;
			
			String message="";
			if(deletion(id)) {
				isSuccess=accountFixUp(id);
			}
			
			if(isSuccess) {
				message ="deletion successful";
			}
			else {
				message ="deletion fail";
			}
			
			try {
				jsonObject.put("message", message);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();

				out.println(jsonObject);
				out.close();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}else if(type.equals("edit")) {
			String id = request.getParameter("id");
			JSONObject jsonObjectMain = new JSONObject();
			JSONObject jsonObjectSelect = new JSONObject();
			String name = "";
			try {
				 ArrayList<ArrayList<Object>> a1 = buildMenuStructure();
				 jsonObjectMain = convertToJSON(a1);
				
					

					
				 ArrayList<ArrayList<Object>> a2 = buildSelectedStructure(id);
				 jsonObjectSelect = convertToJSON(a2);
				 name=getName(id);
				 
				 response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					JSONObject responseJson = new JSONObject();

					responseJson.put("jsonObjectMain", jsonObjectMain);
					responseJson.put("jsonObjectSelect", jsonObjectSelect);
					responseJson.put("name", name);
					
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					PrintWriter out = response.getWriter();
					out.println(responseJson);
					out.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
			
			
		}else if(type.equals("editConfirm")) {
			JSONObject jsonObject = new JSONObject();
			String permissionType = request.getParameter("permissionType");
	        List<String> selectedMainMenu = null;
	        List<String> selectedSubMenu = null;
	        String status = "";

	        if(request.getParameter("selectedMainMenu") != null) {
	            selectedMainMenu = parseJsonArray(request.getParameter("selectedMainMenu"));
	        }

	        if(request.getParameter("selectedSubMenu") != null) {
	            selectedSubMenu = parseJsonArray(request.getParameter("selectedSubMenu"));
	        }
	        
	        Result r = editConfrim(permissionType, selectedMainMenu, selectedSubMenu);

	        if(r.isSuccess) {
	        	status="success";
	        }
	        else {
	        	status="fail";
	        }
				
				try {
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
		}

	}
	private Result editConfrim(String permissionType,List<String> selectedMainMenu,List<String> selectedSubMenu) {
		boolean isSuccess=false;
		QueryBean b = null;
	    String id = "";
	    String message="";
	  String idSql ="select access_level from levelinfo where level_name = '"+permissionType+"'";
	    ArrayList<ArrayList<Object>> a = null;
	    Result r = null ;
	    
	    try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			
			a=b.querySQL(idSql);
			id=(String) a.get(0).get(0);
			
			isSuccess=deletion(id);
			System.out.println("deletion " +isSuccess);
			isSuccess=deletion(id);
			if(isSuccess) {
			    
			    
			        r = insertData(permissionType, selectedMainMenu, selectedSubMenu,1,id);
			        System.out.println("r " +r.getMessage());
			    

			    if(r != null) {
			        if(r.isSuccess) {
			            r.message = "modification successful";
			        } else {
			            r.message = "error ";
			        }
			    } else {
			       System.out.println("something error");
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

					e.printStackTrace();
				}
			}
			b = null;
		}
	    
	    
		return r;
		
		
	}
	/**
	 * Edit
	 * @param id
	 * @return
	 */
	private String getName(String id) {
		QueryBean b = null;
	    String sql = "select level_name from levelinfo where access_level = "+id;
	  String name ="";
	  
	    ArrayList<ArrayList<Object>> a = null;
	    
	        try {
				b = new QueryBean("qb", false, "utf-8", "utf-8");
				a=b.querySQL(sql);
				name = (String) a.get(0).get(0);
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
	        
		
	return name;
	}
	/**
	 * edit 
	 * @param id
	 * @return
	 */
	private ArrayList<ArrayList<Object>> buildSelectedStructure (String id) {
		
		QueryBean b = null;
	    String mainMenuSql = "select name from main_menu, mmenu_access where main_menu.id=mmenu_access.id and mmenu_access.access_level= "+id;
	  
	    ArrayList<ArrayList<Object>> selectedMenu =null;
	    try {
	        b = new QueryBean("qb", false, "utf-8", "utf-8");
	        selectedMenu = b.querySQL(mainMenuSql);
	        
	        if(selectedMenu==null || selectedMenu.isEmpty()) {
	        	selectedMenu = new ArrayList<>();
	            ArrayList<Object> sublist = new ArrayList<>();
	            sublist.add("empty");
	            selectedMenu.add(sublist);
	        }
	       
	        for (ArrayList<Object> mainRow : selectedMenu) {
	            String mainMenuItem = (String) mainRow.get(0);  

	         
	            String subPageSql = "select page_name from page,access_level \r\n"
	            		+ " where page.page_html=access_level.page_html and \r\n"
	            		+ " prev_name = '"+mainMenuItem+"' and access_level.access_level= "+id;
	            ArrayList<ArrayList<Object>> subPageAl = b.querySQL(subPageSql);
	            
	            
	            if (subPageAl != null && !subPageAl.isEmpty()) {
	              
	                for (ArrayList<Object> subRow : subPageAl) {
	                    String subPageItem = (String) subRow.get(0);
	                    mainRow.add(subPageItem);
	                }
	            }
	            
	          
	        }
	        for (ArrayList<Object> mainRow : selectedMenu) {
	            for (Object item : mainRow) {
	                System.out.println(item);
	            }
	        }
	    } catch (Exception e) {
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

	    return selectedMenu;
		
		
		
		
	}
	
	/**
	 * delete
	 * @return
	 */
	private boolean deletion(String accessLevel) {
		
		boolean isSuccess =false;
		 QueryBean b = null;
		 
		 String levelInfo ="delete from levelinfo where access_level = "+accessLevel;
		 String access_level ="delete from access_level where access_level = "+accessLevel;
		 String mmenu_access="delete from mmenu_access where access_level = "+accessLevel;
		 
		 try {
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			ArrayList<String> deletion = new ArrayList<>();
			deletion.add(levelInfo);
			deletion.add(access_level);
			deletion.add(mmenu_access);
			
			String a = b.executeSQL(deletion);
			
			System.out.println("result: "+a);
			
			if(a.equals("")) {
				isSuccess=true;
			}
			
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
		
		
		
		
		 return isSuccess;
	}
	
	/**
	 * delete 
	 * fix up the account which the id has been deleted. reset the id to 
	 * the default value, id = 1.
	 * @return
	 */
	private boolean accountFixUp(String accessLevel) {
		boolean isSuccess =false;
		 QueryBean b = null;
		 
		 //set the id to default value
		 String fixUp="update account set access_level = 1 where access_level = "+accessLevel;
		
			try {
				b = new QueryBean("qb", false, "utf-8", "utf-8");
				isSuccess = b.executeSQL(fixUp);
				
				
				
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
		    
		return isSuccess;
	}
	
	/**
	 * addConfrim
	 * @return
	 */
	private int getNextId() {
	    String sql = "select Max(access_level) from levelinfo";
	    QueryBean b = null;
	  int id = 0;

	    try {
	        b = new QueryBean("qb", false, "utf-8", "utf-8");
	        ArrayList<ArrayList<Object>> a = b.querySQL(sql);

	        id = Integer.parseInt((String) a.get(0).get(0));
	       id= id+1;
	    } catch (Exception e) {
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
	    System.out.println("id: "+id);
	    return id;
	}

	
	
	/**
	 * addConfrim
	 * @param permissionType
	 * @param selectedMainMenu
	 * @param selectedSubMenu
	 * @return
	 */
	private Result insertData (String permissionType,List<String> selectedMainMenu,List<String> selectedSubMenu,int i,String id) {
		Result r = new Result();
		
		if(i==0) {
			 r = insertDataHelper(permissionType);
		}else {
			// call helper method
			 r = editinsertDataHelper(permissionType,id);
		}
		// call helper method
		
	
		boolean isSuccess=false;
		String message="";
		int accesslevel=r.getAcessLevel();
		String stringLevel = String.valueOf(accesslevel);
		List<String> newMainMenu = new ArrayList<>();
		List<String> newSubMenu = new ArrayList<>();
		
		String mmenu_accessSql="insert into mmenu_access values(?,?)";
		String submenu_accessSql="insert into access_level values(?,?)";
		
		
		 QueryBean b = null;
		if(!r.isSuccess) {
			
			return r;
		}else {
			try {
				b = new QueryBean("qb", false, "utf-8", "utf-8");
				
				
				// loop the mainMenu list to get id 
				for(String object :selectedMainMenu) {
					
					 String sql = "select id from main_menu where name= '"+object+"'";
					 ArrayList<ArrayList<Object>> a=b.querySQL(sql);
					 
					 //if the user doesn't select any main menu
					 if(a!=null) {
					 newMainMenu.add((String) a.get(0).get(0));	
					 }
				}
				// loop the mainMenu list to get page_html
				for(String object :selectedSubMenu) {
					
					 String sql = "select page_html from page where page_name= '"+object+"'";
					 ArrayList<ArrayList<Object>> a=b.querySQL(sql);
					 
					 if(a!=null){
						 
						 newSubMenu.add((String) a.get(0).get(0));	
					 }
					
				}
				
				//insertion
				for(String object :newMainMenu) {
					 Vector<String> vtrParams = new Vector<>();
					 
					vtrParams.add(stringLevel);
					vtrParams.add(object);
					b.executeSQL(mmenu_accessSql,vtrParams);
				}
				
				//insertion
				for(String object : newSubMenu) {
					//
					
					 Vector<String> vtrParams = new Vector<>();
					vtrParams.add(stringLevel);
					vtrParams.add(object);
					b.executeSQL(submenu_accessSql,vtrParams);
				}
				 String checkSql="select access_level from mmenu_access where access_level = "+accesslevel;
				ArrayList<ArrayList<Object>> al = b.querySQL(checkSql);
				//al==null||al.size()==0
				
				if(al==null) {
					message = "main sub menu insertion fail";
					
					r= new Result(isSuccess, message, r.getAcessLevel());
					
				}
				else {
					message = "insertion complete";
					isSuccess=true;
					r= new Result(isSuccess, message, r.getAcessLevel());
					
					
				}
				
				
				
				
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
			
		}
		return r;
		
		
		
		
	}
	
	private Result editinsertDataHelper(String permissionType,String id) {
	 	boolean isSuccess=false;
		String message="";
		int accesslevel=0;
		Result r = null ;
		
		QueryBean b = null;
	 	String checkSql="select level_name from levelinfo where level_name = '"+permissionType+"'";
	   String levelInfoSql = "insert into levelinfo values (?,?)";
	   
	   Vector<String> vtrParams = new Vector<>();
	   
	   Boolean bl =false;
	   
	   
	    try {
	    	
			b = new QueryBean("qb", false, "utf-8", "utf-8");
			
			ArrayList<ArrayList<Object>> al = b.querySQL(checkSql);
			
			System.out.println("id "+id);
			
			if(al!=null) {
				message="the name already exists in the database,insertion fail!";
				r= new Result(isSuccess,message,accesslevel);
				System.out.println(message);
				return r;
				
				
			}
			
			accesslevel=Integer.parseInt(id);
			
			vtrParams.add(id);
			vtrParams.add(permissionType);
			
		
			
			 isSuccess=b.executeSQL(levelInfoSql, vtrParams);
			
			 if(isSuccess) {
				 message="levelinfo insertion successful";
				 
				 r = new Result(isSuccess, message, accesslevel);
				 System.out.println(message);
				 
				 return r;
			 }
			 else {
				 message="database error";
				 r = new Result(isSuccess, message, accesslevel);
				 
				 return r;
			 }
			
			
			
		} catch (Exception e) {
			 e.printStackTrace();

		}
	    finally {
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
	 * addConfrim
	 * @return
	 */
	private Result insertDataHelper(String permissionType) {
		 	boolean isSuccess=false;
			String message="";
			int accesslevel=0;
			Result r = null ;
			
			QueryBean b = null;
		 	String checkSql="select level_name from levelinfo where level_name = '"+permissionType+"'";
		   String levelInfoSql = "insert into levelinfo values (?,?)";
		   String idSql="select access_level from levelinfo where access_level = '"+permissionType+"'";
		   Vector<String> vtrParams = new Vector<>();
		   
		   Boolean bl =false;
		   
		   
		    try {
		    	
				b = new QueryBean("qb", false, "utf-8", "utf-8");
				
				ArrayList<ArrayList<Object>> al = b.querySQL(checkSql);
				
				
				if(al!=null) {
					message="the name already exists in the database,insertion fail!";
					r= new Result(isSuccess,message,accesslevel);
					System.out.println(message);
					return r;
					
					
				}
				
				int id=getNextId();
				System.out.println(id);
				System.out.println(accesslevel);
				accesslevel=id;
				String newid = String.valueOf(id);
				
				vtrParams.add(newid);
				vtrParams.add(permissionType);
				
				 isSuccess=b.executeSQL(levelInfoSql, vtrParams);
				
				 if(isSuccess) {
					 message="levelinfo insertion successful";
					 
					 r = new Result(isSuccess, message, accesslevel);
					 System.out.println(message);
					 
					 return r;
				 }
				 else {
					 message="database error";
					 r = new Result(isSuccess, message, accesslevel);
					 
					 return r;
				 }
				
				
				
			} catch (Exception e) {
				 e.printStackTrace();
//				    isSuccess = false;
//				    message = "An exception occurred: " + e.getMessage();
//				    r = new Result(isSuccess, message, accesslevel);
			}
		    finally {
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
	private class Result {
	    private boolean isSuccess;
	    private String message;
	    private  int accessLevel;
	   
	    
	    public Result() {
	    	 this.isSuccess = false;
		        this.message = "error";
		        this.accessLevel= -1;
	    }

	    /**
	     * Constructor for the Result class.
	     * @param isSuccess a boolean indicating the success of the operation.
	     * @param message a string containing a message about the result.
	     */
	    public Result(boolean isSuccess, String message, int accessLevel ) {
	        this.isSuccess = isSuccess;
	        this.message = message;
	        this.accessLevel= accessLevel;
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

	
	/**
	 * addConfrim
	 * @param jsonArrayString
	 * @return
	 */
	 private List<String> parseJsonArray(String jsonArrayString) {
	        List<String> result = null;
			try {
				result = new ArrayList<>();
				JSONArray jsonArray = new JSONArray(jsonArrayString);
				for (int i = 0; i < jsonArray.length(); i++) {
				    result.add(jsonArray.getString(i));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return result;
	    }

	
	/**
	 * add
	 * @param list
	 * @return
	 * @throws JSONException
	 */
	private JSONObject convertToJSON(ArrayList<ArrayList<Object>> list) throws JSONException {
	    JSONObject jsonObject = new JSONObject();
	    for (ArrayList<Object> subList : list) {
	        if (!subList.isEmpty()) {
	        
	            String mainMenuKey = (String) subList.get(0);
	            
	            ArrayList<String> menuItems = new ArrayList<>();
	            for (int i = 1; i < subList.size(); i++) { // Start from index 1 to skip the main menu item
	                menuItems.add((String) subList.get(i));
	            }

	           
	            jsonObject.put(mainMenuKey, menuItems);
	        }
	    }
	    return jsonObject;
	}

	
	
	/**
	 * add
	 * @param account
	 * @return main menu list
	 * @throws Exception
	 */
	private ArrayList<ArrayList<Object>> buildMenuStructure() {
	    QueryBean b = null;
	    String mainMenuSql = "select name from main_menu";
	  
	    ArrayList<ArrayList<Object>> mainMenu = null;
	    try {
	        b = new QueryBean("qb", false, "utf-8", "utf-8");
	        mainMenu = b.querySQL(mainMenuSql);

	        
	        for (ArrayList<Object> mainRow : mainMenu) {
	            String mainMenuItem = (String) mainRow.get(0);  

	            
	            String subPageSql = "select page_name from page where prev_name = '" + mainMenuItem + "'";
	            ArrayList<ArrayList<Object>> subPageAl = b.querySQL(subPageSql);
	            
	            
	            if (subPageAl != null && !subPageAl.isEmpty()) {
	              
	                for (ArrayList<Object> subRow : subPageAl) {
	                    String subPageItem = (String) subRow.get(0);
	                    mainRow.add(subPageItem);
	                }
	            }
	        }
	    } catch (Exception e) {
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

	    return mainMenu;
	}

	 
	 
	

	

	
	/**
	 * search 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<ArrayList<Object>> getData() throws Exception {
		String sql = "select * from levelinfo";
		ArrayList<ArrayList<Object>> a =null;;
		QueryBean b= null;
		try {
			 b = new QueryBean("qb", false, "utf-8", "utf-8");

			a = b.querySQL(sql);

//			for (ArrayList<Object> innerList : a) {
//				for (Object obj : innerList) {
//					System.out.print(obj + " ");
//				}
//
//				System.out.println();
//			}
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
	    

		return a;

	}
	

}
