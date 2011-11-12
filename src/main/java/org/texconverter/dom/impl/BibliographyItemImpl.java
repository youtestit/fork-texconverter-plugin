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
 * created: 08.08.2006 tfrana
 */
package org.texconverter.dom.impl;

import java.util.List;

import org.texconverter.dom.BibliographyItem;
import org.texconverter.dom.Node;
import org.texconverter.dom.Referencable;

/**
 * @author tfrana
 */
public class BibliographyItemImpl extends NodeWithChildsImpl implements
        BibliographyItem, Referencable {

    private String bibEntryKey = null;

    private List<Node> referenceText = null;

    /**
     * @param bibEntryKey
     *            the key which uniquely identifies this bibliography entry
     */
    public BibliographyItemImpl(final String bibEntryKey) {
        this.bibEntryKey = bibEntryKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canEndNodes() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return bibEntryKey;
    }

    /**
     * {@inheritDoc}
     */
    public void setLabel(final String label) {
        bibEntryKey = label;
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getReferenceText() {
        return referenceText;
    }

    /**
     * @param referenceText
     *            the reference key
     */
    public void setReferenceText(final List<Node> referenceText) {
        this.referenceText = referenceText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEndedBy(final Node node) {
        boolean isEnded = false;
        if (node instanceof BibliographyItemImpl) {
            isEnded = true;
        }
        return isEnded;
    }
}
