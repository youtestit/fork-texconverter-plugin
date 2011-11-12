/**
 * $Revision: 1.2 $
 * $Date: 2006/09/28 12:08:29 $
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
package org.texconverter.dom;

import java.util.List;

/**
 * Nodes implementing this interface do reference a {@link Referencable} node.
 * The {@link RefStyle} tells how this reference should be represented in the
 * text.
 * 
 * @author tfrana
 */
public interface Reference extends Node {

    /**
     * Reference styles indicating how it should be represented in text form.
     * 
     * @author tfrana
     */
    public static enum RefStyle {
        /**
         * The text for the reference should contain a textual representation of
         * the referenced target. In TeX, references created by "\ref" are given
         * this style. Note that due to that, most references will return only a
         * counter or an identifier from the referenced entity from
         * {@link Reference#getReferenceText()}.
         * 
         * @see Reference#getReferenceText()
         */
        BY_REFTEXT,
        /**
         * Main goal of the textual representation of the reference is to enable
         * the user to quickly find the referenced part, as what is being
         * referenced is already part of the textual context the reference is
         * embedded in. For documents on paper this would be the page number,
         * for electronic documents links could be used if available. In TeX,
         * references created by "\pageref" are given this style.
         */
        BY_LINK;
    }

    /**
     * @return the style of the reference (how it should be represented in text)
     */
    RefStyle getStyle();

    /**
     * @return the referenced node
     */
    Referencable getReferencedNode();

    /**
     * @param referencedNode
     *            the referenced node
     */
    void setReferencedNode(Referencable referencedNode);

    /**
     * @return the label string which uniquely identifies the referenced Node in
     *         the document
     * @see Referencable#getLabel()
     */
    String getReferencedLabel();

    /**
     * @param label
     *            the referenced label
     */
    void setReferencedLabel(String label);

    /**
     * Get the text representation of the reference. Usually this is some kind
     * of key of the referenced node (e.g. a counter for sections, tables etc,
     * plain or hierarchical like "1.5.3", or some other string). Similar to the
     * label, only this isn't necessarly unique, and is intended to be
     * human-readable. If the style of the reference is set to
     * {@link RefStyle#BY_REFTEXT}, this should be used to represent the
     * reference in the text.
     * 
     * @return the reference text
     */
    List<Node> getReferenceText();
}