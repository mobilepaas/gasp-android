package com.cloudbees.gasp.test;

import com.cloudbees.gasp.loader.RESTLoader;
import com.cloudbees.gasp.loader.RESTLoader.RESTResponse;

import android.net.Uri;
import android.test.LoaderTestCase;
import android.util.Log;

public class GaspRESTLoaderTest extends LoaderTestCase {
    private static final String TAG = GaspRESTLoaderTest.class.getName();
    
	public void testRESTLoader() {
		
        Uri gaspReviewsUri = Uri.parse("http://gasp.mqprichard.cloudbees.net/reviews");
        Log.i(TAG, "Using Gasp Server URI: " + gaspReviewsUri );
        
	    // Create & execute loader
	    RESTLoader loader = new RESTLoader(getContext(),
	            RESTLoader.HTTPVerb.GET, gaspReviewsUri);
	    RESTResponse theResponse = getLoaderResultSynchronously(loader);

	    Log.i(TAG, "Received data: " + theResponse.getData());

	    assertEquals(theResponse.getCode(), 200);
	    assertFalse(theResponse.getData().isEmpty());
	}

}
