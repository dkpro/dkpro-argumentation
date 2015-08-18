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

import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Ivan Habernal
 */
@RunWith(JUnit4.class)
public class ArgumentUnitUtilsTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ArgumentUnit argumentUnit;
    private CAS cas;

    @Before
    public void setUp()
            throws Exception
    {
        cas = CasCreationUtils.createCas(
                TypeSystemDescriptionFactory.createTypeSystemDescription(), null, null);
        cas.setDocumentLanguage("en");
        cas.setDocumentText("The quick brown fox jumps over the lazy dog");

        argumentUnit = new ArgumentUnit(cas.getJCas());
        argumentUnit.setBegin(0);
        argumentUnit.setEnd(10);
        argumentUnit.addToIndexes();
    }

    @Test
    public void testSetProperties()
            throws Exception
    {
        // empty properties
        Properties properties = ArgumentUnitUtils.getProperties(argumentUnit);
        assert properties.isEmpty();

        properties = new Properties();
        properties.setProperty(ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT, Boolean.TRUE.toString());
        properties.setProperty(ArgumentUnitUtils.PROP_KEY_REPHRASED_CONTENT, "Some other text");

        ArgumentUnitUtils.setProperties(argumentUnit, properties);

        Properties properties2 = ArgumentUnitUtils.getProperties(argumentUnit);
        assertFalse(properties2.isEmpty());
        assertEquals(properties2.getProperty(ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT),
                Boolean.TRUE.toString());
        assertEquals(properties2.getProperty(ArgumentUnitUtils.PROP_KEY_REPHRASED_CONTENT),
                "Some other text");
    }

    @Test
    public void testEmptyProperties()
            throws Exception
    {
        Properties properties = new Properties();
        ArgumentUnitUtils.setProperties(argumentUnit, properties);

        Properties properties2 = ArgumentUnitUtils.getProperties(argumentUnit);
        assertTrue(properties2.isEmpty());
    }

    @Test
    public void testSetProperties2()
            throws Exception
    {
        ArgumentUnitUtils.setProperty(argumentUnit, ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT,
                Boolean.TRUE.toString());
        ArgumentUnitUtils.setProperty(argumentUnit, ArgumentUnitUtils.PROP_KEY_REPHRASED_CONTENT,
                "Some other text");

        Properties properties2 = ArgumentUnitUtils.getProperties(argumentUnit);
        assertFalse(properties2.isEmpty());
        assertEquals(properties2.getProperty(ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT),
                Boolean.TRUE.toString());
        assertEquals(properties2.getProperty(ArgumentUnitUtils.PROP_KEY_REPHRASED_CONTENT),
                "Some other text");
    }

    @Test
    public void testSetProperties3()
            throws Exception
    {
        ArgumentUnitUtils.setProperty(argumentUnit, ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT,
                Boolean.TRUE.toString());
        ArgumentUnitUtils.setProperty(argumentUnit, ArgumentUnitUtils.PROP_KEY_REPHRASED_CONTENT,
                "Some other text");

        assertEquals(Boolean.TRUE.toString(), ArgumentUnitUtils
                .getProperty(argumentUnit, ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT));
        assertEquals("Some other text", ArgumentUnitUtils
                .getProperty(argumentUnit, ArgumentUnitUtils.PROP_KEY_REPHRASED_CONTENT));
    }

    @Test
    public void testImplicit()
            throws Exception
    {
        assertFalse(ArgumentUnitUtils.isImplicit(argumentUnit));

        // create inconsistent implicit unit
        ArgumentUnit au1 = new ArgumentUnit(cas.getJCas());
        au1.setBegin(0);
        au1.setEnd(0);
        au1.addToIndexes();

        exception.expect(IllegalStateException.class);
        ArgumentUnitUtils.isImplicit(au1);

        // create inconsistent implicit unit
        ArgumentUnit au2 = new ArgumentUnit(cas.getJCas());
        au2.setBegin(1);
        au2.setEnd(2);
        au2.addToIndexes();
        ArgumentUnitUtils
                .setProperty(au2, ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT, Boolean.TRUE.toString());

        exception.expect(IllegalStateException.class);
        ArgumentUnitUtils.isImplicit(au2);

        // create implicit unit
        ArgumentUnit au3 = new ArgumentUnit(cas.getJCas());
        au3.setBegin(1);
        au3.setEnd(1);
        au3.addToIndexes();
        ArgumentUnitUtils
                .setProperty(au3, ArgumentUnitUtils.PROP_KEY_IS_IMPLICIT, Boolean.TRUE.toString());
        assertTrue(ArgumentUnitUtils.isImplicit(au3));
    }

    @Test
    public void testSetIsImplicit()
            throws Exception
    {
        // create implicit unit
        ArgumentUnit au = new ArgumentUnit(cas.getJCas());
        au.setBegin(1);
        au.setEnd(1);
        au.addToIndexes();

        ArgumentUnitUtils.setIsImplicit(au, true);
        assertTrue(ArgumentUnitUtils.isImplicit(au));

        // create implicit unit
        ArgumentUnit au2 = new ArgumentUnit(cas.getJCas());
        au2.setBegin(1);
        au2.setEnd(2);
        au2.addToIndexes();

        exception.expect(IllegalArgumentException.class);
        ArgumentUnitUtils.setIsImplicit(au2, true);
    }
}