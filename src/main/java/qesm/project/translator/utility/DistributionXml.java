package qesm.project.translator.utility;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class DistributionXml {
    @XmlAttribute(required = true) public String type;
    @XmlAttribute public Double rate;
    
	@Override
	public int hashCode() {
		return Objects.hash(rate, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DistributionXml other = (DistributionXml) obj;
		return Objects.equals(rate, other.rate) && Objects.equals(type, other.type);
	}
	
	@Override
	public String toString() {
		return "DistributionXml [type=" + type + ", rate=" + rate + "]";
	}
}