/**
 * $Revision: 1.2 $
 * $Date: 2006/09/26 09:31:36 $
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
package org.texconverter.renderer.velocity;

import org.apache.velocity.VelocityContext;
import org.texconverter.dom.Listing;
import org.texconverter.dom.Node;
import org.texconverter.dom.TexDocument;
import org.texconverter.reader.tex.exceptions.RendererException;

/**
 * Adds special handling of linebreaks for Wicked Wiki format.
 * 
 * @author tfrana
 */
public class WikiWickedVelocityRenderer extends VelocityRenderer {

    /**
     * To keep track of list nesting to get the indenting right.
     */
    protected Integer listNestingLevel;

    protected Node lastNode = null;

    /**
     * Flag that is toggled during rendering to start/stop inserting linebreaks
     * dependant on the context.
     */
    protected boolean doLinebreaks = true;

    /**
     * {@inheritDoc}
     */
    @Override
    protected StringBuffer render(final TexDocument document)
            throws RendererException {
        listNestingLevel = -1;
        document.setTitle(null);
        return super.render(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processNode(final Node aNode, final StringBuffer result,
            final String tag) throws RendererException {

        checkNode(aNode);

        VelocityContext context = new VelocityContext();
        context.put("toplevel", topSectionLevel);
        
        context.put("object", aNode);

        if (aNode instanceof Listing) {
            listNestingLevel++;
        }
        context.put("listnestinglvl", listNestingLevel);
        // else if (aNode instanceof Table) {
        // doLinebreaks = false;
        // }

        // render the node
        final StringBuffer sbRenderedNode = renderNode(aNode, context);

        if (aNode instanceof Listing) {
            listNestingLevel--;
        }
        // else if (aNode instanceof Table) {
        // doLinebreaks = true;
        // }

        if (sbRenderedNode != null) {

            // Problem with labels:
            // Positioning always in front is not ok, interferes with list
            // elements etc. Positioning always behind is not ok, e.g.
            // a label for a section being at the end of the whole body of the
            // section.

            // For each node which can be labelled, we must be able to define a
            // specific place where an eventual label is placed.

            // need to start render output of this node on a new line?
            // if (doLinebreaks
            // && lastNode instanceof TextContent
            // && !(aNode instanceof TextContent
            // || aNode instanceof TypeFace
            // || aNode instanceof AlignedNode || aNode instanceof
            // BibliographyRef)) {
            // sbRenderedNode.insert(0, "\n");
            // }
            // lastNode = aNode;

            // insert rendering output of node into final result
            insertBeforeTag(sbRenderedNode, result, tag);
        }
    }
}
