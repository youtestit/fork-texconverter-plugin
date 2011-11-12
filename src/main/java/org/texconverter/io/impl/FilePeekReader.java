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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.texconverter.io.PeekReader;

/**
 * Simple Filereader that supports peeking (by another buffer).
 * 
 * @author tfrana
 */
public class FilePeekReader extends FileReader implements PeekReader {

    private static final int BUFFERSIZE = 4096;

    private final char[] buffer = new char[BUFFERSIZE];

    private int endOfBuffer = 0;

    // set bufferpos to the end of the buffer, so it will be filled upon the
    // next read/peek
    private int bufferpos = endOfBuffer;

    /**
     * @param file
     *            the file to read from
     * @throws FileNotFoundException
     *             if file was not found
     */
    public FilePeekReader(final File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * {@inheritDoc}
     */
    public int peek() throws IOException {
        if (bufferpos >= endOfBuffer && !fillBuffer()) {
            return PeekReader.EOF;
        }
        return buffer[bufferpos];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        if (bufferpos >= endOfBuffer && !fillBuffer()) {
            return PeekReader.EOF;
        }
        return buffer[bufferpos++];
    }

    /**
     * {@inheritDoc}
     */
    public int unread(final int length) {
        int unread = 0;
        if (length > PeekReader.MAX_UNREAD) {
            throw new IllegalArgumentException(
                    "Can not safely rewind longer than "
                            + PeekReader.MAX_UNREAD + " chars");
        }

        unread = Math.min(length, bufferpos);
        bufferpos -= unread;

        return unread;
    }

    /**
     * Fills the buffer from the file.
     * 
     * @return if there were still bytes left in the file
     * @throws IOException
     */
    private boolean fillBuffer() throws IOException {
        final int charsToKeep = Math.min(PeekReader.MAX_UNREAD, endOfBuffer);
        if (charsToKeep > 0) {
            System.arraycopy(buffer, endOfBuffer - charsToKeep, buffer, 0,
                    charsToKeep);
        }
        bufferpos = charsToKeep;
        if (bufferpos < 0) {
            bufferpos = 0;
        }

        endOfBuffer = super.read(buffer, bufferpos, buffer.length - bufferpos);

        if (endOfBuffer == -1) {
            return false;
        }
        endOfBuffer += bufferpos;
        return true;
    }
}
