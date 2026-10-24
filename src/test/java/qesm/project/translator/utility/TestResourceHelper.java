package qesm.project.translator.utility;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import qesm.project.translator.graph.AttackGraphXmlLoaderTest;

public class TestResourceHelper {
	
	public static Path resource(String relPath, String name) {
		try {
			URL url = AttackGraphXmlLoaderTest.class.getResource(relPath + name);
			if (url == null) throw new IllegalArgumentException("Missing test resource: " + relPath + name);
			return Path.of(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
}
