package edu.lmu.cs.xlg.manatee.entities;

import java.util.ArrayList;
import java.util.List;

import edu.lmu.cs.xlg.util.Log;

/**
 * ObjectType represents the declaration of a custom object
 */
public class ObjectType extends Type {
    private List<Property> properties;
    
    public ObjectType(String name, List<Property> properties) {
        super(name);
        this.properties = properties;
    }
    
    public List<Property> getProperties() {
        return properties;
    }
    
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
        ArrayList<String> history = new ArrayList<String>();
        
        // Check for duplicate property IDs
        for (Property p: this.properties) {
            if (history.contains(p.name)) {
                log.error("Duplicate property IDs in ObjectType.");
            } else {
                history.add(p.name);
            }
        }
        
        // Insert that beotch into the table if all is well
        table.insert(this, log);
        
        // Individually analyze all the properties in object
        for (Property p: this.properties){
            p.analyze(log, table, owner, inLoop);
        }
    }
    
    /**
     * Properties represent properties of an ObjectType
     */
    public static class Property extends Expression {
        private String name;
        private Type type;
        String typeName;
        private String parentType;
        
        public Property(String name, String type, String parentType) {
            this.name = name;
            this.typeName = type;
            this.parentType = parentType;
        }
        
        public String getName() {
            return name;
        }
        
        public Type getType() {
            return type;
        }
        
        public String getParentType() {
            return parentType;
        }
        
        @Override
        public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
            Type t = table.lookupType(typeName, log);
            if (t == null) {
                log.error("Invalid object property type.");
            } else {
                /*
                 * If the type of a property of an object is the object itself,
                 * don't bother analyzing the type since it is handled later.
                 */
                if (!t.getName().equals(this.getParentType())) {
                    t.analyze(log, table, owner, inLoop);
                    System.out.println("Object property analyzed and type assigned.");
                    type = t;
                }
            }
        }
    }
}