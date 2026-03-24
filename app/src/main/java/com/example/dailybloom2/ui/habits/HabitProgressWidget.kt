package com.example.dailybloom2.ui.habits

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.dailybloom2.R
import com.example.dailybloom2.data.HabitRepository
import com.example.dailybloom2.ui.home.HomeActivity

class HabitProgressWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }
    
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val repository = HabitRepository(context)
            repository.resetDailyProgress()
            
            val habits = repository.getAllHabits()
            val progress = repository.getTodayProgress()
            
            val views = RemoteViews(context.packageName, R.layout.widget_habit_progress)
            
            // Update progress text
            views.setTextViewText(R.id.widgetProgressText, "$progress%")
            views.setTextViewText(R.id.widgetHabitsText, "${habits.count { it.isCompleted }}/${habits.size} habits")
            
            // Update progress bar
            views.setProgressBar(R.id.widgetProgressBar, 100, progress, false)
            
            // Set click intent to open app
            val intent = Intent(context, HomeActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent)
            
            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}