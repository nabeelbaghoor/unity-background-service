package com.kdg.toast.plugin;

public interface PluginCallback {
    public void onSuccess(String videoPath);
    public void onError(String errorMessage);
}