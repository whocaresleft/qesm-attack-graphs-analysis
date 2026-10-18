package qesm.project.analysis.attack_graph.step;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import qesm.project.analysis.attack_graph.exception.OverallProbabilitiesOverOneException;
import qesm.project.analysis.attack_graph.exception.OverallProbabilitiesUnderOneException;
import qesm.project.analysis.attack_graph.vertex.Goal;
import qesm.project.analysis.attack_graph.vertex.Skill;

class StepTest {

	private Step step;
	private Requirement skill1;
	private Requirement skill2;
	private Outcome goal1;
	private Outcome goal2;
	
	@BeforeEach
	void setup() {
		step = new Step(UUID.randomUUID(), "Step", 0.4, 0.4, 0.2, new HashSet<>(), new HashSet<>());
		skill1 = new Skill(UUID.randomUUID(), "Skill 1");
		skill2 = new Skill(UUID.randomUUID(), "Skill 2");
		goal1 = new Goal(UUID.randomUUID(), "Goal 1", 0.0, 0.0);
		goal2 = new Goal(UUID.randomUUID(), "Goal 2", 0.0, 0.0);
	}
	
	@Test
	void testValidateProbabilitiesUnderOneDuringCreation() {
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> new Step(
				UUID.randomUUID(),
				"Step",
				0.1,
				0.0,
				0.0,
				new HashSet<>(),
				new HashSet<>()
			))
			.withMessage("The three probabilities are under 1.000000: 0.100000");
	}
	
	@Test
	void testValidateProbabilitiesOverOneDuringCreation() {
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> new Step(
				UUID.randomUUID(),
				"Step",
				1.1,
				0.0,
				0.0,
				new HashSet<>(),
				new HashSet<>()
			))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> new Step(
				UUID.randomUUID(),
				"Step",
				0.0,
				1.1,
				0.0,
				new HashSet<>(),
				new HashSet<>()
			))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> new Step(
				UUID.randomUUID(),
				"Step",
				0.0,
				0.0,
				1.1,
				new HashSet<>(),
				new HashSet<>()
			))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> new Step(
				UUID.randomUUID(),
				"Step",
				0.5,
				0.3,
				0.3,
				new HashSet<>(),
				new HashSet<>()
			))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
	}
	
	@Test
	void testValidateProbabilitiesOverOneAfterCreation() {
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> step.setSuccessProbability(0.5))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		assertThat(step.getSuccessProbability()).isEqualTo(0.4);
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> step.setFailureProbability(0.5))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		assertThat(step.getFailureProbability()).isEqualTo(0.4);
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> step.setFailureDetectedProbability(0.3))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		assertThat(step.getFailureDetectedProbability()).isEqualTo(0.2);
	}
	
	@Test
	void testValidateProbabilitiesUnderOneAfterCreation() {
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> step.setSuccessProbability(0.3))
			.withMessage("The three probabilities are under 1.000000: 0.900000");
		assertThat(step.getSuccessProbability()).isEqualTo(0.4);
		
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> step.setFailureProbability(0.3))
			.withMessage("The three probabilities are under 1.000000: 0.900000");
		assertThat(step.getFailureProbability()).isEqualTo(0.4);
		
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> step.setFailureDetectedProbability(0.1))
			.withMessage("The three probabilities are under 1.000000: 0.900000");
		assertThat(step.getFailureDetectedProbability()).isEqualTo(0.2);
	}
	
	@Test
	void testAddRequirementNotPresent() {
		Set<Requirement> requirements = new HashSet<>();
		requirements.add(skill2);
		step.setRequires(requirements);
		assertThat(step.getRequires()).containsExactly(skill2);
		step.addRequirement(skill1);
		assertThat(step.getRequires()).containsExactlyInAnyOrder(skill1, skill2);
	}
	
	@Test
	void testAddOutcomeNotPresent() {
		Set<Outcome> outcomes = new HashSet<>();
		outcomes.add(goal2);
		step.setProduces(outcomes);
		assertThat(step.getProduces()).containsExactly(goal2);
		step.addOutcome(goal1);
		assertThat(step.getProduces()).containsExactlyInAnyOrder(goal1, goal2);
	}
	
	@Test
	void testAddRequirementAlreadyPresent() {
		Set<Requirement> requirements = new HashSet<>();
		requirements.add(skill1);
		requirements.add(skill2);
		step.setRequires(requirements);
		assertThat(step.getRequires()).containsExactlyInAnyOrder(skill1, skill2);
		step.addRequirement(skill1);
		assertThat(step.getRequires()).containsExactlyInAnyOrder(skill1, skill2);
	}
	
	@Test
	void testAddOutcomeAlreadyPresent() {
		Set<Outcome> outcomes = new HashSet<>();
		outcomes.add(goal1);
		outcomes.add(goal2);
		step.setProduces(outcomes);
		assertThat(step.getProduces()).containsExactlyInAnyOrder(goal1, goal2);
		step.addOutcome(goal1);
		assertThat(step.getProduces()).containsExactlyInAnyOrder(goal1, goal2);
	}
}
