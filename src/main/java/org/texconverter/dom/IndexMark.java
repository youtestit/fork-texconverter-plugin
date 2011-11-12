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

/**
 * This is an invisible (->should not be rendered) marker in the text to which
 * an index entry points to. So renderers can e.g. generate an anchor in HTML or
 * determine the page number at the position of the mark, to generate references
 * for an index entry.
 * 
 * @author tfrana
 */
public interface IndexMark extends Referencable {

    /**
     * @return the IndexEntry referencing this mark
     */
    IndexEntry getIndexEntry();
}
