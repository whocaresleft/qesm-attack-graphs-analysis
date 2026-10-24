package qesm.project.translator.utility;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class GraphRefXml {
    @XmlAttribute public String ref;
    @XmlAttribute public String file;
    
	@Override
	public int hashCode() {
		return Objects.hash(file, ref);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphRefXml other = (GraphRefXml) obj;
		return Objects.equals(file, other.file) && Objects.equals(ref, other.ref);
	}
}