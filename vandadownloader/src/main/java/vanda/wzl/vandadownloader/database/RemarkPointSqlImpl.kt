package vanda.wzl.vandadownloader.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import vanda.wzl.vandadownloader.database.RemarkMultiThreadPointSqlKey.DOWNLOADFILE_ID
import vanda.wzl.vandadownloader.database.RemarkMultiThreadPointSqlKey.TABLE_NAME_MULTI_THREAD

class RemarkPointSqlImpl(context: Context) : RemarkPointSql {

    private val db: SQLiteDatabase

    init {
        val openHelper = DefaultSQLiteOpenHelper(context)
        db = openHelper.writableDatabase
    }

    override fun remarkPointSqlEntry(id: Long): RemarkPointSqlEntry {
        val cursor = db.rawQuery("select * from " + RemarkPointSqlKey.TABLE_NAME + " where " + RemarkPointSqlKey.ID + "=?", arrayOf(id.toString()))
        return RemarkPointSqlEntry(cursor)
    }

    override fun update(remarkPointSqlEntry: RemarkPointSqlEntry) {
        db.update(RemarkPointSqlKey.TABLE_NAME, remarkPointSqlEntry.toContentValues(), RemarkPointSqlKey.ID + " = ?", arrayOf(remarkPointSqlEntry.id.toString()))
    }

    override fun insert(remarkPointSqlEntry: RemarkPointSqlEntry) {
        db.insert(RemarkPointSqlKey.TABLE_NAME, null, remarkPointSqlEntry.toContentValues())
    }

    override fun remarkMultiThreadPointSqlEntry(downloadId: Long, threadId: Long): RemarkMultiThreadPointSqlEntry {
        val cursor = db.rawQuery(
                "select * from " + RemarkMultiThreadPointSqlKey.TABLE_NAME_MULTI_THREAD +
                        " where " + RemarkMultiThreadPointSqlKey.DOWNLOADFILE_ID + "=? and " +
                        RemarkMultiThreadPointSqlKey.THREAD_ID + "=? ",
                arrayOf(downloadId.toString(), threadId.toString()))
        return RemarkMultiThreadPointSqlEntry(cursor)
    }

    override fun update(remarkMultiThreadPointSqlEntry: RemarkMultiThreadPointSqlEntry) {
        db.update(RemarkMultiThreadPointSqlKey.TABLE_NAME_MULTI_THREAD, remarkMultiThreadPointSqlEntry.toContentValues(), RemarkMultiThreadPointSqlKey.THREAD_ID + " = ? and " + RemarkMultiThreadPointSqlKey.DOWNLOADFILE_ID + " = ?", arrayOf(remarkMultiThreadPointSqlEntry.threadId.toString(), remarkMultiThreadPointSqlEntry.downloadFileId.toString()))
    }

    override fun insert(remarkMultiThreadPointSqlEntry: RemarkMultiThreadPointSqlEntry) {
        db.insert(RemarkMultiThreadPointSqlKey.TABLE_NAME_MULTI_THREAD, null, remarkMultiThreadPointSqlEntry.toContentValues())
    }

    override fun findThreadInfo(downloadId: Long): ArrayList<RemarkMultiThreadPointSqlEntry> {

        var cursor: Cursor? = null
        val list = ArrayList<RemarkMultiThreadPointSqlEntry>()

        try {

            cursor = db.rawQuery("select * from " + RemarkMultiThreadPointSqlKey.TABLE_NAME_MULTI_THREAD + " where " + RemarkMultiThreadPointSqlKey.DOWNLOADFILE_ID + "=?", arrayOf(downloadId.toString()))

            while (cursor != null && cursor.moveToNext()) {
                val remarkMultiThreadPointSqlEntry = RemarkMultiThreadPointSqlEntry()
                remarkMultiThreadPointSqlEntry.fillingValue(
                        cursor.getLong(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.ID)),
                        cursor.getString(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.URL)),
                        cursor.getLong(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.DOWNLOAD_SOFAR)),
                        cursor.getLong(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.DOWNLOAD_LENGTH)),
                        cursor.getInt(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.STATUS)),
                        cursor.getInt(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.THREAD_ID)),
                        cursor.getLong(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.DOWNLOADFILE_ID)),
                        cursor.getLong(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.NORMAL_SIZE)),
                        cursor.getLong(cursor.getColumnIndex(RemarkMultiThreadPointSqlKey.EXT_SIZE))
                )
                list.add(remarkMultiThreadPointSqlEntry)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return list
    }

    override fun deleteThreadInfo(downloadId: Long) {
        try {
            db.delete(TABLE_NAME_MULTI_THREAD, "$DOWNLOADFILE_ID = ?", arrayOf(downloadId.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}