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
 * created: 26.09.2006 tfrana
 */
package org.texconverter.dom.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.texconverter.dom.IndexEntry;
import org.texconverter.dom.Node;

/**
 * @author tfrana
 */
public class IndexEntryImpl extends NodeWithChildsImpl implements IndexEntry {
    private String term;

    private int level;

    private String sortKey = null;

    private List<IndexMarkImpl> markers = new ArrayList<IndexMarkImpl>();

    private List<IndexMarkReferenceImpl> references = new ArrayList<IndexMarkReferenceImpl>();

    /**
     * {@inheritDoc}
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level
     *            the level of this entry in the index, where 0 is the base
     *            level, 1 is one below etc
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    /**
     * @param sortKey
     *            the string that should be used to sort the entry in the index
     */
    public void setSortKey(final String sortKey) {
        this.sortKey = sortKey;
    }

    /**
     * {@inheritDoc}
     */
    public String getTerm() {
        return term;
    }

    /**
     * @param term
     *            the term of the entry
     */
    public void setTerm(final String term) {
        this.term = term;
        if (sortKey == null) {
            sortKey = term;
        }
    }

    /**
     * Adds a reference to some index marker to the index entry.
     * 
     * @param reference
     *            an index marker reference
     */
    public void addReference(final IndexMarkReferenceImpl reference) {
        references.add(reference);
    }

    /**
     * Same as {@link #getReferencesText()}, but returns the plain list of
     * references without text delimiters.
     * 
     * @return all references to index markers for the entry
     */
    public List<IndexMarkReferenceImpl> getReferences() {
        return references;
    }

    /**
     * Sort a list of {@link IndexEntryImpl}.
     * 
     * @param entries
     *            the entries
     */
    public static void sortEntries(final List<Node> entries) {
        // List<Node> because child IndexEntries are in children

        Comparator<Node> comparator = new Comparator<Node>() {
            public int compare(Node n1, Node n2) {
                int retVal = 0;
                if (n1 instanceof IndexEntryImpl
                        && n2 instanceof IndexEntryImpl) {
                    retVal = ((IndexEntryImpl) n1).sortKey
                            .compareTo(((IndexEntryImpl) n2).sortKey);
                }
                return retVal;
            }
        };
        Collections.sort(entries, comparator);

        Iterator<Node> it = entries.iterator();
        while (it.hasNext()) {
            IndexEntryImpl entry = (IndexEntryImpl) it.next();
            if (entry.getChildren() != null && entry.getChildren().size() > 0) {
                sortEntries(entry.getChildren());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getReferencesText() {
        List<Node> referenceText = new ArrayList<Node>();

        Iterator<IndexMarkReferenceImpl> it = references.iterator();
        while (it.hasNext()) {
            referenceText.add(it.next());
            referenceText.add(new TextImpl(", "));
        }

        if (referenceText.size() > 0) {
            referenceText.remove(referenceText.size() - 1);
        }

        return referenceText;

    }
}
