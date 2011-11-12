/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:21 $
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
package org.texconverter.dom;

/**
 * A node indicating that all its childs (except other alignments and their
 * childs) are aligned in some way.
 * 
 * @author tfrana
 */
public interface AlignedNode extends NodeWithChilds {

    /**
     * Possible alignments.
     * 
     * @author tfrana
     */
    public static enum Alignment {
        /** Left-aligned. */
        LEFT,
        /** Centered. */
        CENTER,
        /** Right-aligned. */
        RIGHT;
    }

    /**
     * @return alignment of this node
     */
    AlignedNode.Alignment getAlignment();

    /**
     * @param alignment
     *            alignment of this node
     */
    void setAlignment(AlignedNode.Alignment alignment);
}
