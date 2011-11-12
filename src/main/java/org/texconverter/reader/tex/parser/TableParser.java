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
package org.texconverter.reader.tex.parser;

import java.io.IOException;

import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.builder.impl.EmptyBuilder;
import org.texconverter.reader.tex.builder.impl.TableBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;

/**
 * A parser for tabular environments. Works basically the same as the other
 * parsers, except for usage of & token.
 * 
 * @author tfrana
 */
public final class TableParser {

    private TableParser() {
    }

    /**
     * Parse tokens from the tokenizer of the builder.
     * 
     * @param builder
     *            the current builder
     * @throws IOException
     *             on IO errors
     * @throws TexSyntaxException
     *             on TeX syntax errors
     * @throws TexParserException
     *             on internal parser errors
     * @throws ConfigurationException
     *             on configuration errors
     */
    public static void parse(final TableBuilder builder) throws IOException,
            TexSyntaxException, TexParserException, ConfigurationException {

        final Tokenizer tokenizer = builder.getTokenizer();
        Token tokenId = tokenizer.getNextToken();

        while (tokenId != Token.NONE) {
            if (tokenId == Token.COMMAND) {
                CommandParser.parseCommand(builder);
            } else if (tokenId == Token.BLOCKOPEN) {
                final AbstractBuilder nodeBuilder = new EmptyBuilder(builder,
                        Token.BLOCKCLOSE);
                nodeBuilder.build();
            } else if (tokenId == Token.BLOCKCLOSE) {
                throw new TexSyntaxException(
                        "encountered '}' without matching opening '{'");
            } else if (tokenId == Token.COMMENT) {
                tokenizer.skipLine();
            } else if (tokenId == Token.AMPERSAND) {
                builder.startNewColumn();
            } else if (tokenId == Token.WHITESPACE) {
                builder.addWhitespace();
            } else if (tokenId != Token.NEWPARAGRAPH) {
                // no multiple paragraphs inside a tablefield, everything else
                // handled as normal text
                builder.appendText(tokenizer.getLastTokenSequence());
            }
            if (builder.isFinished()) {
                break;
            }

            tokenId = tokenizer.getNextToken();
        }
    }
}
