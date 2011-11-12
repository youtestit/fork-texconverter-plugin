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
import java.util.List;

/**
 * Represents a TeX command.
 * 
 * @author tfrana
 */
public class Command {

    private String name;

    private List<Object> mandatoryArgs = null;

    private List<Object> optionalArgs = null;

    private boolean alternativeMode = false;

    private String freeArg = null;

    /**
     * @return the mandatory args or <code>null</code>
     */
    public List getMandatoryArgs() {
        return mandatoryArgs;
    }

    /**
     * @param mandatoryArg
     *            a mandatory arg
     */
    public void addMandatoryArg(final Object mandatoryArg) {
        if (mandatoryArgs == null) {
            mandatoryArgs = new ArrayList<Object>(2);
        }
        mandatoryArgs.add(mandatoryArg);
    }

    /**
     * @return the name of the command
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name of the command
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the optional args or <code>null</code>
     */
    public List getOptionalArgs() {
        return optionalArgs;
    }

    /**
     * @param optionalArg
     *            an optional arg
     */
    public void addOptionalArg(final Object optionalArg) {
        if (optionalArgs == null) {
            optionalArgs = new ArrayList<Object>(2);
        }
        optionalArgs.add(optionalArg);
    }

    /**
     * @return if this command's alternative mode is enabled ('*' after the
     *         command name in TeX)
     */
    public boolean getAlternativeMode() {
        return alternativeMode;
    }

    /**
     * @param alternativeMode
     *            if alternative mode of command is enabled
     */
    public void setAlternativeMode(final boolean alternativeMode) {
        this.alternativeMode = alternativeMode;
    }

    /**
     * A free arg is assigned via a '=' in TeX.
     * 
     * @return a free arg or <code>null</code>
     */
    public String getFreeArg() {
        return freeArg;
    }

    /**
     * @param freeArg
     *            the free args
     */
    public void setFreeArg(final String freeArg) {
        this.freeArg = freeArg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("\\");
        sb.append(name);
        if (alternativeMode) {
            sb.append("*");
        }
        if (freeArg != null) {
            sb.append("=");
            sb.append(freeArg);
        }
        if (mandatoryArgs != null) {
            for (int i = 0; i < mandatoryArgs.size(); i++) {
                sb.append("{");
                sb.append(mandatoryArgs.get(i).toString());
                sb.append("}");
            }
        }
        if (optionalArgs != null) {
            for (int i = 0; i < optionalArgs.size(); i++) {
                sb.append("[");
                sb.append(optionalArgs.get(i).toString());
                sb.append("]");
            }
        }
        return sb.toString();
    }

    /**
     * @param mandatoryArgs
     *            the mandatory args
     */
    public void setMandatoryArgs(final List<Object> mandatoryArgs) {
        this.mandatoryArgs = mandatoryArgs;
    }

    /**
     * @param optionalArgs
     *            the optional args
     */
    public void setOptionalArgs(final List<Object> optionalArgs) {
        this.optionalArgs = optionalArgs;
    }
}
