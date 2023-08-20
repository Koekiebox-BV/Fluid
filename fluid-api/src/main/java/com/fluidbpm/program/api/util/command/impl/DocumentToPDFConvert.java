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

package com.fluidbpm.program.api.util.command.impl;

import java.io.File;
import java.io.IOException;

import com.fluidbpm.program.api.util.command.CommandUtil;
import com.fluidbpm.program.api.util.exception.UtilException;

/**
 * Utility class used for executing document to pdf conversion.
 *
 * @author jasonbruwer
 * @since v1.6
 *
 * @see com.fluidbpm.program.api.util.command.CommandUtil
 */
public class DocumentToPDFConvert {

	private CommandUtil commandUtil;

	//fluid-cli doc-to-pdf-convert -i /fluid_host_system_mapping/1.docx -o /fluid_host_system_mapping/1.pdf
	private static final String COMMAND_CONVERT_DOC_TO_PDF = "doc-to-pdf-convert";

	/**
	 * Default constructor creating instance of {@code CommandUtil}.
	 */
	public DocumentToPDFConvert() {
		super();

		this.commandUtil = new CommandUtil();
	}

	/**
	 * Makes use of the Fluid Core to convert a document into a PDF file.
	 *
	 * @param inputDocumentParam The file to convert to PDF.
	 * @return The {@code File} object of the result PDF.
	 *
	 * @throws UtilException If anything goes wrong during execution.
	 */
	public File convertDocumentToPDF(File inputDocumentParam) {

		if (inputDocumentParam == null ||
				!inputDocumentParam.exists())
		{
			throw new UtilException(
					"Input document to convert not provided or does not exist.",
					UtilException.ErrorCode.COMMAND);
		}

		if (!inputDocumentParam.isFile())
		{
			throw new UtilException(
					"Input document '' is not a file.",
					UtilException.ErrorCode.COMMAND);
		}

		File parentFolder = inputDocumentParam.getParentFile();
		String inputFilenameWithoutExt = inputDocumentParam.getName();

		int indexOfDot = -1;
		if((indexOfDot = inputFilenameWithoutExt.indexOf('.')) > -1)
		{
			inputFilenameWithoutExt = inputFilenameWithoutExt.substring(0,indexOfDot);
		}

		File generatedPdfFileOut = new File(parentFolder.getAbsolutePath().concat(
				File.separator).concat(inputFilenameWithoutExt).concat(".pdf"));

		String completeOutputPath = generatedPdfFileOut.getAbsolutePath();

		try {
			CommandUtil.CommandResult commandResult =
					this.commandUtil.executeCommand(
							CommandUtil.FLUID_CLI,
							COMMAND_CONVERT_DOC_TO_PDF,
							"-i",
							inputDocumentParam.getAbsolutePath(),
							"-o",
							completeOutputPath);

			//There is a problem...
			if (commandResult.getExitCode() != 0)
			{
				throw new UtilException(
						"Unable to convert '"+
								inputDocumentParam.getName()+
								"' to PDF. "+ commandResult.toString(),
						UtilException.ErrorCode.COMMAND);
			}

			File returnVal = new File(completeOutputPath);
			if (!returnVal.exists())
			{
				throw new UtilException(
						"Command executed, but no output file. Expected PDF at '"+
								completeOutputPath+"'.",
						UtilException.ErrorCode.GENERAL);
			}

			return returnVal;
		}
		//
		catch (IOException eParam) {

			throw new UtilException(
					"Problem executing command. "+eParam.getMessage(),
					eParam,UtilException.ErrorCode.GENERAL);
		}
	}
}
