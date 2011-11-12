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

/**
 * Token types, used by tokenizer and parsers.
 * 
 * @author tfrana
 */
public enum Token {
    /** Not a token (e.g. end of file). */
    NONE,
    /** '\'. */
    COMMAND,
    /** '{'. */
    BLOCKOPEN,
    /** '}'. */
    BLOCKCLOSE,
    /** '['. */
    OPTARGBLOCKOPEN,
    /** ']'. */
    OPTARGBLOCKCLOSE,
    /** An empty line (2 linebreaks [+ eventual whitspace]). */
    NEWPARAGRAPH,
    /** '&'. */
    AMPERSAND,
    /** '%'. */
    COMMENT,
    /** Any content that doesn't match one of the other tokens. */
    OTHER,
    /** A sequence of whitespace (several whitespaces seen as one token). */
    WHITESPACE;
}
