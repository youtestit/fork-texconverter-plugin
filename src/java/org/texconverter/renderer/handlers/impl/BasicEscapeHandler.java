/**
 * $Revision: 1.2 $
 * $Date: 2006/09/28 13:13:40 $
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
 * created: 28.09.2006 tfrana
 */
package org.texconverter.renderer.handlers.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.texconverter.dom.Node;
import org.texconverter.renderer.handlers.EscapeHandler;

/**
 * @author tfrana
 */
public class BasicEscapeHandler implements EscapeHandler {

    /**
     * {@inheritDoc}
     */
    public Node escapeNode(final Node node) {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    public String escapeString(final String string) {
        return string;
    }

    /**
     * Escape an Url.
     * 
     * @param url
     *            the url
     * @return the escaped url
     */
    public String escapeUrl(final String url) {
        if (url == null) {
            return null;
        }
        try {
            return URLEncoder.encode(url, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("escapeUrl -> unsupported encoding", e);
        }
    }
}
