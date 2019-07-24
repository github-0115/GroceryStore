package uyun.show.server.domain.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uyun.show.server.domain.Constants;
import uyun.show.server.domain.dto.Result;
import uyun.show.server.domain.dto.ResultData;
import uyun.show.server.domain.model.databank.DataAnalysisQuery;
import uyun.show.server.domain.model.databank.FieldDTO;
import uyun.show.server.domain.model.databank.PageData;
import uyun.show.server.domain.model.databank.PageDataBindTitle;

import java.util.List;

public class DatabankHelper {
    protected static final Logger logger = LoggerFactory.getLogger(DatabankHelper.class);

    protected static String baseUrl = "";

    protected static String DatabankAnalysisQuery = "udap/openapi/v1/portal/datas/analysis/query?pageSize=0&pageIndex=1&apikey=";
    protected static String DatabankAnalysisResultQuery = "udap/openapi/v1/portal/datas/analysis/result?apikey=";
    protected static String DatabankDelAnalysisPath = "udap/openapi/v1/portal/datas/analysis/delete?apikey=";
    protected static String DatabankAnalysisModelPath = "udap/openapi/v1/portal/datas/analysis/field?apikey=";
    protected static String DatabankStatusPath = "udap/openapi/v1/portal/status/test?apikey=";

    public static String GetBaseUrl() {
        if (baseUrl.isEmpty()) {
            baseUrl = Constants.UyunUrl;
        }
        return baseUrl;
    }

    /**
     * @param apiKey
     * @return
     * @desc 获取数据平台状态
     */
    public static boolean DatabankStatus(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("获取数据平台状态 ： apikey错误");
            return false;
        }

        try {
            Result result = null;
            String url = GetBaseUrl() + DatabankStatusPath + apiKey;
            String res = HttpUtil.get(url);
            if (res.isEmpty()) {
                logger.error("获取数据平台状态 ： 获取失败");
                return false;
            }
            result = JSON.parseObject(res, Result.class);
            if (result == null || result.getErrCode() != 200) {
                logger.error("获取数据平台状态 ： 状态异常");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("获取数据平台状态异常 ： " + e);
            return false;
        }
    }

    /**
     * @param name
     * @return
     * @desc 获取聚合分析
     */
    public static PageData<DataAnalysisQuery> ListDataAnalysis(String name, String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("获取聚合分析 ： apikey错误");
            return null;
        }

        try {
            ResultData result = null;
            String url = GetBaseUrl() + DatabankAnalysisQuery + apiKey;
            String res = HttpUtil.get(url);
            if (res.isEmpty()) {
                logger.error("获取聚合分析 ： 获取失败");
                return null;
            }
            result = JSON.parseObject(res, ResultData.class);
            if (result == null || result.getErrCode() != 200 || result.getData() == null) {
                return null;
            }

            PageData<DataAnalysisQuery> data = JSON.parseObject(JSON.toJSONString(result.getData()), PageData.class);

            return data;
        } catch (Exception e) {
            logger.error("获取聚合分析异常 ： " + e);
            return null;
        }
    }

    /**
     * @param id
     * @return
     * @desc 获取聚合分析字段模型
     */
    public static List<FieldDTO> FieldModel(String id, String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("获取聚合分析字段模型 ： apikey错误");
            return null;
        }

        try {
            ResultData<List<FieldDTO>> result = null;
            String url = GetBaseUrl() + DatabankAnalysisModelPath + apiKey + "&id=" + id;
            String res = HttpUtil.get(url);
            if (res.isEmpty()) {
                logger.error("获取聚合分析字段模型 ： 获取失败");
                return null;
            }
            result = JSON.parseObject(res, ResultData.class);
            if (result == null || result.getErrCode() != 200 || result.getData() == null) {
                return null;
            }
            return result.getData();
        } catch (Exception e) {
            logger.error("获取聚合分析字段模型异常 ： " + e);
            return null;
        }
    }

    /**
     * @param id
     * @param apiKey
     * @return
     * @desc 删除聚合分析
     */
    public static boolean DeleteDataAnalysis(String id, String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("删除聚合分析 ： apikey错误");
            return false;
        }

        try {
            ResultData result = null;
            String url = GetBaseUrl() + DatabankDelAnalysisPath + apiKey + "&id=" + id;
            String res = HttpUtil.get(url);
            if (res.isEmpty()) {
                logger.error("删除聚合分析 ： 获取失败");
                return false;
            }
            result = JSON.parseObject(res, ResultData.class);
            if (result == null || result.getErrCode() != 200) {
                logger.error("删除聚合分析 ： 状态异常");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("删除聚合分析异常 ： " + e);
            return false;
        }
    }

    /**
     * @param id
     * @return
     * @desc 获取聚合分析结果
     */
    public static PageDataBindTitle ListAnalysisResult(String id, String apiKey, int page, int size) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("获取聚合分析结果 ： apikey错误");
            return null;
        }

        try {
            ResultData<PageDataBindTitle> result = null;
            String url = GetBaseUrl() + DatabankAnalysisResultQuery + apiKey + "&id=" + id + "&pageIndex=" + page + "&pageSize=" + 10000;//Integer.MAX_VALUE;
            String res = HttpUtil.get(url);
            if (res.isEmpty()) {
                logger.error("获取聚合分析结果 ： 获取失败");
                return null;
            }
            result = JSON.parseObject(res, ResultData.class);
            if (result == null || result.getErrCode() != 200 || result.getData() == null) {
                return null;
            }

            PageDataBindTitle pageDataBindTitle = JSON.parseObject(JSON.toJSONString(result.getData()), PageDataBindTitle.class);
            if (pageDataBindTitle == null) {
                return null;
            }
            return pageDataBindTitle;
        } catch (Exception e) {
            logger.error("获取聚合分析结果异常 ： " + e);
            return null;
        }
    }
}
