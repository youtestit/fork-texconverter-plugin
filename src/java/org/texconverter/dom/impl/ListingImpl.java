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

import org.texconverter.dom.Listing;
import org.texconverter.dom.Node;
import org.texconverter.reader.tex.Helper;

/**
 * @author tfrana
 * @see org.texconverter.dom.Listing
 */
public class ListingImpl extends Environment implements Listing {

    private String label = null;

    private ListingStyles style = null;

    private String counter = null;

    /**
     * @param name
     *            the environment name
     */
    public ListingImpl(final String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    public ListingStyles getStyle() {
        return style;
    }

    /**
     * {@inheritDoc}
     */
    public void setStyle(final ListingStyles style) {
        this.style = style;
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
     * {@inheritDoc}
     */
    public List<Node> getReferenceText() {
        return Helper.getNodeListFromPlainText(counter);
    }
}
