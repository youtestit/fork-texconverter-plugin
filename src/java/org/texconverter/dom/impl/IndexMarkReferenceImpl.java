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
 * created: 27.09.2006 tfrana
 */
package org.texconverter.dom.impl;

import org.texconverter.dom.IndexMarkReference;

/**
 * @author tfrana
 */
public class IndexMarkReferenceImpl extends ReferenceImpl implements
        IndexMarkReference {

    private IndexMarkImpl stopIndexMark = null;

    private String stopIndexLabel = null;

    private IndexRefType type;

    /**
     * Constructs an IndexMarkReference of style {@link IndexRefType#SINGLE}.
     * 
     * @param indexMark
     *            the referenced mark
     */
    public IndexMarkReferenceImpl(final IndexMarkImpl indexMark) {
        super(indexMark.getLabel(), RefStyle.BY_LINK);
        setReferencedNode(indexMark);
        type = IndexRefType.SINGLE;
    }

    /**
     * Constructs an IndexMarkReference of style {@link IndexRefType#RANGE}.
     * 
     * @param indexMarkStart
     *            the mark denoting the start of the range
     * @param indexMarkStop
     *            the mark denoting the end of the range
     */
    public IndexMarkReferenceImpl(final IndexMarkImpl indexMarkStart,
            final IndexMarkImpl indexMarkStop) {
        super(indexMarkStart.getLabel(), RefStyle.BY_LINK);
        setReferencedNode(indexMarkStart);
        stopIndexMark = indexMarkStop;
        if (indexMarkStop != null) {
            stopIndexLabel = indexMarkStop.getLabel();
        }
        type = IndexRefType.RANGE;
    }

    /**
     * {@inheritDoc}
     */
    public IndexRefType getType() {
        return type;
    }

    /**
     * @param type
     *            the type of the index mark reference
     */
    public void setType(final IndexRefType type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public IndexMarkImpl getReferencedNodeStop() {
        return stopIndexMark;
    }

    /**
     * {@inheritDoc}
     */
    public String getReferencedLabelStop() {
        return stopIndexLabel;
    }

    /**
     * {@inheritDoc}
     */
    public void setReferencedLabelStop(final String referencedLabelStop) {
        stopIndexLabel = referencedLabelStop;
    }

    /**
     * @param referencedNodeStop
     *            the index mark which denotes the end of the range of this
     *            reference
     */
    public void setReferencedNodeStop(final IndexMarkImpl referencedNodeStop) {
        stopIndexMark = referencedNodeStop;
        stopIndexLabel = referencedNodeStop.getLabel();
    }
}
