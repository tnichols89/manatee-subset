package edu.lmu.cs.xlg.manatee.entities;

import java.util.List;

import edu.lmu.cs.xlg.util.Log;

public class CallStatement extends Statement {

    private String procedureName;
    private List<Expression> args;
    private Procedure procedure;
    private Expression delay;

    public CallStatement(String procedureName, List<Expression> args, Expression delay) {
        this.procedureName = procedureName;
        this.args = args;
        this.delay = delay;
    }
    
    public Expression getDelay() {
        return delay;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public List<Expression> getArgs() {
        return args;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    @Override
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {

        // Analyze arguments first.
        for (Expression a: args) {
            a.analyze(log, table, owner, inLoop);
        }

        if (delay != null) {
	        delay.analyze(log, table, owner, inLoop);
	        delay.assertArithmetic("Procedure call delay", log);
        }
        
        // Find out which procedure we're referring to.
        procedure = table.lookupProcedure(procedureName, log);

        // If there's no such procedure, just bail on the rest of the analysis because we don't
        // want to generate spurious errors.
        if (procedure == null) {
            return;
        }

        // Now check all the arguments against all the parameters.
        procedure.assertCanBeCalledWith(args, log);
    }
}
