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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * An immutable {@link Span} implementation which is also JSON-serializable using
 * <a href="https://github.com/FasterXML/jackson">Jackson</a>.
 *
 * @author Todd Shore
 * @since Jun 6, 2016
 *
 */
@JsonPropertyOrder({ ImmutableSpan.PROPERTY_BEGIN, ImmutableSpan.PROPERTY_END })
public final class ImmutableSpan
    implements Span, Serializable
{

    public static final String PROPERTY_BEGIN = "begin";

    public static final String PROPERTY_END = "end";

    /**
     *
     */
    private static final long serialVersionUID = 5872170320837177233L;

    private final int begin;

    private final int end;

    private final transient int hashCode;

    @JsonCreator
    public ImmutableSpan(@JsonProperty(PROPERTY_BEGIN) final int begin,
            @JsonProperty(PROPERTY_END) final int end)
    {
        this.begin = begin;
        this.end = end;

        hashCode = createHashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    public int createHashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + begin;
        result = prime * result + end;
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ImmutableSpan)) {
            return false;
        }
        final ImmutableSpan other = (ImmutableSpan) obj;
        if (begin != other.begin) {
            return false;
        }
        if (end != other.end) {
            return false;
        }
        return true;
    }

    @Override
    @JsonProperty(PROPERTY_BEGIN)
    public int getBegin()
    {
        return begin;
    }

    @Override
    @JsonProperty(PROPERTY_END)
    public int getEnd()
    {
        return end;
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("ImmutableSpan [getBegin()=");
        builder.append(getBegin());
        builder.append(", getEnd()=");
        builder.append(getEnd());
        builder.append("]");
        return builder.toString();
    }

}
