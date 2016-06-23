/*
 * Copyright 2016
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
package de.tudarmstadt.ukp.dkpro.argumentation.io.annotations;

public interface Span
    extends Comparable<Span>
{

    @Override
    default int compareTo(final Span o)
    {
        int result = Integer.compare(getBegin(), o.getBegin());
        if (result == 0) {
            result = Integer.compare(getEnd(), o.getEnd());
        }
        return result;
    }

    /**
     * @return the begin
     */
    int getBegin();

    /**
     * @return the end
     */
    int getEnd();

}