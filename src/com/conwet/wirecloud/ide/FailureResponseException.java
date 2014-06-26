/*
 *  Copyright (c) 2014 CoNWeT Lab., Universidad Politécnica de Madrid
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
package com.conwet.wirecloud.ide;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class FailureResponseException extends Exception {

	public final String details;

	public FailureResponseException(String message, String details) {
        super(message);
        this.details = details;
	}

	public static FailureResponseException createFailureException(String responseContent) throws JSONException {
		JSONObject responseData = new JSONObject(responseContent);
        return new FailureResponseException(responseData.getString("description"), responseData.getString("details"));
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 4536655928392093474L;

}
