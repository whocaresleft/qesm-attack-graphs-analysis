package qesm.project.translator.scenario.dto.fail_condition;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class FailConditionXml {
    @XmlAttribute(required = true) public String type;

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FailConditionXml other = (FailConditionXml) obj;
		return Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return "FailConditionXml [type=" + type + "]";
	}
}
