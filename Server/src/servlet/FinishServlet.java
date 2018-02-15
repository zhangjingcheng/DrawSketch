package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import utils.JsonReader;
import utils.SvgGenerator;

/**
 * Servlet implementation class FinishServlet
 */
@WebServlet("/finish")
public class FinishServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int RESULT_NULL = -2;
	private static final int RESULT_ERROR = -1;
	private static final int RESULT_FINISH = 0;
	private static final int RESULT_SUCCESS = 1;

	private static final String KEY_RESULT = "result";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FinishServlet() {
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
		JSONObject param = JsonReader.receivePost(request);
		String drawerid = (String) param.get("drawerid");
		DBUtils db = new DBUtils();
		List<Object> queryResult = db.query("select dirname from usertask where userid=?",
				new Object[] { drawerid });
		String filename = (String) param.get("filename");
		double scalex = 1080 / (Integer) param.get("displayx");
		double scaley = 1920 / (Integer) param.get("displayy");
		JSONArray data_x = param.getJSONArray("data_x");
		JSONArray data_y = param.getJSONArray("data_y");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if (queryResult.size() == 0) {
			json.put(KEY_RESULT, RESULT_NULL);
		} else {
			List<List<Double>> datax = new ArrayList<List<Double>>();
			List<List<Double>> datay = new ArrayList<List<Double>>();
			if (data_x.size() == data_y.size()) {
				for (int i = 0; i < data_x.size(); i++) {
					JSONArray line_x = data_x.getJSONArray(i);
					JSONArray line_y = data_y.getJSONArray(i);
					List<Double> linex = new ArrayList<Double>();
					List<Double> liney = new ArrayList<Double>();
					for (int j = 0; j < line_x.size(); j++) {
						linex.add(line_x.getDouble(j) * scalex);
						liney.add(line_y.getDouble(j) * scaley);
					}
					datax.add(linex);
					datay.add(liney);
				}
				SvgGenerator.outputLines(datax, datay, "D:/" + filename.replace("png", "svg"));
				//SvgGenerator.outputLines(datax, datay, "/root/finishSvg/" + filename.replace("png", "svg"));
				if (db.update("update fileinfo set isfinished = 1 where filename = ?", new String[] { filename }) > 0){
					json.put(KEY_RESULT, RESULT_SUCCESS);
				}
			} else {
				json.put(KEY_RESULT, RESULT_ERROR);
			}
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
