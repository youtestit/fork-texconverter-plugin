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
 * Basic interface for Nodes.
 * 
 * @author tfrana
 */
public interface Node {

    /**
     * @return the parent of this node or <code>null</code> if there is no
     *         parent node
     */
    NodeWithChilds getParent();

    /**
     * Get the section this node is in (the lowest lvl section, e.g.
     * subsubsection before subsection).
     * 
     * @return the parent Section or <code>null</code>
     */
    Section getSection();

    /**
     * Same as {@link #getSection()}, but the section of the given level is
     * returned.
     * 
     * @param level
     *            the level of the parent section to return
     * @return the section or <code>null</code> if there is no parent section
     *         of that level
     */
    Section getSection(Section.Lvl level);

}
