/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2023] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.health;

/**
 * Health rating for a fluid instance.
 * Unhealthy -> Instance is facing errors with the matching service.
 * Healthy -> All is well.
 * Degraded -> Service is operational, but not responsive enough.
 *
 * @author jasonbruwer on 2023-06-20.
 * @since 1.13
 */
public enum Health {
	Unhealthy,
	Healthy,
	Degraded
}
