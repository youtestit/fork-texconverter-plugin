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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.texconverter.dom.Node;
import org.texconverter.dom.NodeWithChilds;

/**
 * Parent class for all nodes that can have child nodes.
 * 
 * @author tfrana
 */
public class NodeWithChildsImpl extends AbstractNode implements NodeWithChilds {

    protected List<Node> children = new ArrayList<Node>();

    /**
     * {@inheritDoc}
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * @param children
     *            the children
     */
    public void setChildren(final List<Node> children) {
        this.children = children;
    }

    /**
     * {@inheritDoc}
     */
    public Node getChild(final int index) {
        return children.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public int getNumChildren() {
        return children.size();
    }

    /**
     * @param child
     *            the node to add
     */
    public void addChild(final AbstractNode child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * @param nodes
     *            The nodes to add as children.
     */
    public void addAllChildren(final List<AbstractNode> nodes) {
        final Iterator<AbstractNode> it = nodes.iterator();
        while (it.hasNext()) {
            addChild(it.next());
        }
    }

    /**
     * This method gets called on every node that is considered being added as a
     * child to this node. It should return true if this node is closed (and
     * further nodes should be added to the parent instead)
     * 
     * @param node
     *            The node to check if it ends this one.
     * @return <code>true</code> if this noded is ended, false otherwise
     */
    public boolean isEndedBy(final Node node) {
        return false;
    }

    /**
     * Basically overridden for debugging purposes. Gives an inaccurate
     * LaTeX-code representation of this node.
     * 
     * @return The inaccurate LaTeX representation.
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();

        Node child;
        for (int i = 0; i < children.size(); i++) {
            child = children.get(i);
            sb.append(child.toString());
        }

        return sb.toString();
    }

    /**
     * @param clasz
     *            the Class the node must be assignable to
     * @return the first child node (searches subchildren too, so result may be
     *         child of child..) which is assignable to the clasz
     */
    @SuppressWarnings("unchecked")
    public AbstractNode findFirstChild(final Class clasz) {
        AbstractNode matchingNode = null;

        Iterator<Node> it = children.iterator();
        Node node;
        while (it.hasNext()) {
            node = it.next();
            if (clasz.isAssignableFrom(node.getClass())) {
                matchingNode = (AbstractNode) node;
            } else if (node instanceof NodeWithChildsImpl) {
                matchingNode = findFirstChild(clasz);
            }
        }

        return matchingNode;
    }

    /**
     * @param clasz
     *            the Class the node must be assignable to
     * @return the last child node (searches subchildren too, so result may be
     *         child of child..) which is assignable to the clasz
     */
    @SuppressWarnings("unchecked")
    public AbstractNode findLastChild(final Class clasz) {
        AbstractNode matchingNode = null;

        for (int i = children.size(); i >= 0 && matchingNode == null; i--) {
            if (clasz.isAssignableFrom(children.get(i).getClass())) {
                matchingNode = (AbstractNode) children.get(i);
            } else if (children.get(i) instanceof NodeWithChildsImpl) {
                matchingNode = ((NodeWithChildsImpl) children.get(i))
                        .findLastChild(clasz);
            }
        }

        return matchingNode;
    }
}
