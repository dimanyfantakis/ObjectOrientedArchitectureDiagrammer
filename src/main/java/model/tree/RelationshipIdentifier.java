package model.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**This class is responsible for the creation of the branches between the Java
 * source files. The branches have a type, e.g., inheritance, implementation.
 * The branches are also directed with a starting and an ending node
 */
public class RelationshipIdentifier {
	private final Map<String, PackageNode> packageNodes;
	private final List<LeafNode> allLeafNodes;

	/**This method is responsible for retrieving the leaf nodes that have been created
	 * and then creating the branches between them
	 * @param packageNodes a collection with the package nodes created by the parser
	 */
	public RelationshipIdentifier(Map<String, PackageNode> packageNodes) {
		this.packageNodes = packageNodes;
		allLeafNodes = new ArrayList<>();
		populateLeafNodes();
		identifyLeafNodesRelationship();
	}
	
	private void populateLeafNodes() {
		for (PackageNode p: packageNodes.values()) {
			allLeafNodes.addAll(p.getLeafNodes().values());
		}
	}
	
	private void identifyLeafNodesRelationship() {
		for (int i = 0; i < allLeafNodes.size(); i++) {
			  for (int j = i+1; j < allLeafNodes.size(); j++) {
				  checkRelationship(i, j);
				  checkRelationship(j, i);
			  }
		}
	}
	
	private void checkRelationship(int i, int j) {
		if (isDependency(i, j)) {
			createRelationship(i, j, RelationshipType.DEPENDENCY);
		}else if (isAggregation(i, j)) {
			createRelationship(i, j, RelationshipType.AGGREGATION);
		}else if (isAssociation(i, j)) {
			createRelationship(i, j, RelationshipType.ASSOCIATION);
		}
		if (isInheritance(i)) {
			if (isExtension(i, j)) {
				createRelationship(i, j, RelationshipType.EXTENSION);
			}
			if (isImplementation(i, j)) {
				createRelationship(i, j, RelationshipType.IMPLEMENTATION);
			}
		}
	}
	
	private boolean isDependency(int i, int j) {
		return isRelationshipDependency(allLeafNodes.get(i).getMethodParameterTypes(), allLeafNodes.get(j).getName()) ||
				isRelationshipDependency(allLeafNodes.get(i).getMethodsReturnTypes(), allLeafNodes.get(j).getName());
	}

	private boolean isAssociation(int i, int j) {
		return isRelationshipAssociation(allLeafNodes.get(i).getFieldsTypes(), allLeafNodes.get(j).getName());
	}
	
	private boolean isAggregation(int i, int j) {
		return isRelationshipAggregation(allLeafNodes.get(i).getFieldsTypes(), allLeafNodes.get(j).getName());
	}
	
	private boolean isInheritance(int i) {
		return allLeafNodes.get(i).getInheritanceLine().length > 2;
	}
	
	private boolean isExtension(int i, int j) {
		if ( allLeafNodes.get(i).getInheritanceLine()[2].equals("extends") ) {
			for (int k = 0; k < allLeafNodes.size(); k++) {
				if (allLeafNodes.get(i).getInheritanceLine()[3].equals(allLeafNodes.get(j).getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isImplementation(int i, int j) {
		if ( allLeafNodes.get(i).getInheritanceLine()[2].equals("implements") ) {
			for (int l = 3; l < allLeafNodes.get(i).getInheritanceLine().length; l++) {
				if (allLeafNodes.get(i).getInheritanceLine()[l].equals(allLeafNodes.get(j).getName())) {
					return true;
				}
			}
		}else if (allLeafNodes.get(i).getInheritanceLine().length > 5 && allLeafNodes.get(i).getInheritanceLine()[4].equals("implements")) {
			for (int l = 5; l < allLeafNodes.get(i).getInheritanceLine().length; l++) {
				if (allLeafNodes.get(i).getInheritanceLine()[l].equals(allLeafNodes.get(j).getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isRelationshipDependency(List<String> leafNodesTypes, String leafNodesName) {
		for (String leafNodesType : leafNodesTypes) {
			if (leafNodesType.equals(leafNodesName)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isRelationshipAssociation(List<String> leafNodesTypes, String leafNodesName) {
		for (String leafNodesType : leafNodesTypes) {
			if (isFieldOfTypeClassObject(leafNodesType, leafNodesName)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isRelationshipAggregation(List<String> leafNodesTypes, String leafNodesName) {
		for (String leafNodeType: leafNodesTypes) {
			if (isFieldOfTypeList(leafNodeType, leafNodesName) && isListOfTypeClassObject(leafNodeType, leafNodesName)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isFieldOfTypeClassObject(String leafNodesType, String leafNodesName) {
		return leafNodesName.equals(leafNodesType);
	}
	
	private boolean isListOfTypeClassObject(String fieldType, String leafNodesName) {
		return fieldType.contains(leafNodesName);
	}

	private boolean isFieldOfTypeList(String s, String leafNodesName) {
		return (s.startsWith("List") || s.startsWith("ArrayList") || s.startsWith("Map") || s.startsWith("HashMap") || s.contains(leafNodesName+"["));
	}
	
	private void createRelationship(int i, int j, RelationshipType relationshipType) {
		allLeafNodes.get(i).addNodeRelationship(new Relationship(allLeafNodes.get(i), allLeafNodes.get(j), relationshipType));

		for (Relationship r: allLeafNodes.get(i).getParentNode().getNodeRelationships()) {
			if (doesPackageRelationshipAlreadyExist(j, r)) {
				return;
			}
		}
		if (!isRelationshipBetweenTheSamePackages(i, j)) {
			allLeafNodes.get(i).getParentNode().addNodeRelationship(new Relationship(allLeafNodes.get(i).getParentNode(),
					allLeafNodes.get(j).getParentNode(), RelationshipType.DEPENDENCY));
		}

	}

	private boolean doesPackageRelationshipAlreadyExist(int j, Relationship r) {
		return r.getEndingNode().equals(allLeafNodes.get(j).getParentNode());
	}

	private boolean isRelationshipBetweenTheSamePackages(int i, int j) {
		return allLeafNodes.get(i).getParentNode().equals(allLeafNodes.get(j).getParentNode());
	}

}
