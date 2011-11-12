/**
 * $Revision: 1.3 $
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
package org.texconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.NodeWithChilds;
import org.texconverter.dom.Picture;
import org.texconverter.dom.TexDocument;
import org.texconverter.reader.tex.builder.TeXDocumentBuilder;
import org.texconverter.reader.tex.builder.impl.BuilderFactory;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.RendererException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.renderer.Renderer;
import org.texconverter.renderer.velocity.VelocityRenderer;
import org.texconverter.renderer.velocity.WikiWickedVelocityRenderer;
import org.texconverter.resource.ResourceLoader;
import org.texconverter.resource.ResourceManager;

/**
 * @author sgodau, tfrana
 */
public abstract class TexConverter {

    /**
     * Apache commons logging reference.
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(TexConverter.class);

    private static final String SUBFOLDER_IMAGES = "images";

    /**
     * @author tfrana
     */
    public static enum OUTPUTFORMATS {
        /** HTML output. */
        HTML(VelocityRenderer.class, new String[] { "JPG", "JPEG", "PNG",
                "GIF", "BMP" }),
        /** Xdoc output. */
        XDOC(VelocityRenderer.class, new String[] { "JPG", "JPEG", "PNG",
                "GIF", "BMP" }),
        /** Wicked Wiki output format. */
        WIKI_WICKED(WikiWickedVelocityRenderer.class, new String[] { "JPG",
                "JPEG", "PNG", "GIF", "BMP" });

        private final Class rendererClass;

        private final String[] validPictureFormats;

        private OUTPUTFORMATS(final Class rendererClass,
                final String[] validPictureFormats) {
            assert (Renderer.class.isAssignableFrom(rendererClass));
            this.rendererClass = rendererClass;
            this.validPictureFormats = validPictureFormats;
        }

        /**
         * @return a Class implementing {@link Renderer} to render this output
         *         format
         */
        public Class getRendererClass() {
            return rendererClass;
        }

        /**
         * @return An array with the file extensions of picture formats that are
         *         usable by the output document format. Preferred picture
         *         formats are first in the array.
         */
        public String[] getValidPictureFormats() {
            return validPictureFormats;
        }
    }

    protected static final String PROP_PROJECTS = "texconverter.projects";

    protected static final String PROP_INPUT_FILE = "inputFileName";

    protected static final String PROP_OUTPUT_FILE = "outputFileName";

    protected static final String PROP_OUTPUT_FORMAT = "outputFormatName";

    protected static final String PROP_LOCALECODE = "localeCode";

    protected static final String COMMANDDEFS_FILE = "resources/cmdDefs.xml";

    protected static final String LOG4JPROPERTIES = "resources/log4j.properties";

    private String resourceDir = "";

    protected boolean isInitialized = false;

    /**
     * Standard constructor.
     */
    public TexConverter() {
        ResourceManager.getInstance().addResourceLoader(getResourceLoader());
    }

    protected void init(final String baseDir, final boolean verbose) {
        String basDir = baseDir;

        if (!isInitialized) {
            if (basDir == null) {
                basDir = "";
            } else if (basDir.length() > 0) {
                basDir = convertSlashes(basDir);
                if (!basDir.endsWith("/")) {

                    basDir += "/";
                }
            }

            this.resourceDir = basDir;

            isInitialized = true;
        }
    }

    /**
     * Start conversion. This will simply call
     * {@link #startSingleConversion(String, String, String, Locale)} if the
     * propertiesFile parameter is <code>null</code>, otherwise
     * {@link #startConversionFromProperties(String)} will be called.
     * 
     * @param propertiesFile
     *            Path to the properties file containing the conversion
     *            properties. If <code>null</code>, the other paramaters will
     *            be used.
     * @param inputFilePath
     *            Path of the input TeX file. Only used when propertiesFile is
     *            <code>null</code>.
     * @param outputFilePath
     *            Path of the output file. Only used when propertiesFile is
     *            <code>null</code>.
     * @param outputFormatName
     *            To what output format to render. Only used when propertiesFile
     *            is <code>null</code>.
     * @param localeCode
     *            the ISO-3-letter language code, for localized resources
     */
    public void startConversion(final String propertiesFile,
            final String inputFilePath, final String outputFilePath,
            final String outputFormatName, final String localeCode) {

        try {
            if (propertiesFile != null) {
                startConversionFromProperties(propertiesFile);
            } else {

                startSingleConversion(inputFilePath, outputFilePath,
                        outputFormatName, getLocale(localeCode));
            }
        } catch (final IOException ex) {
            LOGGER.error(ex.getMessage(),ex);
        }
        LOGGER.info("Finished all conversions.");
    }

    /**
     * Read one or several conversion projects from a properties file, and
     * execute each one.
     * 
     * @param propertiesFilePath
     *            the filepath
     * @param verbose
     * @param locale
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void startConversionFromProperties(final String propertiesFilePath)
            throws FileNotFoundException, IOException {

        final Properties props = new Properties();
        final InputStream istream = new FileInputStream(propertiesFilePath);
        props.load(istream);
        istream.close();

        String[] projects = null;
        if (!props.containsKey(PROP_PROJECTS)) {
            LOGGER.error("No property " + PROP_PROJECTS + " found!");
        } else {
            projects = props.getProperty(PROP_PROJECTS).split(",");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("" + projects.length + " conversion projects found.");
            }

            boolean usePropsDirAsCwd = false;
            File inputFile, outputFile;
            String inputFilePath, outputFilePath;
            String outputFormatName, localeCode;
            File propsDir = new File(propertiesFilePath).getParentFile();

            for (int i = 0; i < projects.length; i++) {
                inputFilePath = props.getProperty(projects[i] + "."
                        + PROP_INPUT_FILE);
                if (inputFilePath != null) {
                    inputFile = new File(inputFilePath);
                    if (!inputFile.isAbsolute() && !inputFile.exists()) {
                        // input file not found, try relative to props dir
                        inputFile = new File(propsDir, inputFile.getPath());
                        if (inputFile.exists()) {
                            usePropsDirAsCwd = true;
                            inputFilePath = inputFile.getAbsolutePath();
                        }
                    }
                }

                outputFilePath = props.getProperty(projects[i] + "."
                        + PROP_OUTPUT_FILE);
                if (outputFilePath != null) {
                    outputFile = new File(props.getProperty(projects[i] + "."
                            + PROP_OUTPUT_FILE));
                    if (!outputFile.isAbsolute() && usePropsDirAsCwd) {
                        outputFile = new File(propsDir, outputFile.getPath());
                    }
                }

                outputFormatName = props.getProperty(projects[i] + "."
                        + PROP_OUTPUT_FORMAT);

                localeCode = props.getProperty(projects[i] + "."
                        + PROP_LOCALECODE);

                startSingleConversion(inputFilePath, outputFilePath,
                        outputFormatName, getLocale(localeCode));
            }
        }
    }

    private boolean startSingleConversion(final String inputFilePath,
            final String outputFilePath, final String outputFormatName,
            final Locale locale) {
        boolean success = false;

        if (inputFilePath == null) {
            LOGGER.error("no inputFileName specified");
        } else if (outputFilePath == null) {
            LOGGER.error("no outputFileName specified");
        } else if (outputFormatName == null) {
            LOGGER.error("no outputFormat specified");
        } else {

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Starting conversion: Input file " + inputFilePath
                        + ", output file " + outputFilePath + ", using format "
                        + outputFormatName);
            }

            File inputFile = new File(inputFilePath);
            File outputFile = new File(outputFilePath);

            try {
                OUTPUTFORMATS outputFormat = null;
                try {
                    outputFormat = OUTPUTFORMATS.valueOf(outputFormatName
                            .toUpperCase());
                } catch (final IllegalArgumentException e) {
                    LOGGER.error("Unknown output format name \""
                            + outputFormatName + "\"");
                    throw new RendererException(e);
                }

                TeXDocumentBuilder builder;
                try {
                    builder = BuilderFactory.getInstance()
                            .getTeXDocumentBuilder(
                                    resourceDir + COMMANDDEFS_FILE, locale);
                } catch (final Exception e) {
                    LOGGER.error("Exception while accessing command definitions!",
                            e);
                    throw new RendererException(e);
                }

                // creating TexDocument from input
                TexDocument texdoc = null;
                try {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Parsing file " + inputFilePath);
                    }
                    texdoc = builder.build(new File(inputFilePath));
                } catch (final TexSyntaxException e) {
                    LOGGER.error("Exception while parsing input file!", e);
                    throw new RendererException(e);
                } catch (final IOException e) {
                    LOGGER.error("Exception while accessing input file!", e);
                    throw new RendererException(e);
                } catch (final Exception e) {
                    LOGGER.error("Exception while building tex dom!", e);
                    throw new RendererException(e);
                }

                // rendering TexDocument
                LOGGER.debug("Creating new instance of Renderer class "
                        + outputFormat.getRendererClass());

                Renderer renderer;
                try {
                    renderer = (Renderer) outputFormat.getRendererClass()
                            .newInstance();
                } catch (final InstantiationException e) {
                    final String msg = "Renderer class "
                            + outputFormat.getRendererClass()
                            + " could not be instantiated";
                    LOGGER.error(msg, e);
                    throw new RendererException(e);
                } catch (final IllegalAccessException e) {
                    final String msg = "Renderer class "
                            + outputFormat.getRendererClass()
                            + " could not be accessed";
                    LOGGER.error(msg, e);
                    throw new RendererException(e);
                }

                checkAndCopyReferencedImages(texdoc, inputFile, outputFile,
                        outputFormat);

                String result = null;
                try {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Rendering text dom to output format "
                                + outputFormatName);
                    }

                    result = renderer.render(outputFormat, texdoc, resourceDir);
                } catch (final RendererException e) {
                    LOGGER.error("RendererException while rendering tex dom!", e);
                    throw new RendererException(e);
                } catch (final ConfigurationException e) {
                    LOGGER.error("Error on configuring Renderer", e);
                    throw new RendererException(e);
                }

                // writing output
                try {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Writing output to file " + outputFilePath);
                    }
                    outputFile.getParentFile().mkdirs();
                    final FileWriter tmpWriter = new FileWriter(outputFile,
                            false);
                    tmpWriter.write(result);
                    tmpWriter.flush();
                    tmpWriter.close();
                } catch (final IOException e) {
                    LOGGER.error("Exception while accessing output file!", e);
                    throw new RendererException(e);
                }

                success = true;

            } catch (final RendererException e) {
                // using this exception type for flow control above, already
                // logged and handled
            }
        }

        if (LOGGER.isInfoEnabled()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Conversion finished.");
            }
        }

        return success;
    }

    /**
     * @return the {@link ResourceLoader} used for accessing application
     *         resources
     */
    protected abstract ResourceLoader getResourceLoader();

    /**
     * Checks if node is a picture or has any childs which are pictures
     * (recursive). If yes, check where the file resides, choose correct
     * extension (log error if no fitting one is available), copy it to the
     * target directory, and adjust the path in the picture node accordingly.
     * 
     * @param aNode
     *            the node
     * @param inputFile
     *            used to find picture files
     * @param outputFile
     *            used to determine target location
     * @param outputFormat
     *            used to determine valid/preferred extensions
     */
    private static void checkAndCopyReferencedImages(final Node aNode,
            final File inputFile, final File outputFile,
            final OUTPUTFORMATS outputFormat) {
        if (aNode instanceof Picture) {

            File imageFile = new File(((Picture) aNode).getPath());
            if (!imageFile.isAbsolute()) {
                imageFile = new File(inputFile.getParentFile(), imageFile
                        .getPath());
            }

            // find extension if none is specified
            if (imageFile.getName().indexOf('.') == -1) {
                String chosenExtension = null;

                // find available extensions
                final Set<String> extensions = getAvailableExtensions(imageFile);
                // is there a format usable for the current output format
                final String[] formats = outputFormat.getValidPictureFormats();
                for (int i = 0; i < formats.length; i++) {
                    if (extensions.contains(formats[i])) {
                        chosenExtension = formats[i];
                        break;
                    }
                }

                if (chosenExtension == null) {
                    // no valid extension found
                    LOGGER.error("No valid format found for picture "
                            + imageFile.getName()
                            + " (accepted extensions for output format "
                            + outputFormat
                            + " are: "
                            + Arrays.toString(outputFormat
                                    .getValidPictureFormats()) + ")");
                    imageFile = null;
                } else {
                    imageFile = new File(imageFile.getPath() + "."
                            + chosenExtension);
                }
            }

            // copy image file and update picture node with new path
            if (imageFile != null) {
                try {
                    FileUtils.copyFileToDirectory(imageFile, new File(
                            outputFile.getParentFile(), SUBFOLDER_IMAGES));
                    ((Picture) aNode).setPath(SUBFOLDER_IMAGES + "/"
                            + imageFile.getName());

                } catch (final IOException e) {
                    LOGGER.warn("Exception while copying image "
                            + imageFile.getName() + "!", e);
                }
            }
        } else if (aNode instanceof NodeWithChilds) {

            // check child nodes
            final NodeWithChilds tmpNode = (NodeWithChilds) aNode;
            for (int i = 0; tmpNode.getChildren() != null
                    && i < tmpNode.getNumChildren(); i++) {
                checkAndCopyReferencedImages(tmpNode.getChild(i), inputFile,
                        outputFile, outputFormat);
            }
        }
    }

    private static Set<String> getAvailableExtensions(final File pic) {

        final Set<String> extensions = new HashSet<String>();

        if (!pic.isDirectory()) {
            File dir = pic.getParentFile();
            if (dir == null) {
                dir = new File(".");
            }

            dir.list(new FilenameFilter() {

                public boolean accept(final File dir, final String name) {

                    final int extoffset = name.lastIndexOf('.');
                    if (extoffset != -1
                            && pic.getName().equals(
                                    name.substring(0, extoffset))) {
                        extensions.add(name.substring(extoffset + 1)
                                .toUpperCase());
                    }
                    return false;
                }
            });
        }

        return extensions;
    }

    private static String convertSlashes(final String path) {
        String tmpPath = path;
        if (tmpPath != null) {
            int tmpIndex = tmpPath.indexOf('\\');
            while (tmpIndex != -1) {
                tmpPath = tmpPath.substring(0, tmpIndex) + "/"
                        + tmpPath.substring(tmpIndex + 1, tmpPath.length());
                tmpIndex = tmpPath.indexOf('\\');
            }
        }
        return tmpPath;
    }

    private static Locale getLocale(final String localeCode) {
        Locale locale = null;

        if (localeCode != null) {
            Locale[] locales = Locale.getAvailableLocales();
            for (int i = 0; i < locales.length; i++) {
                if (localeCode.equalsIgnoreCase(locales[i].getISO3Language())) {
                    locale = locales[i];
                    break;
                }
            }
            if (locale == null) {
                locale = Locale.getDefault();
                LOGGER.warn("getLocale -> unknown locale code " + localeCode
                        + ", using system locale " + locale.getISO3Language()
                        + " instead.");
            }
        } else {
            locale = Locale.getDefault();
        }

        return locale;
    }
}
