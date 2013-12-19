/*
 *  Copyright (c) 2013 CoNWeT Lab., Universidad Politécnica de Madrid
 *  
 *  This file is part of Wirecloud IDE.
 *
 *  Wirecloud IDE is free software: you can redistribute it and/or modify
 *  it under the terms of the European Union Public Licence (EUPL)
 *  as published by the European Commission, either version 1.1
 *  of the License, or (at your option) any later version.
 *
 *  Wirecloud IDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 *
 *  You should have received a copy of the European Union Public Licence
 *  along with Wirecloud IDE.
 *  If not, see <https://joinup.ec.europa.eu/software/page/eupl/licence-eupl>.
 */

package com.conwet.wirecloud.ide.wizards;

import java.util.*;
import java.net.URLEncoder;
import java.net.URLDecoder;

@SuppressWarnings("deprecation")
public class QueryParameters {
	
	private static class KVP {
		final String key;
		final String value;

		KVP(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}

	List<KVP> query = new ArrayList<KVP>();

	public QueryParameters(String queryString) {
		parse(queryString);
	}

	public QueryParameters() {
	}

	public void addParam(String key, String value) {
		if (key == null || value == null)
			throw new NullPointerException("null parameter key or value");
		query.add(new KVP(key, value));
	}

	private void parse(String queryString) {
		for (String pair : queryString.split("&")) {
			int eq = pair.indexOf("=");
			if (eq < 0) {
				// key with no value
				addParam(URLDecoder.decode(pair), "");
			} else {
				// key=value
				String key = URLDecoder.decode(pair.substring(0, eq));
				String value = URLDecoder.decode(pair.substring(eq + 1));
				query.add(new KVP(key, value));
			}
		}
	}

	public String toQueryString() {
		StringBuilder sb = new StringBuilder();
		for (KVP kvp : query) {
			if (sb.length() > 0) {
				sb.append('&');
			}
			sb.append(URLEncoder.encode(kvp.key));
			if (!kvp.value.equals("")) {
				sb.append('=');
				sb.append(URLEncoder.encode(kvp.value));
			}
		}
		return sb.toString();
	}

	public String getParameter(String key) {
		for (KVP kvp : query) {
			if (kvp.key.equals(key)) {
				return kvp.value;
			}
		}
		return null;
	}

	public List<String> getParameterValues(String key) {
		List<String> list = new LinkedList<String>();
		for (KVP kvp : query) {
			if (kvp.key.equals(key)) {
				list.add(kvp.value);
			}
		}
		return list;
	}
}