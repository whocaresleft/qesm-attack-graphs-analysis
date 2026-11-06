package qesm.project.graph.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import qesm.project.common.Distribution;
import qesm.project.common.NoneDistribution;
import qesm.project.graph.exception.MissingStepDurationException;
import qesm.project.graph.exception.NegativeStepProbabilityException;
import qesm.project.graph.exception.StepProbabilitySumOverOneException;
import qesm.project.graph.exception.StepProbabilitySumUnderOneException;

public class StepTest {

	private static final UUID STEP_UUID = UUID.fromString("00000000-0000-0000-0000-000000000051");
	private static final String STEP_NAME = "Step";
	private static final Distribution DEFAULT_DISTRIBUTION = NoneDistribution.get();

	@Test
	public void testBuildStepWhenProbabilitiesSumToOne() {
		Step step = createStepWithProbabilities(0.6, 0.3, 0.1);

		assertThat(step.getUuid()).isEqualTo(STEP_UUID);
		assertThat(step.getName()).isEqualTo(STEP_NAME);
		assertThat(step.getSuccessProbability()).isEqualTo(0.6);
		assertThat(step.getFailureProbability()).isEqualTo(0.3);
		assertThat(step.getFailureDetectedProbability()).isEqualTo(0.1);
	}

	@Test
	public void testBuildStepWhenProbabilitySumIsUnderOneThrowsException() {
		assertThatExceptionOfType(StepProbabilitySumUnderOneException.class)
			.isThrownBy(() -> createStepWithProbabilities(0.1, 0.0, 0.0))
			.withMessage("The three probabilities are under 1.000000 (0.100000)");
	}

	@Test
	public void testBuildStepWhenSuccessProbabilityExceedsOneThrowsException() {
		assertThatExceptionOfType(StepProbabilitySumOverOneException.class)
			.isThrownBy(() -> createStepWithProbabilities(1.1, 0.0, 0.0))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
	}

	@Test
	public void testBuildStepWhenFailureProbabilityExceedsOneThrowsException() {
		assertThatExceptionOfType(StepProbabilitySumOverOneException.class)
			.isThrownBy(() -> createStepWithProbabilities(0.0, 1.1, 0.0))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
	}

	@Test
	public void testBuildStepWhenFailureDetectedProbabilityExceedsOneThrowsException() {
		assertThatExceptionOfType(StepProbabilitySumOverOneException.class)
			.isThrownBy(() -> createStepWithProbabilities(0.0, 0.0, 1.1))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
	}

	@Test
	public void testBuildStepWhenProbabilitySumIsOverOneThrowsException() {
		assertThatExceptionOfType(StepProbabilitySumOverOneException.class)
			.isThrownBy(() -> createStepWithProbabilities(0.5, 0.3, 0.3))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
	}

	@Test
	public void testBuildStepWhenProbabilityIsNegativeThrowsException() {
		assertThatExceptionOfType(NegativeStepProbabilityException.class)
			.isThrownBy(() -> createStepWithProbabilities(-0.1, 0.6, 0.5))
			.withMessage("Probability is negative (-0.100000)");
	}

	@Test
	public void testBuildStepWhenDurationIsMissingThrowsException() {
		StepBuilder builder = StepBuilder.create()
			.withUuid(STEP_UUID)
			.withName(STEP_NAME)
			.withSuccessProbability(1.0);

		assertThatExceptionOfType(MissingStepDurationException.class)
			.isThrownBy(builder::build)
			.withMessage("Missing duration");
	}

	private static Step createStepWithProbabilities(double success, double failure, double failureDetected) {
		return StepBuilder.create()
			.withUuid(STEP_UUID)
			.withName(STEP_NAME)
			.withSuccessProbability(success)
			.withFailureProbability(failure)
			.withFailureDetectedProbability(failureDetected)
			.withDuration(DEFAULT_DISTRIBUTION)
			.withDetectionTime(DEFAULT_DISTRIBUTION)
			.withRequirements(new HashSet<>())
			.withOutcomes(new HashSet<>())
			.build();
	}
}
