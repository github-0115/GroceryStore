package uyun.show.server.domain.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uyun.show.server.domain.Constants;
import uyun.show.server.domain.dto.AnalysisDatas;
import uyun.show.server.domain.dto.AnalysisDatasHttpResult;
import uyun.show.server.domain.dto.AnalysisHttpResult;
import uyun.show.server.domain.dto.AnalysisModelHttpResult;
import uyun.show.server.domain.dto.DataAnalysisDatas;
import uyun.show.server.domain.dto.DataAnalysisResult;
import uyun.show.server.domain.dto.ModelField;

public class DatabankUtil {

	private static Logger logger = LoggerFactory.getLogger(DatabankUtil.class);

	/**
	 * 获取databank状态
	 *
	 */
	public static boolean getDatabankStatus(String apikey) {
		String url = Constants.DatabankBaseurl + Constants.DatabankStatusPath + "?apikey=%s";
		url = String.format(url, apikey);
		try {
			String result = HttpUtil.get(url);
			if (result != null) {
				return true;
			}
		} catch (Exception e) {
			logger.error("GetDatabankStatus HttpResult Error");
			return false;
		}
		return false;
	}

	/**
	 * 删除databank聚合分析
	 *
	 */
	public static boolean delAnalysisHttpResult(String id, String apikey) {
		String url = Constants.DatabankBaseurl + Constants.DatabankDelAnalysisPath + "?apikey=%s&id=%s";
		url = String.format(url, apikey, id);
		try {
			String result = HttpUtil.post(url, "");
			if (result != null) {
				return true;
			}
		} catch (Exception e) {
			logger.error("delAnalysisHttpResult HttpResult Error");
			return false;
		}
		return false;
	}

	/**
	 * 获取databank聚合分析
	 *
	 */
	public static DataAnalysisResult getDatabankAnalysis(String apikey) {
		String url = Constants.DatabankBaseurl + Constants.DatabankAnalysisQuery + "?apikey=%s";
		url = String.format(url, apikey);
		try {
			String result = HttpUtil.get(url);
			if (result != null) {
				return JsonUtil.readJson(result, AnalysisHttpResult.class).getData();
			}
		} catch (Exception e) {
			logger.error("GetDatabankStatus HttpResult Error");
			return null;
		}
		return null;
	}

	/**
	 * 获取databank聚合分析模型
	 *
	 */
	public static List<ModelField> getDatabankAnalysisModel(String apikey, String id) {
		String url = Constants.DatabankBaseurl + Constants.DatabankAnalysisModelPath + "?apikey=%s&id=%s";
		url = String.format(url, apikey);
		try {
			String result = HttpUtil.get(url);
			if (result != null) {
				return JsonUtil.readJson(result, AnalysisModelHttpResult.class).getData();
			}
		} catch (Exception e) {
			logger.error("GetDatabankStatus HttpResult Error");
			return null;
		}
		return null;
	}
	
	/**
	 * 获取databank聚合分析数据
	 *
	 */
	public static AnalysisDatas getDatabankAnalysisDatas(String apikey, String id) {
		DataAnalysisDatas dataAnalysisDatas = getDatabankAnalysisResult(id, apikey);
		return dataConversion(dataAnalysisDatas);
	}

	private static AnalysisDatas dataConversion(DataAnalysisDatas data) {
		if (data == null || data.getList() == null || data.getTitle() == null) {
			return null;
		}
		AnalysisDatas analysisDatas = new AnalysisDatas();
		analysisDatas.setTitle(data.getTitle());
		
		List<Map<String, Object>> results = new ArrayList<>();
		for(Object list : data.getList()){
			if (list == null) {
				logger.error("DataConversion data.List Err");
				continue;
			}
			if (list instanceof List) {
				List lists = (List)list;
				if (lists == null || lists.size() == 0) {
					logger.error("DataConversion data.List list Err");
				}
				if (lists.size() != data.getTitle().size()) {
					logger.error("DataConversion data.List list Err");
				}
				Map<String, Object> result = new HashMap<>();
				for(int i = 0;i < lists.size();i++){
					result.put(data.getTitle().get(i).getCode(), lists.get(i));
				}
				results.add(result);
			}else{
				logger.error("DataConversion data.List list Err");
				continue;
			}
		}
		analysisDatas.setTotal(results.size());
		analysisDatas.setData(results);
		return analysisDatas;
	}

	public static DataAnalysisDatas getDatabankAnalysisResult(String id, String apikey) {
		String url = Constants.DatabankBaseurl + Constants.DatabankAnalysisResultQuery + "?apikey=%s&id=%s";
		url = String.format(url, apikey);
		try {
			String result = HttpUtil.get(url);
			if (result != null) {
				return JsonUtil.readJson(result, AnalysisDatasHttpResult.class).getData();
			}
		} catch (Exception e) {
			logger.error("GetDatabankStatus HttpResult Error");
			return null;
		}
		return null;
	}

}
