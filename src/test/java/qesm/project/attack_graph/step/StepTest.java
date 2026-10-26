package qesm.project.attack_graph.step;

import java.util.UUID;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import qesm.project.attack_graph.exception.OverallProbabilitiesOverOneException;
import qesm.project.attack_graph.exception.OverallProbabilitiesUnderOneException;
import qesm.project.utility.Distribution;
import qesm.project.utility.NoneDistribution;

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
		assertThatExceptionOfType(OverallProbabilitiesUnderOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ONE, ZERO_DOT_ZERO, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are under 1.000000 (0.100000)");
	}
	
	@Test
	void testValidateProbabilitiesOverOneDuringCreation() {
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ONE_DOT_ONE, ZERO_DOT_ZERO, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ZERO, ONE_DOT_ONE, ZERO_DOT_ZERO))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_ZERO, ZERO_DOT_ZERO, ONE_DOT_ONE))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
		
		assertThatExceptionOfType(OverallProbabilitiesOverOneException.class)
			.isThrownBy(() -> createStepFromProbabilities(ZERO_DOT_FIVE, ZERO_DOT_THREE, ZERO_DOT_THREE))
			.withMessage("The three probabilities are over 1.000000 (1.100000)");
	}
}
