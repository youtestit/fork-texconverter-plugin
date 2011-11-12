/**
 * $Revision: 1.2 $
 * $Date: 2006/09/28 12:08:29 $
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
 * created: 16.08.2006 tfrana
 */
package org.texconverter.dom;

import java.util.List;

/**
 * Nodes implementing this interface can be referenced by {@link Reference}
 * nodes.
 * 
 * @author tfrana
 */
public interface Referencable extends Node {

    /**
     * @return the label string which uniquely identifies this Node in the
     *         document, or <code>null</code> if this node has no label (and
     *         thus is not referencable)
     * @see Reference#getReferencedLabel()
     */
    String getLabel();

    /**
     * @param label
     *            the label
     * @see #getLabel()
     */
    void setLabel(String label);

    /**
     * Renderers should use {@link Reference#getReferenceText()} instead of this
     * one.
     * 
     * @return the reference text
     */
    List<Node> getReferenceText();

}
