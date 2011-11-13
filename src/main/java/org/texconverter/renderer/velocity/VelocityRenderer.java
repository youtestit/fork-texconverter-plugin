/**
 * $Revision: 1.4 $
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
 * created: 02.08.2006 tfrana
 */
package org.texconverter.renderer.velocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.TexConverter;
import org.texconverter.TexConverter.OUTPUTFORMATS;
import org.texconverter.dom.Node;
import org.texconverter.dom.Section;
import org.texconverter.dom.TexDocument;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.RendererException;
import org.texconverter.renderer.Renderer;
import org.texconverter.renderer.handlers.EscapeHandler;
import org.texconverter.renderer.handlers.impl.HtmlEscapeHandler;
import org.texconverter.renderer.handlers.impl.WikiEscapeHandler;
import org.texconverter.renderer.handlers.impl.XdocEscapeHandler;
import org.texconverter.resource.ResourceManager;

/**
 * This renderer uses Velocity templates for rendering.
 * 
 * @author sgodau, tfrana
 */
public class VelocityRenderer implements Renderer {

    protected static final String VELTEMPL_ATTRSTART_TAG = "~<object.";

    protected static final String VELTEMPL_ATTRSTOP_TAG = ">~";

    protected static final String TEMPLATE_BASEDIR = "vm/";

    protected String templateDir;

    protected StringWriter mergeWriter;

    protected String childrenTag = "--CHILDREN--";

    protected EscapeHandler escapeHandler;

    protected VelocityEngine engine;

    protected String titleTag = "--TITLE--";

    protected String[] validPictureExtensions = new String[0];

    private final static Logger LOGGER = LoggerFactory.getLogger(VelocityRenderer.class);

    protected Integer topSectionLevel;

    private boolean isInitialized = false;

    private void init(final String resourceDir) throws ConfigurationException {
        String resDir = resourceDir;
        final Properties props = new Properties();

        File file = new File(resourceDir+"velocity.properties");
        if(!file.exists()){
            throw  new ConfigurationException("Can't find the velocity.properties");
        }

        try {
            final InputStream istream =  new FileInputStream(file);
            props.load(istream);
            istream.close();
        } catch (final IOException e) {
            final String msg = "Could not access velocity properties";
            LOGGER.error(msg, e);
            throw new ConfigurationException(msg, e);
        }

        final String rloaderName = props.getProperty("resource.loader");
        if (rloaderName == null) {
            final String msg = "Velocity properties are missing property \"resource.loader\"";
            LOGGER.error(msg);
            throw new ConfigurationException(msg);
        }

        // props.put(rloaderName + ".resource.loader.path", TEMPLATE_BASEDIR);
        if (resDir.length() < 1) {
            // The velocity file resource loader doesn't interprete an empty
            // path as the current working directory, so change it to ".".
            // Luckily the ".resource.loader.path" property is only used by the
            // file resource loader, so we need no knowledge about the used
            // resource loader class here.
            resDir = ".";
        }
        props.put(rloaderName + ".resource.loader.path", resDir);
        props.put("runtime.log.logsystem.log4j.category",
                        "org.apache.velocity");

        engine = new VelocityEngine();
        try {
            engine.init(props);
        } catch (final Exception e) {
            throw new ConfigurationException(e);
        }

        isInitialized = true;
    }

    /**
     * This method renders the {@link TexDocument}.
     * 
     * @param document
     *            the {@link TexDocument}
     * @return the rendered content
     * @throws RendererException
     */
    protected StringBuffer render(final TexDocument document)
            throws RendererException {

        final StringBuffer result = new StringBuffer();
        result.append("--DOCSTART--");
        processNode(document, result, "--DOCSTART--");
        removeTag(result, "--DOCSTART--");
        return result;
    }

    /**
     * Look for any custom attribute access tags in the intermediate render
     * output, and render the contents of that attribute there.
     * 
     * @param aNode
     *            The node whose attributes are used to replace the attribute
     *            tags.
     * @param nodeRenderOutput
     *            The intermediate render output who eventually contains
     *            attribute tags.
     * @throws RendererException
     */
    @SuppressWarnings("unchecked")
    protected void renderNodeAttributes(final Node aNode,
            final StringBuffer nodeRenderOutput) throws RendererException {

        // look for attribute tags
        List<String> usedAttributes = null;
        int fromIndex = -1, toIndex = -1;
        fromIndex = nodeRenderOutput.indexOf(VELTEMPL_ATTRSTART_TAG, 0);
        if (fromIndex != -1) {
            usedAttributes = new ArrayList<String>();
        }
        while (fromIndex != -1) {
            fromIndex += VELTEMPL_ATTRSTART_TAG.length();
            toIndex = nodeRenderOutput
                    .indexOf(VELTEMPL_ATTRSTOP_TAG, fromIndex);
            if (toIndex == -1) {
                break;
            }
            usedAttributes.add(nodeRenderOutput.substring(fromIndex, toIndex));

            fromIndex = nodeRenderOutput.indexOf(VELTEMPL_ATTRSTART_TAG,
                    toIndex);
        }

        // If attribute tags are found in intermediate render output, replace
        // attribute tags with values from aNode
        if (usedAttributes != null && usedAttributes.size() > 0) {
            StringBuffer sbMethName = new StringBuffer();
            final Iterator<String> itUsedAttr = usedAttributes.iterator();
            while (itUsedAttr.hasNext()) {
                final String attr = itUsedAttr.next();
                final String placeholderTag = VELTEMPL_ATTRSTART_TAG + attr
                        + VELTEMPL_ATTRSTOP_TAG;

                sbMethName = new StringBuffer("get");
                sbMethName.append(attr);
                sbMethName.setCharAt(3, Character.toUpperCase(sbMethName
                        .charAt(3)));

                try {
                    final Method m = aNode.getClass().getMethod(
                            sbMethName.toString(), (Class[]) null);
                    final Object rv = m.invoke(aNode, (Object[]) null);
                    if (rv instanceof List) {
                        final Iterator<Node> itChildren = ((List<Node>) rv)
                                .iterator();
                        while (itChildren.hasNext()) {
                            processNode(itChildren.next(), nodeRenderOutput,
                                    placeholderTag);
                        }
                    } else if (rv instanceof Node) {
                        processNode((Node) rv, nodeRenderOutput, placeholderTag);
                    } else if (rv instanceof String) {
                        // Plain string attributes could use standard velocity
                        // "$oject.attr" syntax, but this way we can escape the
                        // string more conveniently (opposed to manually adding
                        // each plain text attribute of each node to the escape
                        // handling classes).
                        String value = (String) rv;
                        if (escapeHandler != null) {
                            value = escapeHandler.escapeString(value);
                        }
                        insertBeforeTag(new StringBuffer(value),
                                nodeRenderOutput, placeholderTag);

                    } else if (rv != null) {
                        LOGGER.error("renderNodeAttributes -> Method "
                                + sbMethName + " of " + aNode.getClass()
                                + " returns unhandled type " + rv.getClass());
                    }
                } catch (final NoSuchMethodException e) {
                    LOGGER.error("Attribute access for node "
                            + aNode.getClass().getName()
                            + " failed, no getter method \"" + sbMethName
                            + "\"", e);
                } catch (final IllegalArgumentException e) {
                    LOGGER.error("Attribute access for node "
                            + aNode.getClass().getName()
                            + " failed, no no-arguments getter method \""
                            + sbMethName + "\"", e);
                } catch (final IllegalAccessException e) {
                    LOGGER.error("Attribute access for node "
                            + aNode.getClass().getName()
                            + " failed, no access to getter method \""
                            + sbMethName + "\"", e);
                } catch (final InvocationTargetException e) {
                    LOGGER.error("Attribute access for node "
                            + aNode.getClass().getName()
                            + " failed, could not invoce method \""
                            + sbMethName + "\"", e);
                }
                sbMethName.delete(0, sbMethName.length());
                removeTag(nodeRenderOutput, placeholderTag);
            }
        }
    }

    /**
     * Processes a {@link Node}. Childnodes are processed recursively. The
     * output is added to the result.
     * 
     * @param aNode
     *            the {@link Node} to process
     * @param result
     *            the {@link StringBuffer} containing the rendered content so
     *            far
     * @param tag
     *            the tag that marks the current inserting position in result
     * @throws RendererException
     */
    protected void processNode(final Node aNode, final StringBuffer result,
            final String tag) throws RendererException {

        checkNode(aNode);

        VelocityContext context = new VelocityContext();
        context.put("toplevel", topSectionLevel);
        context.put("object", aNode);

        // render the node
        final StringBuffer sbRenderedNode = renderNode(aNode, context);

        if (sbRenderedNode != null) {
            // insert rendering output of node into final result
            insertBeforeTag(sbRenderedNode, result, tag);
        }
    }

    /**
     * Creates the render output for a single {@link Node}.
     * 
     * @param aNode
     *            The node to render.
     * @param context
     * @return The rendering output.
     * @throws RendererException
     *             if the node could not be rendered
     */
    protected StringBuffer renderNode(final Node aNode,
            final VelocityContext context) throws RendererException {
        StringBuffer nodeRenderOutput = null;

        final String templateName = searchForTemplate(aNode);
        if (templateName != null) {

            // render this node
            nodeRenderOutput = merge(context, templateName);

            // render any custom attribute tags left in the render output
            renderNodeAttributes(aNode, nodeRenderOutput);

            // if (aNode instanceof Referencable
            // && ((Referencable) aNode).getLabel() != null) {
            // nodeRenderOutput.insert(0, merge(context, "Label"));
            // }

        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No velocity template for Node "
                    + aNode.getClass().getName());
        }

        return nodeRenderOutput;
    }

    /**
     * This method changes nodes if necessary.
     * 
     * @param aNode
     *            a {@link Node}
     */
    protected void checkNode(final Node aNode) {

        if (aNode instanceof Section && topSectionLevel == null) {
            topSectionLevel = Integer.valueOf(((Section) aNode).getLevel()
                    .ordinal());
        }

        if (escapeHandler != null) {
            escapeHandler.escapeNode(aNode);
        }
    }

    /**
     * This method merges the {@link VelocityContext} with a template starting
     * with the template prefix.
     * 
     * @param context
     *            the {@link VelocityContext}
     * @param templatePrefix
     *            the template prefix
     * @return the merged content
     * @throws RendererException
     *             if merging failed
     */
    protected StringBuffer merge(final VelocityContext context,
            final String templatePrefix) throws RendererException {
        mergeWriter = new StringWriter();
        try {
            final Template template = engine.getTemplate(templateDir
                    + templatePrefix + ".vm");
            template.merge(context, mergeWriter);
            mergeWriter.flush();
            mergeWriter.close();
        } catch (final Exception e) {
            throw new RendererException("Exception while merging template for "
                    + templatePrefix, e);
        }
        return mergeWriter.getBuffer();
    }

    /**
     * This method inserts the content buffer into the result buffer at the
     * position of the tag.
     * 
     * @param content
     *            the buffer to be inserted
     * @param result
     *            the buffer where the content is inserted
     * @param tag
     *            the tag where to insert the content
     * @throws RendererException
     *             if no children tag is found
     */
    protected void insertBeforeTag(final StringBuffer content,
            final StringBuffer result, final String tag)
            throws RendererException {
        final int tmpIndex = result.indexOf(tag);
        if (tmpIndex == -1) {
            throw new RendererException("Missing tag: " + tag);
        }
        result.insert(tmpIndex, content);
    }

    /**
     * This method removes the first children tag that is found.
     * 
     * @param result
     *            the buffer containing the content
     * @param tag
     *            the tag to remove
     * @throws RendererException
     *             if no children tag is found
     */
    protected void removeTag(final StringBuffer result, final String tag)
            throws RendererException {
        final int tmpIndex = result.indexOf(tag);
        if (tmpIndex == -1) {
            throw new RendererException("Missing tag: " + tag);
        }
        result.replace(tmpIndex, tmpIndex + tag.length(), "");
    }

    /**
     * This method checks if a template for one of the node interfaces is
     * available.
     * 
     * @param aNode
     *            the {@link Node} that needs a template
     * @return the template prefix or null
     */
    protected String searchForTemplate(final Node aNode) {
        String templateName = "";

        final Class[] classes = aNode.getClass().getInterfaces();
        for (int i = 0; i < classes.length; i++) {

            templateName = new String(classes[i].getSimpleName());
            LOGGER.debug("seaching for template " + templateDir + templateName
                    + ".vm");

            if (engine.templateExists(templateDir + templateName + ".vm")) {
                return templateName;
            }

        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String render(final OUTPUTFORMATS outputFormat,
            final TexDocument document, final String baseDir)
            throws RendererException, ConfigurationException {
        String basDir = baseDir;

        if (basDir.length() > 0 && !basDir.endsWith("/")) {
            basDir += "/";
        }

        if (!isInitialized) {
            init(basDir);
        }

        switch (outputFormat) {
        case HTML:
            escapeHandler = new HtmlEscapeHandler();
            templateDir = TEMPLATE_BASEDIR + "html/";
            break;
        case XDOC:
            escapeHandler = new XdocEscapeHandler();
            templateDir = TEMPLATE_BASEDIR + "xdoc/";
            break;
        case WIKI_WICKED:
            escapeHandler = new WikiEscapeHandler();
            templateDir = TEMPLATE_BASEDIR + "wiki_wicked/";
            break;
        default:
            final String msg = "Output format " + outputFormat
                    + " not handled by VelocityRenderer";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return render(document).toString();
    }
}
