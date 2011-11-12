/**
 * $Revision: 1.3 $
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
package org.texconverter.reader.tex.builder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.texconverter.dom.Node;
import org.texconverter.dom.NodeWithChilds;
import org.texconverter.dom.Section;
import org.texconverter.dom.TypeFace;
import org.texconverter.dom.Verbatim;
import org.texconverter.dom.impl.ParagraphImpl;
import org.texconverter.dom.impl.SectionImpl;
import org.texconverter.dom.impl.TextImpl;
import org.texconverter.reader.tex.Helper;
import org.texconverter.resource.ResourceManager;

/**
 * Provides final formatting of a TeXDocument.
 * 
 * @author tfrana
 */
public class TeXDocFormatter {

    private static String[][] wsreplacements, replacements;

    private static Matcher[] matchers, wsmatchers;

    static {
        wsreplacements = new String[][] { { "(\\n)|~", " " },
                { "\\s{2,}", " " } };

        replacements = new String[][] { 
                { "('')|(´´)|(\"['´`])", "\"" }, 
                { "\\$-\\$", "-" }, { "-{2,3}", "-" }, { "\\$<\\$", "<" }, 
                { "\\$>\\$", ">" }, 
                { "\\\\\"a", "ä" }, { "\\\\\"o", "ö" }, { "\\\\\"u", "ü" }, 
                { "\\\\\"A", "Ä" }, { "\\\\\"O", "Ö" }, { "\\\\\"U", "Ü" } };

        wsmatchers = new Matcher[wsreplacements.length];
        for (int i = 0; i < wsreplacements.length; i++) {
            wsmatchers[i] = Pattern.compile(wsreplacements[i][0],
                    Pattern.MULTILINE | Pattern.DOTALL).matcher("");
        }

        matchers = new Matcher[replacements.length];
        for (int i = 0; i < replacements.length; i++) {
            matchers[i] = Pattern.compile(replacements[i][0],
                    Pattern.MULTILINE | Pattern.DOTALL).matcher("");
        }
    }

    private int depth = -1;

    private List<Integer> indices = new ArrayList<Integer>();

    private ResourceBundle captionNameResources;

    private Section.Lvl toplevelSection;

    private Section.Lvl highestNumberedSectionLvl;

    private Section.Lvl minNumberedSectionLvl;

    /**
     * Format the text in the DOM (all child nodes of <code>node</code>).
     * This includes replacing certain TeX expressions, excess linebreaks and
     * whitespace, removing and joining of Text nodes where applicable, removing
     * of excess Break nodes, etc.
     * 
     * @param node
     *            the basic node whose childs should be formatted (should be the
     *            TeXDocument in most cases)
     * @param topLvlSection
     *            the topmost section level occuring in the document
     * @param highestNumberedSectionLvl
     *            any section higher in hierarchy than this will not have numbers
     *            in their title anymore
     * @param sectionNumberingDepth
     *            starting from (including) highestNumberedSectionLvl, how many
     *            of the lower levels should still be numbered
     */
    public void formatTextDoc(final NodeWithChilds node,
            final Section.Lvl topLvlSection,
            final Section.Lvl highestNumberedSectionLvl,
            final int sectionNumberingDepth) {
        if (captionNameResources == null) {
            captionNameResources = ResourceManager.getInstance()
                    .getLocalizedResource(ResourceManager.RES_STRINGS, null);
        }

        depth = -1;
        this.toplevelSection = topLvlSection;


        if (toplevelSection != null && toplevelSection.compareTo(highestNumberedSectionLvl) > 0) {
            this.highestNumberedSectionLvl = topLvlSection;
        } else {
            this.highestNumberedSectionLvl = highestNumberedSectionLvl;
        }

        minNumberedSectionLvl = Section.Lvl.values()[highestNumberedSectionLvl
                .ordinal()
                + sectionNumberingDepth - 1];

        formatTextRecursive(node);
    }

    private void formatTextRecursive(final NodeWithChilds node) {
        depth++;
        Node child;

        for (int i = 0; i < node.getChildren().size(); i++) {
            child = node.getChild(i);

            if (child instanceof SectionImpl) {
                processSectionNumbering((SectionImpl) child);
            }
            // handleCaption(node);

            if (child instanceof ParagraphImpl
                    && ((ParagraphImpl) child).getNumChildren() == 0) {
                node.getChildren().remove(i--);
            } else if (child instanceof TextImpl
                    && !(child instanceof Verbatim)) {
                final String newTxt = replaceText(((TextImpl) child)
                        .getContent());
                ((TextImpl) child).setContent(newTxt);
            } else if (child instanceof NodeWithChilds) {
                if (depth > indices.size()) {
                    indices.add(Integer.valueOf(i));
                } else {
                    indices.add(depth, Integer.valueOf(i));
                }
                formatTextRecursive((NodeWithChilds) child);
            }
        }
        depth--;
    }

    private void processSectionNumbering(final SectionImpl section) {

        // implement hierarchical section numbering (e.g. "2.3.5")
        final Section.Lvl lvl = section.getLevel();

        if (lvl.compareTo(highestNumberedSectionLvl) >= 0
                && lvl.compareTo(minNumberedSectionLvl) <= 0) {
            // prepend section number
            StringBuffer hierarchicalCounter = new StringBuffer(section
                    .getCounter());

            Section currentParentSection;
            for (int i = lvl.ordinal() - 1; i >= highestNumberedSectionLvl
                    .ordinal(); i--) {
                currentParentSection = section
                        .getSection(Section.Lvl.values()[i]);
                if (currentParentSection == null) {
                    // happens if e.g. a subsubsection is the direct
                    // child of a section, thus there is no parent section
                    // at the directly superiour level
                    hierarchicalCounter.insert(0, "0.");
                } else {
                    hierarchicalCounter.insert(0, Helper
                            .getPlainTextFromNodeList(currentParentSection
                                    .getReferenceText())
                            + ".");
                }
            }

            List<Node> title = section.getTitle();
            if (title == null) {
                title = new ArrayList<Node>();
            }
            title.add(0, new TextImpl(hierarchicalCounter.toString() + " "));
            section.setTitle(title);
            section.setCounter(hierarchicalCounter.toString());
        } else {
            section.setCounter("");
        }
    }

    /**
     * Any replacements for non-command TeX sequences.
     * 
     * @param text
     *            the old text
     * @return the new text
     */
    private String replaceText(final String text) {
        String newTxt = text;

        for (int i = 0; i < wsmatchers.length; i++) {
            wsmatchers[i].reset(newTxt);
            newTxt = wsmatchers[i].replaceAll(wsreplacements[i][1]);
        }

        if (newTxt.length() > 0) {

            for (int i = 0; i < matchers.length; i++) {
                matchers[i].reset(newTxt);
                newTxt = matchers[i].replaceAll(replacements[i][1]);
            }
        }

        return newTxt;
    }

    /**
     * @param parentNode
     * @param index
     * @return next node
     * @deprecated
     */
    @Deprecated
    @SuppressWarnings("unused")
    private Node nextNode(final NodeWithChilds parentNode, final int index) {
        Node nextNode = null;
        int tmpdepth = depth, idx = index;
        NodeWithChilds parent = parentNode;

        while (nextNode == null) {
            if (idx + 1 < parent.getChildren().size()) {
                nextNode = parent.getChild(idx + 1);
            } else {
                if (!(parent instanceof TypeFace)) {
                    // don't look beyond anything but typeface for
                    // textformatting purposes
                    break;
                }
                parent = parent.getParent();
                tmpdepth--;
                if (parent == null) {
                    break;
                }
                idx = indices.get(tmpdepth).intValue();
            }
        }

        return nextNode;
    }

    /**
     * @param parent
     * @param index
     * @return previous node
     * @deprecated
     */
    @Deprecated
    @SuppressWarnings("unused")
    private Node previousNode(final NodeWithChilds parentNode, final int index) {
        Node prevNode = null;
        int tmpdepth = depth, idx = index;
        NodeWithChilds parent = parentNode;

        while (prevNode == null) {
            if (idx - 1 >= 0) {
                prevNode = parent.getChild(idx - 1);
            } else {
                if (!(parent instanceof TypeFace)) {
                    break;
                }
                parent = parent.getParent();
                tmpdepth--;
                if (parent == null) {
                    break;
                }
                idx = indices.get(tmpdepth).intValue();
            }
        }
        return prevNode;
    }

}
