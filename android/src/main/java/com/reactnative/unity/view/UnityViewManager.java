package com.reactnative.unity.view;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by xzper on 2018-02-07.
 */

public class UnityViewManager extends SimpleViewManager<UnityView> implements LifecycleEventListener {
    private static final String REACT_CLASS = "UnityView";

    public static final int COMMAND_POST_MESSAGE = 1;

    private ReactApplicationContext context;

    UnityViewManager(ReactApplicationContext context) {
        super();
        this.context = context;
        context.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public @Nullable Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "postMessage", COMMAND_POST_MESSAGE
        );
    }

    @Override
    public void receiveCommand(UnityView root, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_POST_MESSAGE:
                String gameObject = args.getString(0);
                String methodName = args.getString(1);
                String message = args.getString(2);
                UnityUtils.postMessage(gameObject, methodName, message);
                break;
        }
    }

    @Override
    protected UnityView createViewInstance(ThemedReactContext reactContext) {
        if (!UnityUtils.hasUnityPlayer()) {
            UnityUtils.createPlayer(context.getCurrentActivity());
        }
        UnityView view = new UnityView(reactContext, UnityUtils.getPlayer());
        UnityUtils.addUnityEventListener(view);
        return view;
    }

    @Override
    public void onDropViewInstance(UnityView view) {
        UnityUtils.removeUnityEventListener(view);
        super.onDropViewInstance(view);
    }

    @Override
    public @Nullable Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                UnityMessageEvent.EVENT_NAME, MapBuilder.of("registrationName", "onMessage")
        );
    }

    @Override
    public void onHostResume() {
        if (UnityUtils.hasUnityPlayer()) {
            UnityUtils.getPlayer().resume();
        }
    }

    @Override
    public void onHostPause() {
        if (UnityUtils.hasUnityPlayer()) {
            UnityUtils.getPlayer().pause();
        }
    }

    @Override
    public void onHostDestroy() {
        UnityUtils.getPlayer().quit();
    }
}
