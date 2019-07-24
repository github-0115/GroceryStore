package uyun.show.server.domain.util;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uyun.show.server.domain.dto.FieldDTO;
import uyun.show.server.domain.dto.JdbcInfo;
import uyun.show.server.domain.dto.JdbcInfoMapped;
import uyun.show.server.domain.enumeration.SourceType;
import uyun.show.server.domain.exception.FileldCommonException;
import uyun.show.server.domain.model.DataAccessSource;

import java.sql.*;
import java.util.*;

/**
 * @author weixy
 * @create 2018年11月8日
 */
public class ConnectTestUtil {

    protected final static Logger logger = LoggerFactory.getLogger(ConnectTestUtil.class.getName());

    private static final String[] TEXT = {"java.lang.String"};
    private static final String[] DATE = {"java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};
    private static final String[] NUMBER = {"java.math.BigDecimal", "java.lang.Boolean", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double"};

    private static final String TEST_SQL = "select 1";

    public static boolean testConnect(DataAccessSource dataAccessSource) throws ClassNotFoundException, SQLException {
        final SourceType type = SourceType.valueOf(dataAccessSource.getType());
        switch (type) {
            case MYSQL:
            case ORACLE:
            case SQLSERVER:
            case POSTGRESQL:
                JdbcInfo jdbc = JSONObject.parseObject(JSONObject.toJSONString(dataAccessSource.getAccessInfo()), JdbcInfo.class);
                JdbcInfoMapped jdbcMapped = JdbcInfoMapped.mapped(jdbc, type);
                return testJdbcConnect(jdbcMapped);
            case KAFKA:
                return true;
//                KafkaInfo kafka = JSONObject.parseObject(JSONObject.toJSONString(dataAccessSource.getAccessInfo()), KafkaInfo.class);
//                return testKafkaConnect(kafka);
            default:
                return true;
        }
    }

    public static boolean testJdbcConnect(JdbcInfoMapped jdbc) {
        try {
            Class.forName(jdbc.getDriver());
        } catch (ClassNotFoundException e) {
            throw new FileldCommonException("系统内部错误");
        }
        try (Connection conn = DriverManager.getConnection(jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword())) {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(TEST_SQL);
            return true;
        } catch (Exception e) {
            throw new FileldCommonException("数据库连接错误");
        }
    }

    public static List<FieldDTO> executeSql(DataAccessSource dataAccessSource) {
        JdbcInfo jdbcInfo = JSONObject.parseObject(JSONObject.toJSONString(dataAccessSource.getAccessInfo()), JdbcInfo.class);
        JdbcInfoMapped jdbc = JdbcInfoMapped.mapped(jdbcInfo, SourceType.valueOf(dataAccessSource.getType()));
        return executeJdbc(jdbc, jdbcInfo.getSql());
    }

    public static List<FieldDTO> executeJdbc(JdbcInfoMapped jdbc, String sql) {
        try {
            Class.forName(jdbc.getDriver());
        } catch (ClassNotFoundException e) {
            throw new FileldCommonException("系统内部错误");
        }
        try (Connection conn = DriverManager.getConnection(jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword())) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int colCount = rsmd.getColumnCount();
                List<FieldDTO> fields = new ArrayList<>();
                Set<String> codes = new HashSet<>();
                for (int i = 1; i <= colCount; i++) {
                    FieldDTO field = getField(rsmd.getColumnClassName(i));
                    String code = rsmd.getColumnLabel(i);
                    if (codes.contains(code)) {
                        logger.error(String.format("Column '%s' in field list is ambiguous", code));
                        throw new FileldCommonException(String.format("列名 '%s' 在字段列表中重复", code));
                    }
                    field.setCode(code);
                    field.setName(code);
                    fields.add(field);
                    codes.add(field.getCode());
                }
                return fields;
            } catch (SQLException e) {
                throw new FileldCommonException("SQL语句错误");
            }
        } catch (CommunicationsException e) {
            throw new FileldCommonException("数据库地址或端口错误");
        } catch (SQLException e) {
            if (Objects.equals(e.getSQLState(), "08001")) {
                throw new FileldCommonException("数据库地址或端口错误");
            }
            if (Objects.equals(e.getSQLState(), "3D000") || Objects.equals(e.getSQLState(), "42000")) {
                throw new FileldCommonException("数据库不存在");
            }
            if (Objects.equals(e.getSQLState(), "28P01") || Objects.equals(e.getSQLState(), "28000")) {
                throw new FileldCommonException("用户名或密码错误");
            }
            throw new FileldCommonException("数据库连接失败");
        }
    }

    private static FieldDTO getField(String columnTypeName) {
        FieldDTO field = new FieldDTO();
        for (String type : TEXT) {
            if (Objects.equals(type, columnTypeName)) {
                field.setDataType("TEXT");
                return field;
            }
        }

        for (String type : DATE) {
            if (Objects.equals(type, columnTypeName)) {
                field.setDataType("DATE");
                return field;
            }
        }

        for (String type : NUMBER) {
            if (Objects.equals(type, columnTypeName)) {
                field.setDataType("NUMBER");
                return field;
            }
        }

        field.setDataType("TEXT");
        return field;
    }
}
