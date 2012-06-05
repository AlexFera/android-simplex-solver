package solver.simplex.logic;

import java.util.Formatter;

public final class SimplexTableau
{

	private int[] objectiveFunction_;
	private int[] basis_;
	private Fraction[][] z_;

	public static SimplexTableau create(int[] objectiveFunction, int[] basis,
			int[][] restrictions)
	{
		Fraction[][] z = new Fraction[restrictions.length + 1][restrictions[0].length];
		for (int i = 1; i < z.length; i++)
		{
			for (int j = 0; j < z[i].length; j++)
			{
				z[i][j] = new Fraction(restrictions[i - 1][j]);
			}
		}
		z[0][0] = new Fraction(0);
		for (int i = 1; i < z.length; i++)
		{
			z[0][0].add(Fraction.multiply(z[i][0], new Fraction(
					objectiveFunction[basis[i - 1]])));
		}
		for (int j = 1; j < z[0].length; j++)
		{
			z[0][j] = new Fraction(objectiveFunction[j - 1]).opposite();
			for (int i = 1; i < z.length; i++)
			{
				z[0][j].add(Fraction.multiply(z[i][j], new Fraction(
						objectiveFunction[basis[i - 1]])));
			}
		}
		return new SimplexTableau(objectiveFunction, basis, z);
	}

	public static SimplexTableau create(int[] objectiveFunction,
			SimplexTableau tableau)
	{
		int[] basis = tableau.basis_;
		Fraction[][] z = new Fraction[tableau.z_.length][tableau.z_[0].length
				- tableau.z_.length + 1];
		for (int i = 1; i < z.length; i++)
		{
			for (int j = 0; j < z[i].length; j++)
			{
				z[i][j] = tableau.z_[i][j];
			}
		}
		z[0][0] = new Fraction(0);
		for (int i = 1; i < z.length; i++)
		{
			z[0][0].add(Fraction.multiply(z[i][0], new Fraction(
					objectiveFunction[basis[i - 1]])));
		}
		for (int j = 1; j < z[0].length; j++)
		{
			z[0][j] = new Fraction(objectiveFunction[j - 1]).opposite();
			for (int i = 1; i < z.length; i++)
			{
				z[0][j].add(Fraction.multiply(z[i][j], new Fraction(
						objectiveFunction[basis[i - 1]])));
			}
		}
		return new SimplexTableau(objectiveFunction, tableau.basis_, z);
	}

	private SimplexTableau(int[] objectiveFunction, int[] basis, Fraction[][] z)
	{
		objectiveFunction_ = objectiveFunction;
		basis_ = basis;
		z_ = z;
	}

	public SimplexTableau applyPivotRule()
	{
		int pivotColumn = pivotColumn();
		int pivotRow = pivotRow();
		for (int i = 0; i < z_.length; i++)
		{
			for (int j = 0; j < z_[i].length; j++)
			{
				if (i != pivotRow && j != pivotColumn)
				{
					z_[i][j] = Fraction.substract(
							Fraction.multiply(z_[pivotRow][pivotColumn],
									z_[i][j]),
							Fraction.multiply(z_[pivotRow][j],
									z_[i][pivotColumn])).divide(
							z_[pivotRow][pivotColumn]);
				}
			}
		}
		for (int j = 0; j < z_[pivotRow].length; j++)
		{
			if (j != pivotColumn)
			{
				z_[pivotRow][j].divide(z_[pivotRow][pivotColumn]);
			}
		}
		for (int i = 0; i < z_.length; i++)
		{
			z_[i][pivotColumn] = new Fraction(0);
		}
		z_[pivotRow][pivotColumn] = new Fraction(1);
		basis_[pivotRow - 1] = pivotColumn - 1;
		return this;
	}

	public boolean foundSolution()
	{
		for (int j = 1; j < z_[0].length; j++)
		{
			if (z_[0][j].compareTo(Fraction.ZERO) > 0)
			{
				return false;
			}
		}
		return true;
	}

	public boolean unboundedOptimum()
	{
		if (foundSolution())
		{
			return false;
		}
		for (int j = 1; j < z_[0].length; j++)
		{
			if (z_[0][j].compareTo(Fraction.ZERO) > 0)
			{
				boolean hasPositive = false;
				for (int i = 1; i < z_.length; i++)
				{
					if (z_[i][j].compareTo(Fraction.ZERO) > 0)
					{
						hasPositive = true;
						break;
					}
				}
				if (!hasPositive)
				{
					return true;
				}
			}
		}
		return false;
	}

	private int pivotColumn()
	{
		if (foundSolution() || unboundedOptimum())
		{
			throw new RuntimeException("can't choose a pivot");
		}
		Fraction maxValue = new Fraction(0);
		int maxPosition = -1;
		for (int j = 1; j < z_[0].length; j++)
		{
			if (z_[0][j].compareTo(maxValue) > 0)
			{
				maxValue = z_[0][j];
				maxPosition = j;
			}
		}
		return maxPosition;
	}

	private int pivotRow()
	{
		int j = pivotColumn();
		Fraction minValue = new Fraction(0);
		int minPosition = -1;
		for (int i = 1; i < z_.length; i++)
		{
			if (z_[i][j].compareTo(Fraction.ZERO) > 0)
			{
				Fraction proportion = Fraction.divide(z_[0][j], z_[i][j]);
				if (minValue.compareTo(Fraction.ZERO) == 0
						|| proportion.compareTo(minValue) < 0)
				{
					minValue = proportion;
					minPosition = i;
				}
			}
		}
		return minPosition;
	}

	public String print()
	{
		StringBuilder result = new StringBuilder();
		Formatter formatter = new Formatter(result);
		formatter.format("\n                   ");
		for (int i = 0; i < objectiveFunction_.length; i++)
		{
			formatter.format("  %8d", objectiveFunction_[i]);
		}
		formatter.format("\n       |----------|");
		for (int i = 0; i < objectiveFunction_.length; i++)
		{
			formatter.format("|---------");
		}
		formatter.format("\n       | %8s |", z_[0][0]);
		for (int j = 1; j < z_[0].length; j++)
		{
			formatter.format("| %8s", z_[0][j]);
		}
		formatter.format("\n       |----------|");
		for (int i = 0; i < objectiveFunction_.length; i++)
		{
			formatter.format("|---------");
		}
		for (int i = 1; i < z_.length; i++)
		{
			formatter.format("\n %5s | %8s |", "x" + (basis_[i - 1] + 1),
					z_[i][0]);
			for (int j = 1; j < z_[0].length; j++)
			{
				formatter.format("| %8s", z_[i][j]);
			}
		}
		formatter.format("\n");
		return formatter.toString();
	}

	public String printSolution()
	{
		StringBuilder result = new StringBuilder();
		Formatter formatter = new Formatter(result);
		Fraction[] solution = new Fraction[objectiveFunction_.length];
		for (int i = 0; i < basis_.length; i++)
		{
			solution[basis_[i]] = z_[i + 1][0];
		}
		for (int i = 0; i < solution.length; i++)
		{
			if (solution[i] == null)
			{
				solution[i] = new Fraction(0);
			}
			formatter.format("\n %5s = %s", "x" + (i + 1), solution[i]);
		}
		formatter.format("\n");
		return formatter.toString();
	}
}