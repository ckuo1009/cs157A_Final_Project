import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.json.*;

import com.pub.database.QueryBean;
@WebServlet("/GroupServlet")
public class GroupServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> group;
		try {
			group = getGroup();
			JSONArray jsonArray1 = new JSONArray(group);
	        System.out.println("轉換array");
	        for(int i = 0; i<group.size();i++) {
	            System.out.println(group.get(i));
	        }
	        
	        response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();

			out.println(jsonArray1);

			out.close();
	    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
       

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    private ArrayList<String> getGroup() throws Exception  {
		ArrayList<String> group = new ArrayList<>();
		QueryBean b = new QueryBean("qb", false, "utf-8", "utf-8");
		String sql = "select level_name from levelinfo ";
		ArrayList<ArrayList<Object>> a = b.querySQL(sql);

		a = b.querySQL(sql);

		for (ArrayList<Object> innerList : a) {
			group.add((String) innerList.get(0)); // 將內部列表的第一個元素加入到group中
		}
		for (ArrayList<Object> innerList : a) {

			for (Object obj : innerList) {
				System.out.print(obj + " ");
			}

			System.out.println();
		}
		try {

		} catch (Exception e) {
			System.out.println("system error");
			e.printStackTrace();
		} finally {
			if (b != null) {
				b.close();
			}
			b = null;
		}

		return group;

	}
}
