package qesm.project.scenario.exception;

import java.util.UUID;

public class UnresolvedScenarioVertexException extends RuntimeException {

	private static final long serialVersionUID = 4416710116347400203L;

	private final UUID vertexUuid;

	public UnresolvedScenarioVertexException(UUID vertexUuid) {
		super("Vertex is not in graph");
		this.vertexUuid = vertexUuid;
	}

	public UUID getVertexUuid() { return vertexUuid; }
}
