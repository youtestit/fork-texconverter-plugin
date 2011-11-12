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
package org.texconverter.reader.tex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.texconverter.dom.Node;
import org.texconverter.dom.TextContent;
import org.texconverter.dom.impl.NodeWithChildsImpl;
import org.texconverter.dom.impl.TextImpl;

/**
 * @author tfrana
 */
public final class Helper {

    private static final int NUMASCIIALPHABET = 1 + ('Z' - 'A');

    private Helper() {
    }

    /**
     * Get the plain text as a String from a list of nodes.
     * 
     * @param nodeList
     *            The nodes to extract the text from.
     * @return The extracted text.
     */
    public static String getPlainTextFromNodeList(final List<Node> nodeList) {
        final StringBuffer sb = new StringBuffer();

        if (nodeList != null) {
            final Iterator<Node> it = nodeList.iterator();
            while (it.hasNext()) {
                final Node node = it.next();
                if (node instanceof NodeWithChildsImpl) {
                    sb
                            .append(getPlainTextFromNodeList(((NodeWithChildsImpl) node)
                                    .getChildren()));
                } else if (node instanceof TextContent) {
                    sb.append(((TextContent) node).getContent());
                }
            }
        }
        return sb.toString();
    }

    /**
     * @param plainText
     *            the string to convert
     * @return a List of Nodes containing the plain text
     */
    public static List<Node> getNodeListFromPlainText(final String plainText) {
        List<Node> nodeList = new ArrayList<Node>();
        nodeList.add(new TextImpl(plainText));
        return nodeList;
    }

    /**
     * Creates a letter representation of a number. 0 will be an empty string, 1
     * will be "A", 2 "B", 27 "AA", 28 "AB" etc.
     * 
     * @param number
     *            the number to translate
     * @return the letter string
     */
    public static String translateNumberToLetters(final int number) {
        final StringBuffer sb = new StringBuffer();
        int num = number;

        if (num < 0) {
            sb.append("-");
            num = Math.abs(num);
        }

        while (num > 0) {
            int y = num % NUMASCIIALPHABET;
            if (y == 0) {
                // there is no "zero"
                y = NUMASCIIALPHABET;
            }
            sb.insert(0, (char) (y - 1 + 'A'));
            num = (num - y) / NUMASCIIALPHABET;
        }

        return sb.toString();
    }
}
