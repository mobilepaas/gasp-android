gasp-android
============

> <img src="http://www.cloudbees.com/sites/all/themes/custom/cloudbees_zen/css/bidesign/_ui/images/logo.png"/>
>
> <b>Note</b>: <i>This repo is part of the Gasp demo project - a showcase of <a href="https://developer.cloudbees.com/bin/view/Mobile">cloudbees mobile services</a>.
> You can see the big picture of the <a href="http://mobilepaas.cloudbees.com">showcase here</a>.
> Feel free to fork and use this repo as a template.</i>

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

GaspLocationsActivity shows a Google Maps-based activity to display all Restaurants within a given radius of the current location.  There are two other activities (GaspRESTLoaderActivity and GaspRESTServiceActivity), which show examples of how to load data from a cloud-based REST service (gasp-server) using an async loader and a service implementation, respectively. These are closely modeled on Neil Goodman's example of using Android Loaders for async REST data services. 

I have modified these to remove the Android support library, so that it requires a minimum Android target of 3.0 and uses android.app.Activity instead of android.support.v4.app.ActivityFragment. This simplifies unit testing, since com.cloudbees.gasp.test.GaspRESTLoaderTest can simply extend android.test.LoaderTestCase. 

Neil's code is [here](https://github.com/posco2k8/rest_loader_tutorial.git). Please refer to the original blog post located [here](http://neilgoodman.net/2011/12/26/modern-techniques-for-implementing-rest-clients-on-android-4-0-and-below-part-1/) for further detail.  The second part of the tutorial, which uses a Service instead of a Loader, can be found [here](http://neilgoodman.net/2012/01/01/modern-techniques-for-implementing-rest-clients-on-android-4-0-and-below-part-2)

The Gasp! endpoint url is set in GaspReviews/res/xml/preferences.xml (android:key="gasp_endpoint_uri"): this can be reset using Android shared preferences with the Options Menu (fn-cmd-F2 with the Android emulator on a MacBook). The first time the GaspLocationsActivity is run, it will load the default value of gasp_endpoint_uri, but subsequent activations will use the value stored using on the device's shared preferences. Note that the async loader will not pick up a change in the endpoint uri until the Activity is reloaded (typically when the app is restarted).

