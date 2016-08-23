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

package com.fluid.program.api.util.elasticsearch;

import org.elasticsearch.client.Client;

import com.fluid.program.api.util.ABaseUtil;

/**
 * ElasticSearch Utility class used for {@code Form} related actions.
 * A {@code Form} in Fluid is the equivalent to a Document in ElasticSearch.
 *
 * @author jasonbruwer on 2016/08/19.
 * @since 1.3
 * @see ABaseUtil
 */
public class ESFormUtil extends ABaseUtil {

    //TODO This needs to be completed once...
    private Client client;

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param clientParam The ES Client.
     */
    public ESFormUtil(Client clientParam) {
        super();

        this.client = clientParam;
    }



}
