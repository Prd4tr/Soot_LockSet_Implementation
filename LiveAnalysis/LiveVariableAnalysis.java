//package dfa;

import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.HybridPointsToSet;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.tagkit.Host;
import soot.tagkit.Tag;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Switch;

class LiveVariableAnalysis 
	extends ForwardFlowAnalysis<Unit, FlowSet<Local>>
{
	
	private FlowSet<Local> emptySet;
	private FlowSet<Local> locksHeld;
	public LiveVariableAnalysis(DirectedGraph g,FlowSet<Local> lh) {
		// First obligation
		super(g);
		
		// Create the emptyset
		emptySet = new ArraySparseSet<Local>();

		//Fill locksheld
		if(lh.isEmpty())
		{
			locksHeld = new ArraySparseSet<Local>();
		}
		else
		{
			locksHeld =lh.clone();
		}
		// Second obligation
		doAnalysis();
	}

	// This method performs the joining of successor nodes
	// Since live variables is a may analysis we join by union 
	@Override
	protected void merge(FlowSet<Local> inSet1, 
		FlowSet<Local> inSet2, 
		FlowSet<Local> outSet) 
	{
		inSet1.union(inSet2, outSet);
	}


	@Override
	protected void copy(FlowSet<Local> srcSet, 
		FlowSet<Local> destSet) 
	{
		srcSet.copy(destSet);
	}

	
	// Used to initialize the in and out sets for each node. In
	// our case we build up the sets as we go, so we initialize
	// with the empty set.
	@Override
	protected FlowSet<Local> newInitialFlow() {
		return emptySet.clone();
	}


	// Returns FlowSet representing the initial set of the entry
	// node. In our case the entry node is the last node and it
	// should contain the empty set.
	@Override
	protected FlowSet<Local> entryInitialFlow() {
		return locksHeld.clone();
	}

	
	// Sets the outSet with the values that flow through the 
	// node from the inSet based on reads/writes at the node
	// Set the outSet (entry) based on the inSet (exit)
	@Override
	protected void flowThrough(FlowSet<Local> inSet, 
		Unit node, FlowSet<Local> outSet) {
		//System.out.println(node);
		// outSet is the set at entry of the node
		// inSet is the set at exit of the node
		// out <- (in - write(node)) union read(node)
		
		// out <- (in - write(node))
		//FlowSet writes = (FlowSet)emptySet.clone();
		Stmt st = (Stmt) node;
		System.out.print(st);

		//Checking for method calls
		InvokeStmt ist = null;
		try{
			ist = (InvokeStmt) st;
		}
		catch(Exception e){}

		if(ist!=null)
		{
			System.out.print("\n\n" + ist.getInvokeExpr().getMethod()+"\n");
			SootMethod sMethod= ist.getInvokeExpr().getMethod();
			UnitGraph graph = new BriefUnitGraph(sMethod.getActiveBody());
			LiveVariableAnalysis analysis = new LiveVariableAnalysis(graph,inSet);
		}


		//Checking for enter/exit Monitors

		EnterMonitorStmt emst = null;
		try{
			emst = (EnterMonitorStmt)st;
		} catch(Exception e){}

		if(emst!=null)
		{
			System.out.print(emst.getOp());/*
			PointsToSet pts = Scene.v().getPointsToAnalysis().reachingObjects((Local) emst.getOp());
			PointsToSetInternal Ipts = (PointsToSetInternal) pts;
			HybridPointsToSet pi = (HybridPointsToSet) Ipts.getOldSet();
			pi.forall(new P2SetVisitor() {
							@Override
							public void visit(Node n) {
								AllocNode node = (AllocNode) n;
								node.getNewExpr();
							}
			});
			for(ClassConstant i: pts.possibleClassConstants())
			{
				System.out.print(i+" ");
			}*/
			outSet.add((Local)emst.getOp());
		}

		/*
		for (ValueBox def: node.getUseAndDefBoxes()) {
			if (def.getValue() instanceof Local) {
				writes.add(def.getValue());
			}
		}
		inSet.difference(writes, outSet);

		// out <- out union read(node)

		for (ValueBox use: node.getUseBoxes()) {
			if (use.getValue() instanceof Local) {
				outSet.add((Local) use.getValue());
			}
		}
		*/
		inSet.union(outSet,outSet);
		System.out.print("\t[LockSet: ");
		for (Local local: outSet) {
			System.out.print(local+" ");
		}
		System.out.print("]\n");
	}
}

