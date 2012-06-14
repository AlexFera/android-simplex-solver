package solver.simplex.ui;

import solver.simplex.R;
import solver.simplex.logic.PrimalSimplex;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author Alexandru Fera
 * 
 */
public class MainActivity extends Activity
{
	private EditText objectiveFunctionField;
	private EditText restrictionsField;
	private String result;
	private Button btnSolve;

	private String objectiveFunction;
	private String restrictions;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// Inflate the widgets.
		objectiveFunctionField = (EditText) findViewById(R.id.objectiveFunctionField);
		restrictionsField = (EditText) findViewById(R.id.restrictionsField);
		btnSolve = (Button) findViewById(R.id.btnSolve);

		// Handle callbacks.
		btnSolve.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				handleSolve();
			}
		});

	}

	// Restore the applications state.
	@Override
	protected void onResume()
	{
		super.onResume();

		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		objectiveFunction = settings.getString("ofc", "");
		restrictions = settings.getString("rest", "");

		objectiveFunctionField.setText(objectiveFunction);
		restrictionsField.setText(restrictions);
	}

	// Save the application state.
	@Override
	protected void onPause()
	{
		super.onPause();

		// Retrive the values from the UI.
		objectiveFunction = objectiveFunctionField.getText().toString();
		restrictions = restrictionsField.getText().toString();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("ofc", objectiveFunction);
		editor.putString("rest", restrictions);

		// Commit the edits!
		editor.commit();
	}

	private void handleSolve()
	{
		try
		{
			// Retrieve the numbers from the UI.
			String[] ofc = objectiveFunctionField.getText().toString()
					.split(" ");
			int[] f = new int[ofc.length];

			for (int i = 0; i < f.length; i++)
			{
				f[i] = Integer.parseInt(ofc[i]);
			}

			// Retrieve the restrictions from the UI.
			String[] restrictions = restrictionsField.getText().toString()
					.split("\n");
			int[][] r = new int[restrictions.length][];
			for (int i = 0; i < r.length; i++)
			{
				String[] restriction = restrictions[i].split(" ");
				r[i] = new int[restriction.length];
				r[i][0] = Integer.parseInt(restriction[restriction.length - 1]);
				for (int j = 0; j < restriction.length - 1; j++)
				{
					r[i][j + 1] = Integer.parseInt(restriction[j]);
				}
			}

			// With the data from the UI, resolve the linear problem with the
			// primal-simplex algorithm.
			try
			{
				result = PrimalSimplex.min(f, r).solve();
			} catch (Exception e)
			{
				result = e.getMessage();
			}

		} catch (Exception e)
		{
			result = "Datele problemei sunt invalide!!!";
		}

		Intent resultIntent = new Intent("solver.simplex.SOLVEACTIVITY");
		resultIntent.putExtra("theResult", result);
		startActivity(resultIntent);
	}

}