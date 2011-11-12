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

import org.texconverter.io.PeekReader;

/**
 * Verbatim parsing.
 * 
 * @author tfrana
 */
public final class VerbatimParser {

    private VerbatimParser() {
    }

    /**
     * Parsing an environments content verbatim.
     * 
     * @param tokenizer
     *            the tokenizer to use
     * @param environmentName
     *            the name of the environment
     * @return the environments contents
     * @throws IOException
     *             on IO errors
     */
    public static String parseEnvironmentVerbatim(final Tokenizer tokenizer,
            final String environmentName) throws IOException {
        final StringBuffer sb = new StringBuffer(512);
        final StringBuffer partial = new StringBuffer(16);

        Token tokenId = tokenizer.getNextToken();
        if (tokenId == Token.OTHER) {
            String txt = tokenizer.getLastTokenSequence();
            if (txt.startsWith(PeekReader.LINEBREAK)) {
                txt = txt.substring(PeekReader.LINEBREAK.length());
            }
            sb.append(txt);
            tokenId = tokenizer.getNextToken();
        }

        while (tokenId != Token.NONE) {
            if (tokenId == Token.COMMAND) {
                // environment ends if these tokens appear in this order:
                // cmd, "end", [newline,] block, "verbatim", blockclose
                partial.append(tokenizer.getLastTokenSequence());
                tokenizer.getNextCmdNameToken();
                final String name = tokenizer.getLastTokenSequence();
                partial.append(name);

                boolean onTrack;
                if ((onTrack = "end".equals(name))) {
                    tokenId = tokenizer.getNextToken();
                    partial.append(tokenizer.getLastTokenSequence());
                }
                if (onTrack && tokenId == Token.OTHER) {
                    if (tokenizer.getLastTokenSequence().trim().length() == 0) {
                        tokenId = tokenizer.getNextToken();
                        partial.append(tokenizer.getLastTokenSequence());
                    } else {
                        onTrack = false;
                    }
                }
                if (onTrack && (onTrack = (tokenId == Token.BLOCKOPEN))) {
                    tokenId = tokenizer.getNextToken();
                    partial.append(tokenizer.getLastTokenSequence());
                }
                if (onTrack && (onTrack = (tokenId == Token.OTHER))) {
                    final String envName = tokenizer.getLastTokenSequence();
                    if (onTrack = environmentName.equals(envName)) {
                        tokenId = tokenizer.getNextToken();
                        partial.append(tokenizer.getLastTokenSequence());
                    }
                }
                if (onTrack) {
                    onTrack = tokenId == Token.BLOCKCLOSE;
                }

                if (onTrack) {
                    // end of verbatim env
                    break;
                } else {
                    sb.append(partial);
                }
                partial.delete(0, partial.length());
            } else {
                sb.append(tokenizer.getLastTokenSequence());
            }
            tokenId = tokenizer.getNextToken();
        }
        String content = sb.toString();
        if (content.endsWith(PeekReader.LINEBREAK)) {
            content = content.substring(0, content.length()
                    - PeekReader.LINEBREAK.length());
        }

        return sb.toString();
    }

    /**
     * Get the content of a TeX block as plain text.
     * 
     * @param tokenizer
     *            the tokenizer to use
     * @param endTokenId
     *            the token marking the end of the block
     * @return the blocks content as text
     * @throws IOException
     *             on IO errors
     */
    public static String parseText(final Tokenizer tokenizer,
            final Token endTokenId) throws IOException {
        final StringBuffer body = new StringBuffer(64);
        int depth = 1;

        Token startTokenId = null;
        if (endTokenId == Token.BLOCKCLOSE) {
            startTokenId = Token.BLOCKOPEN;
        } else if (endTokenId == Token.OPTARGBLOCKCLOSE) {
            startTokenId = Token.OPTARGBLOCKOPEN;
        }

        Token tokenId = tokenizer.getNextToken();

        while (tokenId != Token.NONE) {
            if (tokenId == startTokenId) {
                depth++;
            } else if (tokenId == endTokenId) {
                depth--;
                if (depth <= 0) {
                    break;
                }
            }
            body.append(tokenizer.getLastTokenSequence());
            tokenId = tokenizer.getNextToken();
        }

        return body.toString();
    }
}
