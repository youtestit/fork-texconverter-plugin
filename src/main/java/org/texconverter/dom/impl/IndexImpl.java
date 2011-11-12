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
package org.texconverter.dom.impl;

import java.util.List;

import org.texconverter.dom.Index;
import org.texconverter.dom.Node;
import org.texconverter.dom.TitledNode;
import org.texconverter.reader.tex.Helper;

/**
 * @author tfrana
 */
public class IndexImpl extends NodeWithChildsImpl implements Index, TitledNode {

    private List<Node> title;

    /**
     * @param title
     *            the title of the index
     */
    public IndexImpl(final String title) {
        this.title = Helper.getNodeListFromPlainText(title);
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
