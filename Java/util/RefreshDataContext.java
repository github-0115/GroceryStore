package uyun.show.server.domain.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uyun.show.server.domain.dto.RefreshData;

public class RefreshDataContext {
	private Map<String, List<RefreshData>> datasetConnMap = new ConcurrentHashMap<>();
	public static RefreshDataContext instance = new RefreshDataContext();
	public static RefreshDataContext getInstance() {
    return instance;
	}
	public Map<String, List<RefreshData>> getDatasetConnMap() {
		return datasetConnMap;
	}
	public void setDatasetConnMap(Map<String, List<RefreshData>> datasetConnMap) {
		this.datasetConnMap = datasetConnMap;
	}
	
}
