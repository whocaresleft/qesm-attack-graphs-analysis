package qesm.project.attack_graph.step;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import qesm.project.attack_graph.exception.OverallProbabilitiesOverOneException;
import qesm.project.attack_graph.exception.OverallProbabilitiesUnderOneException;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_graph.vertex.Skill;
import qesm.project.utility.Distribution;
import qesm.project.utility.Exponential;

class StepTest {

	private Step step;
	private Requirement skill1;
	private Requirement skill2;
	private Outcome goal1;
	private Outcome goal2;

	private static Distribution d = new Exponential(0.0);
	
	private static double ZERO_DOT_ZERO  = 0.0;
	private static double ZERO_DOT_ONE   = 0.1;
	private static double ZERO_DOT_TWO   = 0.2;
	private static double ZERO_DOT_THREE = 0.3;
	private static double ZERO_DOT_FOUR  = 0.4;
	private static double ZERO_DOT_FIVE  = 0.5;
	private static double ONE_DOT_ONE    = 1.1;
	
	private static Step createStepFromProbabilities(double success, double failure, double failureDetected) {
		return new Step(UUID.randomUUID(), "Step", success, failure, failureDetected, d, d, new HashSet<>(), new HashSet<>());
	}
	
	@BeforeEach
	void setup() {
		step   = new Step(UUID.randomUUID(), "Step", ZERO_DOT_FOUR, ZERO_DOT_FOUR, ZERO_DOT_TWO, d, d, new HashSet<>(), new HashSet<>());
		skill1 = new Skill(UUID.randomUUID(), "Skill 1");
		skill2 = new Skill(UUID.randomUUID(), "Skill 2");
		goal1  = new Goal(UUID.randomUUID(), "Goal 1", 0.0, d);
		goal2  = new Goal(UUID.randomUUID(), "Goal 2", 0.0, d);
	}
	
	@Test
	void testValidateProbabilitiesUnderOneDuringCreation() {
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ONE, ZERO_DOT_ZERO, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are under 1.000000: 0.100000");
	}
	
	@Test
	void testValidateProbabilitiesOverOneDuringCreation() {
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ONE_DOT_ONE, ZERO_DOT_ZERO, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ZERO, ONE_DOT_ONE, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ZERO, ZERO_DOT_ZERO, ONE_DOT_ONE))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_FIVE, ZERO_DOT_THREE, ZERO_DOT_THREE))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
	}
	
	@Test
	void testValidateProbabilitiesOverOneAfterCreation() {
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> step.setSuccessProbability(ZERO_DOT_FIVE))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		assertThat(step.getSuccessProbability()).isEqualTo(ZERO_DOT_FOUR);
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> step.setFailureProbability(ZERO_DOT_FIVE))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		assertThat(step.getFailureProbability()).isEqualTo(ZERO_DOT_FOUR);
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> step.setFailureDetectedProbability(ZERO_DOT_THREE))
			.withMessage("The three probabilities are over 1.000000: 1.100000");
		assertThat(step.getFailureDetectedProbability()).isEqualTo(ZERO_DOT_TWO);
	}
	
	@Test
	void testValidateProbabilitiesUnderOneAfterCreation() {
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> step.setSuccessProbability(ZERO_DOT_THREE))
			.withMessage("The three probabilities are under 1.000000: 0.900000");
		assertThat(step.getSuccessProbability()).isEqualTo(ZERO_DOT_FOUR);
		
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> step.setFailureProbability(ZERO_DOT_THREE))
			.withMessage("The three probabilities are under 1.000000: 0.900000");
		assertThat(step.getFailureProbability()).isEqualTo(ZERO_DOT_FOUR);
		
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> step.setFailureDetectedProbability(ZERO_DOT_ONE))
			.withMessage("The three probabilities are under 1.000000: 0.900000");
		assertThat(step.getFailureDetectedProbability()).isEqualTo(ZERO_DOT_TWO);
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
