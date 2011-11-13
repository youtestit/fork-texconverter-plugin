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

        if (node instanceof TextContent) {
            final TextContent currentNode =(TextContent) node;
            currentNode.setContent(escapeString(currentNode.getContent()));
        }

        if (node instanceof Reference) {
            final Reference currentNode =(Reference) node;
            currentNode.setReferencedLabel(escapeUrl(currentNode.getReferencedLabel()));
        }
        
        if (node instanceof IndexMarkReference 
            && ((IndexMarkReference) node).getType().equals(IndexMarkReference.IndexRefType.RANGE)) {
            
            final IndexMarkReference currentNode =(IndexMarkReference) node;
            currentNode.setReferencedLabelStop(escapeUrl(currentNode.getReferencedLabelStop()));
        }

        return node;
    }

    private String escapeVerbatimText(final String verbatimText) {
        return verbatimText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String escapeString(final String string) {
        if(string!=null && (string.contains("<script")||string.contains("script>"))){
            return string;
        }
        
        return StringEscapeUtils.escapeHtml(string);
    }
}
