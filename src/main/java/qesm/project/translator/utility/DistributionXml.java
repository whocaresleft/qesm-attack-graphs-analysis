package qesm.project.translator.utility;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class DistributionXml {
    @XmlAttribute(required = true) public String type;
    @XmlAttribute public Double rate;
}