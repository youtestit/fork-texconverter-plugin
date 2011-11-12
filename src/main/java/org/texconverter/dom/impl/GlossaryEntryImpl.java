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
 * created: 14.08.2006 tfrana
 */
package org.texconverter.dom.impl;

import java.util.List;

import org.texconverter.dom.GlossaryEntry;
import org.texconverter.dom.Node;
import org.texconverter.reader.tex.Helper;

/**
 * @author tfrana
 */
public class GlossaryEntryImpl extends AbstractNode implements GlossaryEntry,
        Comparable<GlossaryEntryImpl> {

    private List<Node> name;

    private List<Node> description;

    private String sortKey;

    /**
     * @param name
     *            The name of the glossary entry (the term to be explained).
     * @param description
     *            The description.
     * @param sortKey
     *            The key by which this entry should be sorted in the glossary.
     */
    public GlossaryEntryImpl(final List<Node> name,
            final List<Node> description, final String sortKey) {
        this.name = name;
        this.description = description;

        this.sortKey = Helper.getPlainTextFromNodeList(name);
        if (sortKey != null) {
            this.sortKey = sortKey + this.sortKey;
        }
    }

    /**
     * @return the description for the glossary entry
     */
    public List<Node> getDescription() {
        return this.description;
    }

    /**
     * @param description
     *            the description for the glossary entry
     */
    public void setDescription(final List<Node> description) {
        this.description = description;
    }

    /**
     * @return the name of the glossary entry
     */
    public List<Node> getName() {
        return this.name;
    }

    /**
     * @param name
     *            the name of the glossary entry
     */
    public void setName(final List<Node> name) {
        this.name = name;
    }

    private String getSortKey() {
        return sortKey;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(final GlossaryEntryImpl o) {
        return sortKey.compareTo(o.getSortKey());
    }

}
