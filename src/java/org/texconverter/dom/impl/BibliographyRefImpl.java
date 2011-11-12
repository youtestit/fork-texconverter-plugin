/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:20 $
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
 * created: 10.08.2006 tfrana
 */
package org.texconverter.dom.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.Referencable;
import org.texconverter.reader.tex.Helper;

/**
 * @author tfrana
 */
public class BibliographyRefImpl extends ReferenceImpl {

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory
            .getLog(BibliographyRefImpl.class);

    private String note;

    /**
     * @param referencedLabel
     *            the library key of the referenced bibliography entry
     * @param note
     *            an additional note
     */
    public BibliographyRefImpl(final String referencedLabel, final String note) {
        super(referencedLabel, RefStyle.BY_REFTEXT);
        this.note = note;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BibliographyItemImpl getReferencedNode() {
        return (BibliographyItemImpl) referencedNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReferencedNode(final Referencable referencedNode) {
        if (!(referencedNode instanceof BibliographyItemImpl)) {
            throw new IllegalArgumentException(
                    "The referenced node of a BibliographyRef may only be a BibliographyItem");
        }
        super.setReferencedNode(referencedNode);
    }

    /**
     * @return the label of the bibliography entry this node refers to
     */
    @Override
    public String getReferencedLabel() {
        return referencedLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefStyle getStyle() {
        return RefStyle.BY_REFTEXT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Node> getReferenceText() {
        List<Node> refText;

        if (referencedNode == null) {
            refText = Helper.getNodeListFromPlainText("[non-existant bib-ref: "
                    + referencedLabel + "]");
        } else {

            if (note != null) {
                List<Node> oriRefText = referencedNode.getReferenceText();
                refText = new ArrayList<Node>(oriRefText);
                // TODO this is just a hack
                TextImpl txt = (TextImpl) refText.get(refText.size() - 1);

                StringBuffer c = new StringBuffer(txt.getContent());
                if (c.charAt(c.length() - 1) == ']') {
                    c.insert(c.length() - 1, ", " + note);
                    refText.remove(refText.size() - 1);
                    refText.add(new TextImpl(c.toString()));
                } else {
                    log.error("getReferenceText -> Unexpected bibliography"
                            + " reference text format (label "
                            + referencedLabel + "), ignoring reference note");
                }
            } else {
                refText = referencedNode.getReferenceText();
            }
        }
        return refText;
    }
}
