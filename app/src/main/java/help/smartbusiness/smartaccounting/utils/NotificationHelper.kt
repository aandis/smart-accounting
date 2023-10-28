package help.smartbusiness.smartaccounting.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import help.smartbusiness.smartaccounting.R
import io.karn.notify.Notify

object NotificationHelper {
    @JvmStatic
    fun simpleNotification(context: Context, header: Int, message: Int) {
        Notify.with(context)
            .header { icon = R.mipmap.ic_launcher }
            .content {
                title = context.getString(header)
                text = context.getString(message)
            }
            .meta { cancelOnClick = true }
            .show()
    }

    @JvmStatic
    fun stickyNotification(context: Context, header: Int, message: Int, id: Int) {
        Notify.with(context)
            .header { icon = R.mipmap.ic_launcher }
            .content {
                title = context.getString(header)
                text = context.getString(message)
            }
            .meta {
                cancelOnClick = true
                sticky = true
            }
            .show(id)
    }

    @JvmStatic
    fun actionNotification(context: Context,
                           header: Int,
                           message: Int,
                           detail: Int,
                           intent: Intent) {
        Notify.with(context)
            .header { icon = R.mipmap.ic_launcher }
            .asBigText {
                title = context.getString(header)
                text = context.getString(message)
                expandedText = context.getString(message)
                bigText = context.getString(detail)
            }
            .meta {
                clickIntent = PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                )
                cancelOnClick = true
            }
            .show()
    }

    @JvmStatic
    fun cancelNotification(id: Int) {
        Notify.cancelNotification(id)
    }
}