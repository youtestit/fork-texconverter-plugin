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

import java.util.Date;

/**
 * Interface for a TeX document.
 * 
 * @author tfrana
 */
public interface TexDocument extends NodeWithChilds, TitledNode {

    /**
     * @return the documentclass
     */
    String getDocumentClass();

    /**
     * @param documentClass
     *            the documentclass
     */
    void setDocumentClass(String documentClass);

    /**
     * @return the authors (one array element per author) if specified,
     *         otherwise <code>null</code>
     */
    String[] getAuthors();

    /**
     * @param authors
     *            an array of author names
     */
    void setAuthors(String[] authors);

    /**
     * @return the date of the document if specified, otherwise
     *         <code>null</code>
     */
    Date getDate();

    /**
     * @param date
     *            the date of the document
     */
    void setDate(Date date);
}
