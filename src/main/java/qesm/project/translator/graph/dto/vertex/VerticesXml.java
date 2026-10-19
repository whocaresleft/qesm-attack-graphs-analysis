package qesm.project.translator.graph.dto.vertex;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class VerticesXml {
    @XmlElement(name = "access")    public List<AccessXml> accesses = new ArrayList<>();
    @XmlElement(name = "skill")     public List<SkillXml> skills = new ArrayList<>();
    @XmlElement(name = "knowledge") public List<KnowledgeXml> knowledge = new ArrayList<>();
    @XmlElement(name = "goal")      public List<GoalXml> goals = new ArrayList<>();
}