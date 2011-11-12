/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:18 $
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
package org.texconverter.reader.tex.builder.impl;

import java.io.IOException;
import java.util.Locale;

import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.reader.tex.builder.EnvironmentBuilder;
import org.texconverter.reader.tex.builder.TeXDocumentBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.parser.EnvDef;

/**
 * Singleton factory for builders.
 * 
 * @author tfrana
 */
public class BuilderFactory {

    private static BuilderFactory instance = null;

    /**
     * @return singleton instance
     */
    public static final BuilderFactory getInstance() {
        if (instance == null) {
            instance = new BuilderFactory();
        }
        return instance;
    }

    /**
     * Creates and configures a TexDocumentBuilder.
     * 
     * @param cmdDefsFilePath
     *            path to the xml file containing the command definitions
     * @param locale
     *            locale, used for generating caption names for listing, tables
     *            (e.g. table -> EN=Table/DE=Tabelle). If the locale is null,
     *            the system locale will be used. If the wanted locale
     *            translations are not available, a default locale will be used.
     * @return the TexDocumentBuilder
     * @throws IOException
     *             if accessing the cmd defs file fails
     * @throws ConfigurationException
     *             on errors in the cmddefs file
     */
    public TeXDocumentBuilder getTeXDocumentBuilder(
            final String cmdDefsFilePath, final Locale locale)
            throws IOException, ConfigurationException {
        final TeXDocumentBuilderImpl builder = new TeXDocumentBuilderImpl(
                cmdDefsFilePath, locale);
        return builder;
    }

    /**
     * Creates & configures an {@link EnvironmentBuilder}. Not intended for use
     * by renderer.
     * 
     * @param parentBuilder
     *            the parent builder
     * @param envCmd
     *            the environment command
     * @return the EnvironmentBuilder
     * @throws ConfigurationException
     *             on configuration problems
     * @throws IOException
     *             on IO problems
     * @throws TexParserException
     *             on general problems
     */
    public EnvironmentBuilder getEnvironmentBuilder(
            final AbstractBuilder parentBuilder, final EnvCommand envCmd)
            throws ConfigurationException, IOException, TexParserException {
        final EnvDef envDef = parentBuilder.getEnvDef(envCmd.getName());
        AbstractBuilder envBuilder = null;

        if (envDef != null && envDef.getBuilderClass() != null) {
            try {
                envBuilder = (AbstractBuilder) envDef.getBuilderClass()
                        .newInstance();
                envBuilder.setCmdDefs(envDef.getCmdDefs());
            } catch (final InstantiationException e) {
                throw new ConfigurationException(
                        "The specified builder could not be instantiated", e);
            } catch (final IllegalAccessException e) {
                throw new ConfigurationException(
                        "The specified builder could not be instantiated", e);
            }
            envBuilder.init(parentBuilder, envCmd);
        }
        return envBuilder;
    }
}
