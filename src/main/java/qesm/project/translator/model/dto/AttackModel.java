package qesm.project.translator.model.dto;

import java.util.List;

import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_scenario.scenario.AttackScenario;

public record AttackModel(AttackGraph graph, List<AttackScenario> scenarios) {}