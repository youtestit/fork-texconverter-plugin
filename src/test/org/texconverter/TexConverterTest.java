/**
 * $Revision: 1.3 $
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
package org.texconverter;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tfrana
 */
public class TexConverterTest extends TestCase {

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory
            .getLog(TexConverterTest.class);

    /**
     * 
     */
    public void testDesign() {
        // creating the XDoc project documentation here..
        try {
            final TexConverterMaven converter = new TexConverterMaven();
            converter.setVerbose(true);
            converter.setPropertiesFile("texdocs/convert_test.properties");
            converter.setPluginResourceDir("plugin-resources/");
            converter.doExecute();
        } catch (Throwable e) {
            log.error("testDesignXDoc -> ", e);
            fail();
        }
    }
    
    /**
     * 
     */
    public void testDesignXDoc() {
        // creating the XDoc project documentation here..
        try {
            final TexConverterMaven converter = new TexConverterMaven();
            converter.setVerbose(true);
            converter.setPropertiesFile("texdocs/convert_xdoc.properties");
            converter.setPluginResourceDir("plugin-resources/");
            converter.doExecute();
        } catch (Throwable e) {
            log.error("testDesignXDoc -> ", e);
            fail();
        }
    }

    /**
     * 
     */
    public void testDesignHTML() {
        try {
            final TexConverterMaven converter = new TexConverterMaven();
            converter.setVerbose(true);
            converter.setPropertiesFile("texdocs/convert_html.properties");
            converter.setPluginResourceDir("plugin-resources/");
            converter.doExecute();
        } catch (Throwable e) {
            log.error("testDesignHTML -> ", e);
            fail();
        }
    }

    /**
     *
     */
    public void testDesignWikiWicked() {
        try {
            final TexConverterMaven converter = new TexConverterMaven();
            converter.setVerbose(true);
            converter
                    .setPropertiesFile("texdocs/convert_wikiwicked.properties");
            converter.setPluginResourceDir("plugin-resources/");
            converter.doExecute();
        } catch (Throwable e) {
            log.error("testDesignWikiWicked -> ", e);
            fail();
        }
    }
}
