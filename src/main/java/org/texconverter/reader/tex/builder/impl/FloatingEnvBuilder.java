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
 * created: 02.08.2006 tfrana
 */
package org.texconverter.reader.tex.builder.impl;

import org.texconverter.dom.FloatingEnvironment;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.dom.impl.FloatingEnvironmentImpl;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.Token;

/**
 * Builder for floating environments (table, figure, lstlisting).
 * 
 * @author tfrana
 */
public class FloatingEnvBuilder extends AbstractBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final AbstractBuilder parentBuilder, final EnvCommand cmd)
            throws TexParserException {
        final FloatingEnvironmentImpl floatEnv = new FloatingEnvironmentImpl(
                cmd.getName());
        super.init(parentBuilder, floatEnv, Token.NONE);
        floatEnv.setPositionHints(evalPosHints(""));
        evalArgs(cmd);
    }

    private void evalArgs(final EnvCommand cmd) throws TexSyntaxException {
        final FloatingEnvironmentImpl floatEnv = (FloatingEnvironmentImpl) root;
        if (cmd.getOptionalArgs() != null) {
            floatEnv.setPositionHints(evalPosHints((String) cmd
                    .getOptionalArgs().get(0)));
        }
    }

    private static FloatingEnvironment.PositionHint[] evalPosHints(
            final String optArgs) throws TexSyntaxException {

        FloatingEnvironment.PositionHint[] posHints;

        if (optArgs != null) {
            posHints = new FloatingEnvironment.PositionHint[optArgs.length()];
            for (int i = 0; i < optArgs.length(); i++) {
                if (optArgs.charAt(i) == 'h') {
                    posHints[i] = FloatingEnvironment.PositionHint.NORMAL;
                } else if (optArgs.charAt(i) == 't') {
                    posHints[i] = FloatingEnvironment.PositionHint.TOPOFPAGE;
                } else if (optArgs.charAt(i) == 'b') {
                    posHints[i] = FloatingEnvironment.PositionHint.BOTTOMOFPAGE;
                } else if (optArgs.charAt(i) == 'p') {
                    posHints[i] = FloatingEnvironment.PositionHint.EXTRAPAGE;
                } else if (optArgs.charAt(i) != '!') {
                    throw new TexSyntaxException("unknown position hint "
                            + optArgs.charAt(i));
                }
            }

        } else {
            posHints = new FloatingEnvironment.PositionHint[] {
                    FloatingEnvironment.PositionHint.TOPOFPAGE,
                    FloatingEnvironment.PositionHint.BOTTOMOFPAGE,
                    FloatingEnvironment.PositionHint.EXTRAPAGE };
        }
        return posHints;

    }
}
