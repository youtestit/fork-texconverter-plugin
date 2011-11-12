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

import java.util.List;

import org.texconverter.dom.IndexEntry;
import org.texconverter.dom.IndexMark;
import org.texconverter.dom.Node;

/**
 * @author tfrana
 */
public class IndexMarkImpl extends AbstractNode implements IndexMark {

    private IndexEntry indexEntry;

    private String label;

    private Integer counter;

    /**
     * {@inheritDoc}
     */
    public IndexEntry getIndexEntry() {
        return indexEntry;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        if (counter != null) {
            return label + counter;
        }
        return label;
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getReferenceText() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * @return the counter of the mark - for each reference to the same index
     *         entry this counter is incremented
     */
    public Integer getCounter() {
        return counter;
    }

    /**
     * @param counter
     *            the count
     * @see #getCounter()
     */
    public void setCounter(final Integer counter) {
        this.counter = counter;
    }
}
