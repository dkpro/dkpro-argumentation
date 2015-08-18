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

package de.tudarmstadt.ukp.dkpro.argumentation.misc.uima;

import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link JCasUtil2}
 *
 * @author Roland Kluge
 * @author Ivan Habernal
 */
public class JCasUtil2Test
{

    private JCas jCas;

    private Token tokenThis;

    private Token tokenIs;

    private Token tokenA;

    private Token tokenTest;

    private Token tokenDot;

    @Before
    public void setUp()
            throws Exception
    {
        jCas = JCasFactory.createJCas();
        jCas.setDocumentLanguage("en");
        jCas.setDocumentText("This is a test.");

        // Add some annotations
        tokenThis = new Token(jCas, 0, 4);
        tokenThis.addToIndexes();
        tokenIs = new Token(jCas, 5, 7);
        tokenIs.addToIndexes();
        tokenA = new Token(jCas, 8, 9);
        tokenA.addToIndexes();
        tokenTest = new Token(jCas, 10, 14);
        tokenTest.addToIndexes();
        tokenDot = new Token(jCas, 14, 15);
        tokenDot.addToIndexes();
    }

    @Test
    public void testGetToken()
            throws Exception
    {
        assertEquals("a", JCasUtil2.findTokenByBeginPosition(jCas, 8).getCoveredText());
        assertNull(JCasUtil2.findTokenByBeginPosition(jCas, 4));

        assertEquals("a", JCasUtil2.findTokenByEndPosition(jCas, 9).getCoveredText());
        assertNull(JCasUtil2.findTokenByEndPosition(jCas, 8));
    }

    @Test
    public void testPrecedingTokens()
            throws Exception
    {
        Token tokenFinalDot = JCasUtil2.findTokenByBeginPosition(jCas, 14);
        assertEquals(4, JCasUtil2.getPrecedingTokens(jCas, tokenFinalDot).size());

        Token firstToken = JCasUtil2.findTokenByBeginPosition(jCas, 0);
        assertEquals(0, JCasUtil2.getPrecedingTokens(jCas, firstToken).size());
    }

    @Test
    public void testTokenSpan()
            throws Exception
    {
        assertEquals(5, JCasUtil2.getTokenSpan(jCas, tokenThis, tokenDot).size());
        assertEquals(1, JCasUtil2.getTokenSpan(jCas, tokenThis, tokenThis).size());

        try {
            JCasUtil2.getTokenSpan(jCas, tokenIs, tokenThis);
            fail("should have thrown an exception");
        }
        catch (IllegalArgumentException ex) {
            //
        }
    }

    @Test
    public void testOverlap1()
            throws Exception
    {
        final JCas jCas = JCasFactory.createJCas();

        final Annotation annotation1 = this.addAnnotation(jCas, 0, 1);
        final Annotation annotation2 = this.addAnnotation(jCas, 1, 2);

        Assert.assertFalse(JCasUtil2.doOverlap(annotation1, annotation2));
    }

    @Test
    public void testOverlap2()
            throws Exception
    {
        final JCas jCas = JCasFactory.createJCas();

        final Annotation annotation1 = this.addAnnotation(jCas, 0, 2);
        final Annotation annotation2 = this.addAnnotation(jCas, 1, 2);

        Assert.assertTrue(JCasUtil2.doOverlap(annotation1, annotation2));
    }

    private Annotation addAnnotation(final JCas jCas, final int begin, final int end)
    {
        final Annotation annotation1 = new Annotation(jCas);
        annotation1.setBegin(begin);
        annotation1.setEnd(end);
        return annotation1;
    }

    @Test
    public void testCopyParagraphAnnotations()
            throws Exception
    {
        Paragraph p1 = new Paragraph(jCas);
        p1.setBegin(0);
        p1.setEnd(4);
        p1.addToIndexes();

        Paragraph p2 = new Paragraph(jCas);
        p2.setBegin(5);
        p2.setEnd(15);
        p2.addToIndexes();

        // 2 paragraphs
        assertEquals(JCasUtil.select(jCas, Paragraph.class).size(), 2);

        try {
            JCas targetJCas = JCasFactory.createJCas();
            JCasUtil2.copyParagraphAnnotations(jCas, targetJCas);
            targetJCas.setDocumentText("xxx");
            // should fail, the target has different text
            fail();
        }
        catch (IllegalArgumentException ex) {
            // empty
        }

        JCas targetJCas = JCasFactory.createJCas();
        targetJCas.setDocumentText(jCas.getDocumentText());
        // no paragraphs in target
        assertEquals(JCasUtil.select(targetJCas, Paragraph.class).size(), 0);

        JCasUtil2.copyParagraphAnnotations(jCas, targetJCas);
        // 2 paragraphs copied
        assertEquals(JCasUtil.select(targetJCas, Paragraph.class).size(), 2);

        try {
            JCasUtil2.copyParagraphAnnotations(jCas, targetJCas);
            // target has paragraphs already, should throw an exception
            fail();
        }
        catch (IllegalArgumentException ex) {
            // empty
        }

    }

    @Test
    public void testSelectOverlapping()
            throws Exception
    {
        {
            Sentence s1 = new Sentence(jCas);
            s1.setBegin(this.tokenThis.getBegin());
            s1.setEnd(this.tokenIs.getEnd());
            s1.addToIndexes();

            Sentence s2 = new Sentence(jCas);
            s2.setBegin(this.tokenA.getBegin());
            s2.setEnd(this.tokenDot.getEnd());
            s2.addToIndexes();
        }

        List<Sentence> sentences = new ArrayList<Sentence>(
                JCasUtil.select(jCas, Sentence.class));
        assertEquals(2, sentences.size());

        // annotation that covers "is" and "a" (each from different sentence)
        ArgumentComponent argumentComponent = new ArgumentComponent(jCas);
        argumentComponent.setBegin(tokenIs.getBegin());
        argumentComponent.setEnd(this.tokenA.getEnd());
        argumentComponent.addToIndexes();

        Collection<ArgumentComponent> argumentComponents = JCasUtil
                .select(jCas, ArgumentComponent.class);
        assertEquals(1, argumentComponents.size());

        ArgumentComponent component = argumentComponents.iterator().next();

        List<Token> coveredTokens = JCasUtil.selectCovered(Token.class, component);
        assertEquals(2, coveredTokens.size());
        assertEquals(this.tokenIs.getBegin(), coveredTokens.get(0).getBegin());
        assertEquals(this.tokenA.getBegin(), coveredTokens.get(1).getBegin());

        Sentence sent1 = sentences.get(0);
        Sentence sent2 = sentences.get(0);

        // !!! selectCovered won't find the ArgumentComponent annotation, as it crosses
        // the boundaries!!
        assertEquals(0, JCasUtil.selectCovered(ArgumentComponent.class, sent1).size());
        assertEquals(0, JCasUtil.selectCovering(ArgumentComponent.class, sent1).size());

        assertEquals(0, JCasUtil.selectCovered(ArgumentComponent.class, sent2).size());
        assertEquals(0, JCasUtil.selectCovering(ArgumentComponent.class, sent2).size());

        // now we select overlapping -- we get the same component from both sentences
        assertEquals(1, JCasUtil2.selectOverlapping(ArgumentComponent.class, sent1, jCas).size());
        assertEquals(1, JCasUtil2.selectOverlapping(ArgumentComponent.class, sent2, jCas).size());

        // and this is indeed the "component" that overlaps both sentences
        assertEquals(component,
                JCasUtil2.selectOverlapping(ArgumentComponent.class, sent1, jCas).iterator()
                        .next());
        assertEquals(component,
                JCasUtil2.selectOverlapping(ArgumentComponent.class, sent2, jCas).iterator()
                        .next());

    }

    @Test
    public void testSelectImplicitComponent()
            throws Exception
    {
        Sentence s1 = new Sentence(jCas);
        s1.setBegin(this.tokenThis.getBegin());
        s1.setEnd(this.tokenIs.getEnd());
        s1.addToIndexes();

        Sentence s = new ArrayList<Sentence>(JCasUtil.select(jCas, Sentence.class)).get(0);

        // it ignore implicit (zero-length) component -- here at [0, 0], sentence starts at 0
        ArgumentComponent implicitComponent = new ArgumentComponent(jCas, 0, 0);
        implicitComponent.addToIndexes();
        assertEquals(0, JCasUtil2.selectOverlapping(ArgumentComponent.class, s, jCas).size());
    }
}
