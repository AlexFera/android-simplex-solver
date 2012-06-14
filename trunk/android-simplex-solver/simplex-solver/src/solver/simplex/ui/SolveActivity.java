package solver.simplex.ui;

import solver.simplex.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * @author Alexandru Fera
 *
 */
public class SolveActivity extends Activity
{
	private TextView resultField;
	private String result;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.solve);

		// Inflate the widgets.
		resultField = (TextView) findViewById(R.id.resultField);
		
		// Retrieve the result string from the MainActivity.
		result = this.getIntent().getStringExtra("theResult");
		
		// Set the resultField with the text retrieved.
		resultField.setText(result);
	}
}
