package parser;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import model.tree.NodeType;
import model.tree.RelationshipType;
import org.junit.jupiter.api.Test;
import model.tree.PackageNode;
import model.tree.Relationship;
import model.tree.LeafNode;

class TreeStructureArchitectureTest {

	Path currentDirectory = Path.of(".");

	@Test
	void getFieldAndMethodTypesTest() throws IOException {
		Parser parser = new ProjectParser();
		parser.parseSourcePackage(Paths.get(currentDirectory.toRealPath() + "\\src\\test\\resources\\LatexEditor\\src"));
		Map<Path, PackageNode> packages = parser.getPackageNodes();
		List<String> methodReturnTypes = new ArrayList<>(Arrays.asList("Constructor", "void"));
		List<String> fieldTypes = new ArrayList<>(List.of("VersionsManager"));
		List<String> methodParameterTypes = new ArrayList<>(List.of("VersionsManager"));

		PackageNode commandPackage = packages.get(Paths.get(currentDirectory.toRealPath().normalize().toString(), "\\src\\test\\resources\\LatexEditor\\src\\controller\\commands"));
		LeafNode addLatexCommand = commandPackage.getLeafNodes().get("AddLatexCommand");

		List<String> methodReturnTypesTest;
		List<String> fieldTypesTest;
		List<String> methodParameterTypesTest;
		methodParameterTypesTest = addLatexCommand.getMethodParameterTypes();
		fieldTypesTest = addLatexCommand.getFieldsTypes();
		methodReturnTypesTest = addLatexCommand.getMethodsReturnTypes();
		Collections.sort(methodReturnTypesTest);
		Collections.sort(methodReturnTypes);
		assertTrue(methodReturnTypesTest.size() == methodReturnTypes.size() 
				&& methodReturnTypes.containsAll(methodReturnTypesTest) 
				&& methodReturnTypesTest.containsAll(methodReturnTypes));
		Collections.sort(fieldTypesTest);
		Collections.sort(fieldTypes);
		assertTrue(fieldTypesTest.size() == fieldTypes.size() 
				&& fieldTypes.containsAll(fieldTypesTest) 
				&& fieldTypesTest.containsAll(fieldTypes));
		Collections.sort(methodParameterTypesTest);
		Collections.sort(methodParameterTypes);
		assertTrue(methodParameterTypesTest.size() == methodParameterTypes.size() 
				&& methodParameterTypes.containsAll(methodParameterTypesTest) 
				&& methodParameterTypesTest.containsAll(methodParameterTypes));
	}

	@Test
	void leafNodeRelationshipsTest() throws IOException {
		Parser parser = new ProjectParser();
		parser.parseSourcePackage(Paths.get(currentDirectory.toRealPath() + "\\src\\test\\resources\\LatexEditor\\src"));
		Map<Path, PackageNode> packages = parser.getPackageNodes();
		PackageNode commandPackage = packages.get(Paths.get(currentDirectory.toRealPath().normalize().toString(), "\\src\\test\\resources\\LatexEditor\\src\\controller\\commands"));

		LeafNode addLatexCommand = commandPackage.getLeafNodes().get("AddLatexCommand");
		List<Relationship> nodeRelationships = addLatexCommand.getNodeRelationships();
		
		boolean foundObligatoryRel = false;
		int counter = 0;
		for (Relationship relationship : nodeRelationships) {
			if ((relationship.getStartingNode().getName().equals("AddLatexCommand")) && (relationship.getEndingNode().getName().equals("VersionsManager"))) {
				if (relationship.getRelationshipType().equals(RelationshipType.DEPENDENCY)) {
					foundObligatoryRel = true;
				}else {
					foundObligatoryRel = relationship.getRelationshipType().equals(RelationshipType.ASSOCIATION);
				}
			} else if ((relationship.getStartingNode().getName().equals("AddLatexCommand")) && (relationship.getEndingNode().getName().equals("Command"))) {
				assertEquals(RelationshipType.IMPLEMENTATION, relationship.getRelationshipType());
				foundObligatoryRel = true;
			} else {
				foundObligatoryRel = false;
			}
			counter++;
		}
		assertEquals(3, counter);
		assertTrue(foundObligatoryRel);
		assertEquals(NodeType.CLASS, addLatexCommand.getType());

		LeafNode commandFactory = commandPackage.getLeafNodes().get("CommandFactory");
		nodeRelationships = commandFactory.getNodeRelationships();

		foundObligatoryRel = false;
		counter = 0;
		for(Relationship relationship: nodeRelationships) {
			if((relationship.getStartingNode().getName().equals("CommandFactory")) && (relationship.getEndingNode().getName().equals("VersionsManager"))) {
				if (relationship.getRelationshipType().equals(RelationshipType.DEPENDENCY)) {
					foundObligatoryRel = true;
				}else {
					foundObligatoryRel = relationship.getRelationshipType().equals(RelationshipType.ASSOCIATION);
				}
			}else if ((relationship.getStartingNode().getName().equals("CommandFactory")) && (relationship.getEndingNode().getName().equals("Command"))) {
				assertEquals(RelationshipType.DEPENDENCY, relationship.getRelationshipType());
				foundObligatoryRel = true;
			}else {
				foundObligatoryRel = false;
			}
			counter++;
		}

		assertTrue(foundObligatoryRel);
		assertEquals(4, counter);
		assertEquals(NodeType.CLASS, commandFactory.getType());
	}

	@Test
	void leadNodeInheritanceRelationshipTest() throws IOException {
		Parser parser = new ProjectParser();
		parser.parseSourcePackage(Paths.get(currentDirectory.toRealPath() + "\\src\\test\\resources\\InheritanceTesting\\src"));
		Map<Path, PackageNode> packages = parser.getPackageNodes();
		PackageNode sourcePackage = packages.get(Paths.get(currentDirectory.toRealPath().normalize().toString(), "\\src\\test\\resources\\InheritanceTesting\\src"));

		LeafNode implementingClassLeaf = sourcePackage.getLeafNodes().get("ImplementingClass");
		List<Relationship> nodeRelationships = implementingClassLeaf.getNodeRelationships();

		boolean foundObligatoryRel = false;
		int counter = 0;
		for(Relationship relationship: nodeRelationships) {
			if((relationship.getStartingNode().getName().equals("ImplementingClass")) && (relationship.getEndingNode().getName().equals("TestingInterface2"))) {
				assertEquals(RelationshipType.IMPLEMENTATION, relationship.getRelationshipType());
				foundObligatoryRel = true;
			}else if ((relationship.getStartingNode().getName().equals("ImplementingClass")) && (relationship.getEndingNode().getName().equals("ExtensionClass"))) {
				assertEquals(RelationshipType.EXTENSION, relationship.getRelationshipType());
				foundObligatoryRel = true;
			}else if ((relationship.getStartingNode().getName().equals("ImplementingClass")) && (relationship.getEndingNode().getName().equals("TestingInterface"))) {
				assertEquals(RelationshipType.IMPLEMENTATION, relationship.getRelationshipType());
				foundObligatoryRel = true;
			}else {
				foundObligatoryRel = false;
			}
			counter++;
		}

		assertTrue(foundObligatoryRel);
		assertEquals(3, counter);
		assertEquals(NodeType.CLASS, implementingClassLeaf.getType());
	}

	@Test
	void packageNodeRelationshipsTest() throws IOException {
		Parser parser = new ProjectParser();
		parser.parseSourcePackage(Paths.get(currentDirectory.toRealPath() + "\\src\\test\\resources\\LatexEditor\\src"));
		Map<Path, PackageNode> packages = parser.getPackageNodes();

		PackageNode commands = packages.get(Paths.get(currentDirectory.toRealPath().normalize().toString(), "\\src\\test\\resources\\LatexEditor\\src\\controller\\commands"));
		List<Relationship> packageRelationships = commands.getNodeRelationships();

		boolean foundObligatoryRel = false;
		int counter = 0;
		for(Relationship relationship: packageRelationships) {
			if((relationship.getStartingNode().getName().equals("src.controller.commands")) && (relationship.getEndingNode().getName().equals("src.model"))) {
				assertEquals(RelationshipType.DEPENDENCY, relationship.getRelationshipType());
				foundObligatoryRel = true;
			}else {
				foundObligatoryRel = false;
			}
			counter++;
		}

		assertTrue(foundObligatoryRel);
		assertEquals(1, counter);
		assertEquals(NodeType.PACKAGE, commands.getType());

		PackageNode controller = packages.get(Paths.get(currentDirectory.toRealPath().normalize().toString(), "\\src\\test\\resources\\LatexEditor\\src\\controller"));
		packageRelationships = controller.getNodeRelationships();

		foundObligatoryRel = false;
		counter = 0;
		for(Relationship relationship: packageRelationships) {
			if((relationship.getStartingNode().getName().equals("src.controller")) && (relationship.getEndingNode().getName().equals("src.model"))) {
				assertEquals(RelationshipType.DEPENDENCY, relationship.getRelationshipType());
				foundObligatoryRel = true;
			}else if((relationship.getStartingNode().getName().equals("src.controller")) && (relationship.getEndingNode().getName().equals("src.controller.commands"))) {
				assertEquals(RelationshipType.DEPENDENCY, relationship.getRelationshipType());
				foundObligatoryRel = true;
			}else {
				foundObligatoryRel = false;
			}
			counter++;
		}

		assertTrue(foundObligatoryRel);
		assertEquals(2, counter);
		assertEquals(NodeType.PACKAGE, commands.getType());
	}

	@Test
	void leafNodeTypesTest() throws IOException {
		Parser parser = new ProjectParser();
		parser.parseSourcePackage(Paths.get(currentDirectory.toRealPath() + "\\src\\test\\resources\\InheritanceTesting\\src"));
		Map<Path, PackageNode> packages = parser.getPackageNodes();
		PackageNode sourcePackage = packages.get(Paths.get(currentDirectory.toRealPath().normalize().toString(), "\\src\\test\\resources\\InheritanceTesting\\src"));
		List<LeafNode> classLeafs = new ArrayList<>();
		List<LeafNode> interfaceLeafs = new ArrayList<>();
		classLeafs.add(sourcePackage.getLeafNodes().get("ImplementingClass"));
		classLeafs.add(sourcePackage.getLeafNodes().get("ExtensionClass"));
		interfaceLeafs.add(sourcePackage.getLeafNodes().get("TestingInterface"));
		interfaceLeafs.add(sourcePackage.getLeafNodes().get("TestingInterface2"));
		for (LeafNode l: classLeafs) {
			assertEquals(NodeType.CLASS, l.getType());
		}
		for (LeafNode l: interfaceLeafs) {
			assertEquals(NodeType.INTERFACE, l.getType());
		}
	}

}

