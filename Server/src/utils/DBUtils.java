package utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {

	private static final String DRIVENAME = "com.mysql.jdbc.Driver";
	//private static final String URL = "jdbc:mysql://localhost:3306/draw3dsketch";
	private static final String URL = "jdbc:mysql://172.17.0.7:3306/draw3dsketch";
	private static final String USER = "root";
	//private static final String PASSWORD = "101235zh";
	private static final String PASSWORD = "qianran1024";

	private Connection conn = null;
	private Statement st = null;
	private PreparedStatement ppst = null;
	private ResultSet rs = null;

	/**
	 * ��������
	 */
	static {
		try {
			Class.forName(DRIVENAME).newInstance();
		} catch (Exception e) {
			System.out.println("��������ʧ�ܣ�" + e.getMessage());
		}
	}

	/**
	 * �������ݿ�
	 * 
	 * @return
	 */
	public Connection getConn() {
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException e) {
			System.out.println("���ݿ�����ʧ�ܣ�" + e.getMessage());
		}
		return conn;
	}
	
	/**
	 * �½�һ�ű�
	 * 
	 * @return
	 */
	public void createTable(String drawerid, List<Object> dirnames) {
		conn = this.getConn();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet result = meta.getTables(null, null, drawerid, null);
			if (result.next()) {
				System.out.println("table exist");
				return;
			} else {
				System.out.println("table doesn't exist");
				st = conn.createStatement();
				String createSql = "create table " + drawerid +
						" (id INTEGER NOT NULL AUTO_INCREMENT, " + 
						"filename VARCHAR(255), " +
						"dirname VARCHAR(255), " +
						"isfinished TINYINT DEFAULT 0, " + 
						"PRIMARY KEY ( id ))";
				st.executeUpdate(createSql);
				String insertSql = "insert into " + drawerid +
						" (filename, dirname) select filename, dirname from fileinfo where dirname = ";
				for (int i = 0; i < dirnames.size(); i++) {
					Map dirname = (Map) dirnames.get(i);
					insertSql = insertSql + "'" + dirname.get("dirname") + "'";
					if (i != dirnames.size() - 1) {
						insertSql = insertSql + " or dirname = ";
					}
				}
				st.executeUpdate(insertSql);
				System.out.println("table create successful!");
			}
			
		} catch (SQLException e) {
			System.out.println("�½�ʧ�ܣ�" + e.getMessage());
		} finally {
			closeConn();
		}
	}
	/**
	 * ��ȡ��������޲Σ�
	 * 
	 * @param sql
	 * @return
	 */
	private ResultSet getRs(String sql) {
		conn = this.getConn();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			System.out.println("��ѯ���޲Σ�����:" + e.getMessage());
		}
		return rs;
	}

	/**
	 * ��ȡ�����
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	private ResultSet getRs(String sql, Object[] params) {
		conn = this.getConn();
		try {
			ppst = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					ppst.setObject(i + 1, params[i]);
				}
			}
			rs = ppst.executeQuery();
		} catch (SQLException e) {
			System.out.println("��ѯ����" + e.getMessage());
		}

		return rs;
	}

	/**
	 * ��ѯ
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Object> query(String sql, Object[] params) {

		List<Object> list = new ArrayList<Object>();
		ResultSet rs = null;
		if (params != null) {
			rs = getRs(sql, params);
		} else {
			rs = getRs(sql);
		}
		ResultSetMetaData rsmd = null;
		int columnCount = 0;

		try {
			rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			System.out.println("�������������" + e.getMessage());
		} finally {
			closeConn();
		}
		return list;
	}

	/**
	 * ���£��޲Σ�
	 * 
	 * @param sql
	 */
	public int update(String sql) {
		int affectedLine = 0;// ��Ӱ�������
		conn = this.getConn();
		try {
			st = conn.createStatement();
			affectedLine = st.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("���£��޲Σ�ʧ�ܣ�" + e.getMessage());
		} finally {
			closeConn();
		}
		return affectedLine;
	}

	/**
	 * ����
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public int update(String sql, Object[] params) {
		int affectedLine = 0;// ��Ӱ�������
		conn = this.getConn();
		try {
			ppst = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					ppst.setObject(i + 1, params[i]);
				}
			}
			affectedLine = ppst.executeUpdate();
		} catch (SQLException e) {
			System.out.println("����ʧ�ܣ�" + e.getMessage());
		} finally {
			closeConn();
		}
		return affectedLine;
	}

	private void closeConn() {

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		if (ppst != null) {
			try {
				ppst.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

}