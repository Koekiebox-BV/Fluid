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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.config.Configuration;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Migration class for configuration related migrations.
 * @see com.fluidbpm.program.api.vo.config.Configuration
 */
public class MigratorConfig {

    @Builder
    @Data
    public static final class MigrateOptConfig {
        private String theme;
        private String privateLabel;

        private String companyLogoClasspath;
        private String companyLogoFilepath;
        private byte[] companyLogoContent;

        private String companyLogoSmallClasspath;
        private String companyLogoSmallFilepath;
        private byte[] companyLogoSmallContent;
    }

    /**
     * Migrate the configurations.
     *
     * @param cc {@code ConfigurationClient}
     * @param opts {@code MigrateOptConfig}
     */
    public static void migrateConfiguration(
            ConfigurationClient cc, MigrateOptConfig opts
    ) {
        // Company Logo:
        byte[] compLogoContent = opts.companyLogoContent;
        if ((compLogoContent == null || compLogoContent.length == 0) && UtilGlobal.isNotBlank(opts.companyLogoClasspath)) {
            compLogoContent = getContentForClassPath(opts.companyLogoClasspath);
        } else if ((compLogoContent == null || compLogoContent.length == 0) && UtilGlobal.isNotBlank(opts.companyLogoFilepath)) {
            compLogoContent = getContentForFilepath(opts.companyLogoFilepath);
        }
        if (compLogoContent != null && compLogoContent.length > 0) {
            cc.loadCompanyLogo(compLogoContent);
        }

        // Company Logo Small:
        compLogoContent = opts.companyLogoContent;
        if ((compLogoContent == null || compLogoContent.length == 0) && UtilGlobal.isNotBlank(opts.companyLogoSmallClasspath)) {
            compLogoContent = getContentForClassPath(opts.companyLogoSmallClasspath);
        } else if ((compLogoContent == null || compLogoContent.length == 0) && UtilGlobal.isNotBlank(opts.companyLogoSmallFilepath)) {
            compLogoContent = getContentForFilepath(opts.companyLogoSmallFilepath);
        }
        if (compLogoContent != null && compLogoContent.length > 0) {
            cc.loadCompanyLogoSmall(compLogoContent);
        }
        
        // Theme:
        if (UtilGlobal.isNotBlank(opts.theme)) {
            cc.upsertConfiguration(
                    Configuration.Key.PrimeFacesTheme.name(),
                    opts.theme
            );
        }

        // Label:
        if (UtilGlobal.isNotBlank(opts.privateLabel)) {
            cc.upsertConfiguration(
                    Configuration.Key.WhiteLabel.name(),
                    opts.privateLabel
            );
        }
    }

    /**Read the content from class path {@code path}.
     * @param path The resource to fetch content for.
     * @return {@code byte[]} from {@code path}
     * @throws FluidClientException when there is an I/O issue.
     */
    public static byte[] getContentForClassPath(String path) throws FluidClientException {
        if (path == null || path.isEmpty()) throw new FluidClientException(
                "Path to content in package is not set!", FluidClientException.ErrorCode.IO_ERROR
        );
        try (InputStream inputStream = MigratorConfig.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) throw new FluidClientException(
                    String.format("Unable to find '%s'.", path),
                    FluidClientException.ErrorCode.IO_ERROR
            );

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int readVal = -1;
                while ((readVal = inputStream.read()) != -1) baos.write(readVal);
                baos.flush();
                return baos.toByteArray();
            }
        } catch (IOException e) {
            throw new FluidClientException(e.getMessage(), e, FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**Read the content from file path {@code path}.
     * @param path The local folder to read contents from.
     * @return {@code byte[]} from {@code path}
     * @throws FluidClientException when there is an I/O issue.
     */
    public static byte[] getContentForFilepath(String path) throws FluidClientException {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new FluidClientException(e.getMessage(), e, FluidClientException.ErrorCode.IO_ERROR);
        }
    }
}
