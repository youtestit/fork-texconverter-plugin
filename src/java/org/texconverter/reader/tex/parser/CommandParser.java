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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.texconverter.dom.NodeWithChilds;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.io.impl.StringPeekReader;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.builder.impl.NodeBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;

/**
 * Parsing commands & arguments.
 * 
 * @author tfrana
 */
public final class CommandParser {

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory.getLog(CommandParser.class);

    private CommandParser() {
    }

    /**
     * Parse a command and call the mapped handler.
     * 
     * @param builder
     *            the current builder
     * @throws IOException
     *             on IO errors
     * @throws TexParserException
     *             on internal parser errors
     * @throws ConfigurationException
     *             on configuration errors
     */
    public static void parseCommand(final AbstractBuilder builder)
            throws IOException, TexParserException, ConfigurationException {
        final Tokenizer tokenizer = builder.getTokenizer();

        tokenizer.getNextCmdNameToken();

        final String cmdName = tokenizer.getLastTokenSequence();

        final CmdDef cmdDef = builder.getCmdDef(cmdName);
        if (cmdDef != null) {

            final Command cmd = new Command();
            cmd.setName(cmdName);

            if ("verb".equals(cmdName)) {
                tokenizer.getNextVerbCmdContent();
                cmd.addMandatoryArg(tokenizer.getLastTokenSequence());
            } else {
                if (cmdDef.altMode) {
                    final String nextChar = tokenizer.peekNextChar();
                    if ("*".equals(nextChar)) {
                        cmd.setAlternativeMode(true);
                        tokenizer.skipOneChar();
                    }
                }
                if (cmdDef.plainValue) {
                    final String nextChar = tokenizer.peekNextChar();
                    if (!"=".equals(nextChar)) {
                        throw new TexSyntaxException(
                                "A \'=\' is missing after command " + cmdName);
                    }
                    tokenizer.skipOneChar();
                    cmd.setFreeArg(tokenizer.skipLine().trim());
                }

                final List<Object>[] args = parseBlockArguments(
                        cmdDef.interpreteMandArg, cmdDef.interpreteOptArg,
                        builder);

                cmd.setMandatoryArgs(args[0]);
                cmd.setOptionalArgs(args[1]);

                if (cmdDef.interpreteMandArg.length > 0
                        && args[0].size() < cmdDef.interpreteMandArg.length) {
                    throw new TexSyntaxException(
                            "command "
                                    + cmdName
                                    + " is missing "
                                    + (cmdDef.interpreteMandArg.length - args[0]
                                            .size()) + " mandatory arguments");
                }
            }
            if (cmdDef.cmdHandler != null) {
                cmdDef.cmdHandler.handle(cmd, builder);
            }
        } else {
            log.warn("parseCommand -> unknown command: " + cmdName);
        }

    }

    /**
     * Parse arguments of an environment and construct an EnvCommand.
     * 
     * @param cmd
     *            the "begin{[environment name]}" command
     * @param builder
     *            the current builder
     * @return the environment command
     * @throws TexSyntaxException
     *             on TeX syntax errors
     * @throws IOException
     *             on IO errors
     * @throws TexParserException
     *             on internal parser errors
     * @throws ConfigurationException
     *             on configuration errors
     */
    public static EnvCommand parseEnvCommand(final Command cmd,
            final AbstractBuilder builder) throws TexSyntaxException,
            IOException, TexParserException, ConfigurationException {
        EnvCommand envCmd = null;

        String envName = (String) cmd.getMandatoryArgs().get(0);
        final boolean altMode = envName.endsWith("*");
        if (altMode) {
            envName = envName.substring(0, envName.length() - 1);
        }

        final EnvDef envDef = builder.getEnvDef(envName);
        if (envDef == null) {
            log.error("parseEnvCommand -> unknown environment " + envName);
        } else {
            final List[] args = CommandParser.parseBlockArguments(
                    envDef.interpreteMandArg, envDef.interpreteOptArg, builder);

            envCmd = new EnvCommand();
            envCmd.setAlternativeMode(altMode);
            envCmd.setName(envName);
            envCmd.setMandatoryArgs(args[0]);
            envCmd.setOptionalArgs(args[1]);

            final Tokenizer tokenizer = builder.getTokenizer();

            // skip rest of line after end of args
            final Token tokenId = tokenizer.getNextToken();
            if (tokenId == Token.WHITESPACE) {
                final String str = tokenizer.getLastTokenSequence();
                final String stripped = str.replaceAll("\\A\\s*\\n", "");
                if (stripped.length() > 0) {
                    tokenizer.insertInput(new StringPeekReader(stripped));
                }
            } else {
                tokenizer.ungetLastToken();
            }
        }

        return envCmd;
    }

    /**
     * Parse a set of arguments.
     * 
     * @param interpreteMandArg
     *            if mandatory arg number interpreteMandArg[n] should be
     *            interpreted or parsed verbatim (the return value for that arg
     *            will be String then). The size of this array tells how many
     *            mandatory args are expected.
     * @param interpreteOptArg
     *            as interpreteMandArg, just for optional arguments (enclosed in
     *            [])
     * @param builder
     *            the current builder
     * @return the values of the arguments found, as an array with two lists,
     *         one for mandatory and one for optional arguments
     * @throws TexSyntaxException
     *             on TeX syntax problems
     * @throws IOException
     *             on IO problems
     * @throws TexParserException
     *             on general parser problems
     * @throws ConfigurationException
     *             on configuration problems
     */
    @SuppressWarnings("unchecked")
    public static List<Object>[] parseBlockArguments(
            final boolean[] interpreteMandArg,
            final boolean[] interpreteOptArg, final AbstractBuilder builder)
            throws TexSyntaxException, IOException, TexParserException,
            ConfigurationException {
        final List[] args = new List[] { null, null };

        final Tokenizer tokenizer = builder.getTokenizer();
        int mandArgsLeft = interpreteMandArg.length;
        int iMandArg = 0, iOptArg = 0;
        int optArgsLeft = interpreteOptArg.length;
        if (mandArgsLeft > 0) {
            args[0] = new ArrayList(mandArgsLeft);
        }
        if (optArgsLeft > 0) {
            args[1] = new ArrayList(optArgsLeft);
        }

        String lastWhitespace = null;

        Token tokenId = Token.NONE;
        // parse arguments
        while (mandArgsLeft + optArgsLeft > 0) {
            tokenId = tokenizer.getNextToken();
            if (tokenId == Token.NONE) {
                throw new IOException("incomplete argument at end of file");
            }

            if (mandArgsLeft > 0 && tokenId == Token.BLOCKOPEN) {
                lastWhitespace = null;
                if (interpreteMandArg[iMandArg]) {
                    final NodeBuilder nodeBuilder = new NodeBuilder(builder,
                            Token.BLOCKCLOSE);
                    args[0].add(((NodeWithChilds) nodeBuilder.build())
                            .getChildren());
                } else {
                    args[0].add(VerbatimParser.parseText(tokenizer,
                            Token.BLOCKCLOSE));
                }
                iMandArg++;
                mandArgsLeft--;
            } else if (optArgsLeft > 0 && tokenId == Token.OPTARGBLOCKOPEN) {
                lastWhitespace = null;
                if (interpreteOptArg[iOptArg]) {
                    final NodeBuilder nodeBuilder = new NodeBuilder(builder,
                            Token.OPTARGBLOCKCLOSE);
                    args[1].add(((NodeWithChilds) nodeBuilder.build())
                            .getChildren());
                } else {
                    args[1].add(VerbatimParser.parseText(tokenizer,
                            Token.OPTARGBLOCKCLOSE));
                }
                iOptArg++;
                optArgsLeft--;
            } else if (tokenId == Token.WHITESPACE) {
                lastWhitespace = tokenizer.getLastTokenSequence();
            } else {
                tokenizer.ungetLastToken();
                break;
            }
        }

        if (lastWhitespace != null) {
            tokenizer.insertInput(new StringPeekReader(lastWhitespace));
        }

        if (args[1] != null && args[1].size() < 1) {
            args[1] = null;
        }

        return args;
    }
}
