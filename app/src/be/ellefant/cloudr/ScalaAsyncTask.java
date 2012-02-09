package be.ellefant.cloudr;

import android.os.AsyncTask;

/**
 * Quick hack, because overriding vararg methods in Scala leads to doInBackground not being called.
 * see https://issues.scala-lang.org/browse/SI-1459
 */
public abstract class ScalaAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected abstract Result doInBackground();

    protected Result doInBackground(Params... params) {
        return doInBackground();
    }
}
