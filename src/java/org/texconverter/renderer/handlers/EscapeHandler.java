/**
 * $Revision: 1.4 $
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
 * created: 02.08.2006 tfrana
 */
package org.texconverter.renderer.handlers;

import org.texconverter.dom.Node;

/**
 * This is the interface for all EscapeHandlers.
 * 
 * @see org.texconverter.renderer.handlers.impl.HtmlEscapeHandler
 * @see org.texconverter.renderer.handlers.impl.XdocEscapeHandler
 * @see org.texconverter.renderer.handlers.impl.WikiEscapeHandler
 * @author sgodau, tfrana
 */
public interface EscapeHandler {

    /**
     * Escape contents of a node.
     * 
     * @param node
     *            the node to escape
     * @return the node with escaped contents
     */
    Node escapeNode(Node node);

    /**
     * Escape a string.
     * 
     * @param string
     *            a string
     * @return the escaped string
     */
    String escapeString(String string);
}
