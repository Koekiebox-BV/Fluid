/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2018] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox Innovations.
 */

package com.fluidbpm.program.api.vo.field;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * General 'name and value' object.
 *
 * @author jasonbruwer on 1/16/18.
 * @since 1.8
 * @version 1.8
 */
@Getter
@Setter
public class NameValue implements Serializable {
	private String name;
	private Object value;

	/**
	 * Default constructor.
	 */
	public NameValue() {
		this(null,null);
	}

	/**
	 * Constructor to init name and value.
	 *
	 * @param nameParam The key name.
	 * @param valueParam The key value.
	 */
	public NameValue(String nameParam, Object valueParam) {
		this.name = nameParam;
		this.value = valueParam;
	}

	/**
	 * If the value is {@code null}, {@code null} will be returned.
	 * Otherwise {@code this.value.toString()} is called.
	 *
	 * @return {@code String} value of {@code this.value}.
	 */
	public String toString() {
		if (this.value == null) return null;
		return this.value.toString();
	}
}
