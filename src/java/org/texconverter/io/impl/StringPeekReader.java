/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:39 $
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
package org.texconverter.io.impl;

import org.texconverter.io.PeekReader;

/**
 * Simple reader that uses a String as input.
 * 
 * @author tfrana
 */
public class StringPeekReader implements PeekReader {

    private String string;

    private int pos;

    /**
     * @param string
     *            the String to use as input
     */
    public StringPeekReader(final String string) {
        super();
        this.string = string;
        pos = 0;
    }

    /**
     * {@inheritDoc}
     */
    public int read() {
        if (pos < string.length()) {
            return string.charAt(pos++);
        }
        return PeekReader.EOF;
    }

    /**
     * {@inheritDoc}
     */
    public int peek() {
        if (pos < string.length()) {
            return string.charAt(pos);
        }
        return PeekReader.EOF;
    }

    /**
     * {@inheritDoc}
     */
    public int unread(final int length) {
        final int unread = Math.min(length, pos);
        pos -= unread;
        return unread;
    }

}
