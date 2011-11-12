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
package org.texconverter.dom;

import java.util.List;

/**
 * An entry in the index, similar to a {@link ListingItem}.
 * 
 * @author tfrana
 */
public interface IndexEntry extends NodeWithChilds {

    // sort key, name, parent entry, level

    /**
     * @return the level of this entry in the index, where 0 is the base level,
     *         1 is one below etc
     */
    int getLevel();

    /**
     * @return a list of the references this entry has, delimited by commas
     *         (text nodes).
     */
    List<Node> getReferencesText();

    /**
     * @return the term of the entry
     */
    String getTerm();
}
