package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import utils.DBUtils;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int ID_NULL = -2;
	private static final int ID_ERROR = -1;
	private static final int ID_FINISH = 0;
	private static final int ID_SUCCESS = 1;
	private static final String KEY_RESULT = "result";
	private static final String KEY_UNFINISHED_COUNT = "unfinished_count";
	private static final String KEY_RESOURCE_DIR = "resource_dir";
	private static final String KEY_RESOURCE_ARRAY = "resource_array";
	
	private static final String FILE_HOST = "https://model-1256072725.cos.ap-beijing.myqcloud.com/";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
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
		String drawerid = request.getParameter("drawerid");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if (drawerid != null && drawerid.length() != 0) {
			DBUtils db = new DBUtils();
			List<Object> queryResult = db.query("select dirname from usertask where userid=?",
					new Object[] { drawerid });
			if (queryResult.size() > 0) {
				Map dirmap = (Map) queryResult.get(0);
				String dirname = (String) dirmap.get("dirname");
				List<Object> checkResult = db.query("select filename from fileinfo where dirname=? and isfinished=0",
						new Object[] { dirname });
				if (checkResult.size() > 0) {
					json.put(KEY_RESULT, ID_SUCCESS);
					json.put(KEY_UNFINISHED_COUNT, checkResult.size());
					json.put(KEY_RESOURCE_DIR, dirname);
					JSONArray array = new JSONArray();
					for (Object file : checkResult) {
						Map filemap = (Map) file;
						String filename = (String) filemap.get("filename");
						array.add(FILE_HOST + dirname + "/" + filename);
					}
					json.put(KEY_RESOURCE_ARRAY, array.toString());
				} else {
					json.put(KEY_RESULT, ID_FINISH);
					json.put(KEY_UNFINISHED_COUNT, 0);
					json.put(KEY_RESOURCE_DIR, dirname);
					json.put(KEY_RESOURCE_ARRAY, "");
				}
			} else {
				json.put(KEY_RESULT, ID_ERROR);
				json.put(KEY_UNFINISHED_COUNT, 0);
				json.put(KEY_RESOURCE_DIR, "");
				json.put(KEY_RESOURCE_ARRAY, "");
			}
		} else {
			json.put(KEY_RESULT, ID_NULL);
			json.put(KEY_UNFINISHED_COUNT, 0);
			json.put(KEY_RESOURCE_DIR, "");
			json.put(KEY_RESOURCE_ARRAY, "");
		}
		out.write(json.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
