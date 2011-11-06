package be.ellefant.droid.cloudapp

import roboguice.service.RoboService

class SyncService extends RoboService
    with Base.Service
    with sdroid.Service
    with Injection.SyncAdapter {

  def onBind = {
    case intent if intent.getAction == "android.content.SyncAdapter" =>
      logger.debug("Returning the CloudAppSyncAdapter binder for intent '%s'." format intent)
      syncAdapter.getSyncAdapterBinder
  }

}