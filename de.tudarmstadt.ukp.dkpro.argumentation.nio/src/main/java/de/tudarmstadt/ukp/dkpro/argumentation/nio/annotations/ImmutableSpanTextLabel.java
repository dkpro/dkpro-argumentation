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
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * An immutable {@link SpanTextLabel} implementation which is also JSON-serializable using
 * <a href="https://github.com/FasterXML/jackson">Jackson</a>.
 *
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Apr 29, 2016
 *
 */
@JsonPropertyOrder({ ImmutableSpanTextLabel.PROPERTY_TEXT_SPAN,
        ImmutableSpanTextLabel.PROPERTY_LABEL, ImmutableSpanTextLabel.PROPERTY_ATTRIBUTES })
public final class ImmutableSpanTextLabel
    implements Serializable, SpanTextLabel
{

    public static final String PROPERTY_ATTRIBUTES = "attrs";

    public static final String PROPERTY_LABEL = "label";

    public static final String PROPERTY_TEXT_SPAN = "textSpan";

    /**
     *
     */
    private static final long serialVersionUID = -5564537471325147494L;

    private final Map<Attribute, Object> attributes;

    private final transient int hashCode;

    private final String label;

    private final ImmutableSpanText spanText;

    /**
     *
     */
    @JsonCreator
    public ImmutableSpanTextLabel(
            @JsonProperty(PROPERTY_TEXT_SPAN) final ImmutableSpanText spanText,
            @JsonProperty(PROPERTY_LABEL) final String label,
            @JsonProperty(PROPERTY_ATTRIBUTES) final Map<Attribute, Object> attributes)
    {
        this.spanText = spanText;
        this.label = label;
        this.attributes = attributes;

        hashCode = createHashCode();
    }

    public int createHashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (label == null ? 0 : label.hashCode());
        result = prime * result + (spanText == null ? 0 : spanText.hashCode());
        result = prime * result + (attributes == null ? 0 : attributes.hashCode());
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
        if (!(obj instanceof ImmutableSpanTextLabel)) {
            return false;
        }
        final ImmutableSpanTextLabel other = (ImmutableSpanTextLabel) obj;
        if (label == null) {
            if (other.label != null) {
                return false;
            }
        }
        else if (!label.equals(other.label)) {
            return false;
        }
        if (spanText == null) {
            if (other.spanText != null) {
                return false;
            }
        }
        else if (!spanText.equals(other.spanText)) {
            return false;
        }
        return Objects.equals(attributes, other.attributes);
    }

    @Override
    public Map<Attribute, Object> getAttributes()
    {
        return Collections.unmodifiableMap(attributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.tudarmstadt.ukp.dkpro.argumentation.annotations.SpanAnnotation# getAnnotationType()
     */
    // @Override
    /*
     * (non-Javadoc)
     *
     * @see de.tudarmstadt.ukp.dkpro.argumentation.annotations.LabelledTextSpan# getLabel()
     */
    @Override
    @JsonProperty(PROPERTY_LABEL)
    public String getLabel()
    {
        return label;
    }

    // @Override
    /*
     * (non-Javadoc)
     *
     * @see de.tudarmstadt.ukp.dkpro.argumentation.annotations.LabelledTextSpan# getTextSpan()
     */
    @Override
    @JsonProperty(PROPERTY_TEXT_SPAN)
    public ImmutableSpanText getSpanText()
    {
        return spanText;
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
        builder.append("ImmutableLabelledTextSpan [getTextSpan()=");
        builder.append(getSpanText());
        builder.append(", getLabel()=");
        builder.append(getLabel());
        builder.append(", getAttributes()=");
        builder.append(getAttributes());
        builder.append("]");
        return builder.toString();
    }

}
