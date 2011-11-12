/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:30 $
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
package org.texconverter.reader.tex.handlers.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.impl.AbstractNode;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.reader.tex.builder.EnvironmentBuilder;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.builder.impl.BuilderFactory;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.handlers.CommandHandler;
import org.texconverter.reader.tex.parser.CommandParser;
import org.texconverter.reader.tex.parser.EnvDef;

/**
 * @author tfrana
 */
public class EnvironmentHandler implements CommandHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(EnvironmentHandler.class);

    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws TexParserException, IOException, ConfigurationException {

        String envName = (String) cmd.getMandatoryArgs().get(0);
        if (envName.endsWith("*")) {
            envName = envName.substring(0, envName.length() - 1);
        }
        final EnvDef envDef = builder.getEnvDef(envName);

        if ("begin".equals(cmd.getName())) {

            final EnvCommand envCmd = CommandParser.parseEnvCommand(cmd,
                    builder);

            if (envDef == null && LOGGER.isErrorEnabled()) {
                LOGGER.error("handle -> unknown environment " + envName);
            } else {
                final EnvironmentBuilder envBuilder = BuilderFactory
                        .getInstance().getEnvironmentBuilder(builder, envCmd);

                if (envBuilder == null && LOGGER.isWarnEnabled()) {
                    LOGGER.warn("handle -> no builder specified for environment "
                            + envName);
                } else {
                    final AbstractNode env = envBuilder.build();
                    if (env != null) {
                        builder.consumeNode(env, false);
                    }
                }
            }
        } else if ("end".equals(cmd.getName())) {
            if (envDef != null
                    && builder.getClass().equals(envDef.getBuilderClass())) {
                builder.setFinished();
            }
        }
    }
}
