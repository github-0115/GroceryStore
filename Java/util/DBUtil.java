package uyun.show.server.domain.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 关系型数据库工具类
 * 
 * @author yangbin Create at 2018年9月2日 上午7:43:40
 */
public class DBUtil {
	public static Logger logger = LoggerFactory.getLogger(DBUtil.class);
	private String driver;
	private String url;
	private String username;
	private String password;
	private static final String[] TEXT = { "java.lang.String" };
	private static final String[] DATE = { "java.sql.Timestamp", "java.sql.Date", "java.sql.Time" };
	private static final String[] NUMBER = { "java.math.BigDecimal", "java.lang.Boolean", "java.lang.Byte", "java.lang.Short",
			"java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double" };

	DBUtil(String driver, String url, String username, String password) {
		this.setDriver(driver);
		this.setUrl(url);
		this.setUsername(username);
		this.setPassword(password);
	}

	public static Connection getConn(String driver, String url, String username, String password) {
		Connection conn = null;
		try {
			Class.forName(driver); // classLoader,加载对应驱动
			conn = (Connection) DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			logger.error("ERROR [mysql connection error:]" + "Url=" + url + "Username=" + username + "Password=" + password);
		} catch (SQLException e) {
			logger.error("ERROR [mysql sql error：]" + e.getMessage());
		}
		return conn;
	}

	public static boolean queryTables(Map<String, String> params) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		conn = getConn(params.get("driver"), params.get("jdbcUrl"), params.get("userName"), params.get("password"));

		try {
			pstmt = conn.prepareStatement("select * from " + params.get("tableName"));
			pstmt.execute();
			return true;
		} catch (Exception e) {
			logger.error("ERROR [createTables error:]" + e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					logger.error("ERROR [ResultSet close error:]" + e.getMessage());
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					logger.error("ERROR [PreparedStatement close error:]" + e.getMessage());
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					logger.error("ERROR [SQL Connection close error:]" + e.getMessage());
				}
			}
		}
		return false;
	}

	public static boolean createUsers(Map<String, String> params) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		conn = getConn(params.get("driver"), params.get("jdbcUrl"), params.get("userName"), params.get("password"));
		String createUserSql = "CREATE USER 'ADDUSER'@'DBADDR' IDENTIFIED BY 'ADDPSW';";
		String authUserSql = "GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP ON DBNAME.* TO 'ADDUSER'@'DBADDR'";
		String jdbcUrl = params.get("jdbcUrl");
		String addr = jdbcUrl.split(":")[2].substring(2);
		String user = params.get("accessAct");
		String psw = params.get("accessPwd");
		String dbname = jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1, jdbcUrl.lastIndexOf("?"));
		createUserSql = createUserSql.replace("DBADDR", addr).replace("ADDUSER", user).replace("ADDPSW", psw);
		authUserSql = authUserSql.replace("DBADDR", addr).replace("ADDUSER", user).replace("DBNAME", dbname);
		logger.info("INFO [Mysql Create User info:] Sql:" + createUserSql);
		logger.info("INFO [Mysql authUserSql info:] Sql:" + authUserSql);
		try {
			pstmt = conn.prepareStatement(createUserSql);
			pstmt.execute();
			pstmt.close();
			pstmt = conn.prepareStatement(authUserSql);
			pstmt.execute();
			return true;
		} catch (Exception e) {
			logger.error("ERROR [create user error:]" + e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					logger.error("ERROR [ResultSet close error:]" + e.getMessage());
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					logger.error("ERROR [PreparedStatement close error:]" + e.getMessage());
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					logger.error("ERROR [SQL Connection close error:]" + e.getMessage());
				}
			}
		}
		return false;
	}

	public static boolean createTables(Map<String, String> params) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		conn = getConn(params.get("driver"), params.get("jdbcUrl"), params.get("userName"), params.get("password"));
		String createSql = params.get("columns");
		String conbineSql = "CREATE TABLE IF NOT EXISTS `%s` (" + createSql
				+ "`auto_increment_id_` bigint(20) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`auto_increment_id_`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;";
		conbineSql = String.format(conbineSql, params.get("tableName"));
		logger.info("INFO [Mysql Create Connection info:] Sql:" + conbineSql);
		try {
			pstmt = conn.prepareStatement(conbineSql);
			pstmt.execute();
			return true;
		} catch (Exception e) {
			logger.error("ERROR [createTables error:]" + e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					logger.error("ERROR [ResultSet close error:]" + e.getMessage());
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					logger.error("ERROR [PreparedStatement close error:]" + e.getMessage());
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					logger.error("ERROR [SQL Connection close error:]" + e.getMessage());
				}
			}
		}
		return false;
	}

	public static int delete(String driver, String url, String username, String password, String sql) {
		int i = 0;
		try (Connection conn = getConn(driver, url, username, password);
				PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql)) {
			i = pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("ERROR [mysql sql error：]" + sql);
		}
		return i;
	}

	public static List<Map<String, String>> executeJdbc(String driver, String url, String username, String password, String sql) {
		Connection conn = null;
		conn = getConn(driver, url, username, password);
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			List<Map<String, String>> fieldsData = new ArrayList<>();
			Map<String, String> data = new HashMap<>();
			List<String> titles = new ArrayList<>();
			for (int i = 1; i <= colCount; i++) {
				String code = rsmd.getColumnLabel(i);
				titles.add(code);
			}
			while (rs.next()) {
				for (String code : titles) {
					data.put(code, rs.getString(code));
				}
				fieldsData.add(data);
			}
			return fieldsData;
		} catch (Exception e) {
			logger.error("ERROR [mysql sql error：]" + sql + "[error:]" + e.getMessage());
		} finally {
			pstmt = null;
			conn = null;
		}
		return null;
	}

	public static String getDriver(String type) {
		switch (type) {
		case "MYSQL":
			return "com.mysql.jdbc.Driver";
		case "ORACLE":
			return "oracle.jdbc.driver.OracleDriver";
		default:
			return "";
		}
	}

	public static String getUrlFormat(String type) {
		switch (type) {
		case "MYSQL":
			return "jdbc:mysql://%s:%s/%s";
		case "ORACLE":
			return "jdbc:oracle:thin:@%s:%s:%s";
		default:
			return "";
		}
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
