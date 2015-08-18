/*
 * Copyright 2014
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tudarmstadt.ukp.dkpro.argumentation.types;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Tools for handling java {@code Properties} as a {@code properties} field
 * in {@linkplain de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit}
 *
 * @author Ivan Habernal
 */
public class ArgumentUnitUtils
{

    /**
     * Property key for information whether argument unit is implicit
     */
    public static final String PROP_KEY_IS_IMPLICIT = "implicit";

    /**
     * Property key for rephrased content of argument unit
     */
    public static final String PROP_KEY_REPHRASED_CONTENT = "rephrasedContent";

    /**
     * Property key whether argument unit is appeal to emotion.
     */
    public static final String PROP_KEY_IS_APPEAL_TO_EMOTION = "appealToEmotion";

    /**
     * Converts String-serialized properties from {@code properties} field to
     * {@linkplain Properties} object. If null, returns empty properties.
     *
     * @param propertiesString propertiesString string
     * @return properties
     */
    protected static Properties stringToProperties(String propertiesString)
    {
        Properties properties = new Properties();

        if (propertiesString != null) {
            try {
                properties.load(new StringReader(propertiesString));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return properties;
    }

    /**
     * Serializes properties to String
     *
     * @param properties properties
     * @return string
     */
    protected static String propertiesToString(Properties properties)
    {
        StringWriter stringWriter = new StringWriter();

        String comments = Properties.class.getName();
        try {
            properties.store(stringWriter, comments);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        stringWriter.flush();
        return stringWriter.toString();
    }

    /**
     * Sets the given properties to the argumentUnit (into the {@code properties} field).
     *
     * @param argumentUnit argumentUnit
     * @param properties   properties
     * @throws IllegalArgumentException if params are null
     */
    public static void setProperties(ArgumentUnit argumentUnit, Properties properties)
            throws IllegalArgumentException
    {
        if (argumentUnit == null) {
            throw new IllegalArgumentException("argumentUnit is null");
        }

        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }

        argumentUnit.setProperties(propertiesToString(properties));
    }

    /**
     * Extract properties from {@code properties} field of the given argument unit
     *
     * @param argumentUnit argument unit
     * @return properties
     * @throws IllegalArgumentException if argumentUnit is null
     */
    public static Properties getProperties(ArgumentUnit argumentUnit)
            throws IllegalArgumentException
    {
        if (argumentUnit == null) {
            throw new IllegalArgumentException("argumentUnit is null");
        }

        return stringToProperties(argumentUnit.getProperties());
    }

    /**
     * Sets the property
     *
     * @param argumentUnit  argument component
     * @param propertyName  property name
     * @param propertyValue property value
     * @return the previous value of the specified key in this property
     * list, or {@code null} if it did not have one.
     */
    public static String setProperty(ArgumentUnit argumentUnit, String propertyName,
            String propertyValue)
    {
        Properties properties = ArgumentUnitUtils.getProperties(argumentUnit);
        String result = (String) properties.setProperty(propertyName, propertyValue);
        ArgumentUnitUtils.setProperties(argumentUnit, properties);

        return result;
    }

    /**
     * Returns the property value
     *
     * @param argumentUnit argument component
     * @param propertyName property name
     * @return the value to which the specified key is mapped, or
     * {@code null} if this map contains no mapping for the key
     */
    public static String getProperty(ArgumentUnit argumentUnit, String propertyName)
    {
        Properties properties = ArgumentUnitUtils.getProperties(argumentUnit);
        return (String) properties.get(propertyName);
    }

    /**
     * Returns true is the argumentUnit length is 0 and flag is set to implicit. If length is
     * greater than zero and flag is not set to implicit, returns false. In any other case, throws
     * an exception
     *
     * @param argumentUnit argument unit
     * @return boolean
     * @throws java.lang.IllegalStateException if the implicit flag and length are inconsistent
     */
    public static boolean isImplicit(ArgumentUnit argumentUnit)
            throws IllegalStateException
    {
        // is the implicit flag set?
        String implicitProperty = ArgumentUnitUtils.getProperty(argumentUnit,
                ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT);

        // is the length really zero?
        int length = argumentUnit.getEnd() - argumentUnit.getBegin();
        boolean zeroSize = length == 0;

        if (implicitProperty != null && Boolean.valueOf(implicitProperty) && zeroSize) {
            return true;
        }

        if (implicitProperty == null && !zeroSize) {
            return false;
        }

        throw new IllegalStateException(
                "argumentUnit is inconsistent. 'implicit' flag: " + implicitProperty
                        + ", but length: " + length);
    }

    /**
     * Sets the implicit value to the argument
     *
     * @param argumentUnit argument unit
     * @param implicit     boolean value
     * @throws java.lang.IllegalArgumentException if the length of argument is non-zero
     */
    public static void setIsImplicit(ArgumentUnit argumentUnit, boolean implicit)
            throws IllegalArgumentException
    {
        int length = argumentUnit.getEnd() - argumentUnit.getBegin();

        if (length > 0) {
            throw new IllegalArgumentException("Cannot set 'implicit' property to component " +
                    "with non-zero length (" + length + ")");
        }

        ArgumentUnitUtils.setProperty(argumentUnit, ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT,
                Boolean.valueOf(implicit).toString());
    }
}
