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
package org.texconverter.renderer.handlers.impl;

import org.apache.commons.lang.StringEscapeUtils;
import org.texconverter.dom.IndexMarkReference;
import org.texconverter.dom.LstListing;
import org.texconverter.dom.Node;
import org.texconverter.dom.Reference;
import org.texconverter.dom.TextContent;
import org.texconverter.dom.Verbatim;

/**
 * This is the {@link org.texconverter.renderer.handlers.EscapeHandler} for
 * HTML.
 * 
 * @author sgodau
 * @author tfrana
 */
public class HtmlEscapeHandler extends BasicEscapeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public Node escapeNode(final Node node) {

        if (node instanceof LstListing || node instanceof Verbatim) {
            ((TextContent) node)
                    .setContent(escapeVerbatimText(((TextContent) node)
                            .getContent()));
        } else if (node instanceof TextContent) {
            ((TextContent) node).setContent(escapeString(((TextContent) node)
                    .getContent()));
        }

        if (node instanceof Reference) {
            ((Reference) node).setReferencedLabel(escapeUrl(((Reference) node)
                    .getReferencedLabel()));
        }
        if (node instanceof IndexMarkReference
                && ((IndexMarkReference) node).getType().equals(
                        IndexMarkReference.IndexRefType.RANGE)) {
            ((IndexMarkReference) node)
                    .setReferencedLabelStop(escapeUrl(((IndexMarkReference) node)
                            .getReferencedLabelStop()));
        }

        return node;
    }

    private String escapeVerbatimText(final String verbatimText) {
        final StringBuffer result = new StringBuffer();
        if (verbatimText != null) {
            char tmpChar;
            for (int i = 0; i < verbatimText.length(); i++) {
                tmpChar = verbatimText.charAt(i);
                if (tmpChar == ' ') {
                    result.append("&nbsp;");
                } else if (tmpChar == '\n') {
                    result.append("<br/>");
                } else if (tmpChar == '\t') {
                    result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                } else {
                    result.append(escapeString("" + tmpChar));
                }
            }
        }
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String escapeString(final String string) {
        return StringEscapeUtils.escapeHtml(string);
    }
}
