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
import java.util.List;
import java.util.regex.Pattern;

import org.texconverter.dom.impl.Command;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.handlers.CommandHandler;
import org.texconverter.reader.tex.parser.CommandParser;
import org.texconverter.reader.tex.parser.Tokenizer;

/**
 * @author tfrana
 */
public class AccentHandler implements CommandHandler {

    private static String[][] umlautReplacements;

    // private static Matcher[] umlautMatchers;

    static {
        umlautReplacements = new String[][] { { "a", "ä" }, { "o", "ö" },
                { "u", "ü" }, { "A", "Ä" }, { "O", "Ö" }, { "U", "Ü" },
                { "\\\\i", "ï" } };

        // umlautMatchers = new Matcher[umlautReplacements.length];
        // for (int i = 0; i < umlautReplacements.length; i++) {
        // umlautMatchers[i] = Pattern.compile(umlautReplacements[i][0],
        // Pattern.MULTILINE | Pattern.DOTALL).matcher("");
        // }
    }

    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws TexParserException, IOException, ConfigurationException {
        final Tokenizer tokenizer = builder.getTokenizer();

        if ("\"".equals(cmd.getName())) {
            String umlaut;
            if ("{".equals(tokenizer.peekNextChar())) {
                final List[] arg = CommandParser.parseBlockArguments(
                        new boolean[] { false }, new boolean[0], builder);
                umlaut = (String) arg[0].get(0);
            } else {
                umlaut = tokenizer.peekNextChar();
                tokenizer.skipOneChar();
            }

            for (int i = 0; i < umlautReplacements.length; i++) {
                if (Pattern.matches(umlautReplacements[i][0], umlaut)) {
                    umlaut = umlautReplacements[i][1];
                    break;
                }
            }

            builder.appendText(umlaut);
        }

    }

}
