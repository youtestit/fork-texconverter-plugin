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

import org.texconverter.resource.FileResourceLoader;
import org.texconverter.resource.ResourceLoader;

/**
 * @author tfrana
 */
public class TexConverterMaven extends TexConverter {

    private String inputFileName = null;

    private String outputFileName = null;

    private String outputFormatName = null;

    private String propertiesFile = null;

    private String pluginResourceDir = null;

    private boolean verbose = true;

    private String basePath = null;

    private String localeCode = null;

    /**
     * Start conversion using the current settings.
     */
    public void doExecute() {

        basePath = pluginResourceDir.replaceAll("\\\\", "/");
        basePath = basePath.replaceAll("plugin-resources/{0,1}\\z", "");

        init(basePath, verbose);
        startConversion(propertiesFile, inputFileName, outputFileName,
                outputFormatName, localeCode);
    }

    @Override
    protected ResourceLoader getResourceLoader() {
        return new FileResourceLoader();
    }

    /**
     * @param inputFileName
     *            The inputFileName to set.
     */
    public void setInputFileName(final String inputFileName) {
        this.inputFileName = inputFileName;
    }

    /**
     * @param outputFileName
     *            The outputFileName to set.
     */
    public void setOutputFileName(final String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * @param outputFormatName
     *            The outputFormatName to set.
     */
    public void setOutputFormatName(final String outputFormatName) {
        this.outputFormatName = outputFormatName;
    }

    /**
     * @param pluginResourceDir
     *            The pluginResourceDir to set.
     */
    public void setPluginResourceDir(final String pluginResourceDir) {
        this.pluginResourceDir = pluginResourceDir;
    }

    /**
     * @param propertiesFile
     *            The propertiesFile to set.
     */
    public void setPropertiesFile(final String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    /**
     * @param verbose
     *            The verbose to set.
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * @return Returns the inputFileName.
     */
    public String getInputFileName() {
        return inputFileName;
    }

    /**
     * @return Returns the outputFileName.
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * @return Returns the outputFormatName.
     */
    public String getOutputFormatName() {
        return outputFormatName;
    }

    /**
     * @return Returns the pluginResourceDir.
     */
    public String getPluginResourceDir() {
        return pluginResourceDir;
    }

    /**
     * @return Returns the propertiesFile.
     */
    public String getPropertiesFile() {
        return propertiesFile;
    }

    /**
     * @return Returns the verbose.
     */
    public boolean getVerbose() {
        return verbose;
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
