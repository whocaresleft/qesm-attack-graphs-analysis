package qesm.project.translator.utility;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class GraphRefXml {
    @XmlAttribute public String ref;
    @XmlAttribute public String file;
}