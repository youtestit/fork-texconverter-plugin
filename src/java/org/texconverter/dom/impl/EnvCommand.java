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

/**
 * @author tfrana
 */
public class EnvCommand {

    private String name;
    private List mandatoryArgs = null;
    private List optionalArgs = null;
    private boolean alternativeMode;

    /**
     * @return the mandatory args or <code>null</code>
     */
    public List getMandatoryArgs() {
        return mandatoryArgs;
    }

    /**
     * @param mandatoryArgs
     *            the mandatory args
     */
    public void setMandatoryArgs(final List mandatoryArgs) {
        this.mandatoryArgs = mandatoryArgs;
    }

    /**
     * @return the name of the environment
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name of the environment
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
     * @param optionalArgs
     *            the optional args
     */
    public void setOptionalArgs(final List optionalArgs) {
        this.optionalArgs = optionalArgs;
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

}
