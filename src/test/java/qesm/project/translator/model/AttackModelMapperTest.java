package qesm.project.translator.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qesm.project.translator.exception.AttackMapException;
import qesm.project.translator.graph.AttackGraphMapper;
import qesm.project.translator.model.dto.AttackModel;
import qesm.project.translator.scenario.AttackScenarioMapper;
import qesm.project.translator.utility.TestResourceHelper;

class AttackModelMapperTest {
	
	private AttackModelMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = AttackModelMapper.create(AttackGraphMapper.get(), AttackScenarioMapper.get());
	}
	
	@Nested
	class HappyCases {
		
		@Test
		void xmlGraph01() {
			AttackModel model = mapper.toAttackModel(AttackModelXmlLoader.get().load(resource("01-valid.xml")));
			
			assertThat(model.graph()).isNotNull();
			assertThat(model.scenarios()).hasSize(2);
			
			assertThat(model.scenarios().get(0)).isNotNull();
			assertThat(model.scenarios().get(1)).isNotNull();
		}
	}
	
	@Nested
	class ErrorCases{

		@Test
		void xmlGraph02NoGraphRefInScenarios() {
			assertThatExceptionOfType(AttackMapException.class)
				.isThrownBy(() -> mapper.toAttackModel(AttackModelXmlLoader.get().load(resource("02-invalid-model-scenarios-with-no-graph-ref.xml"))))
				.withMessage("Error while mapping: Graph uuid is not coherent with given AttackGraph");
		}
}	

	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/model/map/", name);
	}
}
