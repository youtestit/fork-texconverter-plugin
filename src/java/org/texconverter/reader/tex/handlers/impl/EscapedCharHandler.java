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

import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.TextImpl;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * @author tfrana
 */
public class EscapedCharHandler implements CommandHandler {

    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws IOException {

        final String escapedChar = cmd.getName();
        builder.consumeNode(new TextImpl(escapedChar), false);
    }

}
