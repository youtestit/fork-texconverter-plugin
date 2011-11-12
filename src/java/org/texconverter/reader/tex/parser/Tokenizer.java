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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.texconverter.io.PeekReader;
import org.texconverter.io.impl.StringPeekReader;
import org.texconverter.reader.tex.exceptions.TexParserException;

/**
 * For tokenizing TeX input.
 * 
 * @author tfrana
 */
public class Tokenizer {

    private final Stack<PeekReader> readers = new Stack<PeekReader>();

    private PeekReader reader = null;

    private final StringBuffer sb = new StringBuffer(128);

    private String lastTokenSequence = null;

    private Token lastTokenId = null;

    private static final char[] VERB_CMDNAME = "verb".toCharArray();

    private static final Map<Character, Token> TOKENS = new HashMap<Character, Token>();
    static {
        TOKENS.put('\\', Token.COMMAND);
        TOKENS.put('{', Token.BLOCKOPEN);
        TOKENS.put('[', Token.OPTARGBLOCKOPEN);
        TOKENS.put('}', Token.BLOCKCLOSE);
        TOKENS.put(']', Token.OPTARGBLOCKCLOSE);
        TOKENS.put('%', Token.COMMENT);
        TOKENS.put('&', Token.AMPERSAND);

        TOKENS.put('\t', Token.WHITESPACE);
        TOKENS.put(' ', Token.WHITESPACE);
        TOKENS.put('\r', Token.WHITESPACE);
        TOKENS.put('\n', Token.WHITESPACE);
    }

    /**
     * Get the next token. The character sequence that the token consists of is
     * accessible afterwards via {@link #getLastTokenSequence()}. Note that
     * command names are a bit different, so they got an extra method:
     * {@link #getNextCmdNameToken()}. Same goes for the body of a \verb
     * command ({@link #getNextVerbCmdContent()}).
     * 
     * @return the token id
     * @throws IOException
     *             on IO errors
     */
    public Token getNextToken() throws IOException {

        lastTokenSequence = null;
        lastTokenId = null;

        // if (!bufferedTokens.isEmpty()) {
        // Token bufferedToken = (Token) bufferedTokens.poll();
        // currentTokenId = bufferedToken.id;
        // lastTokenSeq = bufferedToken.sequence;
        // } else {

        char c = 0;
        Token tokenId = null;

        int rval = reader.read();

        while (rval != PeekReader.EOF || !readers.empty()) {
            if (rval == PeekReader.EOF) {
                // take next reader if this one is empty
                reader = readers.pop();
                rval = reader.read();
                continue;
            }

            c = (char) rval;

            tokenId = TOKENS.get(c);

            if (sb.length() == 0) {
                // first char read, remember what kind of token we are parsing
                // here in lastTokenId
                lastTokenId = tokenId;
                if (!(tokenId == Token.WHITESPACE || tokenId == null)) {
                    // this is a 1-char token, stop
                    sb.append(c);
                    break;
                }
            } else if (tokenId != lastTokenId) {
                // already read several chars, and the last one read belongs to
                // the next token -> this token is finished
                reader.unread(1);
                break;
            }

            sb.append(c);

            rval = reader.read();
        }

        if (lastTokenId == null) {
            lastTokenId = Token.OTHER;
        } else if (lastTokenId == Token.WHITESPACE) {
            // check if this whitespace contains >=2 linebreaks
            int i = sb.indexOf(PeekReader.LINEBREAK);
            if (i != -1) {
                i = sb.indexOf(PeekReader.LINEBREAK, i + 1);
                if (i != -1) {
                    lastTokenId = Token.NEWPARAGRAPH;
                }
            }
        }

        if (rval == PeekReader.EOF && sb.length() < 1) {
            lastTokenId = Token.NONE;
        }

        lastTokenSequence = sb.toString();
        sb.delete(0, sb.length());

        return lastTokenId;
    }

    /**
     * Get the next token using command name tokenizing rules.
     * 
     * @return the token id (Token.OTHER for command names)
     * @throws IOException
     *             on IO errors
     */
    public Token getNextCmdNameToken() throws IOException {

        char c = 0;
        int rval = reader.read();

        int verbCmdListeningPos = 0;

        while (rval != PeekReader.EOF || !readers.empty()) {
            if (rval == PeekReader.EOF) {
                // take next reader if this one is empty
                reader = readers.pop();
                rval = reader.read();
                continue;
            }

            c = (char) rval;

            if (!(c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')) {
                if (sb.length() == 0) {
                    sb.append(c);
                } else {
                    reader.unread(1);
                }
                break;
            } else if (c == VERB_CMDNAME[verbCmdListeningPos]) {
                verbCmdListeningPos++;
                if (verbCmdListeningPos == VERB_CMDNAME.length) {
                    sb.append(c);
                    break;
                }
            } else {
                verbCmdListeningPos = 0;
            }

            sb.append(c);
            rval = reader.read();
        }
        lastTokenSequence = sb.toString();
        lastTokenId = Token.OTHER;
        sb.delete(0, sb.length());
        return lastTokenId;
    }

    /**
     * Get the next token using the verb command's body tokenizing rules.
     * 
     * @return the token id (Token.OTHER for verb command body)
     * @throws IOException
     *             on IO problems
     */
    public Token getNextVerbCmdContent() throws IOException {
        char c = 0;
        int rval = reader.read();
        char delim = 0;

        while (rval != PeekReader.EOF || !readers.empty()) {
            if (rval == PeekReader.EOF) {
                // take next reader if this one is empty
                reader = readers.pop();
                rval = reader.read();
                continue;
            }

            c = (char) rval;

            if (delim == 0) {
                delim = c;
            } else if (c == delim) {
                break;
            } else {
                sb.append(c);
            }

            rval = reader.read();
        }

        lastTokenSequence = sb.toString();
        lastTokenId = Token.OTHER;
        sb.delete(0, sb.length());
        return lastTokenId;
    }

    /**
     * @return the next char from the input - note this is only a peek, not a
     *         read
     * @throws IOException
     *             on IO errors
     */
    public String peekNextChar() throws IOException {
        final int rval = reader.peek();
        if (rval != PeekReader.EOF) {
            return "" + (char) rval;
        }
        return null;
    }

    /**
     * Skip one character of the input.
     * 
     * @throws IOException
     *             on IO errors
     */
    public void skipOneChar() throws IOException {
        reader.read();
    }

    /**
     * Ungets the last token, so that the sequence of the last token will be
     * subject to tokenizing again on the next call to #getNextToken(). Can only
     * be called if getNextToken was called prior to this, 2 consecutive calls
     * of ungetLastToken will throw an exception.
     * 
     * @throws TexParserException
     *             if called 2 times in a row
     */
    public void ungetLastToken() throws TexParserException {
        if (lastTokenId != Token.NONE) {
            // bufferedTokens.add(0, new Token(getLastRecognisedTokenSequence()
            // .toCharArray(), lastTokenId));
            insertInput(new StringPeekReader(lastTokenSequence));
        } else {
            throw new TexParserException(
                    "ungetLastToken called 2 times in a row");
        }
        lastTokenId = Token.NONE;
        lastTokenSequence = null;
    }

    /**
     * @return get the character sequence of the last recognized token
     */
    public String getLastTokenSequence() {
        return lastTokenSequence;
    }

    /**
     * @return the enum constant of the last recognized token
     */
    public Token getLastTokenId() {
        return lastTokenId;
    }

    /**
     * Skips everything till the next linebreak, next byte read will be after
     * the linebreak.
     * 
     * @return the content of the line, excluding the linebreak
     * @throws IOException
     *             on IO problems
     */
    public String skipLine() throws IOException {
        final StringBuffer stringBuffer = new StringBuffer(64);

        final char lbrUnix = 0x0a;
        final char lbrMac = 0x0d;

        int rval = reader.read();
        while (rval != PeekReader.EOF || !readers.empty()) {
            if (rval == PeekReader.EOF) {
                // take next reader if this one is empty
                reader = readers.pop();
                rval = reader.read();
                continue;
            }

            if ((char) rval == lbrMac) {
                if (reader.peek() == lbrUnix) {
                    // windows newline
                    reader.read();
                }
                break;
            } else if ((char) rval == lbrUnix) {
                break;
            }
            stringBuffer.append((char) rval);
            rval = reader.read();
        }
        return stringBuffer.toString();
    }

    /**
     * This will treat the passed String like it's located at the current
     * reading position of the tokenizer. After the string is tokenized by
     * subsequent calls to getNextToken(), the tokenizer will seamlessly
     * continue reading its old input source(s).
     * 
     * @param input
     *            the input to be inserted
     */
    public void insertInput(final PeekReader input) {
        if (reader != null) {
            // there is already a reader, delay it
            readers.push(reader);
        }
        // current reader = new input
        reader = input;
    }
}
