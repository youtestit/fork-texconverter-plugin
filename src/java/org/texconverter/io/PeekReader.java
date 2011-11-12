/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:35 $
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
package org.texconverter.io;

import java.io.IOException;

/**
 * @author tfrana
 */
public interface PeekReader {

    /**
     * Indicates that end of the input is reached.
     */
    int EOF = -1;

    /**
     * How many characters can maximally be unread.
     */
    int MAX_UNREAD = 200;

    /** System-specific line-separator. */
    String LINEBREAK = System.getProperty("line.separator");

    /**
     * Read next input char (advancing the reader position).
     * 
     * @return the next char of the input source or {@link PeekReader#EOF}
     * @throws IOException
     *             on IO problems
     */
    int read() throws IOException;

    /**
     * Read next input char (not advancing reader position).
     * 
     * @return the next char of the input source or {@link PeekReader#EOF}
     * @throws IOException
     *             on IO problems
     */
    int peek() throws IOException;

    /**
     * Rewind the reader position by <code>length</code> chars.
     * 
     * @param length
     *            how many chars to rewind, maximum is
     *            {@link PeekReader#MAX_UNREAD}
     * @return how many chars have been unread
     * @throws IOException
     *             on IO problems
     */
    int unread(int length) throws IOException;
}
