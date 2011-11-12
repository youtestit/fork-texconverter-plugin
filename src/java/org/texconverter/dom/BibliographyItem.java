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
 * created: 08.08.2006 tfrana
 */
package org.texconverter.dom;

import java.util.List;

/**
 * A Bibliography entry.
 * 
 * @author tfrana
 */
public interface BibliographyItem extends NodeWithChilds {

    /**
     * Reference key is the string which is used to identify a bibliography
     * entry in the document (as opposed to the attribute "key", which is used
     * to identify an entry in the bib file).
     * 
     * @return the reference key
     */
    List<Node> getReferenceText();
}
