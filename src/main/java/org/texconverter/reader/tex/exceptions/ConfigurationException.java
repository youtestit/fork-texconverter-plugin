/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:18 $
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
package org.texconverter.reader.tex.exceptions;

/**
 * Exception caused by configuration errors.
 * 
 * @author tfrana
 */
public class ConfigurationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            the message
     * @param cause
     *            causing exception
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            the message
     * @see Exception#Exception(java.lang.String)
     */
    public ConfigurationException(final String message) {
        super(message);
    }

    /**
     * @param cause
     *            causing exception
     * @see Exception#Exception(java.lang.Throwable)
     */
    public ConfigurationException(final Throwable cause) {
        super(cause);
    }

}
