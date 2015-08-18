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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.*;

/**
 * Collection of advanced UIMA utility functions
 *
 * @author Roland Kluge
 * @author Ivan Habernal
 * @since 0.0.2
 */
public final class JCasUtil2
{

    private JCasUtil2()
    {
        throw new UnsupportedOperationException("utility class");
    }

    /**
     * Counts the number of features structures of the given type in the JCas
     *
     * @param type  the type
     * @param aJCas the JCas
     * @return the number of occurrences
     */
    public static <T extends TOP> int count(final Class<T> type, final JCas aJCas)
    {
        return JCasUtil.select(aJCas, type).size();
    }

    /**
     * Returns whether there is any feature structure of the given type
     *
     * @param type  the type
     * @param aJCas the JCas
     * @return whether there is any feature structure of the given type
     */
    public static <T extends TOP> boolean hasAny(final Class<T> type, final JCas aJCas)
    {
        return count(type, aJCas) != 0;
    }

    /**
     * Returns whether there is no feature structure of the given type
     *
     * @param type  the type
     * @param aJCas the JCas
     * @return whether there is no feature structure of the given type
     */
    public static <T extends TOP> boolean hasNo(final Class<T> type, final JCas aJCas)
    {
        return !hasAny(type, aJCas);
    }

    /**
     * Returns the number of {@link Token} in the CAS.
     *
     * @param aJCas the CAS
     * @return the token count
     */
    public static int getTokenCount(final JCas aJCas)
    {
        return count(Token.class, aJCas);
    }

    /**
     * Returns the number of {@link Sentence} in the CAS.
     *
     * @param aJCas the CAS
     * @return the sentence count
     */
    public static int getSentenceCount(final JCas aJCas)
    {
        return count(Sentence.class, aJCas);
    }

    /**
     * Returns the number of {@link Paragraph} in the CAS.
     *
     * @param aJCas the CAS
     * @return the paragraph count
     */
    public static int getParagraphCount(final JCas aJCas)
    {
        return count(Paragraph.class, aJCas);
    }

    /**
     * Convenience method that selects all feature structures of the given type and
     * creates a list from them.
     *
     * @param jCas the JCas
     * @return the list of all features structures of the given type in the JCas
     * @see JCasUtil#select(JCas, Class)
     */
    public static <T extends TOP> List<T> selectAsList(final JCas jCas, final Class<T> type)
    {
        return new ArrayList<T>(JCasUtil.select(jCas, type));
    }

    /**
     * Convenience method for getting all tokens in a JCas.
     *
     * @see Token
     * @see JCasUtil#select(JCas, Class)
     */
    public static Collection<Token> selectTokens(final JCas jCas)
    {
        return JCasUtil.select(jCas, Token.class);
    }

    /**
     * Convenience method that selects all tokens and creates a list from them.
     *
     * @param jCas the JCas
     * @return the list of tokens in the JCas
     * @see JCasUtil#select(JCas, Class)
     */
    public static List<Token> selectTokensAsList(final JCas jCas)
    {
        return selectAsList(jCas, Token.class);
    }

    /**
     * Convenience method for getting all setences in a JCas.
     *
     * @see Sentence
     * @see JCasUtil#select(JCas, Class)
     */
    public static Collection<Sentence> selectSentences(final JCas jCas)
    {
        return JCasUtil.select(jCas, Sentence.class);
    }

    /**
     * Returns whether the given annotations have a non-empty overlap.
     * <p/>
     * <p>
     * Note that this method is symmetric. Two annotations overlap
     * if they have at least one character position in common.
     * Annotations that merely touch at the begin or end are not
     * overlapping.
     * <p/>
     * <ul>
     * <li>anno1[0,1], anno2[1,2] => no overlap</li>
     * <li>anno1[0,2], anno2[1,2] => overlap</li>
     * <li>anno1[0,2], anno2[0,2] => overlap (same span)</li>
     * </ul>
     * </p>
     *
     * @param anno1 first annotation
     * @param anno2 second annotation
     * @return whether the annotations overlap
     */
    public static <T extends Annotation> boolean doOverlap(final T anno1, final T anno2)
    {
        return anno1.getEnd() > anno2.getBegin() && anno1.getBegin() < anno2.getEnd();
    }

    /**
     * Returns whether two annotations share the same span
     * <p/>
     * <p>
     * The method checks the spans based on the begin and end indices and not based on the
     * covered text.
     * </p>
     *
     * @param anno1 first annotation
     * @param anno2 second annotation
     * @return whether the spans are identical
     */
    public static boolean haveSameSpan(final Annotation anno1, final Annotation anno2)
    {
        return anno1.getBegin() == anno2.getBegin() && anno1.getEnd() == anno2.getEnd();
    }

    /**
     * Returns the JCas of this annotation.
     * <p/>
     * <p>
     * The method converts the potentially thrown {@link CASException} to an
     * unchecked {@link IllegalArgumentException}.
     * </p>
     *
     * @param annotation the annotation
     * @return the extracted JCas
     */
    public static JCas getJCas(final Annotation annotation)
    {
        JCas result = null;
        try {
            result = annotation.getCAS().getJCas();
        }
        catch (final CASException e) {
            throw new IllegalArgumentException(e);
        }

        return result;

    }

    /**
     * Returns whether the given token is the first token covered by the given annotation.
     *
     * @param token      the token
     * @param annotation the annotation
     * @return whether the token is the first covered token
     */
    public static boolean isFirstCoveredToken(final Token token, final Annotation annotation)
    {
        final JCas jCas = getJCas(annotation);
        final List<Token> coveredTokens = JCasUtil.selectCovered(jCas, Token.class, annotation);

        if (coveredTokens.isEmpty()) {
            return false;
        }
        else {
            final Token firstCoveredToken = coveredTokens.get(0);
            return haveSameSpan(token, firstCoveredToken);
        }
    }

    /**
     * Removes all of the given annotations from the index
     *
     * @param featureStructures the annotations
     * @see Annotation#removeFromIndexes()
     */
    public static void removeFromIndexes(final Iterable<? extends TOP> featureStructures)
    {
        for (final TOP featureStructure : featureStructures) {
            featureStructure.removeFromIndexes();
        }
    }

    public static <T extends TOP> void removeFromIndexes(final JCas jCas, final Class<T> type)
    {
        removeFromIndexes(new ArrayList<T>(JCasUtil.select(jCas, type)));
    }

    /**
     * Adds all of the given annotations to the indexes.
     *
     * @param featureStructures the annotations
     * @see Annotation#addToIndexes()
     */
    public static void addToIndexes(final Iterable<? extends TOP> featureStructures)
    {
        for (final TOP featureStructure : featureStructures) {
            featureStructure.addToIndexes();
        }
    }

    /**
     * Sets the end value of the annotation, updating indexes appropriately
     *
     * @param annotation the annotation
     * @param end        the new end value
     */
    public static void updateEnd(final Annotation annotation, final int end)
    {
        annotation.removeFromIndexes();
        annotation.setEnd(end);
        annotation.addToIndexes();
    }

    /**
     * Sets the begin value of the annotation, updating indexes appropriately
     *
     * @param annotation the annotation
     * @param begin      the new begin value
     */
    public static void updateBegin(final Annotation annotation, final int begin)
    {
        annotation.removeFromIndexes();
        annotation.setBegin(begin);
        annotation.addToIndexes();
    }

    private static final String INITIAL_VIEW = "_InitialView";

    /**
     * Returns the initial view ("_InitialView") of the given jcas
     *
     * @param jCas the JCas
     * @return initial view
     */
    public static JCas getInitialView(JCas jCas)
    {
        try {
            return jCas.getView(INITIAL_VIEW);
        }
        catch (CASException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns token at the given position
     *
     * @param jCas  jCas
     * @param begin token begin position
     * @return Token or null
     */
    public static Token findTokenByBeginPosition(JCas jCas, int begin)
    {
        for (Token token : JCasUtil.select(getInitialView(jCas), Token.class)) {
            if (token.getBegin() == begin) {
                return token;
            }
        }

        return null;
    }

    /**
     * Returns token ending at the given position
     *
     * @param jCas jCas
     * @param end  end
     * @return Token or null
     */
    public static Token findTokenByEndPosition(JCas jCas, int end)
    {
        for (Token token : JCasUtil.select(getInitialView(jCas), Token.class)) {
            if (token.getEnd() == end) {
                return token;
            }
        }

        return null;
    }

    /**
     * Returns a list of tokens preceding the given token
     *
     * @param jCas  jCas
     * @param token token
     * @return list of tokens (may be empty if the token is the first one, but never null)
     */
    public static List<Token> getPrecedingTokens(JCas jCas, Token token)
    {
        List<Token> result = new ArrayList<Token>();

        for (Token t : JCasUtil.select(getInitialView(jCas), Token.class)) {
            if (t.getBegin() < token.getBegin()) {
                result.add(t);
            }
        }

        return result;
    }

    /**
     * Returns a list of tokens succeeding the given token
     *
     * @param jCas  jCas
     * @param token token
     * @return list of tokens (may be empty if the token is the last one, but never null)
     */
    public static List<Token> getSucceedingTokens(JCas jCas, Token token)
    {
        List<Token> result = new ArrayList<Token>();

        for (Token t : JCasUtil.select(getInitialView(jCas), Token.class)) {
            if (t.getBegin() > token.getBegin()) {
                result.add(t);
            }
        }

        return result;
    }

    /**
     * Returns a list of annotations of the same type preceding the given annotation
     *
     * @param jCas       jcas
     * @param annotation sentence
     * @return list of sentences sorted incrementally by position
     */
    public static List<Sentence> getPrecedingSentences(JCas jCas, Sentence annotation)
    {
        List<Sentence> result = new ArrayList<Sentence>();

        for (Sentence sentence : JCasUtil.select(getInitialView(jCas), Sentence.class)) {
            if (sentence.getBegin() < annotation.getBegin()) {
                result.add(sentence);
            }
        }

        Collections.sort(result, new Comparator<Sentence>()
        {
            @Override public int compare(Sentence o1, Sentence o2)
            {
                return o2.getBegin() - o1.getBegin();
            }
        });

        return result;
    }

    /**
     * Returns a list of annotations of the same type succeeding the given annotation
     *
     * @param jCas       jcas
     * @param annotation sentence
     * @return list of sentences sorted incrementally by position
     */
    public static List<Sentence> getSucceedingSentences(JCas jCas, Sentence annotation)
    {
        List<Sentence> result = new ArrayList<Sentence>();

        for (Sentence sentence : JCasUtil.select(getInitialView(jCas), Sentence.class)) {
            if (sentence.getBegin() > annotation.getBegin()) {
                result.add(sentence);
            }
        }

        Collections.sort(result, new Comparator<Sentence>()
        {
            @Override public int compare(Sentence o1, Sentence o2)
            {
                return o2.getBegin() - o1.getBegin();
            }
        });

        return result;
    }

    /**
     * Returns a list of tokens starting with firstToken and ending with lastToken (incl.)
     *
     * @param jCas       jCas
     * @param firstToken first token of the span
     * @param lastToken  last token of the span
     * @return list (never empty, contains at least one token if firstToken = lastToken)
     * @throws IllegalArgumentException if last token precedes first token
     * @throws NullPointerException     if firstToken or lastToken are null
     */
    public static List<Token> getTokenSpan(JCas jCas, Token firstToken, Token lastToken)
    {
        if (firstToken == null) {
            throw new NullPointerException("firstToken is null");
        }

        if (lastToken == null) {
            throw new NullPointerException("lastToken is null");
        }

        if (firstToken.getBegin() > lastToken.getBegin()) {
            throw new IllegalArgumentException("firstToken (begin: " + firstToken.getBegin()
                    + ") appears after lastToken (begin: " + lastToken.getBegin() + ")");
        }

        List<Token> result = new ArrayList<Token>();

        for (Token t : JCasUtil.select(getInitialView(jCas), Token.class)) {
            if (t.getBegin() >= firstToken.getBegin() && t.getBegin() <= lastToken.getBegin()) {
                result.add(t);
            }
        }

        return result;
    }

    /**
     * Same as {@linkplain org.apache.uima.fit.util.JCasUtil#select(org.apache.uima.jcas.cas.FSArray, Class)}
     * but works across all views in jcas.
     *
     * @param jCas jcas
     * @param type desired type
     * @return collection of annotations
     */
    public static <T extends TOP> Collection<T> selectFromAllViews(JCas jCas, Class<T> type)
    {
        Collection<T> result = new ArrayList<T>();

        try {
            Iterator<JCas> viewIterator = jCas.getViewIterator();
            while (viewIterator.hasNext()) {
                JCas next = viewIterator.next();

                result.addAll(JCasUtil.select(next, type));
            }

            return result;
        }
        catch (CASException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates paragraph annotations in {@code target} by copying paragraphs from the {@code source}.
     *
     * @param source source jcas
     * @param target target jcas
     * @throws IllegalArgumentException if source text and target text are different or if target
     *                                  already contains paragraph annotations
     */
    public static void copyParagraphAnnotations(JCas source, JCas target)
            throws IllegalArgumentException
    {
        if (!source.getDocumentText().equals(target.getDocumentText())) {
            throw new IllegalArgumentException(
                    "source.documentText and target.documentText are not equal");
        }

        Collection<Paragraph> targetParagraphs = JCasUtil.select(target, Paragraph.class);
        if (!targetParagraphs.isEmpty()) {
            throw new IllegalArgumentException("target already contains paragraph annotations");
        }

        for (Paragraph paragraph : JCasUtil.select(source, Paragraph.class)) {
            Paragraph paragraphCopy = new Paragraph(target);
            paragraphCopy.setBegin(paragraph.getBegin());
            paragraphCopy.setEnd(paragraph.getEnd());
            paragraphCopy.addToIndexes();
        }
    }

    /**
     * Selects annotations with desired type ({@code type} parameter) that overlap the given
     * {@code annotation}, such that at least a part of the selected annotations
     * {@link #doOverlap(org.apache.uima.jcas.tcas.Annotation, org.apache.uima.jcas.tcas.Annotation)}
     * with the given {@code annotation}.
     * See {@code JCasUtil2Test.testSelectOverlapping()} for details.
     *
     * @param type       desired type
     * @param annotation current annotation for which the overlapping annotations are being selected
     * @param jCas       the JCas
     * @return collection of overlapping annotations
     */
    public static <T extends TOP> List<T> selectOverlapping(Class<T> type,
            Annotation annotation, JCas jCas)
    {
        Collection<T> allAnnotations = JCasUtil.select(jCas, type);

        List<T> result = new ArrayList<T>();

        for (T a : allAnnotations) {
            if ((a instanceof Annotation) && (JCasUtil2.doOverlap(annotation, (Annotation) a))) {
                result.add(a);
            }
        }

        return result;
    }
}
