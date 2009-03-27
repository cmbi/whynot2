/*
 * $Id: Constants.java 471756 2006-11-06 15:01:43Z husted $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package nl.ru.cmbi.whynot.all;

public final class Constants {

	/**
	 * <p> The application scope attribute under which our user database is
	 * stored. </p>
	 */
	public static final String	RDB_KEY				= "rdb";

	/**
	 * <p> The package name for this application. </p>
	 */
	public static final String	PACKAGE						= "nl.ru.cmbi.whynot";

	/**
	 * <p> The session scope attribute under which the User object for the
	 * currently logged in user is stored. </p>
	 */
	public static final String	PDBID_KEY						= "pdbid";

	// ---- Error Messages ----

	/**
	 * <p>
	 * A static message in case message resource is not loaded.
	 * </p>
	 */
	public static final String	ERROR_MESSAGES_NOT_LOADED	= "ERROR:  Message resources not loaded -- check servlet container logs for error messages.";

	/**
	 * <p>
	 * A static message in case database resource is not loaded.
	 * <p>
	 */
	public static final String	ERROR_DATABASE_NOT_LOADED	= "ERROR:  User database not loaded -- check servlet container logs for error messages.";

	/**
	 * <p>
	 * A standard key from the message resources file, to test if it is available.
	 * <p>
	 */
	public static final String	ERROR_DATABASE_MISSING		= "error.database.missing";

}
