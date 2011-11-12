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

import java.util.Date;
import java.util.List;

import org.texconverter.dom.Node;
import org.texconverter.dom.TexDocument;

/**
 * @author tfrana
 * @see org.texconverter.dom.TexDocument
 */
public class TexDocumentImpl extends Environment implements TexDocument {

    private String documentClass = null;

    private List<Node> title = null;

    private String[] authors = null;

    private Date date = null;

    /**
     * 
     */
    public TexDocumentImpl() {
        super("");
    }

    /**
     * @param name
     *            name of the environment
     */
    public TexDocumentImpl(final String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    public String getDocumentClass() {
        return documentClass;
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentClass(final String documentClass) {
        this.documentClass = documentClass;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getAuthors() {
        return authors;
    }

    /**
     * {@inheritDoc}
     */
    public void setAuthors(final String[] authors) {
        this.authors = authors;
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate() {
        return date;
    }

    /**
     * {@inheritDoc}
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(final List<Node> title) {
        this.title = title;
    }
}
