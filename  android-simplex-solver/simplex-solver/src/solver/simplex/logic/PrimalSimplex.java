package solver.simplex.logic;

import java.util.Formatter;

public class PrimalSimplex
{

	private int[] objectiveFunction_;
	private int[][] restrictions_;

	public static PrimalSimplex min(int[] objectiveFunction,
			int[][] restrictions)
	{
		return new PrimalSimplex(objectiveFunction, restrictions);
	}

	private PrimalSimplex(int[] objectiveFunction, int[][] restrictions)
	{
		if (objectiveFunction == null || restrictions == null
				|| objectiveFunction.length < 1 || restrictions.length < 1
				|| objectiveFunction.length != restrictions[0].length - 1)
		{
			throw new IllegalArgumentException("Datele problemei sunt invalide");
		}
		objectiveFunction_ = objectiveFunction;
		restrictions_ = restrictions;
	}

	public String solve()
	{
		StringBuilder result = new StringBuilder();
		Formatter formatter = new Formatter(result);
		int[] aObjectiveFunction = new int[objectiveFunction_.length
				+ restrictions_.length];
		for (int i = 0; i < objectiveFunction_.length; i++)
		{
			aObjectiveFunction[i] = 0;
		}
		for (int i = objectiveFunction_.length; i < aObjectiveFunction.length; i++)
		{
			aObjectiveFunction[i] = 1;
		}

		int[][] aRestrictions = new int[restrictions_.length][restrictions_[0].length
				+ restrictions_.length];
		for (int i = 0; i < restrictions_.length; i++)
		{
			for (int j = 0; j < restrictions_[i].length; j++)
			{
				aRestrictions[i][j] = restrictions_[i][j];
			}
		}
		for (int i = 0; i < restrictions_.length; i++)
		{
			aRestrictions[i][restrictions_[i].length + i] = 1;
		}

		int[] aBasis = new int[restrictions_.length];
		for (int i = 0; i < aBasis.length; i++)
		{
			aBasis[i] = i + restrictions_[0].length - 1;
		}

		SimplexTableau aProblem = SimplexTableau.create(aObjectiveFunction,
				aBasis, aRestrictions);
		formatter.format("PROBLEMA ASOCIATA:");
		formatter.format(aProblem.print());
		while (!aProblem.foundSolution() && !aProblem.unboundedOptimum())
		{
			aProblem.applyPivotRule();
			formatter.format(aProblem.print());
		}

		if (aProblem.unboundedOptimum())
		{
			formatter.format("\nOptim infinit.");
			return formatter.toString();
		}

		formatter.format("\nSolutie pentru problema asociata:");
		formatter.format(aProblem.printSolution());

		formatter.format("\nPROBLEMA INITIALA:");
		SimplexTableau iProblem = SimplexTableau.create(objectiveFunction_,
				aProblem);
		formatter.format(iProblem.print());
		while (!iProblem.foundSolution() && !iProblem.unboundedOptimum())
		{
			iProblem.applyPivotRule();
			formatter.format(iProblem.print());
		}

		if (iProblem.unboundedOptimum())
		{
			formatter.format("\nOptim infinit.");
			return formatter.toString();
		}

		formatter.format("\nSolutie pentru problema initiala:");
		formatter.format(iProblem.printSolution());

		return formatter.toString();
	}
}