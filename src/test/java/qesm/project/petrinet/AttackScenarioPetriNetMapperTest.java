package qesm.project.petrinet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIterable;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oristool.models.gspn.GSPNTransient;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.MarkingCondition;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Transition;
import org.oristool.petrinet.TransitionFeature;
import org.oristool.util.Pair;

import qesm.project.common.Exponential;
import qesm.project.common.GoalCombination;
import qesm.project.common.NoneDistribution;
import qesm.project.graph.AttackGraph;
import qesm.project.graph.AttackGraphBuilder;
import qesm.project.graph.step.Step;
import qesm.project.graph.step.StepBuilder;
import qesm.project.graph.vertex.Access;
import qesm.project.graph.vertex.Goal;
import qesm.project.graph.vertex.Knowledge;
import qesm.project.graph.vertex.Skill;
import qesm.project.petrinet.exception.InvalidStrategyWeightException;
import qesm.project.petrinet.exception.UnsupportedFailConditionException;
import qesm.project.petrinet.exception.UnsupportedStepDurationException;
import qesm.project.scenario.AttackScenario;
import qesm.project.scenario.AttackScenarioBuilder;
import qesm.project.scenario.failcondition.AnyDetection;
import qesm.project.scenario.failcondition.FailCondition;
import qesm.project.scenario.failcondition.NeverFail;
import qesm.project.scenario.strategy.AttackStrategy;
import qesm.project.scenario.strategy.Cautious;
import qesm.project.scenario.strategy.Greedy;
import qesm.project.scenario.strategy.LowHangingFruit;
import qesm.project.scenario.strategy.Random;

public class AttackScenarioPetriNetMapperTest {

	private static final double EPSILON = 1e-6;
	private static final double TIME_BOUND = 100.0;

	private Access accessA1;
	private Access accessA2;
	private Skill skillS1;
	private Knowledge knowledgeK1;
	private Goal goalG1;
	private Goal goalG2;
	private Step stepSt1;
	private Step stepSt2;
	private AttackGraph graph;
	private AttackScenarioPetriNetMapper mapper;

	@BeforeEach
	public void setup() {
		mapper = AttackScenarioPetriNetMapper.get();

		accessA1 = Access.create(uuid("a1"), "A1", Exponential.fromRate(0.05));
		accessA2 = Access.create(uuid("a2"), "A2", NoneDistribution.get());
		skillS1 = Skill.create(uuid("c1"), "S1");
		knowledgeK1 = Knowledge.create(uuid("d1"), "K1");
		goalG1 = Goal.create(uuid("e1"), "G1", 10.0, Exponential.fromRate(0.1));
		goalG2 = Goal.create(uuid("e2"), "G2", 5.0, NoneDistribution.get());

		stepSt1 = StepBuilder.create()
			.withUuid(uuid("51"))
			.withName("ST1")
			.withSuccessProbability(0.6)
			.withFailureProbability(0.3)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withDetectionTime(Exponential.fromRate(0.1))
			.addRequirement(accessA1)
			.addRequirement(skillS1)
			.addRequirement(knowledgeK1)
			.addOutcome(accessA2)
			.addOutcome(goalG1)
			.build();

		stepSt2 = StepBuilder.create()
			.withUuid(uuid("52"))
			.withName("ST2")
			.withSuccessProbability(1.0)
			.withFailureProbability(0.0)
			.withFailureDetectedProbability(0.0)
			.withDuration(Exponential.fromRate(2.0))
			.addRequirement(accessA1)
			.addOutcome(goalG2)
			.build();

		graph = AttackGraphBuilder.create()
			.withUuid(uuid("01"))
			.withName("graph")
			.addVertex(accessA1)
			.addVertex(accessA2)
			.addVertex(skillS1)
			.addVertex(knowledgeK1)
			.addVertex(goalG1)
			.addVertex(goalG2)
			.addStep(stepSt1)
			.addStep(stepSt2)
			.build();
	}

	@Test
	public void testMapVerticesToPlacesAndInitialMarking() {
		AttackScenarioPetriNet petriNet = mapScenario(new Random(), AnyDetection.get());
		Marking initialMarking = petriNet.getInitialMarking();

		assertThatIterable(petriNet.getNet().getPlaceNames())
			.contains("A1", "A2", "S1", "K1", "G1", "G2", "AttackerIdle");
		assertThat(initialMarking.getTokens("AttackerIdle")).isEqualTo(1);
		assertThat(initialMarking.getTokens("A1")).isEqualTo(1);
		assertThat(initialMarking.getTokens("S1")).isEqualTo(1);
		assertThat(initialMarking.getTokens("K1")).isEqualTo(1);
		assertThat(initialMarking.getTokens("A2")).isZero();
		assertThat(initialMarking.getTokens("G1")).isZero();
		assertThat(petriNet.getVertexPlace(goalG1.getUuid()))
			.hasValueSatisfying(place -> assertThat(place.getName()).isEqualTo("G1"));
	}

	@Test
	public void testMapCreatesDetectionSubnetOnlyForVerticesWithDetectionTime() {
		PetriNet net = mapScenario(new Random(), AnyDetection.get()).getNet();

		assertThatIterable(net.getTransitionNames())
			.contains("A1_detection", "G1_detection")
			.doesNotContain("A2_detection", "G2_detection");
		assertThatIterable(net.getPlaceNames())
			.contains("A1_detected", "G1_detected")
			.doesNotContain("A2_detected", "G2_detected");
		assertThat(isExponential(net.getTransition("A1_detection"), 0.05)).isTrue();
	}

	@Test
	public void testMapStepToCompleteSubnet() {
		PetriNet net = mapScenario(new Random(), AnyDetection.get()).getNet();

		assertThatIterable(net.getPlaceNames())
			.contains("ST1_ongoing", "ST1_ended", "ST1_detected", "ST1_detectedAtFailure");
		assertThatIterable(net.getTransitionNames())
			.contains("ST1_start", "ST1_execution", "ST1_detection", "ST1_success", "ST1_failure", "ST1_failureDetected");
		assertThat(isExponential(net.getTransition("ST1_execution"), 1.0)).isTrue();
		assertThat(isExponential(net.getTransition("ST1_detection"), 0.1)).isTrue();
		assertThat(immediateWeight(net, "ST1_success")).isCloseTo(0.6, within(EPSILON));
		assertThat(immediateWeight(net, "ST1_failure")).isCloseTo(0.3, within(EPSILON));
		assertThat(immediateWeight(net, "ST1_failureDetected")).isCloseTo(0.1, within(EPSILON));
	}

	@Test
	public void testMapStepWhenDetectionIsNoneAndFailureProbabilitiesAreZero() {
		PetriNet net = mapScenario(new Random(), AnyDetection.get()).getNet();

		assertThatIterable(net.getTransitionNames())
			.contains("ST2_start", "ST2_execution", "ST2_success")
			.doesNotContain("ST2_detection", "ST2_failure", "ST2_failureDetected");
		assertThatIterable(net.getPlaceNames())
			.doesNotContain("ST2_detected", "ST2_detectedAtFailure");
	}

	@Test
	public void testMapWhenVertexNamesAreDuplicateOrContainSpaces() {
		Skill duplicateSkill = Skill.create(uuid("c2"), "S1");
		Knowledge spacedKnowledge = Knowledge.create(uuid("d2"), "my knowledge-1");
		AttackGraph graphWithDuplicates = AttackGraphBuilder.create()
			.withUuid(uuid("02"))
			.withName("graph")
			.addVertex(skillS1)
			.addVertex(duplicateSkill)
			.addVertex(spacedKnowledge)
			.addVertex(goalG1)
			.build();
		AttackScenario scenario = createScenario(graphWithDuplicates, new Random(), NeverFail.get());

		AttackScenarioPetriNet petriNet = mapper.map(scenario);

		assertThatIterable(petriNet.getNet().getPlaceNames())
			.hasSize(6)
			.contains("S1", "S1_000000c2", "my_knowledge_1");
		assertThat(petriNet.getVertexPlace(skillS1.getUuid()).orElseThrow().getName())
			.isNotEqualTo(petriNet.getVertexPlace(duplicateSkill.getUuid()).orElseThrow().getName());
	}

	@Test
	public void testAttackerExecutesOneStepAtATimeUsingAttackerIdlePlace() {
		AttackScenarioPetriNet petriNet = mapScenario(new Random(), AnyDetection.get());
		PetriNet net = petriNet.getNet();
		Marking marking = petriNet.getInitialMarking();

		assertThat(net.isEnabled(net.getTransition("ST1_start"), marking)).isTrue();
		assertThat(net.isEnabled(net.getTransition("ST2_start"), marking)).isTrue();

		marking.setTokens(petriNet.getAttackerIdle(), 0);
		marking.setTokens(net.getPlace("ST1_ongoing"), 1);

		assertThat(net.isEnabled(net.getTransition("ST1_start"), marking)).isFalse();
		assertThat(net.isEnabled(net.getTransition("ST2_start"), marking)).isFalse();
	}

	@Test
	public void testStepStartIsDisabledWhenAllItsOutcomesAreAlreadyMarked() {
		AttackScenarioPetriNet petriNet = mapScenario(new Random(), AnyDetection.get());
		PetriNet net = petriNet.getNet();
		Marking marking = petriNet.getInitialMarking();

		marking.setTokens(net.getPlace("G1"), 1);
		assertThat(net.isEnabled(net.getTransition("ST1_start"), marking)).isTrue();

		marking.setTokens(net.getPlace("A2"), 1);
		assertThat(net.isEnabled(net.getTransition("ST1_start"), marking)).isFalse();
	}

	@Test
	public void testStepStartDoesNotConsumeRequirementPlaces() {
		PetriNet net = mapScenario(new Random(), AnyDetection.get()).getNet();
		Transition startTransition = net.getTransition("ST1_start");

		assertThat(net.getPrecondition(net.getPlace("A1"), startTransition)).isNotNull();
		assertThat(net.getPostcondition(startTransition, net.getPlace("A1"))).isNotNull();
		assertThat(net.getPrecondition(net.getPlace("S1"), startTransition)).isNotNull();
		assertThat(net.getPostcondition(startTransition, net.getPlace("S1"))).isNotNull();
		assertThat(net.getPrecondition(net.getPlace("K1"), startTransition)).isNotNull();
		assertThat(net.getPostcondition(startTransition, net.getPlace("K1"))).isNotNull();
	}

	@Test
	public void testStopConditionWhenFailConditionIsAnyDetection() {
		AttackScenarioPetriNet petriNet = mapScenario(new Random(), AnyDetection.get());

		assertThat(petriNet.getGoalCondition()).isEqualTo("G1>0");
		assertThat(petriNet.getFailCondition()).hasValueSatisfying(condition ->
			assertThat(condition).contains("A1_detected>0", "G1_detected>0", "ST1_detected>0", "ST1_detectedAtFailure>0"));
		assertThat(petriNet.getStopCondition()).startsWith("(G1>0) || (");
	}

	@Test
	public void testStopConditionWhenFailConditionIsNeverFail() {
		AttackScenarioPetriNet petriNet = mapScenario(new Random(), NeverFail.get());

		assertThat(petriNet.getFailCondition()).isEmpty();
		assertThat(petriNet.getStopCondition()).isEqualTo("G1>0");
	}

	@Test
	public void testOutcomesNeverExceedOneTokenDuringTransientAnalysis() {
		AttackScenarioPetriNet petriNet = mapScenario(new Random(), NeverFail.get());

		Pair<Map<Marking, Integer>, double[][]> result = runTransientAnalysis(petriNet, MarkingCondition.NONE);

		assertThat(result.first().keySet()).allSatisfy(marking -> {
			assertThat(marking.getTokens("A2")).isLessThanOrEqualTo(1);
			assertThat(marking.getTokens("G1")).isLessThanOrEqualTo(1);
			assertThat(marking.getTokens("G2")).isLessThanOrEqualTo(1);
			assertThat(marking.getTokens("AttackerIdle")).isLessThanOrEqualTo(1);
		});
	}

	@Test
	public void testTransientAnalysisWithStopConditionComputesGoalAndDetectionProbabilities() {
		AttackScenarioPetriNet petriNet = mapScenario(new Random(), AnyDetection.get());

		Pair<Map<Marking, Integer>, double[][]> result = runTransientAnalysis(petriNet, petriNet.getStopMarkingCondition());

		double goalProbability = probabilityAtEnd(result, MarkingCondition.fromString(petriNet.getGoalCondition()));
		double failProbability = probabilityAtEnd(result, MarkingCondition.fromString(petriNet.getFailCondition().orElseThrow()));

		assertThat(goalProbability).isGreaterThan(0.0);
		assertThat(failProbability).isGreaterThan(0.0);
		assertThat(goalProbability + failProbability).isLessThanOrEqualTo(1.0 + EPSILON);
	}

	@Test
	public void testMapWhenStrategyIsRandomAssignsEqualWeights() {
		PetriNet net = mapScenario(new Random(), NeverFail.get()).getNet();

		assertThat(immediateWeight(net, "ST1_start")).isEqualTo(immediateWeight(net, "ST2_start"));
	}

	@Test
	public void testMapWhenStrategyIsGreedyAssignsHigherWeightToHigherValueGoal() {
		PetriNet net = mapScenario(new Greedy(), NeverFail.get()).getNet();

		assertThat(immediateWeight(net, "ST1_start")).isGreaterThan(immediateWeight(net, "ST2_start"));
	}

	@Test
	public void testMapWhenStrategyIsCautiousAssignsHigherWeightToSaferStep() {
		PetriNet net = mapScenario(new Cautious(), NeverFail.get()).getNet();

		assertThat(immediateWeight(net, "ST2_start")).isGreaterThan(immediateWeight(net, "ST1_start"));
	}

	@Test
	public void testMapWhenStrategyIsLowHangingFruitAssignsHigherWeightToFasterAndMoreLikelyStep() {
		PetriNet net = mapScenario(new LowHangingFruit(), NeverFail.get()).getNet();

		assertThat(immediateWeight(net, "ST2_start")).isGreaterThan(immediateWeight(net, "ST1_start"));
	}

	@Test
	public void testMapWhenCustomPriorityStrategyIsUsedAppliesPriorityToStartTransitions() {
		AttackStrategy priorityStrategy = new AttackStrategy() {
			@Override
			public int priorityOf(Step step, AttackScenario scenario) {
				return step.equals(stepSt2) ? 5 : 1;
			}
		};

		PetriNet net = mapScenario(priorityStrategy, NeverFail.get()).getNet();

		assertThat(featureOf(net.getTransition("ST2_start"), Priority.class).value()).isEqualTo(5);
		assertThat(featureOf(net.getTransition("ST1_start"), Priority.class).value()).isEqualTo(1);
	}

	@Test
	public void testMapWhenStrategyReturnsNonPositiveWeightThrowsException() {
		AttackStrategy invalidStrategy = new AttackStrategy() {
			@Override
			public double weightOf(Step step, AttackScenario scenario) {
				return 0.0;
			}
		};

		assertThatExceptionOfType(InvalidStrategyWeightException.class)
			.isThrownBy(() -> mapScenario(invalidStrategy, NeverFail.get()));
	}

	@Test
	public void testMapWhenStepDurationIsNotExponentialThrowsException() {
		Step stepWithNoDuration = StepBuilder.create()
			.withUuid(uuid("53"))
			.withName("ST3")
			.withSuccessProbability(1.0)
			.withFailureProbability(0.0)
			.withFailureDetectedProbability(0.0)
			.withDuration(NoneDistribution.get())
			.addRequirement(accessA1)
			.addOutcome(goalG1)
			.build();
		AttackGraph graphWithUnsupportedStep = AttackGraphBuilder.create()
			.withUuid(uuid("03"))
			.withName("graph")
			.addVertex(accessA1)
			.addVertex(goalG1)
			.addStep(stepWithNoDuration)
			.build();
		AttackScenario scenario = createScenario(graphWithUnsupportedStep, new Random(), NeverFail.get());

		assertThatExceptionOfType(UnsupportedStepDurationException.class)
			.isThrownBy(() -> mapper.map(scenario));
	}

	@Test
	public void testMapWhenFailConditionIsUnsupportedThrowsException() {
		FailCondition unsupportedFailCondition = new FailCondition() {};
		AttackScenario scenario = createScenario(graph, new Random(), unsupportedFailCondition);

		assertThatExceptionOfType(UnsupportedFailConditionException.class)
			.isThrownBy(() -> mapper.map(scenario));
	}

	private AttackScenarioPetriNet mapScenario(AttackStrategy strategy, FailCondition failCondition) {
		return mapper.map(createScenario(graph, strategy, failCondition));
	}

	private AttackScenario createScenario(AttackGraph attackGraph, AttackStrategy strategy, FailCondition failCondition) {
		AttackScenarioBuilder builder = AttackScenarioBuilder.create()
			.withUuid(uuid("02"))
			.withName("scenario")
			.withGraph(attackGraph)
			.withGoalCombination(GoalCombination.ANY)
			.withTimeBound(TIME_BOUND)
			.withStrategy(strategy)
			.withFailCondition(failCondition)
			.addTarget(goalG1);
		for (var vertex : attackGraph.getVertices()) {
			if (vertex.equals(accessA1) || vertex.equals(skillS1) || vertex.equals(knowledgeK1)) {
				builder.addVertexToInitialMarking(vertex);
			}
		}
		return builder.build();
	}

	private static Pair<Map<Marking, Integer>, double[][]> runTransientAnalysis(AttackScenarioPetriNet petriNet, MarkingCondition stopCondition) {
		return GSPNTransient.builder()
			.timePoints(0.0, TIME_BOUND, TIME_BOUND / 10.0)
			.stopOn(stopCondition)
			.build()
			.compute(petriNet.getNet(), petriNet.getInitialMarking());
	}

	private static double probabilityAtEnd(Pair<Map<Marking, Integer>, double[][]> result, MarkingCondition condition) {
		double[][] probabilities = result.first().isEmpty() ? new double[0][0] : result.second();
		int lastIndex = probabilities.length - 1;
		double sum = 0.0;
		for (var entry : result.first().entrySet()) {
			if (condition.evaluate(entry.getKey())) {
				sum += probabilities[lastIndex][entry.getValue()];
			}
		}
		return sum;
	}

	private static double immediateWeight(PetriNet net, String transitionName) {
		StochasticTransitionFeature feature = featureOf(net.getTransition(transitionName), StochasticTransitionFeature.class);
		assertThat(feature.isIMM()).isTrue();
		return feature.weight().evaluate(new Marking());
	}

	private static boolean isExponential(Transition transition, double rate) {
		StochasticTransitionFeature feature = featureOf(transition, StochasticTransitionFeature.class);
		StochasticTransitionFeature expected = StochasticTransitionFeature.newExponentialInstance(BigDecimal.valueOf(rate));
		return feature.isEXP() && feature.density().toString().equals(expected.density().toString());
	}

	private static <F extends TransitionFeature> F featureOf(Transition transition, Class<F> type) {
		F feature = transition.getFeature(type);
		assertThat(feature).isNotNull();
		return feature;
	}

	private static UUID uuid(String suffix) {
		return UUID.fromString("00000000-0000-0000-0000-" + "0".repeat(12 - suffix.length()) + suffix);
	}
}
