/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package uyun.show.server.domain.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A Source to read data from a SQL database. This source ask for new data in a
 * table each configured time.
 * <p>
 * 
 * @author <a href="mailto:mvalle@keedio.com">Marcelo Valle</a>
 */
public class ESUtil {
	
	public static Logger logger = LoggerFactory.getLogger(ESUtil.class);

	public static List<Map<String, String>> esQueryResult(String result) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(result);
			JsonNode hitsNode = root.path("hits").path("hits");
			List<Map<String, String>> fieldsData = new ArrayList<>();
			
			for (JsonNode node : hitsNode) {
				JsonNode sourceNode = node.path("_source");
				List<String> fields = new ArrayList<>();
				Iterator<String> iterator = sourceNode.fieldNames();
				Map<String, String> data = new HashMap<>();
				while (iterator.hasNext()) {
					String name = (String) iterator.next();
					String value = sourceNode.get(name).asText();
					data.put(name, value);
				}
				fieldsData.add(data);
			}
			return fieldsData;
		} catch (IOException e) {
			logger.error("ERROR [GET ES DATA FAIL:]" + e.getMessage());
			throw new IOException();
		}
	}

	public static String excuteQuery(String hosts, String index, String type, String jsonQuery) throws IOException {
		String postUrl = "http://hosts/index/type/_search?pretty".replace("hosts", hosts).replace("index", index)
				.replace("type", type);
		jsonQuery = jsonQuery.replace("\\", "");
		try {
			return HttpUtil.post(postUrl, null, jsonQuery);
		} catch (Exception e) {
			logger.error("ERROR [ES query post fail:]" + e.getMessage());
			throw new IOException();
		}
	}

}
