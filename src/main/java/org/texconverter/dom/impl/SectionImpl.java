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
 * created: 02.08.2006 tfrana
 */
package org.texconverter.dom.impl;

import java.util.List;

import org.texconverter.dom.Node;
import org.texconverter.dom.Section;
import org.texconverter.reader.tex.Helper;

/**
 * @author tfrana
 */
public class SectionImpl extends NodeWithChildsImpl implements Section {

    private Section.Lvl level;

    private String counter = "";

    private List<Node> title = null;

    private List<Node> tocTitle = null;

    private String label = null;

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
    @Override
    public boolean isEndedBy(final Node node) {
        // lower enum ordinal means higher in hierarchy
        if (node instanceof Section
                && level.compareTo(((Section) node).getLevel()) >= 0) {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getReferenceText() {
        return Helper.getNodeListFromPlainText(counter);
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

    /**
     * {@inheritDoc}
     */
    public List<Node> getTocTitle() {
        return tocTitle;
    }

    /**
     * {@inheritDoc}
     */
    public void setTocTitle(final List<Node> tocTitle) {
        this.tocTitle = tocTitle;
    }

    /**
     * {@inheritDoc}
     */
    public Section.Lvl getLevel() {
        return level;
    }

    /**
     * {@inheritDoc}
     */
    public void setLevel(final Section.Lvl level) {
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return label;
    }

    /**
     * {@inheritDoc}
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * @return the counter of this section
     */
    public String getCounter() {
        return counter;
    }

    /**
     * @param counter
     *            the counter of this section
     */
    public void setCounter(final String counter) {
        this.counter = counter;
    }
}
