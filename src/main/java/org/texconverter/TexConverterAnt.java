/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:31 $
 *
 * ====================================================================
 * TexConverter
 * Copyright (C) 2006 - NEUSTA GmbH Bremen, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * created: 02.08.2006 tfrana
 */
package org.texconverter;

import org.apache.tools.ant.Task;

/**
 * @author tfrana
 */
public class TexConverterAnt extends Task {

    private String inputFileName = null;

    private String outputFileName = null;

    private String outputFormatName = null;

    private String propertiesFile = null;

    private boolean verbose = true;

    private String localeCode = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        final TexConverter converter = new TexConverterCommandline();
        converter.init(null, verbose);
        converter.startConversion(propertiesFile, inputFileName,
                outputFileName, outputFormatName, localeCode);
    }

    /**
     * @param inputFileName
     *            path of the input file to convert
     */
    public void setInputFileName(final String inputFileName) {
        this.inputFileName = inputFileName;
    }

    /**
     * @param outputFileName
     *            path of the output file to create/overwrite
     */
    public void setOutputFileName(final String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * @param outputFormatName
     *            name of the output format
     */
    public void setOutputFormatName(final String outputFormatName) {
        this.outputFormatName = outputFormatName;
    }

    /**
     * @param propertiesFile
     *            path of a properties file
     */
    public void setPropertiesFile(final String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    /**
     * @param verbose
     *            log output verbose
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * @param localeCode
     *            the ISO3C language code - this locale will be used for
     *            localized resources
     */
    public void setLocaleCode(final String localeCode) {
        this.localeCode = localeCode;
    }

}
