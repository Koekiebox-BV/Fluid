/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox
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

/*----------------------------------------------------------------------------*
#*
#* Copyright (c) 2015 by Koekiebox Limited. All rights reserved.
#*
#* This software is the confidential and proprietary property of
#* Koekiebox Limited and may not be disclosed, copied or distributed
#* in any form without the express written permission of Koekiebox Limited.
#*
#*---------------------------------------------------------------------------*/

package com.fluid.program.api;

import java.util.List;

import com.fluid.program.api.vo.FluidItem;

/**
 *
 */
public interface ICustomWebAction extends IActionBase {

    /**
     * May be 'Form Save' to apply when saving a Form.
     *
     * @return
     */
    public abstract String getActionIdentifier();

    /**
     *
     * @return
     */
    public abstract List<String> getAllowedFormDefinitions();

    /**
     * <code>Execute Order (2)</code>
     *
     * @param fluidItemParam
     * @return
     * @throws Exception
     */
    public abstract List<FluidItem> execute(FluidItem fluidItemParam) throws Exception;
}
