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
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary;
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibraryTaskIdentifier;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import com.fluidbpm.ws.client.v1.user.AES256Local;
import com.google.common.io.BaseEncoding;
import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Migration class for 3rd party related migrations.
 *
 * @see com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary
 */
public class MigratorThirdPartyLib {

    @Builder
    @Data
    public static final class MigrateOptThirdPartLib {
        private String filename;
        private String description;
        private String libContentClasspath;
        private String libContentFilepath;
        private byte[] libContent;
    }

    @Builder
    public static final class MigrateOptRemoveThirdPartLib {
        private Long thirdPartyId;
        private String thirdPartyFilename;
    }

    /**
     * Migrate a third party library.
     *
     * @param cc {@code ConfigurationClient}
     * @param opts {@code MigrateOptThirdPartLib}
     */
    public static void migrateThirdPartyLib(
            ConfigurationClient cc, MigrateOptThirdPartLib opts
    ) {
        List<ThirdPartyLibraryTaskIdentifier> existingLibs;
        try {
            existingLibs = cc.getAllThirdPartyTaskIdentifiers();
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
            existingLibs = new ArrayList<>();
        }

        byte[] content = opts.libContent;
        if ((content == null || content.length == 0) &&
                UtilGlobal.isNotBlank(opts.libContentClasspath)) {
            content = getContentForPath(opts.libContentClasspath);
        } else if ((content == null || content.length == 0) &&
                UtilGlobal.isNotBlank(opts.libContentFilepath)) {
            content = getContentForFilepath(opts.libContentFilepath);
        }

        if (content == null || content.length == 0) {
            throw new FluidClientException("No library content set! Set libContent, libContentClasspath or libContentFilepath.",
                    FluidClientException.ErrorCode.IO_ERROR);
        }

        String sha = BaseEncoding.base16().encode(AES256Local.sha256(content)).toLowerCase();

        ThirdPartyLibraryTaskIdentifier libWithName = existingLibs.stream()
                .filter(itm -> opts.filename.equalsIgnoreCase(itm.getLibraryFilename()))
                .findFirst()
                .orElse(null);

        ThirdPartyLibrary libToCreateUpdate = new ThirdPartyLibrary();
        if (libWithName != null && !sha.equalsIgnoreCase(libWithName.getLibrarySha256sum())) {
            libToCreateUpdate.setId(libWithName.getThirdPartyLibraryId());
        } else if (libWithName != null && sha.equalsIgnoreCase(libWithName.getLibrarySha256sum())) {
            return;// Already exists.
        }

        libToCreateUpdate.setFilename(opts.filename);
        libToCreateUpdate.setDescription(opts.description);
        libToCreateUpdate.setLibraryDataBase64(BaseEncoding.base64().encode(content));
        cc.upsertThirdPartyLibrary(libToCreateUpdate);
    }

    /**Read the content from class path {@code path}.
     * @param path The resource to fetch content for.
     * @return {@code byte[]} from {@code path}
     * @throws FluidClientException when there is an I/O issue.
     */
    public static byte[] getContentForPath(String path) throws FluidClientException {
        if (path == null || path.isEmpty()) throw new FluidClientException(
                "Path to content in package is not set!", FluidClientException.ErrorCode.IO_ERROR
        );
        try (InputStream inputStream = MigratorThirdPartyLib.class.getClassLoader().getResourceAsStream(path);) {
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

    /**Remove a third party library.
     * @param cc {@code ConfigurationClient}
     * @param opts {@code MigrateOptRemoveThirdPartLib}
     */
    public static void removeLibrary(
            ConfigurationClient cc, MigrateOptRemoveThirdPartLib opts
    ) {
        ThirdPartyLibrary toDelete = new ThirdPartyLibrary(opts.thirdPartyId);
        toDelete.setFilename(opts.thirdPartyFilename);
        cc.deleteThirdPartyLibrary(toDelete);
    }
}
