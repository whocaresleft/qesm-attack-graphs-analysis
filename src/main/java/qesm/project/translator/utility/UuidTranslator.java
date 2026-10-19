package qesm.project.translator.utility;

import java.util.UUID;

import qesm.project.translator.exception.AttackGraphLoadException;

public class UuidTranslator {
	public static UUID parseUuid(String raw) {
		try {
			return UUID.fromString(raw);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new AttackGraphLoadException("Invalid UUID: '" + raw + "'", e);
		}
	}
}
