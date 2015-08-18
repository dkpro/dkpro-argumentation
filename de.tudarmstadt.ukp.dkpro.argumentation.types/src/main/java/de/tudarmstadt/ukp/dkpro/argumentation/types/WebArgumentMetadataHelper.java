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

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.jcas.JCas;

/**
 * Helper for {@linkplain WebArgumentMetadata} annotations.
 *
 * @author Ivan Habernal
 */
public class WebArgumentMetadataHelper
{
    private WebArgumentMetadataHelper()
    {
        // empty
    }

    /**
     * Creates new {@linkplain WebArgumentMetadata} in the target and copies all meta data.
     *
     * @param sourceView source jCas
     * @param targetView target jCas
     * @throws IllegalArgumentException if the {@code targetView} already contains
     *                                  {@linkplain WebArgumentMetadata}
     */
    public static void copy(final JCas sourceView, final JCas targetView)
            throws IllegalArgumentException
    {
        if (hasWebArgumentMetadata(targetView)) {
            throw new IllegalArgumentException(
                    "Target view already contains " + WebArgumentMetadata.class.getName());
        }

        WebArgumentMetadata source = get(sourceView);
        WebArgumentMetadata target = new WebArgumentMetadata(targetView);

        target.setAuthor(source.getAuthor());
        target.setDate(source.getDate());
        target.setDocType(source.getDocType());
        target.setOrigUrl(source.getOrigUrl());
        target.setTopic(source.getTopic());
        target.setThumbsDown(source.getThumbsDown());
        target.setThumbsUp(source.getThumbsUp());
        target.setNotes(source.getNotes());
        target.setOrigId(source.getOrigId());
        target.setTitle(source.getTitle());

        target.addToIndexes();
    }

    /**
     * Get the {@link de.tudarmstadt.ukp.dkpro.argumentation.types.WebArgumentMetadata} from the JCas.
     *
     * @throws IllegalArgumentException if no {@link DocumentMetaData} exists in the jCas
     */
    public static WebArgumentMetadata get(final JCas jCas)
    {
        FSIterator<FeatureStructure> iterator = jCas.getCas().getIndexRepository().getAllIndexedFS(
                CasUtil.getType(jCas.getCas(), WebArgumentMetadata.class));

        if (!iterator.hasNext()) {
            throw new IllegalArgumentException(new Throwable("CAS does not contain any "
                    + WebArgumentMetadata.class.getName()));
        }

        WebArgumentMetadata result = (WebArgumentMetadata) iterator.next();

        if (iterator.hasNext()) {
            throw new IllegalArgumentException(new Throwable("CAS contains more than one "
                    + WebArgumentMetadata.class.getName()));
        }

        return result;
    }

    /**
     * Returns {@code true} if {@code jCas} contains {@link WebArgumentMetadata}
     *
     * @param jCas jCas
     * @return boolean
     */

    public static boolean hasWebArgumentMetadata(final JCas jCas)
    {
        FSIterator<FeatureStructure> iterator = jCas.getCas().getIndexRepository().getAllIndexedFS(
                CasUtil.getType(jCas.getCas(), WebArgumentMetadata.class));

        return iterator.hasNext();
    }

}
