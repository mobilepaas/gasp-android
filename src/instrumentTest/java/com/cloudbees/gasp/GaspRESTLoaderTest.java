package com.cloudbees.gasp;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.net.Uri;
import android.test.LoaderTestCase;
import android.util.Log;

import com.cloudbees.gasp.loader.RESTLoader;
import com.cloudbees.gasp.loader.RESTLoader.RESTResponse;

public class GaspRESTLoaderTest extends LoaderTestCase {
    private static final String TAG = GaspRESTLoaderTest.class.getName();
    private static final Uri gaspReviewsUri = Uri.parse("http://gasp.partnerdemo.cloudbees.net/reviews");

    public void testRESTLoader() {
        Log.i(TAG, "Using Gasp Server URI: " + gaspReviewsUri );

        // Create & execute loader
        RESTLoader loader = new RESTLoader(getContext(),
                RESTLoader.HTTPVerb.GET, gaspReviewsUri);
        RESTResponse theResponse = getLoaderResultSynchronously(loader);

        Log.i(TAG, "Received data: " + theResponse.getData());

        // Check for HTTP response code 200 and data
        assertEquals(theResponse.getCode(), 200);
        assertFalse(theResponse.getData().isEmpty());
    }

}