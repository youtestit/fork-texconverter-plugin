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

import java.util.List;

import org.texconverter.dom.FloatingEnvironment;
import org.texconverter.dom.Node;
import org.texconverter.dom.Referencable;
import org.texconverter.reader.tex.Helper;

/**
 * @author tfrana
 */
public class FloatingEnvironmentImpl extends Environment implements
        FloatingEnvironment, Referencable {

    private String label = null;

    private String counter;

    private FloatingEnvironment.PositionHint[] positionHints;

    private final String environmentName;

    /**
     * @param environmentName
     *            name of the environment
     */
    public FloatingEnvironmentImpl(final String environmentName) {
        super(environmentName);
        this.environmentName = environmentName;
    }

    /**
     * {@inheritDoc}
     */
    public FloatingEnvironment.PositionHint[] getPositionHints() {
        return positionHints;
    }

    /**
     * @param positionHints
     *            the position hints
     */
    public void setPositionHints(
            final FloatingEnvironment.PositionHint[] positionHints) {
        this.positionHints = positionHints;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            set the label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * {@inheritDoc}
     */
    public List<Node> getReferenceText() {
        return Helper.getNodeListFromPlainText(counter);
    }

    /**
     * @param counter
     *            the counter for this node
     */
    public void setCounter(final String counter) {
        this.counter = counter;
    }

    /**
     * Get the name of the floating environment (as there are several (tabular,
     * figure, lstlisting in some way) in LaTeX, which are translated to 2 types
     * of nodes (a FloatingEnvironment which only servers positioning purposes,
     * as well as a content node like Table or Picture). As we need to know the
     * kind of environment in postprocessing (see
     * {@link org.texconverter.reader.tex.builder.impl.RootBuilderModule#finish(org.texconverter.dom.NodeWithChilds)})
     * for captions, make it available here.
     * 
     * @return the environment name
     */
    public String getEnvironmentName() {
        return environmentName;
    }
}
