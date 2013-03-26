gasp-android
============

        GGGGGGGGGGGGG               AAA                 SSSSSSSSSSSSSSS PPPPPPPPPPPPPPPPP    !!!
     GGG::::::::::::G              A:::A              SS:::::::::::::::SP::::::::::::::::P  !!:!!
    GG:::::::::::::::G             A:::::A            S:::::SSSSSS::::::SP::::::PPPPPP:::::P !:::!
    G:::::GGGGGGGG::::G            A:::::::A           S:::::S     SSSSSSSPP:::::P     P:::::P!:::!
    G:::::G       GGGGGG           A:::::::::A          S:::::S              P::::P     P:::::P!:::!
    G:::::G                        A:::::A:::::A         S:::::S              P::::P     P:::::P!:::!
    G:::::G                       A:::::A A:::::A         S::::SSSS           P::::PPPPPP:::::P !:::!
    G:::::G    GGGGGGGGGG        A:::::A   A:::::A         SS::::::SSSSS      P:::::::::::::PP  !:::!
    G:::::G    G::::::::G       A:::::A     A:::::A          SSS::::::::SS    P::::PPPPPPPPP    !:::!
    G:::::G    GGGGG::::G      A:::::AAAAAAAAA:::::A            SSSSSS::::S   P::::P            !:::!
    G:::::G        G::::G     A:::::::::::::::::::::A                S:::::S  P::::P            !!:!!
    G:::::G       G::::G    A:::::AAAAAAAAAAAAA:::::A               S:::::S  P::::P             !!!
    G:::::GGGGGGGG::::G   A:::::A             A:::::A  SSSSSSS     S:::::SPP::::::PP
    GG:::::::::::::::G  A:::::A               A:::::A S::::::SSSSSS:::::SP::::::::P           !!!
     GGG::::::GGG:::G A:::::A                 A:::::AS:::::::::::::::SS P::::::::P          !!:!!
        GGGGGG   GGGGAAAAAAA                   AAAAAAASSSSSSSSSSSSSSS   PPPPPPPPPP           !!!

A simple Android client for Kohsuke's gasp-server project
Please see (https://github.com/cloudbees/gasp-server.git) for instructions on how to build and run the server app

GaspReviews contains the main Android project: the main Activity is GaspReviewsActivity, which uses an android.app.ListFragment to display all the reviews returned by the gasp-server via an asynchronous content loader (com.cloudbees.gasp.loader.RESTLoader). The test project (GaspReviewsTest) has
a single test class (com.cloudbees.gasp.test.GaspRESTLoaderTest), which can be used for automated testing of the mobile-backend communciations.

This project is closely modeled on Neil Goodman's example of using Android Loaders for async REST data services. I have modified it to remove the Android support library, so that it requires a minimum Android target of 3.0 and uses android.app.Activity instead of android.support.v4.app.ActivityFragment. This simplifies unit testing, since com.cloudbees.gasp.test.GaspRESTLoaderTest can simply extend android.test.LoaderTestCase.

Neil's code is [here](https://github.com/posco2k8/rest_loader_tutorial.git). Please refer to the original blog post located [here](http://neilgoodman.net/2011/12/26/modern-techniques-for-implementing-rest-clients-on-android-4-0-and-below-part-1/) for further detail.  The second part of the tutorial, which uses a Service instead of a Loader, can be found [here](http://neilgoodman.net/2012/01/01/modern-techniques-for-implementing-rest-clients-on-android-4-0-and-below-part-2)

