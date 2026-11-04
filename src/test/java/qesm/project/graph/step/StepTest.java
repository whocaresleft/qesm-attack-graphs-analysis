package qesm.project.graph.step;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import qesm.project.common.Distribution;
import qesm.project.common.NoneDistribution;
import qesm.project.graph.exception.StepProbabilitySumOverOneException;
import qesm.project.graph.exception.StepProbabilitySumUnderOneException;

class StepTest {

	private static Distribution d = NoneDistribution.get();
	
	private static double ZERO_DOT_ZERO  = 0.0;
	private static double ZERO_DOT_ONE   = 0.1;
	private static double ZERO_DOT_THREE = 0.3;
	private static double ZERO_DOT_FIVE  = 0.5;
	private static double ONE_DOT_ONE    = 1.1;
	
	private static Step createStepFromProbabilities(double success, double failure, double failureDetected) {
		return StepBuilder.create()
				.withUuid(UUID.randomUUID())
				.withName("Step")
				.withSuccessProbability(success)
				.withFailureProbability(failure)
				.withFailureDetectedProbability(failureDetected)
				.withDuration(d)
				.withDetectionTime(d)
				.withRequirements(new HashSet<>())
				.withOutcomes(new HashSet<>())
				.build();
	}
	
	@Test
	void testValidateProbabilitiesUnderOneDuringCreation() {
		assertThatExceptionOfType(StepProbabilitySumUnderOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ONE, ZERO_DOT_ZERO, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are under 1.000000 (0.100000)");
	}
	
	@Test
	void testValidateProbabilitiesOverOneDuringCreation() {
		assertThatExceptionOfType(StepProbabilitySumOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ONE_DOT_ONE, ZERO_DOT_ZERO, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
		
		assertThatExceptionOfType(StepProbabilitySumOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ZERO, ONE_DOT_ONE, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
		
		assertThatExceptionOfType(StepProbabilitySumOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ZERO, ZERO_DOT_ZERO, ONE_DOT_ONE))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
		
		assertThatExceptionOfType(StepProbabilitySumOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_FIVE, ZERO_DOT_THREE, ZERO_DOT_THREE))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
	}
}
