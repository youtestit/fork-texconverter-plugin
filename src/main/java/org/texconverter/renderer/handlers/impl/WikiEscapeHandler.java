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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.texconverter.dom.IndexMarkReference;
import org.texconverter.dom.LstListing;
import org.texconverter.dom.Node;
import org.texconverter.dom.Reference;
import org.texconverter.dom.TextContent;
import org.texconverter.dom.Url;
import org.texconverter.dom.Verbatim;

/**
 * This is a {@link org.texconverter.renderer.handlers.EscapeHandler} that
 * escapes nothing.
 * 
 * @author sgodau
 */
public class WikiEscapeHandler extends BasicEscapeHandler {

    private static final Matcher[] ESCAPEMATCHERS;

    private static final Matcher CAMELCASEMATCHER = Pattern.compile(
            "(\\W+|^)([A-Z]+[0-9a-z]+[A-Z]+)", Pattern.MULTILINE).matcher("");

    static {
        final String[] escapeStrings = { "\\[+", "\\]+", "\\++", "\\*+",
                "\\|+", "#+", "@+", "/+", ":+", ">+" };

        ESCAPEMATCHERS = new Matcher[escapeStrings.length];
        for (int i = 0; i < escapeStrings.length; i++) {
            ESCAPEMATCHERS[i] = Pattern.compile(escapeStrings[i],
                    Pattern.MULTILINE | Pattern.DOTALL).matcher("");
        }
    }

    private final StringBuffer sb = new StringBuffer();

    /**
     * {@inheritDoc}
     */
    @Override
    public Node escapeNode(final Node node) {

        if (node instanceof LstListing || node instanceof Verbatim
                || node instanceof Url) {
            // no need to escape
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String escapeString(final String aString) {
        String newString = aString;

        // escape special Wiki character sequences
        sb.append(newString);
        for (int i = 0; i < ESCAPEMATCHERS.length; i++) {
            ESCAPEMATCHERS[i].reset(sb.toString());
            sb.delete(0, sb.length());

            while (ESCAPEMATCHERS[i].find()) {
                ESCAPEMATCHERS[i].appendReplacement(sb, "``$0``");
            }
            ESCAPEMATCHERS[i].appendTail(sb);
        }

        // escape Strings in "camelcase" (e.g. "ThisIsAWikiLink")
        CAMELCASEMATCHER.reset(sb.toString());
        sb.delete(0, sb.length());
        while (CAMELCASEMATCHER.find()) {
            CAMELCASEMATCHER.appendReplacement(sb, "$1!$2");
        }
        CAMELCASEMATCHER.appendTail(sb);

        newString = sb.toString();
        sb.delete(0, sb.length());

        return newString;
    }
}
