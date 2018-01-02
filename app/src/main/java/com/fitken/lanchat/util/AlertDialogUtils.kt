package com.fitken.lanchat.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.WindowManager
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.RelativeLayout

import com.fitken.lanchat.R


/**
 * Created by Ken on 2/2/2017.
 */

object AlertDialogUtils {

    fun showError(context: Context, message: Int) {
        AlertDialog.Builder(context).setTitle(R.string.app_name)
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setNegativeButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
    }

    fun showError(context: Context, message: String) {
        AlertDialog.Builder(context).setTitle(R.string.app_name)
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setNegativeButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
    }

    fun generatePopupWindow(context: Context, rltLayout: RelativeLayout,
                            listView: ListView): PopupWindow {
        val popupWindow = PopupWindow(context)
        popupWindow.isFocusable = true
        popupWindow.width = rltLayout.width
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.contentView = listView
        return popupWindow
    }

    fun showInfo(context: Context, des: Int): Dialog {
        return showInfo(context, context.getString(des))
    }

    fun showInfo(context: Context, des: Int, btn: Int,
                 dismissListener: DialogInterface.OnDismissListener): Dialog {
        return showInfo(context, context.getString(des), context.getString(btn), dismissListener)
    }

    fun showInfo(context: Context, des: String,
                 dismissListener: DialogInterface.OnDismissListener): Dialog {
        val builder = dialogBuilder(context, des)
        builder.setCancelable(true)
        builder.setPositiveButton(android.R.string.ok, null)
        val dialog = builder.show()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnDismissListener(dismissListener)
        return dialog
    }

    @JvmOverloads
    fun showInfo(context: Context, des: String, btn: String = context.getString(android.R.string.ok),
                 dismissListener: DialogInterface.OnDismissListener? = null): Dialog {
        val builder = dialogBuilder(context, des)
        builder.setCancelable(true)
        builder.setPositiveButton(btn, null)
        val dialog = builder.show()
        dialog.setCanceledOnTouchOutside(true)
        dialog.setOnDismissListener(dismissListener)
        return dialog
    }

    fun showConfirm(context: Context, msg: String,
                    onYesListener: DialogInterface.OnClickListener, onNoListener: DialogInterface.OnClickListener): Dialog {
        val builder = dialogBuilder(context, msg)
        builder.setCancelable(true)
        builder.setPositiveButton(android.R.string.yes, onYesListener)
        builder.setNegativeButton(android.R.string.no, onNoListener)
        val dialog = builder.show()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    fun showConfirmNotCancelable(context: Context, msg: String,
                                 onYesListener: DialogInterface.OnClickListener, onNoListener: DialogInterface.OnClickListener): Dialog {
        val builder = dialogBuilder(context, msg)
        builder.setCancelable(false)
        builder.setPositiveButton(android.R.string.yes, onYesListener)
        builder.setNegativeButton(android.R.string.no, onNoListener)
        val dialog = builder.show()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun showConfirmNotCancelable(context: Context, title: String, msg: String,
                                 onYesListener: DialogInterface.OnClickListener, onNoListener: DialogInterface.OnClickListener): Dialog {
        val builder = dialogBuilder(context, title, msg)
        builder.setCancelable(false)
        builder.setPositiveButton(android.R.string.yes, onYesListener)
        builder.setNegativeButton(android.R.string.no, onNoListener)
        val dialog = builder.show()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun showConfirmNotCancelable(context: Context, msg: String, textYes: String,
                                 textNo: String, onYesListener: DialogInterface.OnClickListener,
                                 onNoListener: DialogInterface.OnClickListener): Dialog {
        val builder = dialogBuilder(context, msg)
        builder.setCancelable(false)
        builder.setPositiveButton(textYes, onYesListener)
        builder.setNegativeButton(textNo, onNoListener)
        val dialog = builder.show()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun showConfirm(context: Context, msg: Int,
                    onYesListener: DialogInterface.OnClickListener, onNoListener: DialogInterface.OnClickListener): Dialog {
        val builder = dialogBuilder(context, msg)
        builder.setCancelable(true)
        builder.setPositiveButton(android.R.string.yes, onYesListener)
        builder.setNegativeButton(android.R.string.no, onNoListener)
        val dialog = builder.show()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    fun showConfirmNotCancelable(context: Context, msg: Int,
                                 onYesListener: DialogInterface.OnClickListener, onNoListener: DialogInterface.OnClickListener): Dialog {
        val builder = dialogBuilder(context, msg)
        builder.setCancelable(false)
        builder.setPositiveButton(android.R.string.yes, onYesListener)
        builder.setNegativeButton(android.R.string.no, onNoListener)
        val dialog = builder.show()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun dialogBuilder(context: Context, title: String, msg: String?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        if (msg != null) {
            builder.setMessage(msg)
        }
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        return builder
    }

    fun dialogBuilder(context: Context, msg: String?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        if (msg != null) {
            builder.setMessage(msg)
        }
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        return builder
    }

    fun dialogBuilder(context: Context, msg: Int): AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(msg)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        return builder
    }
}
