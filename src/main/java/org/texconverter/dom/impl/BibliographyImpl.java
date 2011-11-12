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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.texconverter.dom.Bibliography;
import org.texconverter.dom.Node;
import org.texconverter.reader.tex.Helper;

/**
 * @author tfrana
 */
public class BibliographyImpl extends NodeWithChildsImpl implements
        Bibliography {

    private Map<String, BibliographyItemImpl> bibEntries = new HashMap<String, BibliographyItemImpl>();

    private int bibItemIncrement = 1;

    private List<Node> title;

    /**
     * @param title
     *            the title of the bibliography
     */
    public BibliographyImpl(final String title) {
        this.title = Helper.getNodeListFromPlainText(title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(final AbstractNode child) {
        if (child instanceof BibliographyItemImpl) {
            BibliographyItemImpl bibItem = (BibliographyItemImpl) child;
            if (bibItem.getReferenceText() == null) {
                bibItem.setReferenceText(Helper
                        .getNodeListFromPlainText(Integer
                                .toString(bibItemIncrement++)));
            }

            bibEntries.put(bibItem.getLabel(), bibItem);
        }
        super.addChild(child);
    }

    /**
     * Get an entry (this is NOT looked for in the whole library file, but must
     * be an actual child of this Node (an entry that will actually appear in
     * the output document)) from the bibliography.
     * 
     * @param bibKey
     *            The key of the bibliography entry.
     * @return The entry or null, if none was found.
     */
    public BibliographyItemImpl getEntry(final String bibKey) {
        return bibEntries.get(bibKey);
    }

    /**
     * Copies the content from another bibliography node.
     * 
     * @param bibliography
     *            the bibliography to copy the content from
     */
    public void copyContentFrom(final BibliographyImpl bibliography) {
        bibEntries = bibliography.bibEntries;
        bibItemIncrement = bibliography.bibItemIncrement;
        children = bibliography.children;
        title = bibliography.title;
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(final List<Node> title) {
        this.title = title;
    }

}
