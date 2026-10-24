package qesm.project.translator.graph.dto.vertex;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class VerticesXml {
    @XmlElement(name = "access")    public Set<AccessXml> accesses = new LinkedHashSet<>();
    @XmlElement(name = "skill")     public Set<SkillXml> skills = new LinkedHashSet<>();
    @XmlElement(name = "knowledge") public Set<KnowledgeXml> knowledge = new LinkedHashSet<>();
    @XmlElement(name = "goal")      public Set<GoalXml> goals = new LinkedHashSet<>();
    
	@Override
	public int hashCode() {
		return Objects.hash(accesses, goals, knowledge, skills);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VerticesXml other = (VerticesXml) obj;
		return Objects.equals(accesses, other.accesses) && Objects.equals(goals, other.goals)
				&& Objects.equals(knowledge, other.knowledge) && Objects.equals(skills, other.skills);
	}
}