package com.b4ebusinessdriver.Services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OwnPushService extends FirebaseMessagingService {

    private List<FirebaseMessagingService> messagingServices = new ArrayList<>(2);

    public OwnPushService() {
        messagingServices.add(new MyFirebaseMessagingService());
        messagingServices.add(new com.b4edriver.GCM.MyFirebaseMessagingService());
    }

    @Override
    public void onNewToken(final String s) {
        delegate(new GCAction1<FirebaseMessagingService>() {
            @Override
            public void run(FirebaseMessagingService service) {
                OwnPushService.this.injectContext(service);
                service.onNewToken(s);
            }
        });
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        delegate(new GCAction1<FirebaseMessagingService>() {
            @Override
            public void run(FirebaseMessagingService service) {
                OwnPushService.this.injectContext(service);
                service.onMessageReceived(remoteMessage);
            }
        });
    }

    private void delegate(GCAction1<FirebaseMessagingService> action) {
        for (FirebaseMessagingService service : messagingServices) {
            action.run(service);
        }
    }

    private void injectContext(FirebaseMessagingService service) {
        // tested on emulator with Android Q (Dev Preview 1)
        // Accessing hidden field Landroid/content/ContextWrapper;->mBase:Landroid/content/Context; (greylist, reflection)
        //
        // https://developer.android.com/distribute/best-practices/develop/restrictions-non-sdk-interfaces
        // https://android.googlesource.com/platform/frameworks/base/+/pie-release/config/hiddenapi-light-greylist.txt
        setField(service, "mBase", this);
    }

    private boolean setField(Object targetObject, String fieldName, Object fieldValue) {
        Field field;
        try {
            field = targetObject.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = null;
        }
        Class superClass = targetObject.getClass().getSuperclass();
        while (field == null && superClass != null) {
            try {
                field = superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                superClass = superClass.getSuperclass();
            }
        }
        if (field == null) {
            return false;
        }
        field.setAccessible(true);
        try {
            field.set(targetObject, fieldValue);
            return true;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    interface GCAction1<T> {
        void run(T t);
    }
}
