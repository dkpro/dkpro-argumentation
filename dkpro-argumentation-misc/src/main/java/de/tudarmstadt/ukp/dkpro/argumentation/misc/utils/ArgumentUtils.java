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

package org.dkpro.argumentation.misc.utils;

import org.dkpro.argumentation.misc.uima.JCasUtil2;
import org.dkpro.argumentation.types.ArgumentComponent;
import org.dkpro.argumentation.types.ArgumentUnitUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.jcas.JCas;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Habernal
 */
public class ArgumentUtils
{

    public static List<ArgumentComponent> removeAppealToEmotion(List<ArgumentComponent> list)
    {
        List<ArgumentComponent> result = new ArrayList<>();

        for (ArgumentComponent argumentComponent : list) {
            // do not include appeal to emotion
            if (!Boolean.valueOf(ArgumentUnitUtils.getProperty(argumentComponent,
                    ArgumentUnitUtils.PROP_KEY_IS_APPEAL_TO_EMOTION))) {
                result.add(argumentComponent);
            }
        }

        return result;
    }

    public static List<ArgumentComponent> removeImplicitComponents(
            List<ArgumentComponent> argumentComponents)
    {
        List<ArgumentComponent> result = new ArrayList<>();

        for (ArgumentComponent argumentComponent : argumentComponents) {
            if (!ArgumentUnitUtils.isImplicit(argumentComponent)) {
                result.add(argumentComponent);
            }
        }

        return result;
    }

    /**
     * Select argument components that are present in this sentence (by calling
     * {@code JCasUtil2#selectOverlapping()} and filters the results so that pathos dimension
     * and implicit components are ignored.
     *
     * @param sentence sentence
     * @param jCas     jcas
     * @return list of argument components
     */
    public static List<ArgumentComponent> selectOverlappingComponentsWithoutPathosAndImplicit(
            Sentence sentence, JCas jCas)
    {

        List<ArgumentComponent> result = JCasUtil2
                .selectOverlapping(ArgumentComponent.class, sentence, jCas);

        // remove appeal to emotion
        result = ArgumentUtils.removeAppealToEmotion(result);

        // remove implicit arguments (if there are any remaining)
        result = ArgumentUtils.removeImplicitComponents(result);

        return result;
    }
}
