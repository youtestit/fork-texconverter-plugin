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
package org.texconverter.dom.impl;

import java.util.List;

import org.texconverter.dom.Node;
import org.texconverter.dom.Referencable;
import org.texconverter.dom.Reference;

/**
 * @author tfrana
 * @see org.texconverter.dom.Reference
 */
public class ReferenceImpl extends AbstractNode implements Reference {

    private RefStyle style = null;

    /**
     * Will be only be set in postprocessing!
     */
    protected Referencable referencedNode = null;

    protected String referencedLabel = null;

    /**
     * @param referencedLabel
     *            label of the referenced node
     * @param style
     *            the reference style
     */
    public ReferenceImpl(final String referencedLabel, final RefStyle style) {
        this.referencedLabel = referencedLabel;
    }

    /**
     * {@inheritDoc}
     */
    public RefStyle getStyle() {
        return style;
    }

    /**
     * @param style
     *            the reference style
     */
    public void setStyle(final RefStyle style) {
        this.style = style;
    }

    /**
     * {@inheritDoc}
     */
    public Referencable getReferencedNode() {
        return referencedNode;
    }

    /**
     * @param referencedNode
     *            the referenced node
     */
    public void setReferencedNode(final Referencable referencedNode) {
        this.referencedNode = referencedNode;
        this.referencedLabel = referencedNode.getLabel();
    }

    /**
     * {@inheritDoc}
     */
    public String getReferencedLabel() {
        // label is stored separately because the referencedNode can only be
        // determined in postprocessing, by the referencedLabel, and it
        // needs eventually to be escaped separately on rendering.
        /*
         * label may change later, how do we add it?
         */
        return referencedLabel;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setReferencedLabel(final String referencedLabel) {
        this.referencedLabel = referencedLabel;
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getReferenceText() {
        return referencedNode.getReferenceText();
    }
}
