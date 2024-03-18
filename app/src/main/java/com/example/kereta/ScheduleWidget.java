package com.example.kereta;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.kereta.models.ScheduleContainerModel;
import com.example.kereta.models.ScheduleInterface;
import com.example.kereta.models.ScheduleModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Implementation of App Widget functionality.
 */
public class ScheduleWidget extends AppWidgetProvider {
    private static ScheduleInterface api;
    public static final String REFRESH_ACTION = "refresh_intent";
    private static final String URL = "https://api-partner.krl.co.id/krlweb/v1/";


    public static ScheduleInterface getApi(){
        if (api == null){
            api = new Retrofit.Builder().baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ScheduleInterface.class);
        }
        return api;
    }

    static RemoteViews setupAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);
        Intent refreshIntent = new Intent(context, ScheduleWidget.class);
        refreshIntent.setAction(REFRESH_ACTION);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_IMMUTABLE
        );
        view.setOnClickPendingIntent(R.id.scheduleRefreshBtn, refreshPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, view);
        return view;
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, RemoteViews view, ZonedDateTime updateTime, List<ScheduleModel> schedules) {

        if(schedules.isEmpty()){
            return;
        }

        view.setTextViewText(
            R.id.scheduleSubtitleTV,
            "Last updated at: " + updateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );

        RemoteViews.RemoteCollectionItems.Builder builder = new RemoteViews.RemoteCollectionItems.Builder();
        long cnt = 1;
        for(ScheduleModel entry : schedules){
            String route = entry.getRouteName();
            String dest = entry.getDest();
            String code = entry.getTrainId();
            String stat = entry.getStation();
            String time = entry.getTimeEst();

            RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.schedule_item);
            item.setTextViewText(R.id.titleTV, stat + ": " + dest);
            item.setTextViewText(R.id.subtitleTV, route + " (" + code + ")");
            item.setTextViewText(R.id.hourTV, time);
            builder.addItem(cnt++, item);
        }

        view.setRemoteAdapter(R.id.scheduleLV, builder.build());
        appWidgetManager.updateAppWidget(appWidgetId, view);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(REFRESH_ACTION.equals(intent.getAction())){
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            onUpdate(context, mgr, mgr.getAppWidgetIds(new ComponentName(context, getClass())));
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ArrayList<ScheduleModel> acc = new ArrayList<>();

        ArrayList<String> stations = new ArrayList<>();
        stations.add("TNG");
        stations.add("DU");
        stations.add("SUDB");

        ZonedDateTime now = Instant.now().atZone(ZoneId.of("Asia/Jakarta"));

        for (int appWidgetId : appWidgetIds) {
            RemoteViews view = setupAppWidget(context, appWidgetManager, appWidgetId);
            enqueueStations(acc, stations, now, () -> {
                updateAppWidget(context, appWidgetManager, appWidgetId, view, now, acc);
            });
        }
    }

    private interface Lambda{
        void run();
    }

    private void enqueueStations(List<ScheduleModel> acc, List<String> stations, ZonedDateTime now, Lambda callback){
        String curr = stations.remove(0);
        ZonedDateTime end = now.plusHours(3);
        if(end.getDayOfMonth() != now.getDayOfMonth()){
            return;
        }
        getApi().getSchedules(
                curr,
                now.format(DateTimeFormatter.ofPattern("HH:mm")),
                end.format(DateTimeFormatter.ofPattern("HH:mm"))
            )
            .enqueue(new Callback<ScheduleContainerModel>() {
                @Override
                public void onResponse(Call<ScheduleContainerModel> call, Response<ScheduleContainerModel> response) {
                    for(ScheduleModel m : response.body().getData()){
                        m.setStation(curr);
                        acc.add(m);
                    }

                    if(stations.isEmpty()){
                        acc.sort(Comparator.comparing(ScheduleModel::getTimeEst));
                        callback.run();
                    }
                    else{
                        enqueueStations(acc, stations, now, callback);
                    }
                }

                @Override
                public void onFailure(Call<ScheduleContainerModel> call, Throwable t) {
                    Log.d("KERETA", t.getMessage());
                }
            });
    }
}