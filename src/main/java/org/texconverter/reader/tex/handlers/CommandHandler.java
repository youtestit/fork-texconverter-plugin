/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:37 $
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
package org.texconverter.reader.tex.handlers;

import java.io.IOException;

import org.texconverter.dom.impl.Command;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;

/**
 * Interface for CommandHandlers.
 * 
 * @author tfrana
 */
public interface CommandHandler {

    /**
     * Takes a Command and handles it ( e.g. by constructing and configuring a
     * specific node and adding it to the builder)
     * 
     * @param cmd
     *            the command to handle
     * @param builder
     *            the current builder
     * @throws TexParserException
     *             if internal parser errors occur
     * @throws IOException
     *             on IO Errors
     * @throws ConfigurationException
     *             if the configuration (cmdDefs, ..) causes an error
     */
    void handle(Command cmd, AbstractBuilder builder)
            throws TexParserException, IOException, ConfigurationException;
}
