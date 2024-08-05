/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox B.V.
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

package com.fluidbpm.ws.client.migrator;

import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import com.fluidbpm.ws.client.v1.user.AES256Local;
import com.google.common.io.BaseEncoding;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Migration class for 3rd party related migrations.
 *
 * @see com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary
 */
public class MigratorThirdPartyLib {

    @Builder
    public static final class OptMigrateThirdPartLib {
        private String filename;
        private byte[] libContent;
    }

    /**
     * Migrate a Table field.
     *
     * @param cc {@code ConfigurationClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateThirdPartyLib(
            ConfigurationClient cc, OptMigrateThirdPartLib opts
    ) {
        List<ThirdPartyLibrary> existingLibs;
        try {
            existingLibs = null;//TODO complete...
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
            existingLibs = new ArrayList<>();
        }

        String sha = BaseEncoding.base16().encode(AES256Local.sha256(opts.libContent)).toLowerCase();

        ThirdPartyLibrary libWithName = existingLibs.stream()
                .filter(itm -> opts.filename.equalsIgnoreCase(itm.getFilename()))
                .findFirst()
                .orElse(null);
        if (libWithName == null) {
            // TODO create:
        } else if (!sha.equalsIgnoreCase(libWithName.getSha256sum())) {
            // TODO update:
        }
    }
}
