/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:19 $
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
 * created: 03.08.2006 tfrana
 */
package org.texconverter.dom.impl;

import java.util.List;

import org.texconverter.dom.Glossary;
import org.texconverter.dom.Node;
import org.texconverter.reader.tex.Helper;

/**
 * @author tfrana
 */
public class GlossaryImpl extends NodeWithChildsImpl implements Glossary {

    private List<Node> title;

    /**
     * @param title
     *            the title of the glossary
     */
    public GlossaryImpl(final String title) {
        this.title = Helper.getNodeListFromPlainText(title);
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getTitle() {
        return this.title;
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(final List<Node> title) {
        this.title = title;
    }

}
