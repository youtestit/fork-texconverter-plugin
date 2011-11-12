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

import org.texconverter.dom.Node;
import org.texconverter.dom.Section;

/**
 * Abstract parent class of all nodes.
 * 
 * @author tfrana
 */
public abstract class AbstractNode implements Node {

    protected NodeWithChildsImpl parent = null;

    /**
     * Used internally for tree building purposes.
     * 
     * @return true if this node can end other nodes, false otherwise
     */
    public boolean canEndNodes() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public NodeWithChildsImpl getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent node
     */
    public void setParent(final NodeWithChildsImpl parent) {
        this.parent = parent;
    }

    /**
     * In the line of parents, try to find a node of a certain class.
     * 
     * @param clasz
     *            the <code>class</code> of the code to look for
     * @return a parent node of <code>class</code> clasz or <code>null</code>
     */
    public NodeWithChildsImpl getParent(final Class clasz) {
        NodeWithChildsImpl wantedParent = null;

        NodeWithChildsImpl parentNode = getParent();
        while (parentNode != null) {
            if (clasz.isInstance(parentNode)) {
                wantedParent = parentNode;
                break;
            }
            parentNode = parentNode.getParent(clasz);
        }

        if (clasz.isInstance(parentNode)) {
            wantedParent = parentNode;
        }

        return wantedParent;
    }

    /**
     * {@inheritDoc}
     */
    public Section getSection() {
        Section sec = null;
        final Object section = getParent(Section.class);
        if (section != null) {
            sec = (Section) section;
        }
        return sec;
    }

    /**
     * {@inheritDoc}
     */
    public Section getSection(final Section.Lvl level) {
        Section section = null;

        if (this instanceof Section && ((Section) this).getLevel() == level) {
            section = (Section) this;
        } else {

            NodeWithChildsImpl wantedParent = getParent(Section.class);

            while (section == null && wantedParent != null) {
                if (((Section) wantedParent).getLevel() == level) {
                    section = (Section) wantedParent;
                }
                wantedParent = wantedParent.getParent(Section.class);
            }
        }

        return section;
    }
}
