/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluidbpm.program.api.util;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.fluidbpm.program.api.util.cache.CacheUtil;

/**
 * Base class for any utility classes used in the Fluid framework.
 *
 * @author jasonbruwer on 2016/08/20.
 * @since 1.0
 */
public abstract class ABaseUtil implements Serializable {
	private static final String KEY_ALGO = "AES";
	private static final String ALGO_ECB = "AES";

	/**
	 * Get the encrypted key for encrypted text field value.
	 */
	public static byte[] ENCRYPTED_FIELD_KEY;

	private CacheUtil cacheUtil;

	/**
	 * Create a new instance with a {@link CacheUtil}.
	 *
	 * @param cacheUtilParam The {@link CacheUtil}.
	 *
	 * @see CacheUtil
	 */
	public ABaseUtil(CacheUtil cacheUtilParam) {
		super();
		this.cacheUtil = cacheUtilParam;
	}

	/**
	 * Default constructor.
	 * No use for cache util.
	 */
	public ABaseUtil() {
		super();
		this.cacheUtil = null;
	}

	/**
	 * Returns the {@code CacheUtil} as created
	 * by the constructor.
	 *
	 * @return {@code null} or the {@code CacheUtil} instance.
	 */
	public CacheUtil getCacheUtil() {
		return this.cacheUtil;
	}

	/**
	 * Retrieves a property and returns the value as {@code java.lang.String}.
	 *
	 * @param propertiesParam The origin of the properties.
	 * @param propertyKeyParam The property key.
	 * @return The property value.
	 */
	protected static String getStringPropertyFromProperties(
		Properties propertiesParam,
		String propertyKeyParam
	) {
		if (propertiesParam == null || propertiesParam.isEmpty()) {
			return null;
		}

		return propertiesParam.getProperty(propertyKeyParam);
	}

	/**
	 * Retrieves a property and returns the value as {@code int}.
	 *
	 * @param propertiesParam The origin of the properties.
	 * @param propertyKeyParam The property key.
	 * @return The property value as an {@code int}.
	 */
	protected static int getIntPropertyFromProperties(
			Properties propertiesParam,
			String propertyKeyParam
	) {
		String strProp = getStringPropertyFromProperties(
				propertiesParam, propertyKeyParam);

		if (strProp == null || strProp.trim().isEmpty()) {
			return -1;
		}

		try {
			return Integer.parseInt(strProp);
		} catch(NumberFormatException nfe) {
			return -1;
		}
	}

	/**
	 * Returns -1 if there is a problem with conversion.
	 *
	 * @param toParseParam The {@code String} value to convert to {@code long}.
	 * @return {@code long} primitive version of {@code toParseParam}.
	 */
	protected long toLongSafe(String toParseParam) {
		if (toParseParam == null || toParseParam.trim().isEmpty()) {
			return -1;
		}

		try {
			return Long.parseLong(toParseParam.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Decrypt {@code dataToDecryptParam} using the {@code keyParam} key (Electronic Codebook (ECB)).
	 *
	 * @param dataToDecryptParam The encrypted data to decrypt.
	 * @return Clear text.
	 *
	 * @throws SQLException If unable to decrypt.
	 */
	public static byte[] decryptECB(byte[] dataToDecryptParam)
			throws SQLException{
		Key key = new SecretKeySpec(ENCRYPTED_FIELD_KEY, KEY_ALGO);
		try {
			Cipher cipher = Cipher.getInstance(ALGO_ECB);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(dataToDecryptParam);
		} catch (InvalidKeyException | IllegalBlockSizeException |
				NoSuchPaddingException | NoSuchAlgorithmException |
				BadPaddingException except) {
			throw new SQLException("Unable to decrypt data. " + except.getMessage(), except);
		}
	}
}
