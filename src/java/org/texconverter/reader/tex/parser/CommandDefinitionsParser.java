/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:30 $
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
package org.texconverter.reader.tex.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.texconverter.reader.tex.builder.impl.TeXDocumentBuilderImpl;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.handlers.CommandHandler;
import org.texconverter.resource.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper for parsing the command definitions xml file.
 * 
 * @author tfrana
 */
public final class CommandDefinitionsParser {

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory
            .getLog(CommandDefinitionsParser.class);

    private CommandDefinitionsParser() {
    }

    /**
     * Parse the xml file specifying the TexConverters command/environment
     * definintions, and put the content into the cmdDefs/envDefs Maps.
     * 
     * @param filepath
     *            the full filepath of the definition file
     * @param texbuilder
     *            the builder that should take the configuration read
     * @throws IOException
     *             on IO errors
     * @throws ConfigurationException
     *             on semantic/syntax errors in the configuration file
     */
    public static void readTexConverterDefsFile(final String filepath,
            final TeXDocumentBuilderImpl texbuilder) throws IOException,
            ConfigurationException {

        final DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docbuilder;
        Document doc;

        InputStream istream = null;
        try {
            istream = ResourceManager.getInstance().getResource(filepath);
            if (istream == null) {
                final String msg = "The command definitions file " + filepath
                        + " was not found";
                log.fatal(msg);
                throw new ConfigurationException(msg);
            }

            docbuilder = dbfactory.newDocumentBuilder();
            doc = docbuilder.parse(istream);
        } catch (final ParserConfigurationException e) {
            throw new ConfigurationException(e);
        } catch (final SAXException e) {
            throw new ConfigurationException(e);
        } finally {
            if (istream != null) {
                istream.close();
            }
        }

        final NodeList nodes = doc.getDocumentElement().getChildNodes();

        Map<String, CmdDef> globalCmdDefs = null;
        Map<String, EnvDef> envDefs = null;

        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            if ("globalcmddefs".equals(node.getNodeName())) {
                globalCmdDefs = CommandDefinitionsParser.parseCmdDefs(node
                        .getChildNodes());
            } else if ("environments".equals(node.getNodeName())) {
                envDefs = CommandDefinitionsParser.parseEnvDefs(node
                        .getChildNodes());
            }
        }

        texbuilder.setCmdDefs(globalCmdDefs);
        texbuilder.setEnvDefs(envDefs);
    }

    /**
     * @param cmdNodes
     *            the xml nodes defining commands
     * @return the command definitons map (command name -> command definiton)
     * @throws ConfigurationException
     */
    private static Map<String, CmdDef> parseCmdDefs(final NodeList cmdNodes)
            throws ConfigurationException {
        NamedNodeMap attr;
        String name, handlerName;
        int mandArgs, optArgs;
        boolean[] interpreteMandArg, interpreteOptArg;
        String argParsers;
        boolean usesPlainVal, hasAltMode;
        CommandHandler cmdHandler;
        CmdDef cmdDef;
        final Map<String, CommandHandler> instantiatedCmdHandlers = new HashMap<String, CommandHandler>();
        instantiatedCmdHandlers.put("null", null);
        final Map<String, CmdDef> cmdDefs = new HashMap<String, CmdDef>();

        for (int i = 0, imax = cmdNodes.getLength(); i < imax; i++) {
            final Node node = cmdNodes.item(i);
            final String nodeName = node.getNodeName();
            if (!"cmd".equals(nodeName)) {
                continue;
            }

            attr = node.getAttributes();
            name = attr.getNamedItem("name").getNodeValue();
            mandArgs = Integer.parseInt(attr.getNamedItem("mandargs")
                    .getNodeValue());
            optArgs = Integer.parseInt(attr.getNamedItem("optargs")
                    .getNodeValue());

            usesPlainVal = Boolean.valueOf(
                    attr.getNamedItem("plainval").getNodeValue())
                    .booleanValue();
            hasAltMode = Boolean.valueOf(
                    attr.getNamedItem("hasAltMode").getNodeValue())
                    .booleanValue();
            handlerName = attr.getNamedItem("handler").getNodeValue();
            argParsers = attr.getNamedItem("argparsers").getNodeValue();

            interpreteMandArg = new boolean[mandArgs];
            interpreteOptArg = new boolean[optArgs];

            for (int j = 0; j < interpreteMandArg.length; j++) {
                if (argParsers.length() > j && argParsers.charAt(j) == 'n') {
                    interpreteMandArg[j] = false;
                } else {
                    interpreteMandArg[j] = true;
                }
            }
            for (int j = 0; j < interpreteOptArg.length; j++) {
                if (argParsers.length() > j + interpreteMandArg.length
                        && argParsers.charAt(j) == 'y') {
                    interpreteOptArg[j] = true;
                } else {
                    interpreteOptArg[j] = false;
                }
            }

            if (!instantiatedCmdHandlers.containsKey(handlerName)) {
                // handler type not yet instantiated, create one
                try {
                    cmdHandler = (CommandHandler) Class.forName(handlerName)
                            .newInstance();
                } catch (final Exception e) {
                    throw new ConfigurationException(
                            "Could not instantiate handler " + handlerName, e);
                }
                instantiatedCmdHandlers.put(handlerName, cmdHandler);
            } else {
                cmdHandler = instantiatedCmdHandlers.get(handlerName);
            }

            cmdDef = new CmdDef(interpreteMandArg, interpreteOptArg,
                    usesPlainVal, hasAltMode, cmdHandler);

            if (cmdDefs.containsKey(name)) {
                log
                        .error("parseCmdDefs -> The command "
                                + name
                                + " has been defined several times! Only the last parsed definition is used.");
            }

            cmdDefs.put(name, cmdDef);
        }
        return cmdDefs;
    }

    /**
     * @param envNodes
     * @return the environment definitions (environment name -> environment
     *         definition)
     * @throws ConfigurationException
     */
    private static Map<String, EnvDef> parseEnvDefs(final NodeList envNodes)
            throws ConfigurationException {
        NamedNodeMap attr;
        String name, builderName;
        int mandArgs, optArgs;
        boolean[] interpreteMandArg, interpreteOptArg;
        Class builderClass;
        String argParsers;
        @SuppressWarnings("unused")
        NodeList nodes;
        final Map<String, EnvDef> envDefs = new HashMap<String, EnvDef>();
        boolean hasAltMode;

        for (int i = 0, imax = envNodes.getLength(); i < imax; i++) {
            final Node node = envNodes.item(i);
            final String nodeName = node.getNodeName();
            if (!"env".equals(nodeName)) {
                continue;
            }

            attr = node.getAttributes();
            name = attr.getNamedItem("name").getNodeValue();
            mandArgs = Integer.parseInt(attr.getNamedItem("mandargs")
                    .getNodeValue());
            optArgs = Integer.parseInt(attr.getNamedItem("optargs")
                    .getNodeValue());
            builderName = attr.getNamedItem("builder").getNodeValue();
            argParsers = attr.getNamedItem("argparsers").getNodeValue();
            hasAltMode = Boolean.parseBoolean(attr.getNamedItem("hasAltMode")
                    .getNodeValue());

            if ("null".equalsIgnoreCase(builderName)) {
                builderClass = null;
            } else {
                try {
                    builderClass = Class.forName(builderName);
                } catch (final ClassNotFoundException e) {
                    throw new ConfigurationException(
                            "Could find builder class " + builderName, e);
                }
            }
            interpreteMandArg = new boolean[mandArgs];
            interpreteOptArg = new boolean[optArgs];
            for (int j = 0; j < interpreteMandArg.length; j++) {
                if (argParsers.length() > j && argParsers.charAt(j) == 'n') {
                    interpreteMandArg[j] = false;
                } else {
                    interpreteMandArg[j] = true;
                }
            }
            for (int j = 0; j < interpreteOptArg.length; j++) {
                if (argParsers.length() > j + interpreteMandArg.length
                        && argParsers.charAt(j + interpreteMandArg.length) == 'y') {
                    interpreteOptArg[j] = true;
                } else {
                    interpreteOptArg[j] = false;
                }
            }

            nodes = node.getChildNodes();
            Map<String, CmdDef> cmdDefs = null;
            if (node.hasChildNodes()) {
                cmdDefs = parseCmdDefs(node.getChildNodes());
            }

            final EnvDef envDef = new EnvDef(interpreteMandArg,
                    interpreteOptArg, hasAltMode, builderClass, cmdDefs);
            envDefs.put(name, envDef);
        }
        return envDefs;
    }
}
